package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.dao.meddra.LowLevelTermDao;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.ajax.AdverseEventReportingPeriodAjaxableDomainObject;
import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.repository.AdverseEventRoutingAndReviewRepository;
import gov.nih.nci.cabig.caaers.tools.ObjectTools;
import gov.nih.nci.cabig.caaers.web.dwr.AjaxOutput;
import gov.nih.nci.cabig.caaers.web.validation.validator.AdverseEventReportingPeriodValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public class CaptureAdverseEventAjaxFacade  extends CreateAdverseEventAjaxFacade{
	
	 private static Class<?>[] CONTROLLERS = { 	CaptureAdverseEventController.class   };
	 private AdverseEventRoutingAndReviewRepository adverseEventRoutingAndReviewRepository;
	 private AdverseEventReportingPeriodValidator adverseEventReportingPeriodValidator = new AdverseEventReportingPeriodValidator();
	 
	 private static final Log log = LogFactory.getLog(CaptureAdverseEventAjaxFacade.class);
	 @Override
	public Class<?>[] controllers() {
		return CONTROLLERS;
	}
	 
	 public AdverseEventReportingPeriodValidator getAdverseEventReportingPeriodValidator(){
		 return adverseEventReportingPeriodValidator;
	 }
	 
	 public void setAdverseEventReportingPeriodValidator(AdverseEventReportingPeriodValidator adverseEventReportingPeriodValidator){
		 this.adverseEventReportingPeriodValidator = adverseEventReportingPeriodValidator;
	 }
	 
	 public AdverseEventRoutingAndReviewRepository getAdverseEventRoutingAndReviewRepository() {
			return adverseEventRoutingAndReviewRepository;
	 }
	
	 public void setAdverseEventRoutingAndReviewRepository(AdverseEventRoutingAndReviewRepository adverseEventRoutingAndReviewRepository) {
			this.adverseEventRoutingAndReviewRepository = adverseEventRoutingAndReviewRepository;
	 }
	 
   
    /**
     * Create AdverseEvent objects corresponding to the terms(listOfTermIDs).
     *  Add the following parameters to request :- 
     *     1. "index" - corresponds to begin (of AE).
     *     2. "ajaxView" - 'observedAdverseEventSection'
     *  
     * @param listOfTermIDs
     * @return
     */
    public AjaxOutput addObservedAE(int[] listOfTermIDs) {
        
    	AjaxOutput ajaxOutput = new AjaxOutput();
    	
        CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
        command.reassociate();
        int index = command.getAdverseEvents().size();
        
        List<Integer> filteredTermIDs = new ArrayList<Integer>();
        //filter off the terms that are already present
        for(int id : listOfTermIDs){
        	filteredTermIDs.add(id);
        }

        //
        if(filteredTermIDs.isEmpty()) return ajaxOutput;
        
        boolean isMeddra = command.getAdverseEventReportingPeriod().getStudy().getAeTerminology().getTerm() == Term.MEDDRA;
        for (int id : filteredTermIDs) {
            AdverseEvent ae = new AdverseEvent();
            ae.setSolicited(false);
            ae.setRequiresReporting(false);

            if (isMeddra) {
                //populate MedDRA term
                LowLevelTerm llt = lowLevelTermDao.getById(id);
                AdverseEventMeddraLowLevelTerm aellt = new AdverseEventMeddraLowLevelTerm();
                aellt.setLowLevelTerm(llt);
                ae.setAdverseEventMeddraLowLevelTerm(aellt);
                aellt.setAdverseEvent(ae);
            } else {
                //properly set CTCterm
                CtcTerm ctc = ctcTermDao.getById(id);
                AdverseEventCtcTerm aeCtc = new AdverseEventCtcTerm();
                aeCtc.setCtcTerm(ctc);
                ae.setAdverseEventCtcTerm(aeCtc);
                aeCtc.setAdverseEvent(ae);
                if (command.getAdverseEventReportingPeriod().getStudy().isExpectedAdverseEventTerm(ctc)) {
                    if (!ctc.isOtherRequired())
                        ae.setExpected(new Boolean(Boolean.TRUE));
                }
            }

            ae.setReportingPeriod(command.getAdverseEventReportingPeriod());
            command.getAdverseEvents().add(ae);
        }
        
        Map<String, String> params = new LinkedHashMap<String, String>(); // preserve order for testing
    	params.put("adverseEventReportingPeriod", "" + command.getAdverseEventReportingPeriod());
    	params.put("index", Integer.toString(index));
    	 
    	ajaxOutput.setHtmlContent(renderAjaxView("observedAdverseEventSection", 0, params));
    	reportingPeriodDao.save(command.getAdverseEventReportingPeriod());
        return ajaxOutput;
    }
    
    public AjaxOutput addObservedAEByVerbatim(String verbatim) {

    	AjaxOutput ajaxOutput = new AjaxOutput();

        CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
        command.reassociate();
        int index = command.getAdverseEvents().size();

        boolean isMeddra = command.getAdverseEventReportingPeriod().getStudy().getAeTerminology().getTerm() == Term.MEDDRA;
        
        AdverseEvent ae = new AdverseEvent();
        ae.setSolicited(false);
        ae.setRequiresReporting(false);
        ae.setDetailsForOther(verbatim);

        if (isMeddra) {
            // LowLevelTerm llt = new LowLevelTerm();
            AdverseEventMeddraLowLevelTerm aellt = new AdverseEventMeddraLowLevelTerm();
            // aellt.setLowLevelTerm(llt);
            ae.setAdverseEventMeddraLowLevelTerm(aellt);
            aellt.setAdverseEvent(ae);
        } else {
            AdverseEventCtcTerm aeCtc = new AdverseEventCtcTerm();
            ae.setAdverseEventCtcTerm(aeCtc);
            aeCtc.setAdverseEvent(ae);
        }

        ae.setReportingPeriod(command.getAdverseEventReportingPeriod());
        command.getAdverseEvents().add(ae);

        Map<String, String> params = new LinkedHashMap<String, String>(); // preserve order for testing
    	params.put("adverseEventReportingPeriod", "" + command.getAdverseEventReportingPeriod());
    	params.put("index", Integer.toString(index));

    	ajaxOutput.setHtmlContent(renderAjaxView("observedAdverseEventSection", 0, params));
    	reportingPeriodDao.save(command.getAdverseEventReportingPeriod());
        return ajaxOutput;
    }


    public AjaxOutput isExpected(Integer ctcTermID, Integer lowLevelTermID, String verbatim) {
        AjaxOutput ajaxOutput = new AjaxOutput();
        ajaxOutput.setObjectContent(Boolean.TRUE);
        
        CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
        // command.reassociate();

        CtcTerm ctcTerm = null;
        LowLevelTerm lowLevelTerm = null;

        if (ctcTermID > 0)
            ctcTerm = ctcTermDao.getById(ctcTermID);

        if (lowLevelTermID > 0)
             lowLevelTerm = lowLevelTermDao.getById(lowLevelTermID);

        // check the CtcTerms if the passed one is Ctc
        if (ctcTermID > 0) {
            for (ExpectedAECtcTerm t : command.getStudy().getExpectedAECtcTerms()) {

                if (!t.getCtcTerm().getId().equals(ctcTermID)) continue;

                if (!t.isOtherRequired()) {
                    return ajaxOutput;
                }

                // verbatim text is the same as toxicity of the study term
                if (t.getOtherToxicity() != null)
                    if (t.getOtherToxicity().toLowerCase().equals(verbatim.toLowerCase())) {
                        return ajaxOutput;
                    }

                // study term name is the same as verbatim text
                if (t.getOtherMeddraTerm() != null && t.getOtherMeddraTerm().getMeddraTerm().toLowerCase().equals(verbatim.toLowerCase())) {
                    return ajaxOutput;
                }

                // passed meddra term is the same as Study meddra term 
                if (lowLevelTermID > 0 && t.getOtherMeddraTerm() != null) {
                    if (t.getOtherMeddraTerm().getId().equals(lowLevelTermID)) {
                        return ajaxOutput;
                    }
                }


            }
        } else {
            for (ExpectedAEMeddraLowLevelTerm t : command.getStudy().getExpectedAEMeddraLowLevelTerms()) {
                if (t.getId().equals(lowLevelTermID)) {
                    return ajaxOutput;
                }
            }
        }

/*
        try {

            System.out.println("ctcTermID: " + ctcTermID);
            System.out.println("meddraTermID: " + lowLevelTermID);
            System.out.println("verbatim: " + verbatim);
            System.out.println("verbatim: " + verbatim);
            System.out.println(command.getStudy().getId());
            System.out.println(ctcTerm.getCtepTerm());
            System.out.println(lowLevelTerm.getMeddraTerm());

        } catch (Exception ex) {
            System.out.println("ERR: " + ex.getMessage());
        }
*/

        ajaxOutput.setObjectContent("");
        return ajaxOutput;
    }

    /**
     * Will delete (soft delete) the adverse event from course.
     * @param index
     * @param reportId
     * @return
     */
    public AjaxOutput deleteAdverseEvent(int index, String reportId){
    	AjaxOutput ajaxOutput = new AjaxOutput();
    	try {
			CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
			AdverseEvent deletedAe = command.getAdverseEvents().get(index);
			deletedAe.retire(); //soft delete
			reportingPeriodDao.save(command.getAdverseEventReportingPeriod());
		} catch (Exception e) {
			log.error("unable to delete adverse event", e);
			ajaxOutput.setError(true);
			ajaxOutput.setErrorMessage(e.getMessage());
		}
    	return ajaxOutput;
    	
    
    }
    
    public AjaxOutput addReviewComment(String comment, String reportinPeriodIdString){
    	CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
    	command.reassociate();
    	String userId = getUserId();
    	adverseEventRoutingAndReviewRepository.addReportingPeriodReviewComment(command.getAdverseEventReportingPeriod(), comment, userId);
    	
        return fetchPreviousComments(command.getAdverseEventReportingPeriod().getId(), userId);
    }
    
    public AjaxOutput retrieveReviewCommentsAndActions(String reportingPeriodId){
    	AjaxOutput output = retrieveReviewComments();
    	AjaxOutput transitionOutput = retrieveNextTransitions();
    	output.setObjectContent(transitionOutput.getObjectContent());
    	return output;
    }
    
    public AjaxOutput editReviewComment(String comment, Integer commentId, String reportingPeriodIdString){
    	CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
    	command.reassociate();
    	String userId = getUserId();
    	adverseEventRoutingAndReviewRepository.editReportingPeriodReviewComment(command.getAdverseEventReportingPeriod(), comment, userId, commentId);
    	
    	return fetchPreviousComments(command.getAdverseEventReportingPeriod().getId(), userId);
    }
    
    public AjaxOutput deleteReviewComment(Integer commentId, String reportingPeriodIdString){
    	CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
    	command.reassociate();
    	String userId = getUserId();
    	adverseEventRoutingAndReviewRepository.deleteReportingPeriodReviewComment(command.getAdverseEventReportingPeriod(), commentId);
    	
    	return fetchPreviousComments(command.getAdverseEventReportingPeriod().getId(), userId);
    }
    
    public AjaxOutput fetchPreviousComments(Integer entityId, String userId){
		Map params = new HashMap<String, String>();
		params.put(RoutingAndReviewCommentController.AJAX_ENTITY, "reportingPeriod");
        params.put(RoutingAndReviewCommentController.AJAX_ENTITY_ID, entityId.toString());
        params.put("userId", userId);
        params.put(RoutingAndReviewCommentController.AJAX_ACTION, "fetchComments");
        params.put(CaptureAdverseEventController.AJAX_SUBVIEW_PARAMETER, "reviewCommentsList");
        
		String html = renderCommentsAjaxView(params);
		AjaxOutput output = new AjaxOutput();
		output.setHtmlContent(html);
		return output;
	}
    
    public AjaxOutput retrieveReviewComments(){
    	CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
    	try {
			command.reassociate();
		} catch (Exception e) {
			log.warn("Error while reassociating, we can ignore this, as the parent page refresh call might have ended in validation error", e);
		}
    	return fetchPreviousComments(command.getAdverseEventReportingPeriod().getId(), getUserId());
    }
    
    public AjaxOutput retrieveNextTransitions(){
    	CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
    	List<String> transitions = new ArrayList<String>();
    	if(command.getAdverseEventReportingPeriod().getWorkflowId() != null){
    		transitions = adverseEventRoutingAndReviewRepository.nextTransitionNames(command.getAdverseEventReportingPeriod().getWorkflowId(), getUserId());
    	}
    	AjaxOutput output = new AjaxOutput();
    	output.setObjectContent(transitions.toArray());
    	return output;
    }
    
    public AjaxOutput validateAndAdvanceWorkflow(String transitionToTake){
    	CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
    	command.reassociate();
    	AjaxOutput output = new AjaxOutput();
    	Errors errors = new BindException(command.getAdverseEventReportingPeriod(), "adverseEventReportingPeriod");
		
		if(transitionToTake.equals("Submit to Data Coordinator")){
			adverseEventReportingPeriodValidator.validate(command.getAdverseEventReportingPeriod(), errors);
			if(errors.hasErrors()){
				List<String> errorsList = new ArrayList<String>();
				for(Object error: errors.getAllErrors()){
					ObjectError objError = (ObjectError) error;
					errorsList.add(objError.getCode());
				}
				output.setObjectContent(errorsList);
			}
		}
    	
    	return output;
    }
    
    public AjaxOutput advanceWorkflow(String transitionToTake){
    	CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
    	command.reassociate();
    	List<String> transitions = adverseEventRoutingAndReviewRepository.advanceReportingPeriodWorkflow(command.getAdverseEventReportingPeriod().getWorkflowId(), transitionToTake, command.getAdverseEventReportingPeriod(), getUserId());
    	AjaxOutput output = new AjaxOutput();
    	output.setObjectContent(transitions.toArray());
    	return output;
    }
    
    
    public AjaxOutput fetchCourses(Integer studyId, Integer participantId){
    	Study study = studyDao.getById(studyId);
    	Participant participant = participantDao.getById(participantId);
    	StudyParticipantAssignment assignment = assignmentDao.getAssignment(participant, study);
    	List<AdverseEventReportingPeriodAjaxableDomainObject> courses = new ArrayList<AdverseEventReportingPeriodAjaxableDomainObject>();
    	AdverseEventReportingPeriodAjaxableDomainObject rpAjaxable;
    	if(assignment.getActiveReportingPeriods() != null){
    		for(AdverseEventReportingPeriod rp: assignment.getActiveReportingPeriods()){
    			rpAjaxable = new AdverseEventReportingPeriodAjaxableDomainObject();
    			rpAjaxable.setId(rp.getId());
    			rpAjaxable.setName(rp.getName());
    			rpAjaxable.setStartDate(rp.getStartDate());
    			rpAjaxable.setEndDate(rp.getEndDate());
    			if(rp.getEpoch() != null)
    				rpAjaxable.setEpochName(rp.getEpoch().getName());

                // TA can be null because of the "Other TA" field in the "Create RP" Popup
                if (rp.getTreatmentAssignment() != null) {
                    rpAjaxable.setTacDescription(rp.getTreatmentAssignment().getDescription());
                    rpAjaxable.setTacCode(rp.getTreatmentAssignment().getCode());
                } else if (!StringUtils.isEmpty(rp.getTreatmentAssignmentDescription())) {
                    rpAjaxable.setTacDescription(rp.getTreatmentAssignmentDescription());
                }

                courses.add(rpAjaxable);
    		}
    	}
    	AjaxOutput output = new AjaxOutput();
    	output.setObjectContent(courses.toArray());
    	return output;
    }
    
    public AjaxOutput fetchCourseDetails(Integer id){
    	AdverseEventReportingPeriod rp = reportingPeriodDao.getById(id);
    	AdverseEventReportingPeriodAjaxableDomainObject rpAjaxable = new AdverseEventReportingPeriodAjaxableDomainObject();
    	rpAjaxable.setId(rp.getId());
    	rpAjaxable.setStartDate(rp.getStartDate());
    	rpAjaxable.setEndDate(rp.getEndDate());
    	if(rp.getEpoch() != null)
    		rpAjaxable.setEpochName(rp.getEpoch().getName());
    	
    	Integer cycleNumber = rp.getCycleNumber();
    	rpAjaxable.setCycleNumber((cycleNumber != null)? cycleNumber.toString() : "");

        if (rp.getTreatmentAssignment() != null) {
        	
        	String tac = rp.getTreatmentAssignment().getCode();
        	rpAjaxable.setTacCode((tac == null) ? "" : tac);
        	
        	String tacDescription = rp.getTreatmentAssignment().getDescription();
        	rpAjaxable.setTacDescription((tacDescription != null)? tacDescription : "");
        	
        } else if (!StringUtils.isEmpty(rp.getTreatmentAssignmentDescription())) {
            rpAjaxable.setTacDescription(rp.getTreatmentAssignmentDescription());
            rpAjaxable.setTacCode("Other");
        }
        
        AjaxOutput output = new AjaxOutput();
    	output.setObjectContent(rpAjaxable);
    	return output;
    }
}
