/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.repository;

import gov.nih.nci.cabig.caaers.dao.AdverseEventRecommendedReportDao;
import gov.nih.nci.cabig.caaers.dao.query.ReportDefinitionQuery;
import gov.nih.nci.cabig.caaers.dao.report.ReportDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventRecommendedReport;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.domain.factory.ReportFactory;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.domain.report.ReportDelivery;
import gov.nih.nci.cabig.caaers.domain.report.ReportDeliveryDefinition;
import gov.nih.nci.cabig.caaers.domain.report.ReportType;
import gov.nih.nci.cabig.caaers.service.ReportWithdrawalService;
import gov.nih.nci.cabig.caaers.service.SchedulerService;
import gov.nih.nci.cabig.caaers.utils.RoleUtils;
import gov.nih.nci.cabig.ctms.lang.NowFactory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

 
/**
 * The Class ReportRepositoryImpl.
 *
 * @author Biju Joseph
 */
@Transactional(readOnly = false)
public class ReportRepositoryImpl implements ReportRepository {

    /** The report dao. */
    private ReportDao reportDao;
    
    /** The report definition dao. */
    private ReportDefinitionDao reportDefinitionDao;
    
    /** The scheduler service. */
    private SchedulerService schedulerService;
    
    /** The report withdrawal service. */
    private ReportWithdrawalService reportWithdrawalService;

    /** The report factory. */
    private ReportFactory reportFactory;
    
    /** The now factory. */
    private NowFactory nowFactory;
    
    private AdverseEventRecommendedReportDao adverseEventRecommendedReportDao;

    public void setAdverseEventRecommendedReportDao(
			AdverseEventRecommendedReportDao adverseEventRecommendedReportDao) {
		this.adverseEventRecommendedReportDao = adverseEventRecommendedReportDao;
	}

	/** The adverse event routing and review repository. */
    private AdverseEventRoutingAndReviewRepository adverseEventRoutingAndReviewRepository;

    @Transactional(readOnly = false)
    public Report save(Report report) {
        reportDao.save(report);
        return report;
    }

    /**
	 * This method will amend/unamend/withdraw/create the reports.
	 *
	 * @param aeReport the ae report
	 * @param toAmendList - The list of reports to amend
	 * @param toUnAmendList - The list of reports to unamend
	 * @param toWithdrawList - The list of reports to withdraw
	 * @param toCreateList - The list of reports to create
	 */
    @Transactional(readOnly = false)
    public void processReports(ExpeditedAdverseEventReport aeReport,List<Report> toAmendList,List<Report> toUnAmendList,
    		List<Report> toWithdrawList, List<ReportDefinition> toCreateList) {
    	
    	//amend report to amend
    	if(CollectionUtils.isNotEmpty(toAmendList)){
    		for(Report report : toAmendList){
    			Report reportToAmend = aeReport.findReportById(report.getId()); 
    			amendReport(reportToAmend);
    		}
    	}
    	
    	//un amend the reports
    	if(CollectionUtils.isNotEmpty(toUnAmendList)){
    		for(Report report : toUnAmendList){
    			Report reportToUnAmend = aeReport.findReportById(report.getId());
    			unAmendReport(reportToUnAmend);
    		}
    	}
    	
    	//figure out the reports that are getting only withdrawn 
    	List<Report> beingWithdrawnList = new ArrayList<Report>();
    	if(CollectionUtils.isNotEmpty(toWithdrawList)){
    		for(Report report : toWithdrawList){
        		Report reportToWithdraw = aeReport.findReportById(report.getId());
        		if(!isGettingReplaced(reportToWithdraw, toCreateList)){
        			beingWithdrawnList.add(reportToWithdraw);
        		}
        		withdrawReport(reportToWithdraw);
        	}
    	}
    	
    	//create new reports
    	if(CollectionUtils.isNotEmpty(toCreateList)){
    		for(ReportDefinition reportDefinition : toCreateList){
    			createReport(reportDefinition, aeReport);
    		}
    	}
    	

    	//withdraw reports from external agency if needed. 
    	if(CollectionUtils.isNotEmpty(beingWithdrawnList)){
    		for(Report report : beingWithdrawnList){
    			withdrawExternalReport(aeReport, report);
    		}
    	}
    	
    }
    
