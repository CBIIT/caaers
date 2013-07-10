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
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.repository.ReportRepository;
import gov.nih.nci.cabig.caaers.domain.validation.ExpeditedAdverseEventReportValidator;
import gov.nih.nci.cabig.caaers.event.EventFactory;
import gov.nih.nci.cabig.caaers.integration.schema.aereport.AdverseEventReport;
import gov.nih.nci.cabig.caaers.integration.schema.common.CaaersServiceResponse;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.migrator.ExpeditedAdverseEventReportConverter;
import gov.nih.nci.cabig.caaers.service.migrator.report.ExpeditedReportMigrator;
import gov.nih.nci.cabig.caaers.service.synchronizer.report.ExpeditedAdverseEventReportSynchronizer;
import gov.nih.nci.cabig.caaers.validation.ValidationError;
import gov.nih.nci.cabig.caaers.validation.ValidationErrors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class SafetyReportServiceImpl {
	private static Log logger = LogFactory.getLog(SafetyReportServiceImpl.class);
	
	/**	Expedited Report Converter. **/
	private ExpeditedAdverseEventReportConverter eaeConverter;

    private ParticipantServiceImpl participantService;

    private ParticipantDao participantDao;

    private StudyDao studyDao;
    

    private MessageSource messageSource;
    
    private ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao;
    
    /** Validator Service. **/
	private ExpeditedAdverseEventReportValidator aeReportValidator;
	
	/** Expedited Report Migrator. **/
	private ExpeditedReportMigrator aeReportMigrator;
	private ExpeditedAdverseEventReportSynchronizer aeReportSynchronizer;

    /** The report Repository. */
    private ReportRepository reportRepository;
    private EventFactory eventFactory;

    public EventFactory getEventFactory() {
        return eventFactory;
    }

    public void setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }



    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * Does the validation of the input message
     * @param aeSrcReport
     * @return
     */
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

    public void migrate(ExpeditedAdverseEventReport aeSrcReport, ExpeditedAdverseEventReport aeDestReport, ValidationErrors errors){
        DomainObjectImportOutcome<ExpeditedAdverseEventReport> outCome = new DomainObjectImportOutcome<ExpeditedAdverseEventReport>();
        aeReportMigrator.migrate(aeSrcReport, aeDestReport, outCome);
        if(outCome.hasErrors()) errors.addValidationErrors(outCome.getValidationErrors().getErrors());
    }
    public void createSafetyReport(ExpeditedAdverseEventReport aeSrcReport, ExpeditedAdverseEventReport aeDestReport, ValidationErrors errors){
       //Call the Migration
       migrate(aeSrcReport, aeDestReport, errors);
       if(errors.hasErrors()) return;

       for(AdverseEvent ae : aeDestReport.getAdverseEvents()){
           ae.setReport(aeDestReport);
       }
        // Set the signature for the AE.
        aeDestReport.updateAESignatures();

        //Call the ExpediteReportDao and save this report.
        expeditedAdverseEventReportDao.save(aeDestReport);

        // Deep copy the reports as it is throwing ConcurrentModification Exception.
        List<Report> reports = new ArrayList(aeDestReport.getReports());
        aeDestReport.getReports().clear();
        List<Report> newReports = new ArrayList<Report>();
        // Save the report(s) after Migration.
        for ( Report rpt: reports )    {
            createReport(rpt, aeDestReport);
        }

        if(getEventFactory() != null) getEventFactory().publishEntityModifiedEvent(aeDestReport);
    }

    public Report createReport(Report report, ExpeditedAdverseEventReport aeReport){
        Report newReport = reportRepository.createReport(report.getReportDefinition(), aeReport) ;
        newReport.copy(report);
        reportRepository.save(newReport);
        return newReport;
    }

    public Report withdrawReport(Report report, ExpeditedAdverseEventReport aeReport){
        reportRepository.withdrawReport(report);
        reportRepository.withdrawExternalReport(aeReport, report);
        return report;
    }

    public Report amendReport(Report report, ExpeditedAdverseEventReport aeReport){
        reportRepository.amendReport(report);
        return report;
    }
    public Report unAmendReport(Report report, ExpeditedAdverseEventReport aeReport){
        reportRepository.unAmendReport(report);
        return report;
    }

    public void updateSafetyReport(ExpeditedAdverseEventReport aeSrcReport, ExpeditedAdverseEventReport dbReport, ValidationErrors errors){
        ExpeditedAdverseEventReport aeDestReport = new ExpeditedAdverseEventReport();
        migrate(aeSrcReport, aeDestReport, errors);
        if(errors.hasErrors()) return;
        DomainObjectImportOutcome<ExpeditedAdverseEventReport> outCome = new DomainObjectImportOutcome<ExpeditedAdverseEventReport>();
        aeReportSynchronizer.migrate(aeDestReport, dbReport, outCome);
        if(outCome.hasErrors()) errors.addValidationErrors(outCome.getValidationErrors().getErrors());
        // Update AE Signatures.
        aeDestReport.updateAESignatures();

        expeditedAdverseEventReportDao.save(dbReport);

        if(aeDestReport.getReports() == null || aeDestReport.getReports().isEmpty()) {
            //withdraw active reports
            List<Report> reportsToWithdraw = dbReport.getActiveReports();
            for(Report srcReport : reportsToWithdraw){
                withdrawReport(srcReport, dbReport);
            }
        } else {
            //create amend or withdraw reports
            for(Report srcReport : aeDestReport.getReports()){
                List<Report> reportsToAmend = dbReport.findReportsToAmmend(srcReport.getReportDefinition());
                for(Report  report: reportsToAmend){
                    amendReport(report, dbReport);
                }
                List<Report> reportsToWithdraw = dbReport.findReportsToWithdraw(srcReport.getReportDefinition());
                for(Report  report: reportsToWithdraw){
                    withdrawReport(report, dbReport);
                }
                List<Report> reportsToEdit = dbReport.findReportsToEdit(srcReport.getReportDefinition());
                if(reportsToEdit.isEmpty()) createReport(srcReport, dbReport);

                //TODO : BJ implement unammend feature
            }
        }

        if(getEventFactory() != null) getEventFactory().publishEntityModifiedEvent(aeDestReport);
    }

	@Transactional(readOnly=false)
	public CaaersServiceResponse submitSafetyReport(AdverseEventReport adverseEventReport) {

       ValidationErrors errors = new ValidationErrors();
	   CaaersServiceResponse response = Helper.createResponse();

	   try {
		   
           // 1. Call the Converter(s) to construct the domain object.
           ExpeditedAdverseEventReport aeSrcReport = eaeConverter.convert(adverseEventReport);
           //2. Do some basic validations (if needed)
           



           //3. Determine the flow, create vs update
           String externalId = aeSrcReport.getExternalId();
           ExpeditedAdverseEventReport dbAeReport = externalId != null ? expeditedAdverseEventReportDao.getByExternalId(externalId) : null;

           if(dbAeReport == null){
               //create flow
                createSafetyReport(aeSrcReport,new ExpeditedAdverseEventReport(), errors);
           }else{
               //update flow
                updateSafetyReport(aeSrcReport, dbAeReport, errors);
           }

           if(errors.hasErrors()) {
               expeditedAdverseEventReportDao.clearSession();
               return populateErrors(response, errors);
           }
           
           //2. Run the validation (basic)
       //    ValidationErrors errors = validateInput(aeSrcReport);
       //    if(errors.hasErrors()) return populateErrors(response, errors);
           
           // 2. Call the GenericValidator to make sure input is correct.
		//   Errors reportValidatorErrors = new BindException(aeSrcReport, "ExpeditedAdverseEventReport");
		//   aeReportValidator.validate( aeSrcReport, reportValidatorErrors);
		   
		/*   if ( reportValidatorErrors.hasErrors()) {
			   Helper.populateError(response, "GEN_ORH_001", "Error(s) occured during Valdation step.");
			   return response;
		   }*/

           //TODO : below call will change based on create or Amend flow
           //3. Save the report



       }catch(Exception e) {
           expeditedAdverseEventReportDao.clearSession();
		   logger.error("Unable to Create/Update a Report from Safety Management Service", e);
		   Helper.populateError(response, "WS_GEN_000",e.getMessage() );
	   }
       return response;
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
}
