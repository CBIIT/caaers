/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.api.impl;

import static gov.nih.nci.cabig.caaers.domain.dto.ReportDefinitionWrapper.ActionType.AMEND;
import static gov.nih.nci.cabig.caaers.domain.dto.ReportDefinitionWrapper.ActionType.WITHDRAW;
import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.dao.AdverseEventRecommendedReportDao;
import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.StudyParticipantAssignmentDao;
import gov.nih.nci.cabig.caaers.dao.TreatmentAssignmentDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventCtcTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventMeddraLowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventRecommendedReport;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.AeTerminology;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.LocalOrganization;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.ReportTableRow;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.TreatmentAssignment;
import gov.nih.nci.cabig.caaers.domain.dto.ApplicableReportDefinitionsDTO;
import gov.nih.nci.cabig.caaers.domain.dto.EvaluationResultDTO;
import gov.nih.nci.cabig.caaers.domain.dto.ReportDefinitionWrapper;
import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.integration.schema.adverseevent.AdverseEventType;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.AEsOutputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.AdverseEventResult;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.AdverseEvents;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAEsInputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAEsOutputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluatedAdverseEventResults;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.RecommendedActions;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.SaveAndEvaluateAEsInputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.SaveAndEvaluateAEsOutputMessage;
import gov.nih.nci.cabig.caaers.service.EvaluationService;
import gov.nih.nci.cabig.caaers.service.RecommendedActionService;
import gov.nih.nci.cabig.caaers.service.migrator.adverseevent.AdverseEventConverter;
import gov.nih.nci.cabig.caaers.service.migrator.adverseevent.SAEAdverseEventReportingPeriodConverter;
import gov.nih.nci.cabig.caaers.utils.DateUtils;
import gov.nih.nci.cabig.caaers.validation.ValidationErrors;
import gov.nih.nci.cabig.caaers.ws.faults.CaaersFault;
import gov.nih.nci.cabig.ctms.lang.NowFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;

/**
 * EvaluationService evaluate rules on the given AE's submitted by Web service.
 * 
 * @author MedaV
 * 
 */

public class SAEEvaluationServiceImpl implements ApplicationContextAware {

	private AdverseEventRecommendedReportDao adverseEventRecommendedReportDao;
	private StudyDao studyDao;
	private TreatmentAssignmentDao treatmentAssignmentDao;
    private ParticipantDao participantDao;
    private StudyParticipantAssignmentDao studyParticipantAssignmentDao;
    private SAEAdverseEventReportingPeriodConverter reportingPeriodConverter;
    private ReportDefinitionDao reportDefinitionDao;
	/** The now factory. */
	private NowFactory nowFactory;
	private AdverseEventManagementServiceImpl adverseEventManagementService;
	private EvaluationService evaluationService;
	private ApplicationContext applicationContext;
	private MessageSource messageSource;
	private AdverseEventConverter converter;
    private RecommendedActionService recommendedActionService;
    private enum RequestType{SaveEvaluate, Evaluate};
	private static String DEF_ERR_MSG = "Error evaluating adverse events with SAE rules";

	private static Log logger = LogFactory.getLog(SAEEvaluationServiceImpl.class);

    /**
     * Process and Save the Adverse Events.
     *
     * @param saveAndEvaluateAEsInputMessage
     * @return
     * @throws CaaersFault
     */

