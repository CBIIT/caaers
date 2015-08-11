/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.api.impl;


import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.StudyParticipantAssignmentDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.dto.ReportDefinitionWrapper.ActionType;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.domain.repository.ReportRepository;
import gov.nih.nci.cabig.caaers.domain.validation.ExpeditedAdverseEventReportValidator;
import gov.nih.nci.cabig.caaers.event.EventFactory;
import gov.nih.nci.cabig.caaers.integration.schema.aereport.AdverseEventReport;
import gov.nih.nci.cabig.caaers.integration.schema.aereport.BaseAdverseEventReport;
import gov.nih.nci.cabig.caaers.integration.schema.aereport.BaseReportType;
import gov.nih.nci.cabig.caaers.integration.schema.aereport.BaseReports;
import gov.nih.nci.cabig.caaers.integration.schema.aereportid.ReportIdCriteria;
import gov.nih.nci.cabig.caaers.integration.schema.aereportid.SafetyReportIdentifer;
import gov.nih.nci.cabig.caaers.integration.schema.common.CaaersServiceResponse;
import gov.nih.nci.cabig.caaers.integration.schema.common.ResponseDataType;
import gov.nih.nci.cabig.caaers.integration.schema.common.ServiceResponse;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAndInitiateInputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAndInitiateOutputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.RecommendedActions;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.SaveAndEvaluateAEsOutputMessage;
import gov.nih.nci.cabig.caaers.service.AdeersIntegrationFacade;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.ReportSubmissionService;
import gov.nih.nci.cabig.caaers.service.migrator.BaseExpeditedAdverseEventReportConverter;
import gov.nih.nci.cabig.caaers.service.migrator.EvaluateAndInitiateReportConverter;
import gov.nih.nci.cabig.caaers.service.migrator.ExpeditedAdverseEventReportConverter;
import gov.nih.nci.cabig.caaers.service.migrator.report.ExpeditedReportMigrator;
import gov.nih.nci.cabig.caaers.service.synchronizer.report.ExpeditedAdverseEventReportSynchronizer;
import gov.nih.nci.cabig.caaers.utils.DateUtils;
import gov.nih.nci.cabig.caaers.validation.CaaersValidationException;
import gov.nih.nci.cabig.caaers.validation.ValidationError;
import gov.nih.nci.cabig.caaers.validation.ValidationErrors;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

public class SafetyReportServiceImpl {
	private static Log logger = LogFactory.getLog(SafetyReportServiceImpl.class);
	
	/**	Base Expedited Report Converter. **/
	private BaseExpeditedAdverseEventReportConverter baseEaeConverter;
	
	/**	Expedited Report Converter. **/
	private ExpeditedAdverseEventReportConverter eaeConverter;

    private ParticipantServiceImpl participantService;

    private ParticipantDao participantDao;

    private StudyDao studyDao;
    

    private MessageSource messageSource;
    
    private ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao;
    private StudyParticipantAssignmentDao studyParticipantAssignmentDao;
    
    /** Validator Service. **/
	private ExpeditedAdverseEventReportValidator aeReportValidator;
	
	/** Expedited Report Migrator. **/
	private ExpeditedReportMigrator aeReportMigrator;
	private ExpeditedAdverseEventReportSynchronizer aeReportSynchronizer;

    /** The report Repository. */
    private ReportRepository reportRepository;
    private ReportSubmissionService reportSubmissionService;
    
    private IDServiceImpl idServiceImpl;
	
    private AdeersIntegrationFacade adeersIntegrationFacade;

    private EventFactory eventFactory;

	private EvaluateAndInitiateReportConverter evaluateAndInitiateReportConverter;

    public EvaluateAndInitiateReportConverter getEvaluateAndInitiateReportConverter() {
		return evaluateAndInitiateReportConverter;
	}

	public void setEvaluateAndInitiateReportConverter(
			EvaluateAndInitiateReportConverter reportConverter) {
		this.evaluateAndInitiateReportConverter = reportConverter;
	}

	public EventFactory getEventFactory() {
        return eventFactory;
    }

    public void setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    public AdeersIntegrationFacade getAdeersIntegrationFacade() {
        return adeersIntegrationFacade;
    }

    public void setAdeersIntegrationFacade(AdeersIntegrationFacade adeersIntegrationFacade) {
        this.adeersIntegrationFacade = adeersIntegrationFacade;
    }

    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public void setReportSubmissionService(ReportSubmissionService reportSubmissionService) {
        this.reportSubmissionService = reportSubmissionService;
    }
    
    public IDServiceImpl getIdServiceImpl() {
		return idServiceImpl;
	}

