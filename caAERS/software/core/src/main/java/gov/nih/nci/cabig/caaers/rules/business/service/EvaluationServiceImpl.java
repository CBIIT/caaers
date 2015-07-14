/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.rules.business.service;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.dto.ApplicableReportDefinitionsDTO;
import gov.nih.nci.cabig.caaers.domain.dto.EvaluationResultDTO;
import gov.nih.nci.cabig.caaers.domain.dto.ReportDefinitionWrapper;
import gov.nih.nci.cabig.caaers.domain.dto.ReportDefinitionWrapper.ActionType;
import gov.nih.nci.cabig.caaers.domain.dto.SafetyRuleEvaluationResultDTO;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection;
import gov.nih.nci.cabig.caaers.domain.report.*;
import gov.nih.nci.cabig.caaers.rules.common.AdverseEventEvaluationResult;
import gov.nih.nci.cabig.caaers.rules.common.CaaersRuleUtil;
import gov.nih.nci.cabig.caaers.rules.common.RuleType;
import gov.nih.nci.cabig.caaers.service.EvaluationService;
import gov.nih.nci.cabig.caaers.validation.ValidationErrors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.ListUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * This class is a facade to the @{AdverseEventEvaluationService}, provides methods to evaluate serious adverse events
 *
 * @author Srini Akkala
 * @author Biju Joseph
 */

@Transactional(readOnly = true)
public class EvaluationServiceImpl implements EvaluationService {
    private AdverseEventEvaluationService adverseEventEvaluationService;

    private static final Log log = LogFactory.getLog(EvaluationServiceImpl.class);

    private ReportDefinitionDao reportDefinitionDao;

    private OrganizationDao organizationDao;
    

	ReportDefinitionFilter reportDefinitionFilter;
    
    public EvaluationServiceImpl() {
    	reportDefinitionFilter = new ReportDefinitionFilter();
	}

