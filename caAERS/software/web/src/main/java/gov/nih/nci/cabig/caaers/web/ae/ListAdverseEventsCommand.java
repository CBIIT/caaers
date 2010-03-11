package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.UserGroupType;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.repository.ReportValidationService;
import gov.nih.nci.cabig.caaers.security.SecurityUtils;
import gov.nih.nci.cabig.caaers.service.EvaluationService;
import gov.nih.nci.cabig.caaers.service.ReportSubmittability;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rhett Sutphin
 * @author Biju Joseph
 */
public class ListAdverseEventsCommand {
    private StudyParticipantAssignment assignment;

    private Study study;

    private Participant participant;

    Map<Integer, Boolean> reportsSubmittable;
    
    private boolean submitLinkRenderable;

    private boolean workflowEnabled = false;

    private ReportValidationService reportValidationService;
    
    private boolean amendAnOption;


	public ListAdverseEventsCommand(ReportValidationService reportValidationService) {
        this.reportValidationService = reportValidationService;
        reportsSubmittable = new HashMap<Integer, Boolean>();
    }

    // //// LOGIC


    public void updateSubmittability() {
        reportsSubmittable.clear();
        for (ExpeditedAdverseEventReport aeReport : assignment.getAeReports()) {
            for (Report report : aeReport.getReports()) {
                ReportSubmittability errorMessages = reportValidationService.isSubmittable(report);
                reportsSubmittable.put(report.getId(), errorMessages.isSubmittable());
            }
        }
    }
    
    public void updateOptions() {
    	boolean isSuperUser = SecurityUtils.checkAuthorization(UserGroupType.caaers_super_user);
    	if(isSuperUser || SecurityUtils.checkAuthorization(UserGroupType.caaers_data_cd , 
    			UserGroupType.caaers_ae_cd, UserGroupType.caaers_participant_cd)){
    		setAmendAnOption(true);
    	} else {
    		setAmendAnOption(false);
    	}
    	
    }
    public Map<Integer, Boolean> getReportsSubmittable() {
        return reportsSubmittable;
    }

    // //// BOUND PROPERTIES
    public StudyParticipantAssignment getAssignment() {
		return assignment;
	}
    public void setAssignment(StudyParticipantAssignment assignment) {
		this.assignment = assignment;
	}
    
    public Study getStudy() {
		return study;
	}
    public void setStudy(Study study) {
		this.study = study;
	}
    public Participant getParticipant() {
		return participant;
	}
    public void setParticipant(Participant participant) {
		this.participant = participant;
	}
    
    public boolean getIgnoreCompletedStudy() {
        return false;
    }
    
    public boolean isSubmitLinkRenderable() {
		return submitLinkRenderable;
	}
    public void setSubmitLinkRenderable(boolean submitLinkRenderable) {
		this.submitLinkRenderable = submitLinkRenderable;
	}
    
    public boolean getWorkflowEnabled(){
    	return workflowEnabled;
    }
    
    public void setWorkflowEnabled(boolean workflowEnabled){
    	this.workflowEnabled = workflowEnabled;
    }

	public boolean isAmendAnOption() {
		return amendAnOption;
	}

	public void setAmendAnOption(boolean amendAnOption) {
		this.amendAnOption = amendAnOption;
	}

}