	public void setIdServiceImpl(IDServiceImpl idServiceImpl) {
		this.idServiceImpl = idServiceImpl;
	}

    /**
     * Does the validation of the input message
     * @param aeSrcReport
     * @return
     */
    @SuppressWarnings("unused")
	private ValidationErrors validateInput(ExpeditedAdverseEventReport aeSrcReport){

        AdverseEventReportingPeriod rpSrc = aeSrcReport.getReportingPeriod();
        ValidationErrors errors = new ValidationErrors();

        //do I have reporting period ?
        if(rpSrc == null){
            errors.addValidationError( "ER-RP-1", "Missing Reporting period and Adverse event in input message");
            return errors;
        }

        //do I have AEs ?
        if(rpSrc.getAdverseEvents() == null || rpSrc.getAdverseEvents().isEmpty()){
            errors.addValidationError("WS_AEMS_025", "Missing Adverse Events in the input message");
            return errors;
        }

        //do I have study site details ?
        StudySite studySiteSrc = rpSrc.getStudySite();
        if(studySiteSrc == null){
            errors.addValidationError("WS_AEMS_034", "StudySite information is missing in input message");
            return errors;
        }

        if(studySiteSrc.getOrganization() == null || studySiteSrc.getOrganization().getNciInstituteCode() == null){
            errors.addValidationError("ER-STU-3", "Missing Study Site details - Organization NCI code");
            return errors;
        }

        //do I have study details ?
        Study studySrc = rpSrc.getStudy();
        if(studySrc == null || studySrc.getFundingSponsorIdentifierValue() == null){
           logger.error("Missing study identifier");
            errors.addValidationError("WS_AEMS_034",  "Missing Study Identifier" );
            return errors;
        }
        if(studySrc.getFundingSponsorIdentifierValue() == null){
            logger.error("Missing study identifier");
            errors.addValidationError("WS_AEMS_034",  "Missing Study Identifier");
            return errors;
        }

        //do I have subject details ?
        Participant subjectSrc = rpSrc.getParticipant();
        if(subjectSrc == null ){
            errors.addValidationError("ER-SUB-1", "Subject information is missing in input message");
            return errors;
        }
        
        return errors;
    }

    private CaaersServiceResponse populateErrors(CaaersServiceResponse response, ValidationErrors errors){
        logger.error("Adverse Event Management Service create or update call failed :" + String.valueOf(errors));
        for(ValidationError ve : errors.getErrors())  {
            String message = messageSource.getMessage(ve.getCode(),  ve.getReplacementVariables(), ve.getMessage(), Locale.getDefault());
            Helper.populateError(response, ve.getCode(), message);
        }
        return response;
    }
    

    private void migrate(ExpeditedAdverseEventReport aeSrcReport, ExpeditedAdverseEventReport aeDestReport, ValidationErrors errors){
        try{
             adeersIntegrationFacade.updateStudy(aeSrcReport.getStudy().getId(), false);
        }catch (Exception e){
            logger.warn("Study synchronization failed.", e);
        }
        DomainObjectImportOutcome<ExpeditedAdverseEventReport> outCome = new DomainObjectImportOutcome<ExpeditedAdverseEventReport>();
        aeReportMigrator.migrate(aeSrcReport, aeDestReport, outCome);
        if(outCome.hasErrors()) errors.addValidationErrors(outCome.getValidationErrors().getErrors());
    }
    
    private void transferStudySubjectIfRequired(ExpeditedAdverseEventReport aeSrcReport, ExpeditedAdverseEventReport aeDestReport,ValidationErrors errors){
    	try {
			StudySite originalSite = aeDestReport.getAssignment().getStudySite();
			Participant dbParticipant = aeDestReport.getAssignment().getParticipant();
			Organization organizationTransferredTo = aeSrcReport.getAssignment().getStudySite().getOrganization();
			  if(!aeDestReport.getAssignment().getStudySite().getOrganization().getNciInstituteCode().
				   equals(aeSrcReport.getAssignment().getStudySite().getOrganization().getNciInstituteCode())){
			  participantService.transferParticipant(dbParticipant, originalSite, organizationTransferredTo, errors);
			  }
		} catch (Exception e) {
			logger.error("Error while transferring the StudySubject", e);
		}
    }

    /**
     * Will create a Report and associate it to the ExpeditedAdverseEventReport
     * @param report
     * @param aeReport
     * @return
     */
    public Report createReport(Report report, ExpeditedAdverseEventReport aeReport){
        Date gradedDate =  AdverseEventReportingPeriod.findEarliestGradedDate(aeReport.getUnReportedAdverseEvents());
        report.getReportDefinition().setBaseDate(gradedDate);
        Report newReport = reportRepository.createReport(report.getReportDefinition(), aeReport) ;
        newReport.copy(report);
        reportRepository.save(newReport);
        if(logger.isInfoEnabled()) {
            logger.info("Created report : " + newReport.getName() + "(" + String.valueOf(newReport.getId()) + ")");
        }
        return newReport;
    }