    public SaveAndEvaluateAEsOutputMessage saveAndProcessAdverseEvents(SaveAndEvaluateAEsInputMessage saveAndEvaluateAEsInputMessage) throws CaaersFault {
        Map<AdverseEvent, AdverseEventResult> mapAE2DTO = new HashMap<AdverseEvent, AdverseEventResult>();
        List<AdverseEvent> aes = new ArrayList<AdverseEvent>();

        if ( saveAndEvaluateAEsInputMessage == null ) {
            throw Helper.createCaaersFault(DEF_ERR_MSG, "WS_SAE_007",
                    messageSource.getMessage("WS_SAE_007", new String[]{},  "", Locale.getDefault())
            );
        }
        SaveAndEvaluateAEsOutputMessage saveAndEvaluateAEsOutputMessage = (SaveAndEvaluateAEsOutputMessage)createResponseObject(RequestType.SaveEvaluate);

        try {
            // 0. Load the study required.
            String studyIdentifier = saveAndEvaluateAEsInputMessage.getCriteria().getStudyIdentifier();
            Study study = fetchStudy(studyIdentifier);

            // 1. Call the converter and make the required object.
            AdverseEventReportingPeriod reportingPeriod = reportingPeriodConverter.convert(saveAndEvaluateAEsInputMessage,mapAE2DTO);

             // 2. Persist AdverseEvents.
            ValidationErrors errors = new ValidationErrors();
            reportingPeriod = adverseEventManagementService.createOrUpdateAdverseEvents(reportingPeriod, errors, true);

            if(errors.hasErrors()){
               logger.error("Adverse Event Management Service create or update call failed :" + String.valueOf(errors));
                if ( errors.getErrorAt(0).getCode().equals("NO-CODE"))
                    throw Helper.createCaaersFault(DEF_ERR_MSG, errors.getErrorAt(0).getCode(), errors.getErrorAt(0).getMessage() + " "  +errors.getErrorAt(0).getReplacementVariables()[0]);
                else
                    throw Helper.createCaaersFault(DEF_ERR_MSG, errors.getErrorAt(0).getCode(), errors.getErrorAt(0).getMessage());
            }

            // 3. fire Evaluation Service to identify SAE or not ?
            saveAndEvaluateAEsOutputMessage = (SaveAndEvaluateAEsOutputMessage)fireSAERules(reportingPeriod, study, mapAE2DTO,RequestType.SaveEvaluate,saveAndEvaluateAEsOutputMessage);
             
            // CAAERS-6613 re-save the AEs with the requiresReporting flag 
            if(reportingPeriod != null){
	            for(AdverseEvent ae : reportingPeriod.getAdverseEvents()){
	            	AdverseEventResult aeResult = findAdverseEvent(ae, mapAE2DTO);
	            	if(aeResult == null){
	            		continue;
	            	} else if(aeResult.isRequiresReporting()){
	            			ae.setRequiresReporting(true);
	            	}
	            }
	            
	            // save the updated reporting period
	            adverseEventManagementService.saveReportingPeriod(reportingPeriod);
	            
            }
            
        }
        catch(CaaersSystemException ex) {
            throw Helper.createCaaersFault(DEF_ERR_MSG, ex.getErrorCode(), ex.getMessage());
        }

        return saveAndEvaluateAEsOutputMessage;
    }

    /**
     * Process the adverse Events.
     * @param evaluateAEsInputMessage
     * @return
     * @throws CaaersFault
     */
	public EvaluateAEsOutputMessage processAdverseEvents(EvaluateAEsInputMessage evaluateAEsInputMessage) throws CaaersFault {
		if ( evaluateAEsInputMessage == null ) {
			throw Helper.createCaaersFault(DEF_ERR_MSG, "WS_SAE_007",
					messageSource.getMessage("WS_SAE_007", new String[]{},  "", Locale.getDefault())
					);
		}
		
		gov.nih.nci.cabig.caaers.integration.schema.saerules.Study study = evaluateAEsInputMessage.getStudy();
		
		if ( study == null ) {
			throw Helper.createCaaersFault(DEF_ERR_MSG, "WS_GEN_001",
					messageSource.getMessage("WS_GEN_001", new String[]{},  "", Locale.getDefault())
					);
		}
		
		if ( evaluateAEsInputMessage.getAdverseEvents() == null || evaluateAEsInputMessage.getAdverseEvents().getAdverseEvent() == null || evaluateAEsInputMessage.getAdverseEvents().getAdverseEvent().size() == 0 ) {
			throw Helper.createCaaersFault(DEF_ERR_MSG, "WS_SAE_006",
					messageSource.getMessage("WS_SAE_006", new String[]{},  "", Locale.getDefault())
					);
		}
		
		StudyParticipantAssignment spa = new StudyParticipantAssignment();
		StudySite studySite = new StudySite();
		Organization siteOrg = new LocalOrganization();
		siteOrg.setNciInstituteCode(study.getParticipantSiteIdentifier());		
		studySite.setOrganization(siteOrg);
		spa.setStudySite(studySite);
		
		return processAdverseEvents(evaluateAEsInputMessage.getStudy().getStudyIdentifier(), evaluateAEsInputMessage.getAdverseEvents(),spa,evaluateAEsInputMessage.getStudy().getTreatmentAssignmentCode());
	}

