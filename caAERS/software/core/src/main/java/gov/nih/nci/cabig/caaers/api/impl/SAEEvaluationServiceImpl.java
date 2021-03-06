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
import gov.nih.nci.cabig.caaers.dao.AdverseEventReportingPeriodDao;
import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.TreatmentAssignmentDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventCtcTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventMeddraLowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventRecommendedReport;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.AeTerminology;
import gov.nih.nci.cabig.caaers.domain.Epoch;
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
import gov.nih.nci.cabig.caaers.domain.repository.AdverseEventRoutingAndReviewRepository;
import gov.nih.nci.cabig.caaers.integration.schema.adverseevent.AdverseEventType;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.AEsOutputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.AdverseEventResult;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.AdverseEvents;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAEsInputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAEsOutputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAndInitiateInputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluateAndInitiateOutputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.EvaluatedAdverseEventResults;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.RecommendedActions;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.SaveAndEvaluateAEsInputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.SaveAndEvaluateAEsOutputMessage;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.SaveAndEvaluateAEsOutputMessageType;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.EvaluationService;
import gov.nih.nci.cabig.caaers.service.RecommendedActionService;
import gov.nih.nci.cabig.caaers.service.migrator.adverseevent.AdverseEventConverter;
import gov.nih.nci.cabig.caaers.service.migrator.adverseevent.AdverseEventReportingPeriodMigrator;
import gov.nih.nci.cabig.caaers.service.migrator.adverseevent.SAEAdverseEventReportingPeriodConverter;
import gov.nih.nci.cabig.caaers.service.migrator.adverseevent.SAEServiceMessageConverter;
import gov.nih.nci.cabig.caaers.service.synchronizer.adverseevent.AdverseEventReportingPeriodSynchronizer;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.utils.DateUtils;
import gov.nih.nci.cabig.caaers.validation.AdverseEventGroup;
import gov.nih.nci.cabig.caaers.validation.CourseCycleGroup;
import gov.nih.nci.cabig.caaers.validation.ValidationErrors;
import gov.nih.nci.cabig.caaers.validation.validator.AdverseEventValidatior;
import gov.nih.nci.cabig.caaers.ws.faults.CaaersFault;

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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

/**
 * EvaluationService evaluate rules on the given AE's submitted by Web service.
 * 
 * @author MedaV
 * 
 */

public class SAEEvaluationServiceImpl {

	private AdverseEventRecommendedReportDao adverseEventRecommendedReportDao;
	private StudyDao studyDao;
	private TreatmentAssignmentDao treatmentAssignmentDao;
    private ParticipantDao participantDao;
    private SAEAdverseEventReportingPeriodConverter reportingPeriodConverter;
	private EvaluationService evaluationService;
	private Configuration configuration;
	private MessageSource messageSource;
	private AdverseEventConverter converter;
    private RecommendedActionService recommendedActionService;
	private AdverseEventReportingPeriodDao adverseEventReportingPeriodDao;
	private AdverseEventReportingPeriodSynchronizer reportingPeriodSynchronizer;
    private AdverseEventReportingPeriodMigrator reportingPeriodMigrator;
	private AdverseEventValidatior adverseEventValidatior;
	private AdverseEventRoutingAndReviewRepository adverseEventRoutingAndReviewRepository;
	private Validator validator;
	private SAEServiceMessageConverter xmlConverter = new SAEServiceMessageConverter();
    private enum RequestType{SaveEvaluate, Evaluate, EvaluateInitiate};
	private static String DEF_ERR_MSG = "Error evaluating adverse events with SAE rules";
	private ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao;

	public void setExpeditedAdverseEventReportDao(
			ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao) {
		this.expeditedAdverseEventReportDao = expeditedAdverseEventReportDao;
	}

	private static final Log logger = LogFactory.getLog(SAEEvaluationServiceImpl.class);
	
	private SafetyReportServiceImpl safetySvcImpl;
	
	public SafetyReportServiceImpl getSafetySvcImpl() {
		return safetySvcImpl;
	}

	public void setSafetySvcImpl(SafetyReportServiceImpl safetySvcImpl) {
		this.safetySvcImpl = safetySvcImpl;
	}
	