    /**
     * Will update a Report associated it to the ExpeditedAdverseEventReport and in parallel withdraw any Notifications
     * that are submitted previously for the Report being withdrawn.
     * @param report
     * @param aeReport
     * @return
     */
    public Report withdrawReport(Report report, ExpeditedAdverseEventReport aeReport){
        reportRepository.withdrawReport(report);
        reportRepository.withdrawExternalReport(aeReport, report);
        if(logger.isInfoEnabled()) {
            logger.info("Withdrew report : " + report.getName() + "(" + String.valueOf(report.getId()) + ")");
        }
        return report;
    }

    /**
     * Will amend the Report
     * @param report
     * @param aeReport
     * @return
     */
    public Report amendReport(Report report, ExpeditedAdverseEventReport aeReport){
        reportRepository.amendReport(report);
        if(logger.isInfoEnabled()) {
            logger.info("Amended report : " + report.getName() + "(" + String.valueOf(report.getId()) + ")");
        }
        return report;
    }

    /**
     * Will unamend an older version when a new revision of the report is withdrawn.
     * @param report
     * @param aeReport
     * @return
     */
    public Report unAmendReport(Report report, ExpeditedAdverseEventReport aeReport){
        reportRepository.unAmendReport(report);
        if(logger.isInfoEnabled()) {
            logger.info("Unamended report : " + report.getName() + "(" + String.valueOf(report.getId()) + ")");
        }
        return report;
    }
    
    private ExpeditedAdverseEventReport getOrSetReportId(ExpeditedAdverseEventReport aeSrcReport) {
    	String externalId = aeSrcReport.getExternalId();
        ExpeditedAdverseEventReport dbReport = null;
    	if(StringUtils.isEmpty(externalId)) {
        	SafetyReportIdentifer newReportId = idServiceImpl.generateSafetyReportId(new ReportIdCriteria());
        	aeSrcReport.setExternalId(newReportId.getSafetyReportId());
        	List<Report> reports = aeSrcReport.getReports();
        	for (Report report : reports) {
				report.setCaseNumber(newReportId.getSafetyReportId());
			}
        } else {
        	dbReport = externalId != null ? expeditedAdverseEventReportDao.getByExternalId(externalId) : null;
        }
    	
    	return dbReport;
    }
    
    /**
     * Will initiate an safety reporting action, and return the Report Id
     * @param aeSrcReport
     * @param dbReport
     * @param errors
     * @return
     */
    public ExpeditedAdverseEventReport initiateSafetyReportAction(ExpeditedAdverseEventReport aeSrcReport, CaaersServiceResponse caaersServiceResponse, ValidationErrors errors, boolean ignoreER_CA1){
    	
    	//Determine the flow, create vs update
        ExpeditedAdverseEventReport dbReport = getOrSetReportId(aeSrcReport);
        
        List<Report> reportsAffected = new ArrayList<Report>();
        ExpeditedAdverseEventReport aeDestReport = new ExpeditedAdverseEventReport();
        
        migrate(aeSrcReport, aeDestReport, errors);
        if(ignoreER_CA1) errors.removeErrorsWithCode("ER-CA-1");
        if(errors.hasErrors()) return aeDestReport;
        
        if(dbReport != null) {
	        DomainObjectImportOutcome<ExpeditedAdverseEventReport> outCome = new DomainObjectImportOutcome<ExpeditedAdverseEventReport>();
	        aeReportSynchronizer.migrate(aeDestReport, dbReport, outCome);

	        if(outCome.hasErrors()) errors.addValidationErrors(outCome.getValidationErrors().getErrors());
	        if(ignoreER_CA1) errors.removeErrorsWithCode("ER-CA-1");
            if(errors.hasErrors()) return aeDestReport;
        }

        transferStudySubjectIfRequired(aeSrcReport, aeDestReport, errors);
        if(ignoreER_CA1) errors.removeErrorsWithCode("ER-CA-1");
        if(errors.hasErrors()) return aeDestReport;

        if(dbReport == null){
            //create flow
        	// Deep copy the reports as it is throwing ConcurrentModification Exception.
        	aeDestReport.updateAESignatures();
	        expeditedAdverseEventReportDao.save(aeDestReport);
            List<Report> reports = new ArrayList<Report>(aeDestReport.getReports());
            aeDestReport.getReports().clear();
            // Save the report(s) after Migration.
            for ( Report rpt: reports )    {
            	Report createdReport = createReport(rpt, aeDestReport);
                reportsAffected.add(createdReport);
                if(caaersServiceResponse != null){
                	buildReportInformationOutput(createdReport, caaersServiceResponse, ActionType.CREATE);
                }
            }
            //only fire event in create flow
            if(getEventFactory() != null) getEventFactory().publishEntityModifiedEvent(aeDestReport);
        }else{
            //update flow
        	dbReport.updateAESignatures();
        	expeditedAdverseEventReportDao.save(dbReport);
            for(Report r : dbReport.getActiveReports()) {
                reportRepository.save(r);
            }
        	inferReportingAction(aeSrcReport, dbReport,	aeDestReport, reportsAffected, caaersServiceResponse);
        }


        return aeDestReport;
    }