	public EvaluateAEsOutputMessage processAdverseEvents(String studyId, AdverseEvents adverseEvents,
			StudyParticipantAssignment assignment, String tacCode) throws CaaersFault {

		// Construct from the input Message.

		List<AdverseEvent> aes = new ArrayList<AdverseEvent>();
		Map<AdverseEvent, AdverseEventResult> mapAE2DTO = new HashMap<AdverseEvent, AdverseEventResult>();
		
		Study study = null;
		TreatmentAssignment tas = null;

		try {
			
			// Fetch the study from Database
			study = fetchStudy(studyId);
			
			AeTerminology terminology = study.getAeTerminology();
		
			for (AdverseEventType adverseEventDto : adverseEvents.getAdverseEvent()) {
				AdverseEvent ae = new AdverseEvent();
				converter.convertAdverseEventDtoToAdverseEventDomain(adverseEventDto, ae,
						terminology, null, null);
				aes.add(ae);
				
				AdverseEventResult result = new AdverseEventResult();
				result.setAdverseEvent(adverseEventDto);
				result.setRequiresReporting(false);
				
				mapAE2DTO.put(ae, result);
			}

			if (tacCode != null) {
				tas = resolveTreatmentAssignment(tacCode, study);
			}

		} catch (CaaersSystemException e) {
			throw Helper.createCaaersFault(DEF_ERR_MSG, e.getErrorCode(), e.getMessage());
		}

		// Populate AdverseEventReporting Period
		AdverseEventReportingPeriod period = new AdverseEventReportingPeriod();
		
		period.setAdverseEvents(aes);
		period.setAssignment(assignment);
		period.setTreatmentAssignment(tas);
		
		if (assignment.getStudySite() != null) {
			assignment.getStudySite().setStudy(study);
		}
		/**
		 * Fill the reporting period into ae's.
		 */
		for (AdverseEvent ae : aes) {
			ae.setReportingPeriod(period);
		}

        // Create the response Object.
        EvaluateAEsOutputMessage response = (EvaluateAEsOutputMessage)createResponseObject(RequestType.Evaluate);

		return (EvaluateAEsOutputMessage)fireSAERules(period, study, mapAE2DTO, RequestType.Evaluate, response);
	}

	/**
	 * Retrieve TreatmentAssignment from the Study for the
	 * TreatmentAssignmentCode
	 * 
	 * @param tacCode
	 *            - String representing TreatmentAssignmentCode
	 * @param study
	 *            - instance of Study
	 * @return instance of TreatmentAssignment
	 */
	private TreatmentAssignment resolveTreatmentAssignment(String tacCode, Study study) {
		TreatmentAssignment ta = null;
		try {
			ta = treatmentAssignmentDao.getAssignmentsByStudyIdExactMatch(tacCode, study.getId());
		} catch (Exception e) {
			throw new CaaersSystemException("WS_SAE_002", messageSource.getMessage("WS_SAE_002",
					new String[] { String.valueOf(study.getId()) }, "", Locale.getDefault()));
		}
		
		return ta;
	}

    /**
     * Fire the SAE on the Reporting Period Adverse Events.
     * @param reportingPeriod
     * @param study
     * @param mapAE2DTO
     * @return
     * @throws CaaersFault
     */