	public SAEServiceMessageConverter getSAEServiceMessageConverter() {
		return xmlConverter;
	}

	public void setSAEServiceMessageConverter(SAEServiceMessageConverter xmlConverter) {
		this.xmlConverter = xmlConverter;
	}

	private AEsOutputMessage saveAndProcessAdverseEvents(Study study, AdverseEventReportingPeriod reportingPeriod, Map<AdverseEvent, AdverseEventResult> mapAE2DTO, RequestType type) throws CaaersFault {
		SaveAndEvaluateAEsOutputMessageType output;
		switch (type) {
		case Evaluate:
			throw new IllegalArgumentException("Can't take evaluate as an input.");
		case EvaluateInitiate:
			output = new EvaluateAndInitiateOutputMessage();
			break;
		case SaveEvaluate:
			output = new SaveAndEvaluateAEsOutputMessage();
			break;
		default:
			output = null;
		
		}
		try {
             // 2. Persist AdverseEvents.
            ValidationErrors errors = new ValidationErrors();
            reportingPeriod = createOrUpdateAdverseEvents(reportingPeriod, errors, true);

            if(errors.hasErrors()){
               logger.error("Adverse Event Management Service create or update call failed :" + String.valueOf(errors));
                if ( errors.getErrorAt(0).getCode().equals("NO-CODE")) throw Helper.createCaaersFault(DEF_ERR_MSG, errors.getErrorAt(0).getCode(), errors.getErrorAt(0).getMessage() + " "  +errors.getErrorAt(0).getReplacementVariables()[0]);
				throw Helper.createCaaersFault(DEF_ERR_MSG, errors.getErrorAt(0).getCode(), errors.getErrorAt(0).getMessage());
            }

            //initialize requires reporting flag
            for(AdverseEvent ae : reportingPeriod.getAdverseEvents()) {
                AdverseEventResult aeResult = findAdverseEvent(ae, mapAE2DTO);
                if(aeResult != null) aeResult.setRequiresReporting(ae.getRequiresReporting());
            }

            output.setLinkToReport(constructLinkToReport(study.getId(),reportingPeriod.getParticipant().getId(), reportingPeriod.getId()));
            // 3. fire Evaluation Service to identify SAE or not ?
            output = (SaveAndEvaluateAEsOutputMessageType) fireSAERules(reportingPeriod, study, mapAE2DTO, type, output);

            for(AdverseEvent ae : reportingPeriod.getAdverseEvents()){
            	AdverseEventResult aeResult = findAdverseEvent(ae, mapAE2DTO);
            	if(aeResult == null) {
            		continue;
            	}
				ae.setRequiresReporting(aeResult.isRequiresReporting());
            }
            
            // save the updated reporting period
            saveReportingPeriod(reportingPeriod);
        }
        catch(CaaersSystemException ex) {
            throw Helper.createCaaersFault(DEF_ERR_MSG, ex.getErrorCode(), ex.getMessage());
        }
		
		return output;
	}