    /**
     * Will update an ExpeditedAdverseEventReport, and return the list of Reports that got updated.
     * @param aeSrcReport
     * @param dbReport
     * @param errors
     * @return
     */
    public List<Report> updateSafetyReport(ExpeditedAdverseEventReport aeSrcReport, ExpeditedAdverseEventReport dbReport, ValidationErrors errors){
        List<Report> reportsAffected = new ArrayList<Report>();
        ExpeditedAdverseEventReport aeDestReport = new ExpeditedAdverseEventReport();
        migrate(aeSrcReport, aeDestReport, errors);
        if(errors.hasErrors()) return reportsAffected;

        DomainObjectImportOutcome<ExpeditedAdverseEventReport> outCome = new DomainObjectImportOutcome<ExpeditedAdverseEventReport>();
        aeReportSynchronizer.migrate(aeDestReport, dbReport, outCome);
        if(outCome.hasErrors()) errors.addValidationErrors(outCome.getValidationErrors().getErrors());
        if(errors.hasErrors()) return reportsAffected;

        expeditedAdverseEventReportDao.save(dbReport);
        for(Report r : dbReport.getActiveReports()) {
            reportRepository.save(r);
        }
        
        transferStudySubjectIfRequired(aeSrcReport, aeDestReport, errors);
        if(errors.hasErrors()) return reportsAffected;

        dbReport.getAssignment().synchronizeMedicalHistoryFromReportToAssignment(dbReport);
        studyParticipantAssignmentDao.save(dbReport.getAssignment());

        inferReportingAction(aeSrcReport, dbReport,	aeDestReport, reportsAffected, null);

        return reportsAffected;
    }
    