    /**
     * This method will return true, if same category (group-organization) report definition is
     * available in the report definitions list.
     *
     * @param report the report
     * @param reportDefinitionList the report definition list
     * @return true, if is getting replaced
     */
    protected boolean isGettingReplaced(Report report, List<ReportDefinition> reportDefinitionList){
    	if(CollectionUtils.isEmpty(reportDefinitionList)) return false;
    	for(ReportDefinition reportDefinition : reportDefinitionList){
    		if(reportDefinition.isOfSameReportTypeAndOrganization(report.getReportDefinition())) return true;
    	}
    	
    	return false;
    }
    
    /**
     * Will find the external report to be withdrawn, and will withdraw that report from the system.
     *
     * @param aeReport the ae report
     * @param report the report
     */
    @Transactional(readOnly = false)
    public void withdrawExternalReport(ExpeditedAdverseEventReport aeReport, Report report) {
    	Report reportToWithdraw = aeReport.findLastSubmittedReport(report.getReportDefinition());
		if(reportToWithdraw != null && reportToWithdraw.getReportDefinition().getReportType().equals(ReportType.NOTIFICATION)){
			reportWithdrawalService.withdrawExternalReport(aeReport,reportToWithdraw);
		} else {
            //Internal report, so notify people.
            reportWithdrawalService.sendWithdrawEmail(report);
        }
    }
    
    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.repository.ReportRepository#withdrawReport(gov.nih.nci.cabig.caaers.domain.report.Report)
     */
    @Transactional(readOnly = false)
    public void withdrawReport(Report report) {
        assert !report.getStatus().equals(ReportStatus.WITHDRAWN) : "Cannot withdraw a report that is already withdrawn";
        report.setStatus(ReportStatus.WITHDRAWN);
        report.setWithdrawnOn(nowFactory.getNow());
        report.setDueOn(null);
        schedulerService.unScheduleNotification(report);
        save(report);
        reportWithdrawalService.sendWithdrawEmail(report);
    }
    

    /**
     * Creates the report.
     *
     * @param reportDefinition the report definition
     * @param aeReport the ae report
     * @return the report
     * {@inheritDoc}
     */