    /**
     * Process and Save the Adverse Events.
     *
     * @param saveAndEvaluateAEsInputMessage
     * @return
     * @throws CaaersFault
     */
    public SaveAndProcessOutput saveAndProcessAdverseEvents(SaveAndEvaluateAEsInputMessage saveAndEvaluateAEsInputMessage) throws CaaersFault {
        Map<AdverseEvent, AdverseEventResult> mapAE2DTO = new HashMap<AdverseEvent, AdverseEventResult>();

        if (saveAndEvaluateAEsInputMessage == null ) {
            throw Helper.createCaaersFault(DEF_ERR_MSG, "WS_SAE_007",
                    messageSource.getMessage("WS_SAE_007", new String[]{},  "", Locale.getDefault())
            );
        }
        SaveAndEvaluateAEsOutputMessage saveAndEvaluateAEsOutputMessage = (SaveAndEvaluateAEsOutputMessage)createResponseObject(RequestType.SaveEvaluate);

        AdverseEventReportingPeriod reportingPeriod = null;
        try {
            // 0. Load the study required.
            String studyIdentifier = saveAndEvaluateAEsInputMessage.getCriteria().getStudyIdentifier();
            Study study = fetchStudy(studyIdentifier);

            // 1. Call the converter and make the required object.
            reportingPeriod = reportingPeriodConverter.convert(saveAndEvaluateAEsInputMessage,mapAE2DTO);

             // 2. Persist AdverseEvents.
            ValidationErrors errors = new ValidationErrors();
            reportingPeriod = createOrUpdateAdverseEvents(reportingPeriod, errors, true);

            if(errors.hasErrors()){
               logger.error("Adverse Event Management Service create or update call failed :" + String.valueOf(errors));
                if ( errors.getErrorAt(0).getCode().equals("NO-CODE")) throw Helper.createCaaersFault(DEF_ERR_MSG, errors.getErrorAt(0).getCode(), errors.getErrorAt(0).getMessage() + " "  +errors.getErrorAt(0).getReplacementVariables()[0]);
				throw Helper.createCaaersFault(DEF_ERR_MSG, errors.getErrorAt(0).getCode(), errors.getErrorAt(0).getMessage());
            }

            //initialize requires reporting flag
            for(AdverseEvent ae : reportingPeriod.getAdverseEvents()) {
                AdverseEventResult aeResult = findAdverseEvent(ae, mapAE2DTO);
                if(aeResult != null) aeResult.setRequiresReporting(ae.getRequiresReporting());
            }

            saveAndEvaluateAEsOutputMessage.setLinkToReport(constructLinkToReport(study.getId(),reportingPeriod.getParticipant().getId(), reportingPeriod.getId()));
            // 3. fire Evaluation Service to identify SAE or not ?
            saveAndEvaluateAEsOutputMessage = (SaveAndEvaluateAEsOutputMessage)fireSAERules(reportingPeriod, study, mapAE2DTO,RequestType.SaveEvaluate,saveAndEvaluateAEsOutputMessage);

            for(AdverseEvent ae : reportingPeriod.getAdverseEvents()){
            	AdverseEventResult aeResult = findAdverseEvent(ae, mapAE2DTO);
            	if(aeResult == null) {
            		continue;
            	}
				ae.setRequiresReporting(aeResult.isRequiresReporting());
            }
            
            // save the updated reporting period
            saveReportingPeriod(reportingPeriod);
        }
        catch(CaaersSystemException ex) {
            throw Helper.createCaaersFault(DEF_ERR_MSG, ex.getErrorCode(), ex.getMessage());
        }
        
        studyDao.flush();

        return new SaveAndProcessOutput(saveAndEvaluateAEsOutputMessage, reportingPeriod);
    }
    
    @Transactional(readOnly=false)
    public EvaluateAndInitiateOutputMessage processAndInitiate(EvaluateAndInitiateInputMessage evaluateInputMessage) throws CaaersFault {
    	
    	SaveAndEvaluateAEsInputMessage sae = xmlConverter.SAEInputMessage(evaluateInputMessage);
    	
    	// CAAERS-7414 if reportId is passed check if the reporting period attributes are consistent with the one already saved.
    	String reportId = evaluateInputMessage.getReportId();
    	if(!StringUtils.isBlank(reportId)){
        	validateReportIdAndAdverseEventReportingPeriodAttributes(sae, reportId);
    	}
    	
    	SaveAndProcessOutput data = saveAndProcessAdverseEvents(sae);
    	
    	SaveAndEvaluateAEsOutputMessage response = data.getMsg();
    	
    	EvaluateAndInitiateOutputMessage retVal = xmlConverter.EvaluateAndInitiateOutput(response);
    	
    	
    			
    	if(response.getRecommendedActions() != null && data.getPeriod() != null) {
    		safetySvcImpl.initiateSafetyReportAction(evaluateInputMessage, response, retVal, data.getPeriod());
    	}
		return retVal;
    }
    