	private void inferReportingAction(ExpeditedAdverseEventReport aeSrcReport, ExpeditedAdverseEventReport dbReport, ExpeditedAdverseEventReport aeDestReport, List<Report> reportsAffected, CaaersServiceResponse caaersServiceResponse) {
		
		 //Withdraw active reports
		 //find active reports that are eligible for withdraw
        List<Report> withdrawableReports = dbReport.getActiveReports();
		List<Report> reportsToBeWithdrawn = new ArrayList<Report>();
	    for(Report report : aeDestReport.getReports()){
	    	if(report.getWithdrawnOn() != null){
	    		 // add the withdrawn report to withdraw list
				reportsToBeWithdrawn.add(report);
	    		for(Report withdrawableReport : withdrawableReports){
	    			if(withdrawableReport.isSameReportByCaseNumberOrReportDefinition(report)){
	    				 withdrawReport(withdrawableReport, dbReport);
	    	             if(caaersServiceResponse != null){
	    	                buildReportInformationOutput(withdrawableReport, caaersServiceResponse, ActionType.WITHDRAW);
	    	             }
	    			}
	    		}
	    	}
	    }
	    
	    // remove reports that are withdrawn
	    for(Report withdrawnreport : reportsToBeWithdrawn){
	    	aeDestReport.getReports().remove(withdrawnreport);
	    }
        
    	// Find a relationship between parent and child exists. check if the parent report is already submitted.
    	Report parentCompletedReport = null;
    	
        for(Report srcReport : dbReport.getReports()){
    		 if (srcReport.getStatus().equals(ReportStatus.COMPLETED) && srcReport.getReportDefinition().getName().equals(aeSrcReport.getReports().get(0).getReportDefinition().getName())) {
    			 // Check if the child record exists.
    			 parentCompletedReport = srcReport;
    		 }
    	 }
    	 
    	 //if parent report is completed, change the updateReport definition to match the child report, ie, followup report
    	 if ( parentCompletedReport != null ) {
    		 for(Report srcReport : dbReport.getReports()){
    			 if ( ! ( srcReport.getStatus().equals(ReportStatus.INPROCESS) || srcReport.getStatus().equals(ReportStatus.PENDING) )) continue; // If the Report is completed then skip it.
    			 ReportDefinition parentReportDef = srcReport.getReportDefinition().getParent();
    			 if (parentReportDef != null && parentReportDef.getName().equals(parentCompletedReport.getName()) ) {
    				 
    				 // Override the Report Definition of the Source to Child since child Report is active.
    				 
    				 if ( aeDestReport.getReports().size() != 0 ) {
            			 aeDestReport.getReports().get(0).setReportDefinition(srcReport.getReportDefinition());
    				 }
    				 
    			 }
    		 }
    	 }
    	        
        //create, amend or withdraw reports
        for(Report srcReport : aeDestReport.getReports()) {
                List<Report> reportsToAmend = dbReport.findReportsToAmmend(srcReport.getReportDefinition());
                for(Report  report: reportsToAmend){
                	amendReport(report, dbReport);
                    //reportsAffected.add(createReport(srcReport, dbReport));
                	if(caaersServiceResponse != null){
                		buildReportInformationOutput(report, caaersServiceResponse, ActionType.AMEND);
                	}
                }
                List<Report> reportsToWithdraw = dbReport.findReportsToWithdraw(srcReport.getReportDefinition());
                for(Report  report: reportsToWithdraw){
                    withdrawReport(report, dbReport);
                    if(caaersServiceResponse != null){
                    	buildReportInformationOutput(report, caaersServiceResponse, ActionType.WITHDRAW);
                    }
                }
                List<Report> reportsToEdit = dbReport.findReportsToEdit(srcReport.getReportDefinition());
                if(reportsToEdit.isEmpty()) {
                	Report createdReport = createReport(srcReport, dbReport);
                    reportsAffected.add(createdReport);
                    if(caaersServiceResponse != null){
                    	buildReportInformationOutput(createdReport, caaersServiceResponse, ActionType.CREATE);
                    }
                } else {
                	for(Report  report: reportsToEdit){
                		reportsAffected.add(report);
                		// Copy the Submitter Information from the Input Source.
                		//TODO : need to check if we should call the report.copy() to get all the info
                		report.setSubmitter(srcReport.getSubmitter());
                		report.setCaseNumber(srcReport.getCaseNumber());
                        if(logger.isInfoEnabled()) {
                            logger.info("Edited report : " + report.getName() + "( id :" + String.valueOf(report.getId()) + ", caseNumber:" +  srcReport.getCaseNumber() + ")");
                        }
                		if(caaersServiceResponse != null){
                			buildReportInformationOutput(report, caaersServiceResponse, ActionType.EDIT);
                		}
	                }
                }

                //TODO : BJ implement unammend feature
            }
	}

    /**
     * Will create an ExpeditedAdverseEventReport, then will return all the Reports that got created.
     * @param aeSrcReport
     * @param aeDestReport
     * @param errors
     * @return
     */
    public  List<Report> createSafetyReport(ExpeditedAdverseEventReport aeSrcReport, ExpeditedAdverseEventReport aeDestReport, ValidationErrors errors){
        List<Report> reportsAffected = new ArrayList<Report>();

        //Call the Migration
        migrate(aeSrcReport, aeDestReport, errors);
        if(errors.hasErrors()) return reportsAffected;
        
        for(AdverseEvent ae : aeDestReport.getAdverseEvents()){
            ae.setReport(aeDestReport);
        }
        // Set the signature for the AE.
        aeDestReport.updateAESignatures();
        
        //Call the ExpediteReportDao and save this report.
        expeditedAdverseEventReportDao.save(aeDestReport);
        
        // transfer the study subject if required.
        transferStudySubjectIfRequired(aeSrcReport, aeDestReport, errors);
        if(errors.hasErrors()) return reportsAffected;

        aeDestReport.getAssignment().synchronizeMedicalHistoryFromReportToAssignment(aeDestReport);
        studyParticipantAssignmentDao.save(aeDestReport.getAssignment());

        // Deep copy the reports as it is throwing ConcurrentModification Exception.
        List<Report> reports = new ArrayList<Report>(aeDestReport.getReports());
        aeDestReport.getReports().clear();
        // Save the report(s) after Migration.
        for ( Report rpt: reports )    {
            reportsAffected.add(createReport(rpt, aeDestReport));
        }

        if(getEventFactory() != null) getEventFactory().publishEntityModifiedEvent(aeDestReport);
        return reportsAffected;
    }
    