    /**
     * This method evaluates the SAE reporting rules on the reporting period. The output evaluation result will have the following
     *  - For new data collection , what are the suggestions
     *  - For existing data collection, what are the suggestions.
     *  - An index relating which AdverseEvent is evaluated for a data collection.
     *  - An index relating which AdverseEvent is associated to which completed reports
     *  - An index mapping which AdverseEvent is associated which suggested report definition.
     *  - Report definitions, getting amended, withdrawn, edited and created. 
     *  
     * @param reportingPeriod
     * @return
     */
    public EvaluationResultDTO evaluateSAERules(AdverseEventReportingPeriod reportingPeriod){
    	assert reportingPeriod != null : "Reporting period should not be null";
    	EvaluationResultDTO  result = new EvaluationResultDTO();
    	
    	List<ExpeditedAdverseEventReport> aeReports = reportingPeriod.getAeReports();
    	
    	//determine discrete set of AdverseEvents, against which the rules should be fired.
    	List<AdverseEvent> newlyAddedAdverseEvents = reportingPeriod.getNonExpeditedAdverseEvents();
    	
    	// CAAERS-4881 : have to remove unmodified duplicate adverse events from the newly added adverse events;
    	
    	if(aeReports != null && !aeReports.isEmpty()){
    		removeUnModifiedDuplicateAdverseEvents(newlyAddedAdverseEvents, aeReports.get(aeReports.size()-1));
    	}
    	
    	//find the evaluation for default (new data collection)
        if(!newlyAddedAdverseEvents.isEmpty()) {
            //fake expedited report with TreatmentInformation
            ExpeditedAdverseEventReport fakeAeReport = new ExpeditedAdverseEventReport();
            fakeAeReport.setTreatmentInformation(new TreatmentInformation());
            fakeAeReport.getTreatmentInformation().setTreatmentAssignment(new TreatmentAssignment());
            String tac = reportingPeriod.getTreatmentAssignment() != null ? reportingPeriod.getTreatmentAssignment().getCode() : "";
            fakeAeReport.getTreatmentInformation().getTreatmentAssignment().setCode(tac);
            findRequiredReportDefinitions(fakeAeReport, newlyAddedAdverseEvents, reportingPeriod.getStudy(), result);
        }
    	result.addAllAdverseEvents(new Integer(0), newlyAddedAdverseEvents);
    	
    	//for each data collection (existing) find the evaluation
    	if(aeReports != null && !aeReports.isEmpty()){
    		for(ExpeditedAdverseEventReport aeReport : aeReports){
    			List<AdverseEvent> evaluatableAdverseEvents = new ArrayList<AdverseEvent>(newlyAddedAdverseEvents);
    			List<AdverseEvent> existingAdverseEvents = aeReport.isActive() ? aeReport.getActiveAdverseEvents() : aeReport.getActiveModifiedAdverseEvents() ;
                List<AdverseEvent> deletedAdverseEvents = aeReport.getRetiredAdverseEvents();
        		evaluatableAdverseEvents.addAll(existingAdverseEvents);
                evaluatableAdverseEvents.addAll(deletedAdverseEvents);
        		
        		List<AdverseEvent> allAdverseEvents = new ArrayList<AdverseEvent>(newlyAddedAdverseEvents);
        		allAdverseEvents.addAll(aeReport.getAdverseEvents());
        		removeUnModifiedDuplicateAdverseEvents(evaluatableAdverseEvents, aeReport);
        		if(!evaluatableAdverseEvents.isEmpty()) findRequiredReportDefinitions(aeReport, evaluatableAdverseEvents, reportingPeriod.getStudy(), result);
        		result.addAllAdverseEvents(aeReport.getId(), allAdverseEvents);
        		
        		//populate the reported adverse event - report definition map.
        		List<Report> completedAmendableReports = aeReport.findCompletedAmendableReports();
        		for(AdverseEvent ae : aeReport.getAdverseEvents()){
        			List<ReportDefinition> rdList = new ArrayList<ReportDefinition>();
        			for(Report completedReport : completedAmendableReports){
            			if(completedReport.isReported(ae)){
            				rdList.add(completedReport.getReportDefinition());
            			}
            		}
        			result.getReportedAEIndexMap().put(ae.getId(), rdList);
    			}
        		
        		
    		}
    	}
    	
    	result.refreshAdverseEventIndexMap();

    	if(log.isInfoEnabled()){
    		log.info("============== Evaluation result =============");
    		log.info(result.toString());
    		log.info("==============================================");
    	}
    	
    	return result;
    }
    
    
    private void removeUnModifiedDuplicateAdverseEvents(List<AdverseEvent> adverseEvents, ExpeditedAdverseEventReport aeReport){
    	Iterator<AdverseEvent> aeIterator = adverseEvents.iterator();
    	while(aeIterator.hasNext()){
    		AdverseEvent ae = aeIterator.next();
    		if(aeReport.doesAnotherAeWithSameTermExist(ae) != null){
    			// remove the AE from evaluation input if the AE is already part of the report and is not modified according to the signature
    			if(ae.getAddedToReportAtLeastOnce() != null && ae.getAddedToReportAtLeastOnce() && !ae.isModified()){
    				aeIterator.remove();
    			}
    		}
    	}
    	
    }
    
    
    /**
     * This method invokes the {@link AdverseEventEvaluationService} to obtain the report definitions suggested. 
     * Then process that information, to get the adverse event result {@link EvaluationResultDTO}
     * 
     * Overview on extra processing
     *   0. Ignore all the 'soft deleted' reports suggested by rules engine. 
     *   1. If child report or a report of the same group is active , parent report suggested by rules is ignored.
     *   2. All manually selected active reports are suggested by caAERS
     *   3. If there is a manual selection, ignore the others suggested by rules
     *   4. If there is an AE modified, which is part of submitted report, force amend it. 
     *   5. If any, Withdraw all active reports (non manually selected), that are not suggested.
     *   
     * @param aeReport - The {@link ExpeditedAdverseEventReport}
     */
    public void findRequiredReportDefinitions(ExpeditedAdverseEventReport aeReport, List<AdverseEvent> aeList, Study study, EvaluationResultDTO evaluationResult) {
        Map<AdverseEvent,List<ReportDefinition>> adverseEventRecommendedReportsMap = new HashMap<AdverseEvent, List<ReportDefinition>>();

        List<AdverseEvent> deletedAeList = new ArrayList<AdverseEvent>();
        List<AdverseEvent> newAeList = new ArrayList<AdverseEvent>();
        List<AdverseEvent> modifiedAeList = new ArrayList<AdverseEvent>();
        List<AdverseEvent> evaluatableAeList = new ArrayList<AdverseEvent>();
        for(AdverseEvent ae : aeList)  {
            if(ae.isRetired()) {
                deletedAeList.add(ae);
            } else if(ae.getReport() == null) {
                newAeList.add(ae);
            } else {
                modifiedAeList.add(ae);
            }
        }

        evaluatableAeList.addAll(modifiedAeList);
        evaluatableAeList.addAll(newAeList);

        ExpeditedAdverseEventReport expeditedData = aeReport.getId() == null ? null : aeReport;
        //to hold the report defnitions while cleaning up. 
        Map<String , ReportDefinition> loadedReportDefinitionsMap = new HashMap<String, ReportDefinition>();

        Map<AdverseEvent, List<AdverseEventEvaluationResult>> adverseEventEvaluationResultMap;
        Map<AdverseEvent, List<String>> map;

        boolean alertNeeded = false;
        Integer aeReportId = expeditedData == null ? new Integer(0) : expeditedData.getId();
        try {
        	//evaluate the SAE reporting rules
            adverseEventEvaluationResultMap = adverseEventEvaluationService.evaluateSAEReportSchedule(aeReport, evaluatableAeList, study);
            evaluationResult.getRulesEngineRawResultMap().put(aeReportId, adverseEventEvaluationResultMap);
            map = new HashMap<AdverseEvent, List<String>>();
            
            // clear the recommended reports map
            adverseEventRecommendedReportsMap.clear();
            
            //clean up - by eliminating the deleted report definitions.
            for(Map.Entry<AdverseEvent, List<AdverseEventEvaluationResult>> entry : adverseEventEvaluationResultMap.entrySet()){
                Set<String> rdNameSet = new HashSet<String>();
                AdverseEvent adverseEvent = entry.getKey();
                Set<ReportDefinition> recommendedAeReports = new HashSet<ReportDefinition>();
                for(AdverseEventEvaluationResult aeEvalResult : entry.getValue()){
                	for(String response : aeEvalResult.getRuleEvaluationResult().getResponses()){
                		if(!StringUtils.isBlank(response)){
                			ReportDefinition rd = reportDefinitionDao.getByName(response);
                			if(rd != null){
                				recommendedAeReports.add(rd);
                			}
                		}
                	}
                }
                adverseEventRecommendedReportsMap.put(adverseEvent, new ArrayList<ReportDefinition>(recommendedAeReports));
                

                List<String> validReportDefNames   = new ArrayList<String>();
                map.put(adverseEvent, validReportDefNames);
                evaluationResult.addProcessingStep(aeReportId, "RulesEngine: Evaluation for adverse event (" + AdverseEvent.toReadableString(adverseEvent) + ") :", null);
                for(AdverseEventEvaluationResult adverseEventEvaluationResult : entry.getValue()){
                    evaluationResult.addProcessingStep(aeReportId, " RuleSet:", adverseEventEvaluationResult.getRuleMetadata() );
                    evaluationResult.addProcessingStep(aeReportId, " Raw message :", adverseEventEvaluationResult.getMessage() );
                    if(adverseEventEvaluationResult.getRuleEvaluationResult() != null){
                        evaluationResult.addProcessingStep(aeReportId, " Bind URL :", adverseEventEvaluationResult.getRuleEvaluationResult().getBindURI() );
                        evaluationResult.addProcessingStep(aeReportId, " Matched rules :", adverseEventEvaluationResult.getRuleEvaluationResult().getMatchedRules().toString() );
                        for(String note : adverseEventEvaluationResult.getNotes()) {
                            evaluationResult.addProcessingStep(aeReportId, "  Notes: " , note);
                        }
                        evaluationResult.addProcessingStep(aeReportId, " Matched rules :", adverseEventEvaluationResult.getRuleEvaluationResult().getMatchedRules().toString() );

                    } else {
                        evaluationResult.addProcessingStep(aeReportId, " Bind URL :", null );
                        evaluationResult.addProcessingStep(aeReportId, " Matched rules :", null );
                    }


                    if(adverseEventEvaluationResult.isCannotDetermine() || adverseEventEvaluationResult.isNoRulesFound()) continue;

                    evaluationResult.addProcessingStep(aeReportId, " Raw suggestions :", adverseEventEvaluationResult.getRuleEvaluationResult().getResponses().toString() );

                    rdNameSet.addAll(adverseEventEvaluationResult.getRuleEvaluationResult().getResponses());
                }

                //CAAERS-5702
                if(rdNameSet.contains("IGNORE")){
                    rdNameSet.clear();
                    evaluationResult.addProcessingStep(aeReportId, "caAERS : Protocol specific exception, so removing all recommendations","");
                }

                for(String reportDefName : rdNameSet){
                    ReportDefinition rd = loadedReportDefinitionsMap.get(reportDefName);
                    if(rd == null) {
                        rd = reportDefinitionDao.getByName(reportDefName);
                        if(rd == null){
                            evaluationResult.addProcessingStep(aeReportId, "report definition missing in database " , reportDefName);
                            log.warn("Report definition (" + reportDefName + "), is referred in rules but is not found");
                            continue; //we cannot find the report referred by the rule
                        }
                        loadedReportDefinitionsMap.put(reportDefName, rd);
                    }

                    if(rd.getEnabled()){
                        validReportDefNames.add(reportDefName);
                    }

                }
                evaluationResult.addProcessingStep(aeReportId, "caAERS : Plausible suggestions :", validReportDefNames.toString() );
                evaluationResult.addProcessingStep(aeReportId, " ", null );

            }
            
            for(Map.Entry<AdverseEvent,List<ReportDefinition>> entry : adverseEventRecommendedReportsMap.entrySet()){
            	List<ReportDefinition> filteredRdList = reportDefinitionFilter.filter(entry.getValue());
            	entry.setValue(filteredRdList);
            }


            //save this for reference.
            evaluationResult.addRulesEngineResult(aeReportId, map);


            //now load report definitions
            List<ReportDefinition> defList = new ArrayList<ReportDefinition>();
            defList.addAll(loadedReportDefinitionsMap.values());


            List<Report> completedReports = expeditedData == null ? new ArrayList<Report>() : expeditedData.listReportsHavingStatus(ReportStatus.COMPLETED);


            //Remove all NOTIFICATIONS from completed reports. As notifications must be completed by a subsequent full report.
            List<Report> notificationsToRemove = new ArrayList<Report>();
            for(Report report : completedReports) {
                List<ReportDefinition> rdList = ReportDefinition.findByName(defList, report.getName());
                if(!rdList.isEmpty() && rdList.get(0).getReportType() == ReportType.NOTIFICATION) {
                     notificationsToRemove.add(report);
                }
            }
            completedReports.removeAll(notificationsToRemove);

            if(!completedReports.isEmpty()){

                for(AdverseEvent adverseEvent : evaluatableAeList){

                    if(adverseEvent.getReport() == null) continue;      //unreported AE -  continue

                    List<String> nameList = map.get(adverseEvent);

                    if(adverseEvent.isModified()) {
                        //throw away notifications if AE is already reported.
                        for(Report report : completedReports) {
                            if(report.isReported(adverseEvent)) {
                                List<ReportDefinition> rdList = ReportDefinition.findByName(defList, nameList.toArray(new String[0]));
                                List<ReportDefinition> sameOrgGroupList = ReportDefinition.findBySameOrganizationAndGroup(rdList, report.getReportDefinition());
                                if(sameOrgGroupList.size() > 1) {
                                   List<ReportDefinition> rdNotificationList = ReportDefinition.findByReportType(sameOrgGroupList, ReportType.NOTIFICATION);
                                   for(ReportDefinition rd : rdNotificationList) {
                                       // we must remove these from suggestions.
                                       nameList.remove(rd.getName());
                                       boolean removed = defList.remove(rd);
                                       evaluationResult.removeReportDefinitionName(aeReportId, adverseEvent, rd.getName());
                                       evaluationResult.addProcessingStep(aeReportId, "caAERS : Adverse event (" + AdverseEvent.toReadableString(adverseEvent) + ") is already reported in :", "" + report.getId());
                                       evaluationResult.addProcessingStep(aeReportId, " Notifications are not needed again, removing:", rd.getName() );
                                       evaluationResult.addProcessingStep(aeReportId, " removed ? :", String.valueOf(removed) );
                                   }

                                }
                            }
                        }
                    } else {
                        //throw away rules suggestion - if AE is not modified and is part of a submitted report OR if AE is new

                        for(Report report : completedReports){
                            if(report.isReported(adverseEvent)){
                                nameList.remove(report.getName());
                                List<ReportDefinition> rdList = ReportDefinition.findByName(defList,new String[]{report.getName()});
                                if(!rdList.isEmpty()) defList.remove(rdList.get(0));
                                evaluationResult.removeReportDefinitionName(aeReportId, adverseEvent, report.getName());
                                evaluationResult.addProcessingStep(aeReportId, "caAERS : Adverse event (" + AdverseEvent.toReadableString(adverseEvent) + "):", null);
                                evaluationResult.addProcessingStep(aeReportId, " Unmodified and belongs to completed report :", null );
                                evaluationResult.addProcessingStep(aeReportId, " Removing suggestion :", report.getName() );

                            }
                        }

                    }



                }
            }
            

            //Update AE reporting flag (or sae flag)
            for(AdverseEvent ae : map.keySet()){
            	List<String> nameList = map.get(ae);
            	ae.setRequiresReporting(!nameList.isEmpty());
                evaluationResult.addProcessingStep(aeReportId, "caAERS: Adverse event (" + AdverseEvent.toReadableString(ae) + ") may need reporting ? : ", String.valueOf(ae.getRequiresReporting()) );
            }
            
            //logging
            if(log.isDebugEnabled()){
            	log.debug("Rules Engine Result for : " + aeReportId + ", " + String.valueOf(map));
            }


            //  - If child report is active, select that instead of parent. 
            // - If there is a manual selection, ignore rules engine suggestions from the same group
            // - If the manual selection is always a preferred one (ie. by default add active manual selected reports). 
            // - If there is an ae modified, which is part of completed report, force amending it.
            List<Report> activeReports = null;
            if(expeditedData != null){
            	activeReports = expeditedData.getActiveReports();
            	List<Report> manuallySelectedReports = expeditedData.getManuallySelectedReports();
            	
            	//a temporary list
            	List<ReportDefinition> tmplist = new ArrayList<ReportDefinition>(defList);


                //keep active child report instead of parent.
            	for(Report activeReport : activeReports){
            		ReportDefinition rdParent = activeReport.getReportDefinition().getParent();
            		ReportDefinition rdFound = findReportDefinition(tmplist, rdParent);
            		
            		if(rdFound != null){
            			//remove parent and keep child
            			defList.remove(rdFound);
            			defList.add(activeReport.getReportDefinition());
            			evaluationResult.replaceReportDefinitionName(aeReportId, rdFound.getName(), activeReport.getName());
                        evaluationResult.addProcessingStep(aeReportId, "caAERS: Active child report (" + activeReport.getName() + ") present", null );
                        evaluationResult.addProcessingStep(aeReportId, " Removing suggestion", rdFound.getName() );
                    }
            	}
            	
            	//throw away all suggestions of rules engine, (if they belong to the same group as that of manually selected)
            	for(Report manualReport : manuallySelectedReports){
            		ReportDefinition rdManual = manualReport.getReportDefinition();
            		
            		for(ReportDefinition rdSuggested : tmplist){
            			if(rdSuggested.isOfSameReportTypeAndOrganization(rdManual) && manualReport.isActive() ){
            				//remove it from rules engine suggestions
            				defList.remove(rdSuggested);
            				evaluationResult.replaceReportDefinitionName(aeReportId, rdSuggested.getName(), rdManual.getName());
                            evaluationResult.addProcessingStep(aeReportId, "caAERS: Manually selected report (" + rdManual.getName() + ") present", null );
                            evaluationResult.addProcessingStep(aeReportId, " Removing suggestion", rdSuggested.getName() );
                        }
            		}
            		
            		//now add the manually selected report.
            		defList.add(rdManual);
            		evaluationResult.addReportDefinitionName(aeReportId, rdManual.getName());
                    evaluationResult.addProcessingStep(aeReportId, " Adding to suggestion ", rdManual.getName() );

                }


            	//any ae modified/got completed reports ? add those report definitions.
            	if(defList.isEmpty() && !modifiedAeList.isEmpty()){
                  	//Any completed report, suggest amending it to proceed (but no alert).
                	for(Report report : completedReports){
         				
         				ReportDefinition rdCompleted = report.getReportDefinition();
         				
         				if(!rdCompleted.getAmendable()) continue;

                        defList.add(rdCompleted);
                        for(AdverseEvent ae : modifiedAeList){
                            evaluationResult.addReportDefinitionName(aeReportId, ae, rdCompleted.getName());
                            evaluationResult.addProcessingStep(aeReportId, "caAERS: Submitted adverse event (" + AdverseEvent.toReadableString(ae) + ") is modified : ", null);
                            evaluationResult.addProcessingStep(aeReportId, " Adding to suggestion ", rdCompleted.getName() );

                        }
         				
         			}
            	}

                //CAAERS-7067 - the deletions must suggest an Amend (ONLY if the AE was reported on last submitted report)
                if(!deletedAeList.isEmpty()) {
                    // find latest submission from each group and org
                    List<Report> lastSubmittedReports = new ArrayList<Report>();
                    Set<Integer> rdIdSet = new HashSet<Integer>(); //using Set for reports may complicate stuff with equals on hibernate proxy
                    for(Report completedReport : completedReports) {
                        Report latestReport = aeReport.findLastSubmittedReport(completedReport.getReportDefinition());
                        if(rdIdSet.add(latestReport.getReportDefinition().getId())) {
                            lastSubmittedReports.add(latestReport);
                        }
                    }

                    //for each such report, if the AE deleted is submitted on that, then suggest ammend.
                    for(Report submittedReport : lastSubmittedReports) {
                        ReportDefinition rdCompleted = submittedReport.getReportDefinition();
                        if(rdCompleted.getReportType() == ReportType.NOTIFICATION) continue; //CAAERS-7041
                        if(!rdCompleted.getAmendable()) continue;

                        for(AdverseEvent ae : deletedAeList) {
                            boolean reported = submittedReport.isReported(ae);
                            if(reported) {
                                defList.add(rdCompleted);
                                evaluationResult.addReportDefinitionName(aeReportId, ae, rdCompleted.getName());
                                evaluationResult.addProcessingStep(aeReportId, "caAERS: Submitted adverse event (" + AdverseEvent.toReadableString(ae) + ") is deleted : ", null);
                                evaluationResult.addProcessingStep(aeReportId, " Adding to suggestion ", rdCompleted.getName() );
                            }
                        }
                    }
                }
            	
            }
            
            
           //logging 
           if(log.isDebugEnabled()){
        	 log.debug("Report Definitions before filtering for aeReportId: " + aeReportId + ", " + String.valueOf(defList));  
           }
           
           //filter the report definitions
           List<ReportDefinition> reportDefinitions =  reportDefinitionFilter.filter(defList);
            
           if(reportDefinitions != null){
               List<String> filteredReportDefnitionNames = new ArrayList<String>();
               for(ReportDefinition rd: reportDefinitions){
                   filteredReportDefnitionNames.add(rd.getName());
               }
               evaluationResult.addProcessingStep(aeReportId, " ", null );
               evaluationResult.addProcessingStep(aeReportId, "caAERS: Final suggestion after filtering :", filteredReportDefnitionNames.toString());
           }

           
          //modify the alert necessary flag, based on eventual set of report definitions
          if(expeditedData == null){
        	  alertNeeded = !reportDefinitions.isEmpty();
          }else{
        	  for(ReportDefinition reportDefinition : reportDefinitions){
             	alertNeeded |= expeditedData.findReportsToEdit(reportDefinition).isEmpty();
              }
          }
       	  evaluationResult.getAeReportAlertMap().put(aeReportId, alertNeeded);
          evaluationResult.addProcessingStep(aeReportId, "caAERS: Alert is needed ? ",  String.valueOf(alertNeeded));
           
           //logging 
           if(log.isDebugEnabled()){
        	 log.debug("Report Definitions after filtering for aeReportId: " + aeReportId + ", " + String.valueOf(reportDefinitions));  
           }

           //now go through each report definition and set amend/create edit/withdraw/create maps properly
           Set<ReportDefinitionWrapper> rdCreateSet = new HashSet<ReportDefinitionWrapper>();
           Set<ReportDefinitionWrapper> rdEditSet = new HashSet<ReportDefinitionWrapper>();
           Set<ReportDefinitionWrapper> rdWithdrawSet = new HashSet<ReportDefinitionWrapper>();
           Set<ReportDefinitionWrapper> rdAmmendSet = new HashSet<ReportDefinitionWrapper>();
           
           ReportDefinitionWrapper wrapper;
           for(ReportDefinition rd : reportDefinitions){
        	  
        	 if(expeditedData == null){  
        		  //all report definitions, should go in the createMap.
        		  wrapper = new ReportDefinitionWrapper(rd, null, ActionType.CREATE);
        		  wrapper.setStatus("Not started");
        		  rdCreateSet.add(wrapper);
        	 }else{
        		 
         	  	  //find reports getting amended
	        	  List<Report> reportsAmmended = expeditedData.findReportsToAmmend(rd); 
	        	  for(Report report : reportsAmmended){
	        		  wrapper = new ReportDefinitionWrapper(report.getReportDefinition(), rd, ActionType.AMEND);
	        		  wrapper.setStatus(report.getLastVersion().getStatusAsString());
	        		  wrapper.setSubmittedOn(report.getSubmittedOn());
	        		  rdAmmendSet.add(wrapper);
	        	  }
	        	  
	        	  //find reports getting withdrawn
	        	  List<Report> reportsWithdrawn = expeditedData.findReportsToWithdraw(rd);
	        	  for(Report report : reportsWithdrawn){
	        		  wrapper = new ReportDefinitionWrapper(report.getReportDefinition(), rd, ActionType.WITHDRAW);
	        		  wrapper.setStatus("In process");
	        		  wrapper.setDueOn(report.getDueOn());
	        		  rdWithdrawSet.add(wrapper);
	        	  }
	        	  

	        	  //find the reports getting edited
	        	  List<Report> reportsEdited = expeditedData.findReportsToEdit(rd);
	        	  for(Report report : reportsEdited){
	        		  wrapper = new ReportDefinitionWrapper(report.getReportDefinition(), rd, ActionType.EDIT);
	        		  wrapper.setStatus("In process");
	        		  wrapper.setDueOn(report.getDueOn());
	        		  rdEditSet.add(wrapper);
	        	  }
	        	  
	        	  //Nothing getting edited,  add in this report def in create list
	        	  if(reportsEdited.isEmpty() && reportsAmmended.isEmpty() && reportsWithdrawn.isEmpty()){
	        		 wrapper = new ReportDefinitionWrapper(rd, null, ActionType.CREATE);
	         		 wrapper.setStatus("Not started");
	         		 rdCreateSet.add(wrapper);
	        	  }
	        	  
              }//if expeditedData  
         	 
           }//for rd
           
           //Check if there is a need to withdraw any active report. 
           if(expeditedData != null && activeReports != null){
        	   for(Report report : activeReports){
        		   ReportDefinition rdActive = report.getReportDefinition();
        		   if(report.isManuallySelected()) continue;
        		   boolean toBeWithdrawn = true;
        		   for(ReportDefinitionWrapper editWrapper : rdEditSet){
        			   if(editWrapper.getDef().equals(rdActive)){
        				   toBeWithdrawn = false;
        				   break;
        			   }
        		   }
        		   
        		   if(toBeWithdrawn){
        			   for(ReportDefinitionWrapper withdrawWrapper :rdWithdrawSet){
            			   if(withdrawWrapper.getDef().equals(rdActive)){
            				   toBeWithdrawn = false;
            				   break;
            			   }
            		   }  
        		   }
        		   
        		   if(toBeWithdrawn){
        			  wrapper = new ReportDefinitionWrapper(rdActive, null, ActionType.WITHDRAW);
        			  wrapper.setDueOn(report.getDueOn());
 	        		  wrapper.setStatus("In process");
 	        		  rdWithdrawSet.add(wrapper);
        		   }
        	   }
           }
           
           //add everything to the result.
           evaluationResult.getCreateMap().put(aeReportId, rdCreateSet);
           evaluationResult.getAmendmentMap().put(aeReportId, rdAmmendSet);
           evaluationResult.getEditMap().put(aeReportId, rdEditSet);
           evaluationResult.getWithdrawalMap().put(aeReportId, rdWithdrawSet);

           if(!rdCreateSet.isEmpty()){
               evaluationResult.addProcessingStep(aeReportId, "caAERS: Create options :",  null);
               for(ReportDefinitionWrapper rdWrapper : rdCreateSet){
                evaluationResult.addProcessingStep(aeReportId,  " " + rdWrapper.getReadableMessage(), null);
               }
           }

           if(!rdAmmendSet.isEmpty()){
               evaluationResult.addProcessingStep(aeReportId, "caAERS: Amend options :",  null);
               for(ReportDefinitionWrapper rdWrapper : rdAmmendSet){
                   evaluationResult.addProcessingStep(aeReportId,  " " + rdWrapper.getReadableMessage(), null);
               }
           }

           if(!rdEditSet.isEmpty()){
               evaluationResult.addProcessingStep(aeReportId, "caAERS: Edit options :",  null);
               for(ReportDefinitionWrapper rdWrapper : rdEditSet){
                   evaluationResult.addProcessingStep(aeReportId,  " " + rdWrapper.getReadableMessage(), null);
               }
           }

           if(!rdWithdrawSet.isEmpty()){
               evaluationResult.addProcessingStep(aeReportId, "caAERS: Withdraw options :",  null);
               for(ReportDefinitionWrapper rdWrapper : rdWithdrawSet){
                   evaluationResult.addProcessingStep(aeReportId, " " + rdWrapper.getReadableMessage(), null);
               }
           }

           //update the result object
           evaluationResult.addEvaluatedAdverseEvents(aeReportId, evaluatableAeList);
//           evaluationResult.addResult(aeList, reportDefinitions);
           evaluationResult.addResult(expeditedData, reportDefinitions);
            
        } catch (Exception e) {
            throw new CaaersSystemException("Could not determine the reports necessary for the given expedited adverse event data", e);
        }
        
    }