	private AEsOutputMessage fireSAERules(AdverseEventReportingPeriod reportingPeriod, Study study,
			Map<AdverseEvent, AdverseEventResult> mapAE2DTO, RequestType requestType,AEsOutputMessage response)  throws CaaersFault {
		try {
			EvaluatedAdverseEventResults results = new EvaluatedAdverseEventResults();
			response.setEvaluatedAdverseEventResults(results);
			List<AdverseEventResult> aeResultList = results.getAdverseEventResult();

			//populate the output list with the AdverseEventResult objects created for each AdverseEventType
			aeResultList.addAll(mapAE2DTO.values());
            if ( requestType.equals(RequestType.SaveEvaluate)) {
                ((SaveAndEvaluateAEsOutputMessage)response).setLinkToReport(constructLinkToReport(study.getId(),reportingPeriod.getParticipant().getId(), reportingPeriod.getId()));
            }
			
			EvaluationResultDTO dto = evaluationService.evaluateSAERules(reportingPeriod, false);
			
			List<AdverseEvent> neae = reportingPeriod.getNonExpeditedAdverseEvents();
			List<Integer> oldAe = new ArrayList<Integer>();
			List<ExpeditedAdverseEventReport> aeReportList = reportingPeriod.getAeReports();
			if(aeReportList != null && !aeReportList.isEmpty() && neae != null) {
				ExpeditedAdverseEventReport aeReport = aeReportList.get(aeReportList.size()-1);
				Iterator<AdverseEvent> aeIterator = neae.iterator();
		    	while(aeIterator.hasNext()){
		    		AdverseEvent ae = aeIterator.next();
		    		if(aeReport.doesAnotherAeWithSameTermExist(ae) != null){
		    			// remove the AE from evaluation input if the AE is already part of the report and is not modified according to the signature
		    			//CAAERS-7042 ; report the event regardless of modification.
		    			if(ae.getAddedToReportAtLeastOnce() != null && ae.getAddedToReportAtLeastOnce() && !ae.isModified()){
		    				oldAe.add(ae.getId());
		    			}
		    		}
		    	}
			}

            findRecommendedActions(dto, reportingPeriod, response, oldAe);
            populateActionTextAndDueDate(response);
            // create/update/delete AE recommended reports
            manageAdverseEventRecommendedReports(requestType, dto);

            //retrieve all the SAEs identified by rules engine.
            Set<AdverseEvent> seriousAdverseEvents = dto.getAllSeriousAdverseEvents();
           
            //has at least one SAE ? - mark hasSAE flag in the response
            if(requestType.equals(RequestType.SaveEvaluate)) {
            	  //CAAERS-6316 hasSAE is used to suggest that a recommended action is present
            	 if(!((SaveAndEvaluateAEsOutputMessage)response).getRecommendedActions().isEmpty()){
            		 ((SaveAndEvaluateAEsOutputMessage)response).setHasSAE(true);
                 }
              //  ((SaveAndEvaluateAEsOutputMessage)response).setHasSAE(seriousAdverseEvents.size() > 0);
            }

            //Mark the Requires reporting flag on AE
            for(AdverseEvent ae : seriousAdverseEvents) {

                // find DTO object corresponding to Adverse Event.
                AdverseEventResult aeDTO = null ;
                if ( requestType.equals(RequestType.SaveEvaluate)) {
                    aeDTO = findAdverseEvent(ae,mapAE2DTO);
                }   else {
                    aeDTO = mapAE2DTO.get(ae);
                }
                if(aeDTO != null) {
                    aeDTO.setRequiresReporting(true);
                }
            }


		} catch (Exception e) {
			logger.error(" Exception Occured when processing rules" + e.toString());
			throw Helper.createCaaersFault(DEF_ERR_MSG, "WS_SAE_001",
					messageSource.getMessage("WS_SAE_001", new String[]{},  "", Locale.getDefault())
					);			
		}

		return response;
	}
	
	private void populateActionTextAndDueDate(AEsOutputMessage response){
		if(response instanceof SaveAndEvaluateAEsOutputMessage) {
			  Date now = nowFactory.getNow();
	          for(RecommendedActions recActions: ((SaveAndEvaluateAEsOutputMessage) response).getRecommendedActions()){
	          	//recActions.setDueDate(recActions.getDueDate());
	          	recActions.setActionText(recActions.getAction().substring(0, 1).toUpperCase() + recActions.getAction().
	          			substring(1, recActions.getAction().length()).toLowerCase() + " the " + recActions.getReport());
	          	ReportDefinition reportDefinition = reportDefinitionDao.getByName(recActions.getReport());
	          	Date baseDate = reportDefinition.getBaseDate();
	          	Date dueDate = reportDefinition.getExpectedDueDate(baseDate == null ? now : baseDate);
	          	recActions.setDueDate(DateUtils.getDateWithTimeZone(dueDate).toString());
	          }
		}
	}

	private void  refreshReportIndexMap(Map<Integer, ExpeditedAdverseEventReport> aeReportIndexMap) {
        Integer ZERO = new Integer(0);
        aeReportIndexMap.put(ZERO, null);
    }

    /**
     * Find the Actions to be recommended to the client
     * @param evaluationResult
     * @param reportingPeriod
     * @param response
     */
    private void findRecommendedActions(EvaluationResultDTO evaluationResult, AdverseEventReportingPeriod reportingPeriod, AEsOutputMessage response, List<Integer> oldRows) {

        List<RecommendedActions> recommendedActions = new ArrayList<RecommendedActions>();

        ((SaveAndEvaluateAEsOutputMessage)response).setRecommendedActions(recommendedActions);

        Map<Integer, ExpeditedAdverseEventReport> aeReportIndexMap =  reportingPeriod.populateAeReportIndexMap();

        refreshReportIndexMap(aeReportIndexMap);

        Map<Integer, List<ReportTableRow>> recommendedReportTableMap = new LinkedHashMap<Integer, List<ReportTableRow>>();

        Map<Integer, List<ReportTableRow>> applicableReportTableMap = new LinkedHashMap<Integer, List<ReportTableRow>>();

        recommendedActionService.generateRecommendedReportTable(evaluationResult, aeReportIndexMap, recommendedReportTableMap);

        ApplicableReportDefinitionsDTO applicableReportDefinitions = evaluationService.applicableReportDefinitions(reportingPeriod.getStudy(), reportingPeriod.getAssignment());

        recommendedActionService.refreshApplicableReportTable(evaluationResult, aeReportIndexMap, applicableReportTableMap, applicableReportDefinitions);

        // This data structure is used for handling Create/Edit Scneario.
        List<ReportTableRow> ignoredRows = getListOfIgnoredRows(recommendedReportTableMap);

        for ( Integer aeReportId : recommendedReportTableMap.keySet()){

        	if(!oldRows.contains(aeReportId)) {
	            List<ReportTableRow> applicableRows = applicableReportTableMap.get(aeReportId);
	
	            if ( applicableRows != null) {
	                findMatchingRecommendations(applicableRows, recommendedReportTableMap.get(aeReportId), recommendedActions, ignoredRows) ;
	            }
        	}
        }
    }