    @Transactional(readOnly=false)
    public void initiateSafetyReportAction(
			EvaluateAndInitiateInputMessage evaluateInputMessage,
			SaveAndEvaluateAEsOutputMessage response,
			EvaluateAndInitiateOutputMessage retVal,
			AdverseEventReportingPeriod repPeriod) {
    	
    	boolean replace = false;
    	RecommendedActions withdrawAction = null;
		RecommendedActions createAction = null;
		List<RecommendedActions> recActions = response.getRecommendedActions();
		if(evaluateInputMessage.isWithdrawReport() != null && evaluateInputMessage.isWithdrawReport().booleanValue()) {
			final RecommendedActions action = response.getRecommendedActions().get(0);
			action.setAction("Withdraw");
			action.setActionText("Withdraw the " + action.getReport());
			action.setDue(null);
			action.setDueDate(null);
			response.getRecommendedActions().clear();
			response.getRecommendedActions().add(action);
		} else if(response.getRecommendedActions().size() > 1) {
    		boolean withdraw = false;
    		boolean create = false;
    		boolean amend = false;
    		RecommendedActions amendAction = null;
			for(RecommendedActions action : response.getRecommendedActions()) {
				if(!withdraw && "Withdraw".equalsIgnoreCase(action.getAction())) {
					withdraw = true;
					withdrawAction = action;
				}
				if(!create && "Create".equalsIgnoreCase(action.getAction())) {
					create = true;
					createAction = action;
				}
				if(!amend && "Amend".equalsIgnoreCase(action.getAction())) {
					amend = true;
					amendAction = action;
				}
			}
			
			replace = create && withdraw;
			
			response.setRecommendedActions(new ArrayList<RecommendedActions>());
			if(withdraw) {
				response.getRecommendedActions().add(withdrawAction);
			} else if (amend) {
				response.getRecommendedActions().add(amendAction);
			} else if (create) {
				response.getRecommendedActions().add(createAction);
			} else {
				response.setRecommendedActions(recActions);
			}
		}
		
		CaaersServiceResponse caaersServiceResponse = Helper.createResponse();
		ExpeditedAdverseEventReport aeSrcReport = evaluateAndInitiateReportConverter.convert(evaluateInputMessage, repPeriod, response);
		ValidationErrors errors = new ValidationErrors();
		
		initiateSafetyReportAction(aeSrcReport, caaersServiceResponse, errors, true);
		
		errors.removeErrorsWithCode("ER-CA-1");
		
		if(errors.getErrorCount() > 0) {
			throw new CaaersValidationException(errors.toString());
		}
		
		if(replace) {
			response.getRecommendedActions().clear();
			response.getRecommendedActions().add(createAction);
			evaluateInputMessage.setReportId(null);
			aeSrcReport = evaluateAndInitiateReportConverter.convert(evaluateInputMessage, repPeriod, response);
			initiateSafetyReportAction(aeSrcReport, caaersServiceResponse, errors, true);
		}
		
		retVal.setReportId(aeSrcReport.getExternalId());
		
		errors.removeErrorsWithCode("ER-CA-1");
		
		if(errors.getErrorCount() > 0) {
			throw new CaaersValidationException(errors.toString());
		}
		
		response.setRecommendedActions(recActions);
	}
    
    /**
     * Will initiate the safety reporting action
     * @param adverseEventReport
     * @return
     */
    @Transactional(readOnly=false)
    public CaaersServiceResponse initiateSafetyReportAction(BaseAdverseEventReport baseAadverseEventReport) throws Exception {
        CaaersServiceResponse caaersServiceResponse = Helper.createResponse();
        
        ValidationErrors errors = new ValidationErrors();
        ExpeditedAdverseEventReport aeSrcReport = null;
        try {
        	// 1. Call the Converter(s) to construct the domain object.
            aeSrcReport = baseEaeConverter.convert(baseAadverseEventReport);

        }catch (Exception e){
            logger.error("Unable to convert the XML report to domain object", e);
            Helper.populateError(caaersServiceResponse, "WS_GEN_000","Error while converting XML to domain object:" + e.getMessage() );
            throw e;
        }

        try{

            // initialize the service response
            ResponseDataType rdType = new ResponseDataType();
            caaersServiceResponse.getServiceResponse().setResponseData(rdType);
            rdType.setAny(new BaseReports());

            initiateSafetyReportAction(aeSrcReport, caaersServiceResponse, errors, false);

            if(errors.hasErrors())  {
                expeditedAdverseEventReportDao.clearSession();
                populateErrors(caaersServiceResponse, errors);
            } else {
            	caaersServiceResponse.getServiceResponse().setMessage("Initiated safety report action for the safety report, " + baseAadverseEventReport.getExternalId());
            }
            
        }catch (Exception e){
            logger.error("Unable to initiate a safety report action from Safety Management Service", e);
            Helper.populateError(caaersServiceResponse, "WS_GEN_000",e.getMessage() );
            throw e;
        }
        return caaersServiceResponse;
    }