    @Transactional(readOnly = false)
    public Report createReport(ReportDefinition reportDefinition, ExpeditedAdverseEventReport aeReport) {
        return createReport(reportDefinition, aeReport, null);
    }
    private Report createReport(ReportDefinition reportDefinition, ExpeditedAdverseEventReport aeReport, Report parentReport) {
    	
    	//reassociate all the study orgs
//    	studyDao.reassociateStudyOrganizations(aeReport.getStudy().getStudyOrganizations());
    	
//    	reportDefinitionDao.lock(reportDefinition);
    	
        Report report = reportFactory.createReport(reportDefinition, aeReport, reportDefinition.getBaseDate());
        if(parentReport != null ) {
            report.setCaseNumber(parentReport.getCaseNumber());
        }

        //update report version, based on latest amendment. 
        Report lastSubmittedReport = aeReport.findLastSubmittedReport(reportDefinition);
        if(lastSubmittedReport != null){
        	report.getLastVersion().copySubmissionDetails(lastSubmittedReport.getLastVersion());
        	
        	String strLastVersionNumber = lastSubmittedReport.getLastVersion().getReportVersionId();
        	report.getLastVersion().setAmendmentNumber(Integer.parseInt(strLastVersionNumber));
        	report.getLastVersion().setReportVersionId(strLastVersionNumber);
        	
            if(lastSubmittedReport.getReportDefinition().getReportType().equals(ReportType.REPORT) ){
            	//increase the amendment number.
            	report.getLastVersion().incrementAmendmentNumber();
            	report.getLastVersion().incrementReportVersion();
            }
           
        }
        
        //set the manually selected flag.
        report.setManuallySelected(reportDefinition.isManuallySelected());
        
        // update AE added to report at least once flag
        
        for(AdverseEvent reportedAe : aeReport.getAdverseEvents()){
        	reportedAe.setAddedToReportAtLeastOnce(true);
        }
       
        //save the report
        save(report);
        
        // update AE recommended reports flag to reported
        List<AdverseEventRecommendedReport> aeRecomReports = adverseEventRecommendedReportDao.getAllAdverseEventsGivenReportDefinition(reportDefinition);
        
        for(AdverseEvent aeInReport : aeReport.getActiveAdverseEvents()){
        	for(AdverseEventRecommendedReport aeRecomReport : aeRecomReports){
        		if(aeRecomReport.getAdverseEvent().getId().equals(aeInReport.getId())){
        			aeRecomReport.setAeReported(true);
        			adverseEventRecommendedReportDao.save(aeRecomReport);
        			continue;
        		}
        	}
        }

        //schedule the report, if there are scheduled notifications.
        if (report.hasScheduledNotifications()) schedulerService.scheduleNotification(report);
        
        //Check if workflow needs to be instantiated for this report and instantiate one if needed.
        // The system-level configuration for worklow now only controls the course workflow and the report definition level flag for
        // workflowEnabled now controls the workflow for the resptective report.
        if(report.getReportDefinition().getWorkflowEnabled())
        	adverseEventRoutingAndReviewRepository.enactReportWorkflow(report);

        return report;
    }
    
    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.repository.ReportRepository#createChildReports(gov.nih.nci.cabig.caaers.domain.report.Report)
     */
    public List<Report> createChildReports(Report report) {
    	
    	List<Report> instantiatedReports = null;
    	//check if there is children
    	ReportDefinitionQuery query = new ReportDefinitionQuery();
    //	query.filterByParent(report.getReportDefinition().getId());
   // 	query.filterByEnabled();
    	List<ReportDefinition> rdChildren = (List<ReportDefinition>) reportDefinitionDao.search(query);
    	if(CollectionUtils.isNotEmpty(rdChildren)){
    		if(BooleanUtils.isTrue(report.isAmendable())){
    			amendReport(report);
    		}
    		
    		instantiatedReports = new ArrayList<Report>();
    		for(ReportDefinition rdChild : rdChildren){
                rdChild.setManuallySelected(report.isManuallySelected());
    			Report childReport = createReport(rdChild, report.getAeReport(), report);
    			instantiatedReports.add(childReport);
    		}
    	}
    	
    	return instantiatedReports;
    }
    