	public void validateReportIdAndAdverseEventReportingPeriodAttributes(
			SaveAndEvaluateAEsInputMessage sae, String reportId)
			throws CaaersFault {
		
		if(StringUtils.isBlank(reportId)){return;}

		if (sae == null) {
			throw Helper.createCaaersFault(DEF_ERR_MSG, "WS_SAE_007",
					messageSource.getMessage("WS_SAE_007", new String[] {}, "",
							Locale.getDefault()));
		}

    	// find reporting period attributes from input
		
		AdverseEventReportingPeriod rpDest = new AdverseEventReportingPeriod();
		gov.nih.nci.cabig.caaers.integration.schema.adverseevent.CourseType course = null;
		
		if(sae.getCriteria() != null && sae.getCriteria().getCourse() != null && sae.getCriteria().getCourse() != null){
			course = sae.getCriteria().getCourse();
		}
		if(course != null && course.getStartDateOfThisCourse() != null){
			rpDest.setStartDate(course.getStartDateOfThisCourse().toGregorianCalendar().getTime());
		}
		
		if(course != null && sae.getCriteria().getCourse().getCycleNumber() != null){
			rpDest.setCycleNumber(course.getCycleNumber().intValue());
		}
		
		if(course != null && course.getTreatmentAssignmentCode() != null){
			TreatmentAssignment treatmentAssignmentSrc = new TreatmentAssignment();
			treatmentAssignmentSrc.setCode(course.getTreatmentAssignmentCode());
			rpDest.setTreatmentAssignment(treatmentAssignmentSrc);
		}
		
		ExpeditedAdverseEventReport dbReport = expeditedAdverseEventReportDao
				.getByExternalId(reportId);
		if (dbReport != null) {
			String tac = dbReport.getReportingPeriod().getTreatmentAssignment() != null? dbReport.getReportingPeriod().getTreatmentAssignment().getCode():null;
			if (!rpDest.hasSameCoreAttributes(dbReport.getReportingPeriod().getCycleNumber(), 
					dbReport.getReportingPeriod().getStartDate(),tac)) {
				throw Helper.createCaaersFault(DEF_ERR_MSG, "WS_SAE_008",
						messageSource.getMessage("WS_SAE_008",
								new String[] {reportId}, "", Locale.getDefault()));
			}
		}
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
			
			EvaluationResultDTO dto = evaluationService.evaluateSAERules(reportingPeriod);

			if ( requestType.equals(RequestType.SaveEvaluate)) {
	            findRecommendedActions(dto, reportingPeriod, (SaveAndEvaluateAEsOutputMessage) response);
	            populateActionTextAndDueDate((SaveAndEvaluateAEsOutputMessage) response);
			}
            // create/update/delete AE recommended reports
            manageAdverseEventRecommendedReports(requestType, dto);

           
            //CAAERS-6316 If there are no recommended actions or if there is only 1 recommended action and is Withdraw, 
            // hasSae = false, otherwise true
            if(requestType.equals(RequestType.SaveEvaluate)) {
            	 boolean sae = (((SaveAndEvaluateAEsOutputMessage)response).getRecommendedActions() != null && 
                 		((SaveAndEvaluateAEsOutputMessage)response).getRecommendedActions().size() > 1);
            	 if(!sae && ((SaveAndEvaluateAEsOutputMessage)response).getRecommendedActions() != null && 
            			 !((SaveAndEvaluateAEsOutputMessage)response).getRecommendedActions().isEmpty()){
            		 sae = !StringUtils.equals(((SaveAndEvaluateAEsOutputMessage)response).getRecommendedActions().get(0).getAction(), WITHDRAW.getDisplayName());
                 }
            	 ((SaveAndEvaluateAEsOutputMessage)response).setHasSAE(sae);
            }

            //retrieve all the SAEs identified by rules engine.
            Set<AdverseEvent> evaluatedAdverseEvents = dto.getAllEvaluatedAdverseEvents();
            
            //Mark the Requires reporting flag on AE
            for(AdverseEvent ae : evaluatedAdverseEvents) {

                // find DTO object corresponding to Adverse Event.
                AdverseEventResult aeDTO = null ;
                if ( requestType.equals(RequestType.SaveEvaluate)) {
                    aeDTO = findAdverseEvent(ae,mapAE2DTO);
                }   else {
                    aeDTO = mapAE2DTO.get(ae);
                }
                if(aeDTO != null) {
                    aeDTO.setRequiresReporting(ae.getRequiresReporting());
                }
            }


		} catch (Exception e) {
			logger.error("Exception Occured when processing rules; " + e.toString(), e);
			throw Helper.createCaaersFault(DEF_ERR_MSG, "WS_SAE_001",
					messageSource.getMessage("WS_SAE_001", new String[]{},  "", Locale.getDefault())
					);			
		}