    /**
     * This method will find all the report definitions belonging to the Study
     */
    public ApplicableReportDefinitionsDTO applicableReportDefinitions(Study study, StudyParticipantAssignment assignment) {
    	
        List<ReportDefinition> reportDefinitions = new ArrayList<ReportDefinition>();
        // Same organization play multiple roles.
        Set<Integer> orgIdSet = new HashSet<Integer>();
        List<StudyOrganization> studyOrgs =  study.getStudyOrganizations();
        for (StudyOrganization studyOrganization : studyOrgs) {
        	// Ignore the organization if its just a study site and not the one where assignment belongs to.
        	if(studyOrganization instanceof StudySite && !studyOrganization.getId().equals(assignment.getStudySite().getId()))
        		continue;
        	if(orgIdSet.add(studyOrganization.getOrganization().getId()))
        			reportDefinitions.addAll(reportDefinitionDao.getAll(studyOrganization.getOrganization().getId()));
        }
        
        /**
         * Get REport definitions of CTEP for DCP studies , because DCP uses CTEP 
         * report definitions also . TEMP fix
         */
        Organization primarySponsor = study.getPrimaryFundingSponsorOrganization();
        
        //CAAERS-4215
        //if (primarySponsor.getName().equals("Division of Cancer Prevention")) {
        	//reportDefinitions.addAll(reportDefinitionDao.getAll(this.organizationDao.getByName("Cancer Therapy Evaluation Program").getId()));
        //}
        
        ApplicableReportDefinitionsDTO dto = new ApplicableReportDefinitionsDTO();
        for(ReportDefinition rd : reportDefinitions){
    	  dto.addReportDefinition(rd);
        }
        
       return dto;
    }