    /**
     * Find the corresponding applicable report table value of the recommended table value.
     * @param applicableRows
     * @param reportName
     * @return
     */

    private ReportTableRow findApplicableRow(List<ReportTableRow> applicableRows,String reportName) {

            for ( ReportTableRow row: applicableRows) {
                if ( row.getReportDefinition().getName().equals(reportName) ) {
                    return row;
                }
            }

        return null;
    }

    /**
     * Maintain the list of rows with preSelectedFlag is true.
     * @param applicableRows
     * @return
     */
    private List<ReportTableRow> findPreSelectedRows(List<ReportTableRow> applicableRows) {
        List<ReportTableRow> preSelectedRows = new ArrayList<ReportTableRow>();
        for ( ReportTableRow row: applicableRows) {
            if ( row.isPreSelected()) {
              preSelectedRows.add(row);
            }
        }

        return preSelectedRows;
    }

    /**
     * Return true ifAny of the report of the Group is checked.
     * @param row
     * @param preselectedRows
     * @return
     */

    private boolean isAnyInGroupChecked(ReportTableRow row,List<ReportTableRow> preselectedRows) {
        boolean isfound = false;
        for (ReportTableRow preSelectedRow: preselectedRows) {
            if ( preSelectedRow.getReportDefinition() != null && row.getReportDefinition() != null &&
                    preSelectedRow.getReportDefinition().getGroup() != null && row.getReportDefinition().getGroup() != null &&
                        preSelectedRow.getReportDefinition().getGroup().getName().equals(row.getReportDefinition().getGroup().getName())) {
                isfound = true;
                break;
            }
        }

        return isfound;

    }


    /**
     * Convert the Report Table Row to Action object for webservice to send the output.
     * @param row
     * @param preselectedRows
     * @return
     */
    private   RecommendedActions returnActionFromRow(ReportTableRow row, List<ReportTableRow> preselectedRows) {
        RecommendedActions action = new RecommendedActions();

        action.setReport(row.getReportDefinition().getName());
        if ( row.isPreSelected()) {  // If the row is pre-selected.

            action.setAction(row.getAction().getDisplayName());
            action.setDue(row.getDue());
            action.setStatus(row.getStatus());

        } else {
            if ( isAnyInGroupChecked(row, preselectedRows)) {     // If the any one of the Report in the group is selected.

                action.setAction(row.getGrpAction().getDisplayName());
                action.setDue(row.getGrpDue());
                action.setStatus(row.getGrpStatus());

            } else  { // Other Actions.

                action.setAction(row.getOtherAction().getDisplayName());
                action.setDue(row.getOtherDue());
                action.setStatus(row.getOtherStatus());
            }

        }

        return action;
    }

    /**
     * Return the matched is true only when the Action is Edit.
     * @param recommendedReportTableMap
     * @param ignoredRow
     * @return
     */
    private boolean findMatchingRecommendedRow(Map<Integer, List<ReportTableRow>> recommendedReportTableMap, ReportTableRow ignoredRow) {
        boolean isFound = false;

        for ( Integer aeReportId :recommendedReportTableMap.keySet() ) {
            if ( aeReportId == 0 )  continue;

            for (  ReportTableRow row : recommendedReportTableMap.get(aeReportId)) {
                if (  (recommendedReportTableMap.size() == 2) && row.getAction().getDisplayName().equals("Edit")) { // Condition will Evaluate 
                    isFound = true;
                    break;
                }
            }

            if ( isFound)
                break;

        }

        return isFound;
    }

    /**
     * This is to handle the special case of Create-Edit Scenario.
     *
     */
    private List<ReportTableRow> getListOfIgnoredRows(Map<Integer, List<ReportTableRow>> recommendedReportTableMap) {
        List<ReportTableRow> ignoredRows = new ArrayList<ReportTableRow>();
        Integer Zero = new Integer(0);
        List<ReportTableRow> rows = recommendedReportTableMap.get(Zero);

        if ( rows == null )
            return ignoredRows;

        for ( ReportTableRow row : rows ) {
            boolean isFound = findMatchingRecommendedRow(recommendedReportTableMap, row);
            if ( isFound ) ignoredRows.add(row);
        }


        return ignoredRows;

    }