    public List<ReportDefinition> getChildReports(Report report) {
    	
    	ReportDefinitionQuery query = new ReportDefinitionQuery();
    	query.filterByParent(report.getReportDefinition().getId());
    	List<ReportDefinition> rdChildren = (List<ReportDefinition>) reportDefinitionDao.search(query);
    	
    	return rdChildren;  	
    }


    
    /**
     * Find report deliveries.
     *
     * @param aReport the a report
     * @return the list
     * {@inheritDoc}
     */
    public List<ReportDelivery> findReportDeliveries(Report aReport) {
    	List<ReportDelivery> deliveries = new ArrayList<ReportDelivery>();
    	
    	//reload the report
    	Report report = reportDao.getById(aReport.getId());
    	ReportDefinition reportDefinition = report.getReportDefinition();
    	ExpeditedAdverseEventReport aeReport = report.getAeReport();
    	List<ReportDeliveryDefinition> deliveryDefinitions = reportDefinition.getDeliveryDefinitions();
    	
    	if(deliveryDefinitions != null){

            for (ReportDeliveryDefinition reportDeliveryDefinition : deliveryDefinitions) {
                //fetch the contact mechanism for role based entities.
                if (reportDeliveryDefinition.getEntityType() == ReportDeliveryDefinition.ENTITY_TYPE_ROLE) {
                	String roleName = reportDeliveryDefinition.getEndPoint();
                	List<String> addresses = null;
                	if(ArrayUtils.contains(RoleUtils.reportSpecificRoles, roleName)){
                		addresses = report.findEmailAddressByRole(roleName);
                	}else if(ArrayUtils.contains(RoleUtils.sponsorAndCoordinatingCenterSpecificRoles, roleName)){
                        addresses = aeReport.getStudy().getStudyCoordinatingCenter().findEmailAddressByRole(roleName);
                        addresses.addAll(aeReport.getStudy().getStudyFundingSponsors().get(0).findEmailAddressByRole(roleName)) ;
                	}else if(ArrayUtils.contains(RoleUtils.studySiteSpecificRoles, roleName)){
                		addresses = aeReport.getStudySite().findEmailAddressByRole(roleName);
                	}else{
                		addresses = aeReport.getStudy().findEmailAddressByRole(roleName);
                	}
                    for (String address : addresses) {
                        if (StringUtils.isNotEmpty(address)) {
                            ReportDelivery reportDelivery = reportDeliveryDefinition.createReportDelivery();
                            reportDelivery.setEndPoint(address);
                            deliveries.add(reportDelivery);
                        }
                    }
                } else {
                    if (StringUtils.isNotEmpty(reportDeliveryDefinition.getEndPoint())) {
                        ReportDelivery reportDelivery = reportDeliveryDefinition.createReportDelivery();
                        reportDelivery.setEndPoint(reportDeliveryDefinition.getEndPoint());
                        deliveries.add(reportDelivery);
                    }
                }

            }
        
    	}
    	
    	return deliveries;
    }
    
    /**
     * This method will un-amend, an amended report.
     *
     * @param report the report
     */
    public void unAmendReport(Report report){
    	assert report.getStatus() == ReportStatus.AMENDED;
    	
    	report.setStatus(ReportStatus.COMPLETED);
    	report.setAmendedOn(null);

    	//increment the reportVersionId - CAAERS-3016
    	report.getLastVersion().incrementReportVersion();
    	
    	save(report);
    }
    
    
   /**
    * This method will amend the report, by setting the report status to {@link ReportStatus#AMENDED}.
    *
    * @param report the report
    */
    public void amendReport(Report report){
    	report.setDueOn(null);
    	report.setStatus(ReportStatus.AMENDED);
    	report.setAmendedOn(nowFactory.getNow());
    	
    	save(report);
    }
    
    /**
     * Sets the report dao.
     *
     * @param reportDao the new report dao
     */
    @Required
    public void setReportDao(final ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    /**
     * Sets the scheduler service.
     *
     * @param schedulerService the new scheduler service
     */
    @Required
    public void setSchedulerService(final SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    /**
     * Sets the report factory.
     *
     * @param reportFactory the new report factory
     */
    @Required
    public void setReportFactory(final ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    /**
     * Sets the now factory.
     *
     * @param nowFactory the new now factory
     */
    @Required
    public void setNowFactory(final NowFactory nowFactory) {
        this.nowFactory = nowFactory;
    }
    
    /**
     * Sets the report definition dao.
     *
     * @param reportDefinitionDao the new report definition dao
     */
    @Required
    public void setReportDefinitionDao(ReportDefinitionDao reportDefinitionDao) {
		this.reportDefinitionDao = reportDefinitionDao;
	}
  
    /**
     * Sets the report withdrawal service.
     *
     * @param reportWithdrawalService the new report withdrawal service
     */
    @Required
    public void setReportWithdrawalService(ReportWithdrawalService reportWithdrawalService) {
		this.reportWithdrawalService = reportWithdrawalService;
	}
    

    
    /**
     * Sets the adverse event routing and review repository.
     *
     * @param adverseEventRoutingAndReviewRepository the new adverse event routing and review repository
     */
    @Required
    public void setAdverseEventRoutingAndReviewRepository(AdverseEventRoutingAndReviewRepository adverseEventRoutingAndReviewRepository){
    	this.adverseEventRoutingAndReviewRepository = adverseEventRoutingAndReviewRepository;
    }
}