    /**
     * Will find the mandatory sections associated with the report definitions. 
     * @param expeditedData
     * @param reportDefinitions
     * @return
     */
    public Map<Integer, Collection<ExpeditedReportSection>> mandatorySections( ExpeditedAdverseEventReport expeditedData, ReportDefinition... reportDefinitions) {
        
           Map<Integer, Collection<ExpeditedReportSection>> mandatorySectionMap = new HashMap<Integer, Collection<ExpeditedReportSection>>();
        try {
            
            for(ReportDefinition reportDefinition : reportDefinitions ){
               Collection<ExpeditedReportSection> sections = adverseEventEvaluationService.mandatorySections(expeditedData, reportDefinition);
               mandatorySectionMap.put(reportDefinition.getId(), sections);
            }

            if (log.isDebugEnabled()) log.debug("Mandatory sections: " + mandatorySectionMap);
            return mandatorySectionMap;
        } catch (Exception e) {
            throw new CaaersSystemException("Could not get mandatory sections", e);
        }
    }

    public ValidationErrors validateReportingBusinessRules(ExpeditedAdverseEventReport aeReport, ExpeditedReportSection... sections) {
        try {
            return adverseEventEvaluationService.validateReportingBusinessRules(aeReport, sections);
        } catch (Exception e) {
            log.error("Error while evaluating business rules", e);
            throw new CaaersSystemException("Error while evaluating business rules", e);
        }
    }