    /**
     * Will create/update the ExpeditedAdverseEventReport and then will submit the Reports modified to external agency.
     * @param adverseEventReport
     * @return
     */
    @Transactional(readOnly=false)
    public CaaersServiceResponse submitSafetyReport(AdverseEventReport adverseEventReport) throws Exception {
        CaaersServiceResponse response = Helper.createResponse();
        try{
            List<Report> reportsAffected = new ArrayList<Report>();
            ValidationErrors errors = createOrUpdateSafetyReport(adverseEventReport, reportsAffected);
            if(errors.hasErrors()) {
                populateErrors(response, errors);
                return response;
            }

            //submit report
            List<Report> failedReports = new ArrayList<Report>();
            for(Report report : reportsAffected){
                reportSubmissionService.submitReport(report);
                if(ReportStatus.FAILED.equals(report.getStatus())) {
                	failedReports.add(report);
                }
            }
            
            if (!failedReports.isEmpty()) {
            	StringBuilder str = new StringBuilder(1024);
            	str.append("Could not send ").append(failedReports.size()).append(" out of ").append(reportsAffected.size()).append(" reports, for the following reasons;\n");
            	for(Report r : failedReports) {
            		str.append("Report: '").append(r.getName()).append("' (").append(r.getId()).append("); Error: ").append(r.getSubmissionMessage()).append("\n\n");
            	}
            	logger.error(str.toString());
                response = Helper.populateError(response, "WS_GEN_007", str.toString().trim());
            }

        } catch (Exception e) {
            logger.error("Unable to Create/Update a Report from Safety Management Service", e);
            Helper.populateError(response, "WS_GEN_000",e.getMessage() );
            throw e;
        }
        return response;
    }

    /**
     * Will save the ExpeditedAdverseEventReport
     * @param adverseEventReport
     * @return
     */
    @Transactional(readOnly=false)
	public CaaersServiceResponse saveSafetyReport(AdverseEventReport adverseEventReport) {
        CaaersServiceResponse response = Helper.createResponse();
        try{
            ValidationErrors errors = createOrUpdateSafetyReport(adverseEventReport, new ArrayList<Report>());
            if(errors.hasErrors()) populateErrors(response, errors);
        }catch (Exception e){
            logger.error("Unable to Create/Update a Report from Safety Management Service", e);
            Helper.populateError(response, "WS_GEN_000",e.getMessage() );
        }
        return response;
    }
    
    
    private void buildReportInformationOutput(Report report, CaaersServiceResponse caaersServiceResponse, ActionType actionType){
    	 BaseReportType baseReport = new BaseReportType();
    	 baseReport.setReportID(report.getAeReport().getExternalId());
    	 
    	 baseReport.setAction(actionType.getDisplayName());
    	 if(report.getLastVersion().getAmendmentNumber() != null) {
    		 baseReport.setAmendmentNumber(report.getLastVersion().getAmendmentNumber().toString());
    	 }
    	 baseReport.setReportName(report.getReportDefinition().getName());
    	 baseReport.setCaseNumber(report.getCaseNumber());
         if((report.getStatus() == ReportStatus.AMENDED || report.getStatus() == ReportStatus.PENDING || report.getStatus() == ReportStatus.FAILED || 
        		 report.getStatus() == ReportStatus.INPROCESS) && report.getDueOn() != null){
        	baseReport.setDueDate(DateUtils.formatToWSResponseDateWithTimeZone(report.getDueOn()));
         }
         // set action text https://tracker.nci.nih.gov/browse/CAAERS-6962
         baseReport.setActionText(actionType.name().substring(0, 1).toUpperCase() + 
        		 actionType.name().substring(1, actionType.name().length()).toLowerCase()  +
        		 " the " + report.getReportDefinition().getName());
         ServiceResponse serviceResponse = caaersServiceResponse.getServiceResponse();
         if(serviceResponse == null) {
        	 serviceResponse = new ServiceResponse();
        	 caaersServiceResponse.setServiceResponse(serviceResponse);
         }
         ResponseDataType respData = serviceResponse.getResponseData();
         if(respData == null) {
        	 respData = new ResponseDataType();
        	 serviceResponse.setResponseData(respData);
         }
         Object obj = respData.getAny();
         BaseReports reportList;
         if(obj == null || !(obj instanceof BaseReports)) {
        	 reportList = new BaseReports();
        	 respData.setAny(reportList);
         } else  {
        	 reportList = (BaseReports) obj;
         }
         reportList.getBaseReport().add(baseReport);
    }