		return response;
	}
	
	private void populateActionTextAndDueDate(SaveAndEvaluateAEsOutputMessage response){
		for(RecommendedActions recActions: response.getRecommendedActions()){
			recActions.setActionText(recActions.getAction().substring(0, 1).toUpperCase() + recActions.getAction().
					substring(1, recActions.getAction().length()).toLowerCase() + " the " + recActions.getReport());
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
    private void findRecommendedActions(EvaluationResultDTO evaluationResult, AdverseEventReportingPeriod reportingPeriod, SaveAndEvaluateAEsOutputMessage response) {

        List<RecommendedActions> recommendedActions = new ArrayList<RecommendedActions>();

        response.setRecommendedActions(recommendedActions);

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

            List<ReportTableRow> applicableRows = applicableReportTableMap.get(aeReportId);

            if ( applicableRows != null) {
                findMatchingRecommendations(applicableRows, recommendedReportTableMap.get(aeReportId), recommendedActions, ignoredRows) ;
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
            action.setDueDate(generateDueDate(row.getReportDefinition(), row.getBaseDate()));

        } else {
            if ( isAnyInGroupChecked(row, preselectedRows)) {     // If the any one of the Report in the group is selected.

                action.setAction(row.getGrpAction().getDisplayName());
                action.setDue(row.getGrpDue());
                action.setDueDate(generateDueDate(row.getReportDefinition(), row.getBaseDate()));
                action.setStatus(row.getGrpStatus());

            } else  { // Other Actions.

                action.setAction(row.getOtherAction().getDisplayName());
                action.setDue(row.getOtherDue());
                action.setDueDate(generateDueDate(row.getReportDefinition(), row.getBaseDate()));
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
    						preSelectedAction.setDue(preselectedRow.getReportDefinition().getExpectedDisplayDueDate());
                            preSelectedAction.setDueDate(generateDueDate(preselectedRow.getReportDefinition(), new Date()));

    						recommendedActions.add(preSelectedAction);
    					}   else { // If it is already occured, Update the due time.

    						ReportTableRow createAction = findApplicableRow(applicableRows, preSelectedAction.getReport()) ;
    						preSelectedAction.setDue(createAction.getDue());
                            preSelectedAction.setDueDate(generateDueDate(createAction.getReportDefinition(), createAction.getBaseDate()));

                            //ignore the recomended due calculate the due date from today
                            action.setDue(preselectedRow.getReportDefinition().getExpectedDisplayDueDate(new Date()));
                            action.setDueDate(generateDueDate(preselectedRow.getReportDefinition(), new Date()));

                        }

    				}

    			}


                if(StringUtils.equals(action.getAction(), WITHDRAW.getDisplayName()))    {
                    action.setDue("");
                    action.setDueDate("");
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
	
	private void manageAdverseEventRecommendedReports(RequestType requestType,EvaluationResultDTO dto ){
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

    private String generateDueDate(ReportDefinition rd, Date baseDate) {
        if(rd != null && baseDate != null) return DateUtils.formatToWSResponseDateWithTimeZone(rd.getExpectedDueDate(baseDate));
        return "";
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

	 /**
     * To Create or Update Advese Events.
     * Sync Flag is used only incase of SAE Evaluation service, As service can soft-delete the Adverse Events.
     * @param rpSrc
     * @param errors
     * @param syncFlag
     * @return
     */

    private AdverseEventReportingPeriod createOrUpdateAdverseEvents(AdverseEventReportingPeriod rpSrc, ValidationErrors errors, boolean syncFlag){

        Study study = fetchStudy(rpSrc.getStudy().getFundingSponsorIdentifierValue());
        if(study == null){
            logger.error("Study not present in caAERS with the sponsor identifier : " + rpSrc.getStudy().getFundingSponsorIdentifierValue());
            errors.addValidationError("WS_AEMS_003", "Study with sponsor identifier " + rpSrc.getStudy().getFundingSponsorIdentifierValue() +" does not exist in caAERS",
                    rpSrc.getStudy().getFundingSponsorIdentifierValue());
            return null;
        }
        //async update study removed because the updates would most likely happen after this process is finished.
        
        //migrate the domain object
        AdverseEventReportingPeriod rpDest = new AdverseEventReportingPeriod();
        DomainObjectImportOutcome<AdverseEventReportingPeriod> rpOutcome = new DomainObjectImportOutcome<AdverseEventReportingPeriod>();
        reportingPeriodMigrator.migrate(rpSrc, rpDest, rpOutcome);
        logger.info("Reporting period migration result :" + String.valueOf(rpOutcome.getMessages()));
        if(rpOutcome.hasErrors()){
            //translate error and create a response.
            logger.error("Errors while migrating :" + String.valueOf(rpOutcome.getErrorMessages()));
            errors.addValidationErrors(rpOutcome.getValidationErrors().getErrors());
            return null;
        }
        
        //check if we need the create path or update path.
        String tac = rpDest.getTreatmentAssignment() != null ? rpDest.getTreatmentAssignment().getCode() : null;
        String epochName = rpDest.getEpoch() != null ? rpDest.getEpoch().getName() : null;
        AdverseEventReportingPeriod rpFound = rpDest.getAssignment().findReportingPeriod(rpDest.getExternalId(), rpDest.getStartDate(),rpDest.getEndDate(), rpDest.getCycleNumber(), epochName, tac);
      
        
        ArrayList<AdverseEventReportingPeriod> reportingPeriodList = new ArrayList<AdverseEventReportingPeriod>(rpDest.getAssignment().getActiveReportingPeriods());
        if(rpFound != null) {
            // This is used only incase of SAE Evaluation Service.
            if ( syncFlag ) {
                syncAdverseEventWithSrc(rpFound, rpSrc);
            }
            int i = findIndexFromReportPeriodList(reportingPeriodList, rpFound);
            if  ( i >= 0 ) reportingPeriodList.remove(i);
        }

        ValidationErrors dateValidationErrors = validateRepPeriodDates(rpDest, reportingPeriodList, rpDest.getAssignment().getStartDateOfFirstCourse(), rpDest.getEpoch());
        logger.info("Reporting period validation result :" + String.valueOf(dateValidationErrors));
        if(dateValidationErrors.hasErrors()){
            //translate errors and create a response
            logger.error("Errors while migrating :" + String.valueOf(dateValidationErrors));
            errors.addValidationErrors(dateValidationErrors.getErrors());
            return null;
        }

        //validate adverse events
        for(AdverseEvent adverseEvent : rpDest.getAdverseEvents()) {
        	if(adverseEvent.getGradedDate() == null) adverseEvent.setGradedDate(new Date());
            Set<ConstraintViolation<AdverseEvent>> constraintViolations = validator.validate(adverseEvent, AdverseEventGroup.class, Default.class);
            if(!constraintViolations.isEmpty()){
                //translate errors to response.
                for(ConstraintViolation<AdverseEvent> v : constraintViolations){
                    errors.addValidationError("WS_GEN_006", v.getMessage(), v.getPropertyPath());
                }
                return null;
            }
        }
        // validate Reporting Period.
        AdverseEventReportingPeriod rpTarget = rpFound;
        if (rpTarget == null ) rpTarget=rpDest;

        Set<ConstraintViolation<AdverseEventReportingPeriod>> constraintViolations = validator.validate(rpTarget, CourseCycleGroup.class, Default.class);
        if(!constraintViolations.isEmpty()){
            //translate errors to response.
            for(ConstraintViolation<AdverseEventReportingPeriod> v : constraintViolations){
                errors.addValidationError("WS_GEN_006", v.getMessage(), v.getPropertyPath());
            }
            return null;
        }

        if(rpFound == null){
        	//new reporting period
        	rpFound = rpDest;
        	rpFound.getAssignment().addReportingPeriod(rpFound);
        	// Validate the Reporting Period before saving.
        	adverseEventValidatior.validate(rpFound, rpFound.getStudy(),errors);
        	adverseEventReportingPeriodDao.save(rpFound);
        	if(configuration.get(Configuration.ENABLE_WORKFLOW)){
        		Long wfId = adverseEventRoutingAndReviewRepository.enactReportingPeriodWorkflow(rpFound);
        		logger.debug("Enacted workflow : " + wfId);
        	}
        } else {
        	//existing reporting period.
        	reportingPeriodSynchronizer.migrate(rpDest, rpFound, rpOutcome);
        	// Validate the Reporting Period before saving.
        	adverseEventValidatior.validate(rpFound, rpFound.getStudy(),errors);
        	if ( errors.hasErrors()) {
        		logger.error("Error(s) while validating with Adverse Event " + String.valueOf(errors.getErrorCount()));
        		return null;
        	}
        	adverseEventReportingPeriodDao.save(rpFound);

        }
        return rpFound;
    }
    
    /**
     * Method to save the reporting period from outside the API methods. This can be used for re-saving the reporting period
     * as in the case of Adverse Event / requiresReporting flag.
     * @param reportingPeriod
     * @return
     */
    
    private void saveReportingPeriod(AdverseEventReportingPeriod reportingPeriod){
    	adverseEventReportingPeriodDao.save(reportingPeriod);
    }
    
    /**
     * Sync the adverse Events with Input source, as SAE service expects the complete list of adverse events.
     * @param rpFound
     * @param rpSrc
     */
    private void syncAdverseEventWithSrc(AdverseEventReportingPeriod rpFound, AdverseEventReportingPeriod rpSrc) {

        for( AdverseEvent ae: rpFound.getAdverseEvents()) {
            if ( rpSrc.findAdverseEventByIdTermAndDates(ae)  == null ) { // If the reporting period is not found in  the source
                   ae.setRetiredIndicator(true);
            }
        }

    }
    
    private ValidationErrors validateRepPeriodDates(AdverseEventReportingPeriod rPeriod, List<AdverseEventReportingPeriod> rPeriodList, Date firstCourseDate, Epoch epoch) {

		ValidationErrors errors = new ValidationErrors();
        Date startDate = rPeriod.getStartDate();
		Date endDate = rPeriod.getEndDate();
		
		
		// Check if the start date is equal to or before the end date.
		if (firstCourseDate != null && startDate != null && (firstCourseDate.getTime() - startDate.getTime() > 0)) {
			errors.addValidationError("WS_AEMS_014", "Start date of this course/cycle cannot be earlier than the Start date of first course/cycle");
		}

		if (startDate != null && endDate != null && (endDate.getTime() - startDate.getTime() < 0)) {
            errors.addValidationError("WS_AEMS_015", "Course End date cannot be earlier than Start date.");
		}

		// Check if the start date is equal to end date.
		// This is allowed only for Baseline reportingPeriods and not for other
		// reporting periods.

		if (epoch != null && !epoch.getName().equals("Baseline")) {
			if (endDate != null && startDate.equals(endDate)) {
                errors.addValidationError("WS_AEMS_016", "For Non-Baseline treatment type Start date cannot be equal to End date.");
			}

		}

		// Check if the start date - end date for the reporting Period overlaps
		// with the date range of an existing Reporting Period.
		for (AdverseEventReportingPeriod aerp : rPeriodList) {
			Date sDate = aerp.getStartDate();
			Date eDate = aerp.getEndDate();

			if (!aerp.getId().equals(rPeriod.getId())) {
				
				// CAAERS-7323 - If TAC or otherTreatmentAssignmentDescription of the passed in Reporting Period is different than the Reporting Period in loop, 
				// skip the validation for overlapping reporting period dates
				if(rPeriod.hasNoCommonTacOrOtherTreatmentAssignmentDescription((aerp))){
					continue;
				}

				// we should make sure that no existing Reporting Period, start
				// date falls, in-between these dates.
				if (startDate != null && endDate != null) {
					if (DateUtils.compareDate(sDate, startDate) >= 0 && DateUtils.compareDate(sDate, endDate) < 0) {
                        errors.addValidationError("WS_AEMS_017", "Course/cycle cannot overlap with an existing course/cycle.");
						break;
					}
				} else if (startDate != null && DateUtils.compareDate(sDate, startDate) == 0) {
                       errors.addValidationError("WS_AEMS_017", "Course/cycle cannot overlap with an existing course/cycle.");
					break;
				}

				// newly created reporting period start date, should not fall
				// within any other existing reporting periods
				if (sDate != null && eDate != null) {
					if (DateUtils.compareDate(sDate, startDate) <= 0 && DateUtils.compareDate(startDate, eDate) < 0) {
                        errors.addValidationError("WS_AEMS_017", "Course/cycle cannot overlap with an existing course/cycle.");
						break;
					}
				} else if (sDate != null && DateUtils.compareDate(sDate, startDate) == 0) {
                     errors.addValidationError("WS_AEMS_017", "Course/cycle cannot overlap with an existing course/cycle.");
					break;
				}
			}

			// If the epoch of reportingPeriod is not - Baseline , then it
			// cannot be earlier than a Baseline
			if (epoch != null && epoch.getName().equals("Baseline")) {
				if ( aerp.getEpoch() != null && (!aerp.getEpoch().getName().equals("Baseline"))) {
					if (DateUtils.compareDate(sDate, startDate) < 0) {
                        errors.addValidationError("WS_AEMS_018", "Baseline treatment type cannot start after an existing Non-Baseline treatment type.");
						return errors;
					}
				}
			} else {
				if (aerp.getEpoch() != null && aerp.getEpoch().getName().equals("Baseline")) {
					if (DateUtils.compareDate(startDate, sDate) < 0) {
						errors.addValidationError("WS_AEMS_019", "Non-Baseline treatment type cannot start before an existing Baseline treatment type.");
						return errors;
					}
				}
			}

           // Duplicate Baseline check
            if ( epoch != null && epoch.getName().equals("Baseline") ) {
                // Iterating through the already anything exists with the treatment type Baseline.
                for ( AdverseEventReportingPeriod rp : rPeriodList ) {

                    if ( rp.getEpoch() != null && rp.getEpoch().getName()  != null && rp.getEpoch().getName().equals("Baseline") )  {
                        errors.addValidationError("WS_AEMS_085", "A Baseline treatment type already exists");
                        break;
                    }
                }

            }


		}
		return errors;

	}
    
    private int findIndexFromReportPeriodList(List<AdverseEventReportingPeriod> reportingPeriodList, AdverseEventReportingPeriod rpFound) {
        int i = 0 ;
        for ( AdverseEventReportingPeriod rp: reportingPeriodList) {
            if ( rp.getId().equals(rpFound.getId())) {
                 return i;
            }
            i++;
        }

        return -1;

    }
    
	public EvaluationService getEvaluationService() {
		return evaluationService;
	}

	public void setEvaluationService(EvaluationService evaluationService) {
		this.evaluationService = evaluationService;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public AdverseEventReportingPeriodDao getAdverseEventReportingPeriodDao() {
		return adverseEventReportingPeriodDao;
	}

	public void setAdverseEventReportingPeriodDao(
			AdverseEventReportingPeriodDao adverseEventReportingPeriodDao) {
		this.adverseEventReportingPeriodDao = adverseEventReportingPeriodDao;
	}

	public AdverseEventReportingPeriodSynchronizer getReportingPeriodSynchronizer() {
		return reportingPeriodSynchronizer;
	}

	public void setReportingPeriodSynchronizer(
			AdverseEventReportingPeriodSynchronizer reportingPeriodSynchronizer) {
		this.reportingPeriodSynchronizer = reportingPeriodSynchronizer;
	}

	public AdverseEventReportingPeriodMigrator getReportingPeriodMigrator() {
		return reportingPeriodMigrator;
	}

	public void setReportingPeriodMigrator(
			AdverseEventReportingPeriodMigrator reportingPeriodMigrator) {
		this.reportingPeriodMigrator = reportingPeriodMigrator;
	}

	public AdverseEventValidatior getAdverseEventValidatior() {
		return adverseEventValidatior;
	}

	public void setAdverseEventValidatior(
			AdverseEventValidatior adverseEventValidatior) {
		this.adverseEventValidatior = adverseEventValidatior;
	}

	public AdverseEventRoutingAndReviewRepository getAdverseEventRoutingAndReviewRepository() {
		return adverseEventRoutingAndReviewRepository;
	}

	public void setAdverseEventRoutingAndReviewRepository(
			AdverseEventRoutingAndReviewRepository adverseEventRoutingAndReviewRepository) {
		this.adverseEventRoutingAndReviewRepository = adverseEventRoutingAndReviewRepository;
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public AdverseEventRecommendedReportDao getAdverseEventRecommendedReportDao() {
		return adverseEventRecommendedReportDao;
	}

}