    /**
     * Evaluate the mandatoryness of a specific report, the {@link gov.nih.nci.cabig.caaers.domain.report.ReportMandatoryField} will be populated in the Report.
     * @param aeReport
     * @param report
     */
    public void evaluateMandatoryness(final ExpeditedAdverseEventReport aeReport, final Report report) {

        final ReportDefinition rd = report.getReportDefinition();

        //clear the mandatory fields in report
        final List<ReportMandatoryField> mfList = new ArrayList<ReportMandatoryField>();
        report.setMandatoryFields(mfList);

        if(log.isDebugEnabled()) log.debug("Static Mandatory field evaluation");

        //evaluation of static field rules
        CollectionUtils.forAllDo(rd.getAllNonRuleBasedMandatoryFields(), new Closure<ReportMandatoryFieldDefinition>(){
            public void execute(ReportMandatoryFieldDefinition mfd) {
                ReportMandatoryField mf = new ReportMandatoryField(mfd.getFieldPath(), Mandatory.NA);
                //update the mandatory flag
                if(mfd.getMandatory().equals(RequirednessIndicator.OPTIONAL)) mf.setMandatory(Mandatory.OPTIONAL);
                if(mfd.getMandatory().equals(RequirednessIndicator.MANDATORY)) mf.setMandatory(Mandatory.MANDATORY);
                if(log.isDebugEnabled()) log.debug( mfd.getFieldPath() + " -->" + mf.getMandatory().getName());
                mfList.add(mf);
            }
        });


        final List<Object> baseInputObjects = new ArrayList<Object>();
        baseInputObjects.add(aeReport);
        baseInputObjects.add(rd);
        if(aeReport.getStudy() != null) baseInputObjects.add(aeReport.getStudy());
        if(aeReport.getTreatmentInformation() != null) baseInputObjects.add(aeReport.getTreatmentInformation());


        //non self referenced rules
        final List<Object> inputObjects = new ArrayList(baseInputObjects);
        inputObjects.addAll(aeReport.getActiveAdverseEvents());
        
        final HashMap<String, Mandatory> rulesDecisionCache = new HashMap<String, Mandatory>();
        if(log.isDebugEnabled()) log.debug("Non Self referenced rule evaluation");
        final String fieldRulesBindURL = adverseEventEvaluationService.fetchBindURI(RuleType.FIELD_LEVEL_RULES, null, null, null);
        if(StringUtils.isEmpty(fieldRulesBindURL)){
            log.warn("No active field level rules found, so ignoring rule based mandatoryness evaluation");
        }
        CollectionUtils.forAllDo(rd.getNonSelfReferencedRuleBasedMandatoryFields(), new Closure<ReportMandatoryFieldDefinition>(){
            public void execute(ReportMandatoryFieldDefinition mfd) {
               String ruleName = mfd.getRuleName();
               String path = mfd.getFieldPath();
               Mandatory m = rulesDecisionCache.get(ruleName);
               if(StringUtils.isEmpty(fieldRulesBindURL)) {
                   log.info(mfd.getFieldPath() + " marking it as optional, as there is no field rules found");
                   m = Mandatory.OPTIONAL;
               }
               if(m == null){
                   String decision = adverseEventEvaluationService.evaluateFieldLevelRules(fieldRulesBindURL, ruleName, inputObjects);
                   if(log.isDebugEnabled()) log.debug("rules decision : " + decision);
                   m = translateRulesMandatorynessResult(decision);
                   rulesDecisionCache.put(ruleName, m);
                   if(log.isDebugEnabled()) log.debug( "caching --> " + m.getName());
               }
               if(log.isDebugEnabled()) log.debug( mfd.getFieldPath() + " -->" + m.getName());
               mfList.add(new ReportMandatoryField(path, m));
            }
        });

        //self referenced rules
        if(log.isDebugEnabled()) log.debug("Self referenced rule evaluation");
        CollectionUtils.forAllDo(rd.getSelfReferencedRuleBasedMandatoryFields(), new Closure<ReportMandatoryFieldDefinition>(){
            public void execute(ReportMandatoryFieldDefinition mfd) {
                Map<String, Object> map = CaaersRuleUtil.multiplexAndEvaluate(aeReport, mfd.getFieldPath());
                for(String path : map.keySet()){
                    List<Object> inputObjects = new ArrayList(baseInputObjects);
                    Object o = map.get(path);
                    if(o == null) continue;
                    if(o instanceof Collection){
                        inputObjects.addAll((Collection) o);
                    }else {
                        inputObjects.add(o);
                    }
                    String decision = null;
                    if(StringUtils.isEmpty(fieldRulesBindURL)) {
                        log.info(mfd.getFieldPath() + " marking it as optional, as there is no field rules found");
                    }else {
                        decision = adverseEventEvaluationService.evaluateFieldLevelRules(fieldRulesBindURL, mfd.getRuleName(), inputObjects);
                    }
                    if(log.isDebugEnabled()) log.debug("rules decision : " + decision);
                    Mandatory m = translateRulesMandatorynessResult(decision);
                    if(log.isDebugEnabled()) log.debug( mfd.getFieldPath() + " -->" + m.getName());
                    mfList.add(new ReportMandatoryField(path, m));
                }
            }
        });

    }