    private boolean isMatchedIgnoredRow(List<ReportTableRow> ignoredRows,ReportTableRow recommRow) {
        boolean isFound = false;

        for ( ReportTableRow row: ignoredRows ) {
            if ( row.getAction().equals(recommRow.getAction()) && row.getReportDefinition().getName().equals(recommRow.getReportDefinition().getName())) {
                isFound = true;
                break;
            }
        }

        return isFound;
    }

    /**
     * Return the Recommendations required report group. ( AMend/WithDraw/Create/Edit).
     *
     * @param applicableRows
     * @param recommRows
     * @param recommendedActions
     */

    private void findMatchingRecommendations(List<ReportTableRow> applicableRows, List<ReportTableRow> recommRows, List<RecommendedActions> recommendedActions,  List<ReportTableRow> ignoredRows)  {
        // Find the report group of the pre-selected row.
        List<ReportTableRow> preselectedRows = findPreSelectedRows(applicableRows);

        for (ReportTableRow recommRow : recommRows) {
            ReportTableRow applicableRow = findApplicableRow(applicableRows, recommRow.getReportDefinition().getName());

            if ( applicableRow == null || isMatchedIgnoredRow(ignoredRows, recommRow)) continue;

            RecommendedActions action = returnActionFromRow(applicableRow, preselectedRows);
            recommendedActions.add(action);


            boolean withdrawOrAmend = StringUtils.equals(action.getAction(), WITHDRAW.getDisplayName()) ||    StringUtils.equals(action.getAction(), AMEND.getDisplayName()) ;

            if (withdrawOrAmend) {
                for (ReportTableRow preselectedRow: preselectedRows ) {
                    if ( preselectedRow != null ) {

                            // Update the Group Due.
                            action.setDue(applicableRow.getGrpDue());

                            // find if the report already exists.
                            RecommendedActions preSelectedAction = null;
                            for (RecommendedActions actionIter: recommendedActions) {

                                if ( actionIter.getReport().equals(preselectedRow.getReportDefinition().getName()) && actionIter.getAction().equals("Create") ) {
                                    preSelectedAction = actionIter;
                                    break;
                                }
                            }
                            if ( preSelectedAction == null){      // If the Create Action is not occured before, Create one manually.
                                preSelectedAction = new RecommendedActions();
                                preSelectedAction.setAction(ReportDefinitionWrapper.ActionType.CREATE.toString()); // Make it Create.
                                preSelectedAction.setStatus("Not Started");
                                preSelectedAction.setReport(preselectedRow.getReportDefinition().getName());
                                preSelectedAction.setDue(preselectedRow.getDue());

                                recommendedActions.add(preSelectedAction);
                            }   else { // If it is already occured, Update the due time.

                                ReportTableRow createAction = findApplicableRow(applicableRows, preSelectedAction.getReport()) ;
                                preSelectedAction.setDue(createAction.getDue());
                            }

                  }

               }
            }
        }

    }


