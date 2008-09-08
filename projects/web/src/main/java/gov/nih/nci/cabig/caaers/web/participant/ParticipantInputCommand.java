package gov.nih.nci.cabig.caaers.web.participant;

import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.utils.IndexFixedList;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * @author Ion C. Olaru
 */

public class ParticipantInputCommand {
    protected final Log log = LogFactory.getLog(getClass());

    protected Participant participant;
    private Study study;
    protected StudyParticipantAssignment assignment;
    private Organization organization;
    private String[] studySiteArray;
    private List<StudySite> studySites = new ArrayList<StudySite>();

    private List<Study> studies = new ArrayList<Study>();

    private String searchTypeText;
    private String searchType;
    private String searchText;

    private String studySubjectIdentifier;
    
    //fields for patient medical history
    
    private String currentItem; //currentItem - corresponds to the item that we are working on now (eg: conmed, priorTherapy). 
    private String task; // will tell the action we perform on the current item.
    private Integer index; //corresponds to the index of the item (eg: conmed[3])
    private Integer parentIndex; // corresponds to the index of the parent item (eg: priorTherapy[parentIndex].agents[index])
    
    private AnatomicSite metastaticDiseaseSite;
    private PreExistingCondition preExistingCondition;
    private PriorTherapy priorTherapy;
    private ChemoAgent chemoAgent;
    private String concomitantMedication;

    private Map<Object, Object> studyDiseasesMap;
    private List<? extends AbstractStudyDisease> studyDiseases;
    
    private DiseaseCodeTerm diseaseCodingTerm;


    public ParticipantInputCommand() {
    }

    public ParticipantInputCommand(Participant participant) {
        this.participant = participant;
    }

    /**
     * This method will initialize the objects that we have to work in the flow.
     * @param identifierType
     */
    void init(String identifierType) {
        this.participant = new Participant();
        this.assignment = new StudyParticipantAssignment();
        this.assignment.setDateOfEnrollment(new Date());
        this.assignment.setParticipant(this.participant);
        this.assignment.setPriorTherapies(new ArrayList<StudyParticipantPriorTherapy>());
        StudyParticipantDiseaseHistory studyParticipantDiseaseHistory = new StudyParticipantDiseaseHistory();
        studyParticipantDiseaseHistory.setAssignment(this.assignment);
        this.assignment.setDiseaseHistory(studyParticipantDiseaseHistory);
        this.assignment.setPreExistingConditions(new ArrayList<StudyParticipantPreExistingCondition>());
        this.assignment.setConcomitantMedications(new ArrayList<StudyParticipantConcomitantMedication>());
        
        OrganizationAssignedIdentifier organizationAssignedIdentifier = new OrganizationAssignedIdentifier();
        organizationAssignedIdentifier.setPrimaryIndicator(Boolean.TRUE);
        organizationAssignedIdentifier.setType(identifierType);
        
        this.participant.addIdentifier(organizationAssignedIdentifier);
    }
    

    
    /**
     * This method will properly initialize the study diseases, so that it can be used in the flow. 
     */
    public void refreshStudyDiseases(){
    	diseaseCodingTerm = getStudy().getDiseaseTerminology().getDiseaseCodeTerm();
    	//based on the disease coding term, initialize appropriate list
    	if(diseaseCodingTerm.equals(DiseaseCodeTerm.MEDDRA)){
    		studyDiseases = getStudy().getMeddraStudyDiseases();
    		studyDiseasesMap = WebUtils.collectOptions(studyDiseases, "id", "term.meddraTerm", "Please select");
    	}else {
    		studyDiseases = getStudy().getCtepStudyDiseases();
    		studyDiseasesMap = WebUtils.collectOptions(studyDiseases,"id", "term.term", "Please select");
    	}
    }
    
    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public StudyParticipantAssignment getAssignment() {
        return assignment;
    }

    public List<StudyParticipantAssignment> getAssignments() {
        List<StudyParticipantAssignment> assignments = new ArrayList<StudyParticipantAssignment>();
        assignments.add(assignment);
        return assignments;
    }

    public void setAssignment(StudyParticipantAssignment assignment) {
        this.assignment = assignment;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String[] getStudySiteArray() {
        return studySiteArray;
    }

    public void setStudySiteArray(String[] studySiteArray) {
        this.studySiteArray = studySiteArray;
    }

    public String getSearchTypeText() {
        return searchTypeText;
    }

    public void setSearchTypeText(String searchTypeText) {
        this.searchTypeText = searchTypeText;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public List<Study> getStudies() {
        return studies;
    }

    public void setStudies(List<Study> studies) {
        this.studies = studies;
    }

    public String getStudySubjectIdentifier() {
        return studySubjectIdentifier;
    }

    public void setStudySubjectIdentifier(String studySubjectIdentifier) {
        this.studySubjectIdentifier = studySubjectIdentifier;
    }

    public List<StudySite> getStudySites() {
        return studySites;
    }

    public void setStudySites(List<StudySite> studySites) {
        this.studySites = studySites;
    }
    
 
    
    public Map<Object, Object> getStudyDiseasesMap() {
		return studyDiseasesMap;
	}
    
    public List<? extends AbstractStudyDisease> getStudyDiseases() {
		return studyDiseases;
	}
    
    /**
     * Will tell which subitem that we are dealing with. 
     * @return
     */
    public String getCurrentItem() {
		return currentItem;
	}
    /**
     * Which tell which subitem that we are dealing with. 
     * @param currentItem
     */
    public void setCurrentItem(String currentItem) {
		this.currentItem = currentItem;
	}
    
    public String getTask() {
		return task;
	}
    public void setTask(String task) {
		this.task = task;
	}
    
    public Integer getIndex() {
		return index;
	}
    public void setIndex(Integer index) {
		this.index = index;
	}
    public void setParentIndex(Integer parentIndex) {
		this.parentIndex = parentIndex;
	}
    public Integer getParentIndex() {
		return parentIndex;
	}
    
    public AnatomicSite getMetastaticDiseaseSite() {
		return metastaticDiseaseSite;
	}
    public void setMetastaticDiseaseSite(AnatomicSite metastaticDiseaseSite) {
		this.metastaticDiseaseSite = metastaticDiseaseSite;
	}
    public PreExistingCondition getPreExistingCondition() {
		return preExistingCondition;
	}
    public void setPreExistingCondition(
			PreExistingCondition preExistingCondition) {
		this.preExistingCondition = preExistingCondition;
	}
    public PriorTherapy getPriorTherapy() {
		return priorTherapy;
	}
    public void setPriorTherapy(PriorTherapy priorTherapy) {
		this.priorTherapy = priorTherapy;
	}
    public ChemoAgent getChemoAgent() {
		return chemoAgent;
	}
    public void setChemoAgent(ChemoAgent chemoAgent) {
		this.chemoAgent = chemoAgent;
	}
    
    public String getConcomitantMedication() {
		return concomitantMedication;
	}
    public void setConcomitantMedication(String concomitantMedication) {
		this.concomitantMedication = concomitantMedication;
	}
}