    protected Mandatory translateRulesMandatorynessResult(String decision){
       if(StringUtils.isEmpty(decision)) return Mandatory.OPTIONAL;                                   
       String[] nameArray = StringUtils.split(decision,"||");
       Set<Mandatory> set = new TreeSet<Mandatory>(new Comparator<Mandatory>(){
           public int compare(Mandatory o1, Mandatory o2) {
               return o1.ordinal() - o2.ordinal();
           }
       });
       for(String s : nameArray) set.add(Mandatory.valueOf(s));
       if(!set.isEmpty()) return set.iterator().next();
       return Mandatory.OPTIONAL;
    }

    /////move this else where
    private ReportDefinition findReportDefinition(List<ReportDefinition> rdList, ReportDefinition toFind){
    	if(toFind == null) return null;
    	for(ReportDefinition rd : rdList){
    		if(rd.getId().equals( toFind.getId())) return rd;
    	}
    	return null;
    }

    public SafetyRuleEvaluationResultDTO evaluateSafetySignallingRules(ObservedAdverseEventProfile observedAEProfile) {
    	if(observedAEProfile.getNotificationStatus() == NotificationStatus.NOTIFY || observedAEProfile.getNotificationStatus() == NotificationStatus.IGNORE_ALREADY_NOTIFIED){
    		SafetyRuleEvaluationResultDTO result = new SafetyRuleEvaluationResultDTO();
            result.setNotificationStatus(NotificationStatus.IGNORE_ALREADY_NOTIFIED);
            result.setRulesMatched(Arrays.asList(new String[]{}));
            return result;
    	}
        return adverseEventEvaluationService.evaluateSafetySignallingRules(observedAEProfile);
    }

// //// CONFIGURATION

    public void setReportDefinitionDao(ReportDefinitionDao reportDefinitionDao) {
        this.reportDefinitionDao = reportDefinitionDao;
    }


    public void setAdverseEventEvaluationService(
                    AdverseEventEvaluationService adverseEventEvaluationService) {
        this.adverseEventEvaluationService = adverseEventEvaluationService;
    }

    public AdverseEventEvaluationService getAdverseEventEvaluationService() {
        return adverseEventEvaluationService;
    }


 
}