    /**
     * Will create or update an ExpeditedAdverseEventReport, and updates the reportsAffected with the Reports that
     * got amended/edited/created.
     * @param adverseEventReport
     * @param reportsAffected
     * @return
     * @throws Exception
     */
    public ValidationErrors createOrUpdateSafetyReport(AdverseEventReport adverseEventReport, List<Report> reportsAffected) throws Exception {
       ValidationErrors errors = new ValidationErrors();
        ExpeditedAdverseEventReport aeSrcReport = null;

        try {

            // 1. Call the Converter(s) to construct the domain object.
           aeSrcReport = eaeConverter.convert(adverseEventReport);

        }catch(Exception e) {
            logger.error("Error while converting AdverseEvent XML to domain object", e);
            errors.addValidationError( "WS_GEN_008","Error while converting XML to domain object:" + e.getMessage() );
            return errors;
        }

        try {
		   

           //2. Do some basic validations (if needed)
           //3. Determine the flow, create vs update
           String externalId = aeSrcReport.getExternalId();
           ExpeditedAdverseEventReport dbAeReport = externalId != null ? expeditedAdverseEventReportDao.getByExternalId(externalId) : null;

           if(dbAeReport == null){
               //create flow
                reportsAffected.addAll(createSafetyReport(aeSrcReport, new ExpeditedAdverseEventReport(), errors));
           }else{
               //update flow
               reportsAffected.addAll(updateSafetyReport(aeSrcReport, dbAeReport, errors));
           }
           
           if(errors.hasErrors())  {
               expeditedAdverseEventReportDao.clearSession();
               return errors;
           }

       }catch(Exception e) {
           expeditedAdverseEventReportDao.clearSession();
		   logger.error("Unable to Create/Update a Report from Safety Management Service", e);
           errors.addValidationError( "WS_GEN_000","Error while creating or updating safety report:" + e.getMessage() );
	   }
       return errors;
	}
    
    public BaseExpeditedAdverseEventReportConverter getBaseEaeConverter() {
		return baseEaeConverter;
	}

	public void setBaseEaeConverter(
			BaseExpeditedAdverseEventReportConverter baseEaeConverter) {
		this.baseEaeConverter = baseEaeConverter;
	}

	public ExpeditedAdverseEventReportConverter getEaeConverter() {
        return eaeConverter;
    }

    public void setEaeConverter(ExpeditedAdverseEventReportConverter eaeConverter) {
        this.eaeConverter = eaeConverter;
    }

    public ParticipantServiceImpl getParticipantService() {
        return participantService;
    }

    public void setParticipantService(ParticipantServiceImpl participantService) {
        this.participantService = participantService;
    }

    public ParticipantDao getParticipantDao() {
        return participantDao;
    }

    public void setParticipantDao(ParticipantDao participantDao) {
        this.participantDao = participantDao;
    }

    public StudyDao getStudyDao() {
        return studyDao;
    }

    public void setStudyDao(StudyDao studyDao) {
        this.studyDao = studyDao;
    }


    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ExpeditedAdverseEventReportDao getExpeditedAdverseEventReportDao() {
        return expeditedAdverseEventReportDao;
    }

    public void setExpeditedAdverseEventReportDao(ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao) {
        this.expeditedAdverseEventReportDao = expeditedAdverseEventReportDao;
    }

    public ExpeditedReportMigrator getAeReportMigrator() {
        return aeReportMigrator;
    }

    public void setAeReportMigrator(ExpeditedReportMigrator aeReportMigrator) {
        this.aeReportMigrator = aeReportMigrator;
    }

	public ExpeditedAdverseEventReportValidator getAeReportValidator() {
		return aeReportValidator;
	}

	public void setAeReportValidator(
			ExpeditedAdverseEventReportValidator aeReportValidator) {
		this.aeReportValidator = aeReportValidator;
	}

    public ExpeditedAdverseEventReportSynchronizer getAeReportSynchronizer() {
        return aeReportSynchronizer;
    }

    public void setAeReportSynchronizer(ExpeditedAdverseEventReportSynchronizer aeReportSynchronizer) {
        this.aeReportSynchronizer = aeReportSynchronizer;
    }

    public StudyParticipantAssignmentDao getStudyParticipantAssignmentDao() {
        return studyParticipantAssignmentDao;
    }

    public void setStudyParticipantAssignmentDao(StudyParticipantAssignmentDao studyParticipantAssignmentDao) {
        this.studyParticipantAssignmentDao = studyParticipantAssignmentDao;
    }

}