    public AdverseEventResult findAdverseEvent(AdverseEvent thatAe,Map<AdverseEvent, AdverseEventResult> mapAE2DTO){
        for(AdverseEvent thisAe : mapAE2DTO.keySet()){
            //are Ids matching ?
            if(thatAe.getExternalId() != null && thisAe.getExternalId() != null && StringUtils.equals(thisAe.getExternalId(), thatAe.getExternalId()) ) return mapAE2DTO.get(thisAe);

            //Compare Grade
            if(thatAe.getGrade() != null && thisAe.getGrade() != null && !thisAe.getGrade().equals(thatAe.getGrade()) ) continue;

            //are dates matching ?
            if(DateUtils.compareDate(thisAe.getStartDate(), thatAe.getStartDate()) != 0)  continue;
            if(DateUtils.compareDate(thisAe.getEndDate(), thatAe.getEndDate()) != 0)  continue;
            if(thisAe.getEventApproximateTime() != null && thatAe.getEventApproximateTime()!= null && !(thisAe.getEventApproximateTime().equals(thatAe.getEventApproximateTime())))  continue;

            //is the term matching ?
            if(thisAe.getAdverseEventCtcTerm() != null){
                //ctc terminology
                AdverseEventCtcTerm thisCtcTerm = thisAe.getAdverseEventCtcTerm();
                AdverseEventCtcTerm thatCtcTerm = thatAe.getAdverseEventCtcTerm();
                if ( (thisCtcTerm == null && thatCtcTerm != null) || (thatCtcTerm == null && thisCtcTerm != null) ) continue;
                if( (thisCtcTerm != null && thatCtcTerm != null )&& thisCtcTerm.getTerm().getId() != thatCtcTerm.getTerm().getId()) continue;
                if(!StringUtils.equals(thisAe.getOtherSpecify(), thatAe.getOtherSpecify())) continue;

                LowLevelTerm thisLLT = thisAe.getLowLevelTerm();
                LowLevelTerm thatLLT = thatAe.getLowLevelTerm();
                if((thisLLT == null && thatLLT != null ) || (thatLLT == null && thisLLT != null)) continue;
                if((thisLLT != null && thatLLT != null ) && thisLLT.getId() != thatLLT.getId()) continue;

            } else {
                //MedDRA terminology
                AdverseEventMeddraLowLevelTerm thisMedDRATerm = thisAe.getAdverseEventMeddraLowLevelTerm();
                AdverseEventMeddraLowLevelTerm thatMedDRATerm = thatAe.getAdverseEventMeddraLowLevelTerm();
                if((thisMedDRATerm == null && thatMedDRATerm != null) && (thatMedDRATerm == null && thisMedDRATerm != null)) continue;
                if((thisMedDRATerm != null && thatMedDRATerm != null) && thisMedDRATerm.getLowLevelTerm().getId() != thatMedDRATerm.getLowLevelTerm().getId()) continue;
            }
            //found a match
            return mapAE2DTO.get(thisAe);
        }
        return null;
    }

    private String constructLinkToReport(int studyId, int participantId, int rpId) {
        String hostname = messageSource.getMessage("rules.hostname",
                new String[] {}, "", Locale.getDefault());
        if ( hostname == "" || hostname == null || hostname.contains("<hostname>")) {
            try {
                hostname = java.net.InetAddress.getLocalHost().getHostName();
            }catch(UnknownHostException ue) {
                hostname = "localhost";
            }
        }

        String port = messageSource.getMessage("rules.port",
                new String[] {}, "", Locale.getDefault());
        if ( port == "" || port == null ) {
            port = "8443";
        }

        String linkToReport = "https://" + hostname + ":" + port + "/caaers/pages/ae/captureRoutine?study=" + studyId + "&participant="+ participantId
                + "&adverseEventReportingPeriod="+ rpId +"&_page=0&_target2=2&displayReportingPeriod=true&addReportingPeriodBinder=true";
        return linkToReport;
    }

    private   AEsOutputMessage createResponseObject(RequestType requestType) {
        AEsOutputMessage response = null;
        if ( requestType.equals(RequestType.Evaluate)) {
            response = new EvaluateAEsOutputMessage();
        }   else {
            response = new SaveAndEvaluateAEsOutputMessage();
        }
        return response;
    }

	public AdverseEventConverter getConverter() {
		return converter;
	}

	public void setConverter(AdverseEventConverter converter) {
		this.converter = converter;
	}

	private Study fetchStudy(String identifier) {
		if (StringUtils.isEmpty(identifier)) {
			throw new CaaersSystemException("WS_SAE_004", messageSource.getMessage("WS_SAE_004", new String[] {}, "",
					Locale.getDefault()));
		}
		Study study = null;
		try {
			Identifier si = new Identifier();
			si.setValue(identifier);
			study = studyDao.getByIdentifier(si);
		} catch (Exception e) {
			throw new CaaersSystemException("WS_GEN_001", messageSource.getMessage("WS_GEN_001", new String[] {}, "",
					Locale.getDefault()));
		}
		if (study == null) {
			throw new CaaersSystemException("WS_SAE_005", messageSource.getMessage("WS_SAE_005",
					new String[] { identifier }, "", Locale.getDefault()));
		}
		return study;
	}
	
	private void manageAdverseEventRecommendedReports(RequestType requestType, EvaluationResultDTO dto ){
		 Map<AdverseEvent,List<ReportDefinition>> adverseEventReportDefinitionMap = dto.getAdverseEventRecommendedReportsMap();
		 for (Map.Entry<AdverseEvent, List<ReportDefinition>> entry : adverseEventReportDefinitionMap.entrySet()) {
					AdverseEvent ae = entry.getKey();
					List<ReportDefinition> rds = entry.getValue();
					// Find out if the AE is serious
					if (rds != null && rds.size() > 0) {
						// update existing AE recommendation report or create new one 
						Iterator<ReportDefinition> reportDefinitionIterator = rds.iterator();
						while(reportDefinitionIterator.hasNext()){
							ReportDefinition reportDefinition = reportDefinitionIterator.next();
							AdverseEventRecommendedReport aeRecomReport;
							List<AdverseEventRecommendedReport> dbAeRecomReports = adverseEventRecommendedReportDao.
									searchAdverseEventRecommendedReportsByAdverseEvent(ae);
							if (dbAeRecomReports != null && !dbAeRecomReports.isEmpty()) {
								// AE recommendation report already exists
								aeRecomReport = dbAeRecomReports.get(0);
								if (!reportDefinition.getOrganization().equals(aeRecomReport.getReportDefinition().getOrganization())
										|| !reportDefinition.getGroup().equals(aeRecomReport.getReportDefinition().getGroup())) {
									// CAAERS-6961: Only if there is a change in the recommended Report Org or Group set the AE
									// reported flag to false, otherwise the AE is already considered added to the report.
									aeRecomReport.setAeReported(false);
								}
							} else {
								// create AE recommendation report
								aeRecomReport = new AdverseEventRecommendedReport();
								aeRecomReport.setAdverseEvent(ae);
								aeRecomReport.setAeReported(false);
							}
							
							aeRecomReport.setReportDefinition(reportDefinition);
							aeRecomReport.setDueDate(reportDefinition.getExpectedDueDate(ae.getGradedDate()));
							adverseEventRecommendedReportDao.save(aeRecomReport);
						}
					} else {
						// delete old serious AE recommendation reports that are no longer serious in current evaluation
						List<AdverseEventRecommendedReport> dbAeRecomReports = adverseEventRecommendedReportDao.
								searchAdverseEventRecommendedReportsByAdverseEvent(ae);
						if(dbAeRecomReports != null && !dbAeRecomReports.isEmpty()){
							AdverseEventRecommendedReport aeRecomReport = dbAeRecomReports.get(0);
							adverseEventRecommendedReportDao.delete(aeRecomReport);
						}
					}
				}
	}


    public ParticipantDao getParticipantDao() {
        return participantDao;
    }

    public void setParticipantDao(ParticipantDao participantDao) {
        this.participantDao = participantDao;
    }

    public AdverseEventManagementServiceImpl getAdverseEventManagementService() {
        return adverseEventManagementService;
    }

    public void setAdverseEventManagementService(AdverseEventManagementServiceImpl adverseEventManagementService) {
        this.adverseEventManagementService = adverseEventManagementService;
    }

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public StudyDao getStudyDao() {
		return studyDao;
	}

	public void setStudyDao(StudyDao studyDao) {
		this.studyDao = studyDao;
	}

	public TreatmentAssignmentDao getTreatmentAssignmentDao() {
		return treatmentAssignmentDao;
	}

	public void setTreatmentAssignmentDao(TreatmentAssignmentDao treatmentAssignmentDao) {
		this.treatmentAssignmentDao = treatmentAssignmentDao;
	}

	public EvaluationService getAdverseEventEvaluationService() {
		return evaluationService;
	}

	public void setAdverseEventEvaluationService(EvaluationService evaluationService) {
		this.evaluationService = evaluationService;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

    public StudyParticipantAssignmentDao getStudyParticipantAssignmentDao() {
        return studyParticipantAssignmentDao;
    }

    public void setStudyParticipantAssignmentDao(StudyParticipantAssignmentDao studyParticipantAssignmentDao) {
        this.studyParticipantAssignmentDao = studyParticipantAssignmentDao;
    }

    public SAEAdverseEventReportingPeriodConverter getReportingPeriodConverter() {
        return reportingPeriodConverter;
    }

    public void setReportingPeriodConverter(SAEAdverseEventReportingPeriodConverter reportingPeriodConverter) {
        this.reportingPeriodConverter = reportingPeriodConverter;
    }

    public RecommendedActionService getRecommendedActionService() {
        return recommendedActionService;
    }

    public void setRecommendedActionService(RecommendedActionService recommendedActionService) {
        this.recommendedActionService = recommendedActionService;
    }
    
    public void setAdverseEventRecommendedReportDao(
			AdverseEventRecommendedReportDao adverseEventRecommendedReportDao) {
		this.adverseEventRecommendedReportDao = adverseEventRecommendedReportDao;
	}
    
    public void setNowFactory(NowFactory nowFactory) {
		this.nowFactory = nowFactory;
	}

	public void setReportDefinitionDao(ReportDefinitionDao reportDefinitionDao) {
		this.reportDefinitionDao = reportDefinitionDao;
	}
}
