package gov.nih.nci.cabig.caaers.web.participant;

import gov.nih.nci.cabig.caaers.dao.PreExistingConditionDao;
import gov.nih.nci.cabig.caaers.dao.PriorTherapyDao;
import gov.nih.nci.cabig.caaers.domain.AnatomicSite;
import gov.nih.nci.cabig.caaers.domain.DateValue;
import gov.nih.nci.cabig.caaers.domain.PreExistingCondition;
import gov.nih.nci.cabig.caaers.domain.PriorTherapy;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantConcomitantMedication;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantMetastaticDiseaseSite;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantPreExistingCondition;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantPriorTherapy;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantPriorTherapyAgent;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.fields.TabWithFields;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Biju Joseph
 *
 */
public class SubjectMedHistoryTab <T extends ParticipantInputCommand> extends TabWithFields<T> {
	
	//the below static variables corresponds to the field group names
	private static final String GENERAL = "general";
	private static final String PRIOR_THERAPY = "priorTherapy";
	private static final String PRIOR_THERAPY_AGENT = "priorTherapyAgent";
	
	private static final String METASTATIC_DISEASE_SITE = "metastaticDiseaseSite";
	private static final String PRE_EXISTING_CONDITION = "preExistingCondition";
	private static final String CONCOMITANT_MEDICATION = "concomitantMedication";
	
	private int[] agentsPossiblePriorTherapies = {3,4,5,7,8,11};
	
	
    private static final Log log = LogFactory.getLog(SubjectMedHistoryTab.class);
    Map<String, String> methodNameMap = new HashMap<String, String>();

	
    private PriorTherapyDao priorTherapyDao;
    private PreExistingConditionDao preExistingConditionDao;
    private ConfigProperty configurationProperty;
    
    
    //static options of dropdowns are cached at Tab level. 
    Map<Object,Object> priorTherapyOptions;
    Map<Object, Object> preExistingConditionOptions;
    Map<Object, Object> baselinePerformanceOptions;
    
	public SubjectMedHistoryTab() {
        super("Subject Medical History", "Subject Medical History", "par/par_subject_med_history");
        
        methodNameMap.put("add" + METASTATIC_DISEASE_SITE, "addMetastaticDiseaseSite");
        methodNameMap.put("remove" + METASTATIC_DISEASE_SITE, "removeMetastaticDiseaseSite");
        
        methodNameMap.put("add" + PRE_EXISTING_CONDITION, "addPreExistingCondition");
        methodNameMap.put("remove" + PRE_EXISTING_CONDITION, "removePreExistingCondition");
        
        methodNameMap.put("add" + PRIOR_THERAPY, "addPriorTherapy");
        methodNameMap.put("remove" + PRIOR_THERAPY, "removePriorTherapy");
        
        methodNameMap.put("add" + PRIOR_THERAPY_AGENT, "addPriorTherapyAgent");
        methodNameMap.put("remove" + PRIOR_THERAPY_AGENT, "removePriorTherapyAgent");
        
        methodNameMap.put("add" + CONCOMITANT_MEDICATION, "addConcomitantMedication");
        methodNameMap.put("remove" + CONCOMITANT_MEDICATION, "removeConcomitantMedication");
    }
	
	@Override
    public Map<String, InputFieldGroup> createFieldGroups(T command) {
    	return new  InputFieldGroupMap();
	}

    @Override
    public Map<String, Object> referenceData(HttpServletRequest request, T command) {
    	Map<String, Object> refData = super.referenceData(request, command);

        command.refreshStudyDiseases();

        refData.put("preExistingConditionOptions", initializePreExistingConditionOptions());
    	refData.put("studyDiseasesOptions", command.getStudyDiseasesMap());
    	refData.put("baselinePerformanceOptions", initializeBaselinePerformanceOptions());
    	refData.put("priorTherapyOptions", initializePriorTherapyOptions());
    	return refData;
    	
    }
    
 
    
    //----- Create/Edit/Save/Delete operations (tasks) ----------------- 
    
    public ModelAndView addMetastaticDiseaseSite(HttpServletRequest request , Object cmd, Errors errors){
    	ParticipantInputCommand command =(ParticipantInputCommand)cmd;
    	ModelAndView modelAndView = new ModelAndView("par/ajax/metastaticDiseaseSiteFormSection");
    	List<StudyParticipantMetastaticDiseaseSite> sites = command.getAssignment().getDiseaseHistory().getMetastaticDiseaseSites();
    	modelAndView.getModel().put("metastaticDiseaseSites", sites);
    	int size = sites.size();
    	Integer[] indexes = new Integer[]{size};
    	modelAndView.getModel().put("indexes", indexes);
    	
    	AnatomicSite site = command.getMetastaticDiseaseSite();
    	StudyParticipantMetastaticDiseaseSite metastaticSite = new StudyParticipantMetastaticDiseaseSite();
    	metastaticSite.setCodedSite(site);
    	command.getAssignment().getDiseaseHistory().addMetastaticDiseaseSite(metastaticSite);
    	command.setMetastaticDiseaseSite(null);
    	
    	return modelAndView;
    }
    
    public ModelAndView removeMetastaticDiseaseSite(HttpServletRequest request , Object cmd, Errors errors){
    	ParticipantInputCommand command =(ParticipantInputCommand)cmd;
    	ModelAndView modelAndView = new ModelAndView("par/ajax/metastaticDiseaseSiteFormSection");
    	List<StudyParticipantMetastaticDiseaseSite> sites = command.getAssignment().getDiseaseHistory().getMetastaticDiseaseSites();
    	sites.remove(sites.get(command.getIndex())); //remove the object from command. 
    	
    	//create the indexs to display in reverse order
    	int size = sites.size();
    	Integer[] indexes = new Integer[size];
    	for(int i = 0 ; i < size ; i++){
    		indexes[i] = size - (i + 1);
    	}
    	modelAndView.getModel().put("metastaticDiseaseSites", sites);
    	modelAndView.getModel().put("indexes", indexes);
    	return modelAndView;
    }
    
    
    public ModelAndView addPreExistingCondition(HttpServletRequest request , Object cmd, Errors errors){
    	ParticipantInputCommand command =(ParticipantInputCommand)cmd;
    	List<StudyParticipantPreExistingCondition> preConditions = command.getAssignment().getPreExistingConditions();
    	
    	ModelAndView modelAndView = new ModelAndView("par/ajax/preExistingConditionFormSection");
    	modelAndView.getModel().put("preExistingConditions", preConditions);
    	int size = preConditions.size();
    	Integer[] indexes = new Integer[]{size};
    	modelAndView.getModel().put("indexes", indexes);
    	
    	StudyParticipantPreExistingCondition preCondition = new StudyParticipantPreExistingCondition();
    	preCondition.setPreExistingCondition(command.getPreExistingCondition());
    	command.getAssignment().addPreExistingCondition(preCondition);
    	command.setPreExistingCondition(null);
    	
    	return modelAndView;
    }
    
    public ModelAndView removePreExistingCondition(HttpServletRequest request , Object cmd, Errors errors){
    	ParticipantInputCommand command =(ParticipantInputCommand)cmd;
    	List<StudyParticipantPreExistingCondition> preConditions = command.getAssignment().getPreExistingConditions();
    	preConditions.remove(preConditions.get(command.getIndex())); //remove the element
    	
    	//create the indexes in reverse order
    	int size = preConditions.size();
    	Integer[] indexes = new Integer[size];
    	for(int i = 0 ; i < size ; i++){
    		indexes[i] = size - (i + 1);
    	}
    	
    	ModelAndView modelAndView = new ModelAndView("par/ajax/preExistingConditionFormSection");
    	modelAndView.getModel().put("preExistingConditions", preConditions);
    	modelAndView.getModel().put("indexes", indexes);
    	
    	return modelAndView;
    }    

    
    
    public ModelAndView addConcomitantMedication(HttpServletRequest request , Object cmd, Errors errors){
    	ParticipantInputCommand command =(ParticipantInputCommand)cmd;
    	List<StudyParticipantConcomitantMedication> conmeds = command.getAssignment().getConcomitantMedications();
    	
    	ModelAndView modelAndView = new ModelAndView("par/ajax/concomitantMedicationFormSection");
    	modelAndView.getModel().put("concomitantMedications", conmeds);
    	int size = conmeds.size();
    	Integer[] indexes = new Integer[]{size};
    	modelAndView.getModel().put("indexes", indexes);
    	
    	StudyParticipantConcomitantMedication conmed = new StudyParticipantConcomitantMedication();
    	conmed.setAgentName(command.getConcomitantMedication());
    	conmed.setStartDate(new DateValue());
    	conmed.setEndDate(new DateValue());
    	command.getAssignment().addConcomitantMedication(conmed);
    	command.setConcomitantMedication(null);
    	
    	return modelAndView;
    }
    
    public ModelAndView removeConcomitantMedication(HttpServletRequest request , Object cmd, Errors errors){
    	ParticipantInputCommand command =(ParticipantInputCommand)cmd;
    	List<StudyParticipantConcomitantMedication> conmeds = command.getAssignment().getConcomitantMedications();
    	conmeds.remove(conmeds.get(command.getIndex())); //remove the element
    	
    	//create the indexes in reverse order
    	int size = conmeds.size();
    	Integer[] indexes = new Integer[size];
    	for(int i = 0 ; i < size ; i++){
    		indexes[i] = size - (i + 1);
    	}
    	
    	ModelAndView modelAndView = new ModelAndView("par/ajax/concomitantMedicationFormSection");
    	modelAndView.getModel().put("concomitantMedications", conmeds);
    	modelAndView.getModel().put("indexes", indexes);
    	
    	return modelAndView;
    }
    
    
    public ModelAndView addPriorTherapy(HttpServletRequest request , Object cmd, Errors errors){
    	ParticipantInputCommand command =(ParticipantInputCommand)cmd;
    	List<StudyParticipantPriorTherapy> priorTherapies = command.getAssignment().getPriorTherapies();
    	
    	ModelAndView modelAndView = new ModelAndView("par/ajax/priorTherapyFormSection");
    	modelAndView.getModel().put("priorTherapies", priorTherapies);
    	int size = priorTherapies.size();
    	Integer[] indexes = new Integer[]{size};
    	modelAndView.getModel().put("indexes", indexes);
    	
    	StudyParticipantPriorTherapy priorTherapy = new StudyParticipantPriorTherapy();
    	priorTherapy.setPriorTherapy(command.getPriorTherapy());
    	priorTherapy.setStartDate(new DateValue());
    	priorTherapy.setEndDate(new DateValue());
    	priorTherapy.setPriorTherapyAgents(new ArrayList<StudyParticipantPriorTherapyAgent>());
    	command.getAssignment().addPriorTherapy(priorTherapy);
    	command.setPriorTherapy(null);
    	command.getPriorTherapyAgents().add(null); //increment the element size
    	
    	return modelAndView;
    }
    
    public ModelAndView removePriorTherapy(HttpServletRequest request , Object cmd, Errors errors){
    	ParticipantInputCommand command =(ParticipantInputCommand)cmd;
    	List<StudyParticipantPriorTherapy> priorTherapies = command.getAssignment().getPriorTherapies();
    	priorTherapies.remove(priorTherapies.get(command.getIndex())); //remove the element
    	command.getPriorTherapyAgents().remove(command.getIndex()); //decrement the size of priortherapy agents by 1. 
    	
    	//create the indexes in reverse order
    	int size = priorTherapies.size();
    	Integer[] indexes = new Integer[size];
    	for(int i = 0 ; i < size ; i++){
    		indexes[i] = size - (i + 1);
    	}
    	
    	ModelAndView modelAndView = new ModelAndView("par/ajax/priorTherapyFormSection");
    	modelAndView.getModel().put("priorTherapies", priorTherapies);
    	modelAndView.getModel().put("indexes", indexes);
    	
    	return modelAndView;
    }
    
    public ModelAndView addPriorTherapyAgent(HttpServletRequest request , Object cmd, Errors errors){
    	ParticipantInputCommand command =(ParticipantInputCommand)cmd;
    	StudyParticipantPriorTherapy priorTherapy = command.getAssignment().getPriorTherapies().get(command.getParentIndex());
    	List<StudyParticipantPriorTherapyAgent> priorTherapyAgents = priorTherapy.getPriorTherapyAgents();
    	
    	ModelAndView modelAndView = new ModelAndView("par/ajax/priorTherapyAgentFormSection");
    	modelAndView.getModel().put("priorTherapyAgents", priorTherapyAgents);
    	modelAndView.getModel().put("parentIndex", command.getParentIndex());
    	int size = priorTherapyAgents.size();
    	Integer[] indexes = new Integer[]{size};
    	modelAndView.getModel().put("indexes", indexes);
    	
    	StudyParticipantPriorTherapyAgent agent = new StudyParticipantPriorTherapyAgent();
    	agent.setChemoAgent(command.getPriorTherapyAgents().get(0));
    	priorTherapy.addPriorTherapyAgent(agent);
    	command.getPriorTherapyAgents().set(0, null);
    	
    	return modelAndView;
    }
    
    public ModelAndView removePriorTherapyAgent(HttpServletRequest request , Object cmd, Errors errors){
    	ParticipantInputCommand command =(ParticipantInputCommand)cmd;
    	StudyParticipantPriorTherapy priorTherapy = command.getAssignment().getPriorTherapies().get(command.getParentIndex());
    	List<StudyParticipantPriorTherapyAgent> priorTherapyAgents = priorTherapy.getPriorTherapyAgents();
    	
    	priorTherapyAgents.remove(priorTherapyAgents.get(command.getIndex())); //remove the element
    	
    	//create the indexes in reverse order
    	int size = priorTherapyAgents.size();
    	Integer[] indexes = new Integer[size];
    	for(int i = 0 ; i < size ; i++){
    		indexes[i] = size - (i + 1);
    	}
    	
    	ModelAndView modelAndView = new ModelAndView("par/ajax/priorTherapyAgentFormSection");
    	modelAndView.getModel().put("priorTherapyAgents", priorTherapyAgents);
    	modelAndView.getModel().put("indexes", indexes);
    	modelAndView.getModel().put("parentIndex", command.getParentIndex());
    	
    	return modelAndView;
    }
   
  
    
    /**
     * Will initialize the Priortherapy drop down options
     * @return
     */
    private Map<Object, Object> initializePriorTherapyOptions() {
    	if(priorTherapyOptions == null){
    		this.priorTherapyOptions = WebUtils.collectOptions(priorTherapyDao.getAll(),"id", "text","Please select");
    	}
        return priorTherapyOptions;
    }
    
    /**
     * Will initialize the pre-existing condition options.
     * @return
     */
    private Map<Object, Object> initializePreExistingConditionOptions(){
    	if(preExistingConditionOptions == null){
    		 List<PreExistingCondition> list = preExistingConditionDao.getAll();
    	        if (list != null) {
    	        	preExistingConditionOptions = new LinkedHashMap<Object, Object>();
    	        	preExistingConditionOptions.put(" ", " Please select                                    .");
    	        	preExistingConditionOptions.putAll(WebUtils.collectOptions(list, "id", "text", "Other, specify"));
    	        }
    	}
    	return preExistingConditionOptions;
    }
    /**
     * Will return the options for baseline performance
     * @return
     */
    private Map<Object, Object> initializeBaselinePerformanceOptions() {
    	if(baselinePerformanceOptions == null){
    		baselinePerformanceOptions = WebUtils.collectOptions(configurationProperty.getMap().get("bpsRefData"), "code", "desc", " Please select                                    .");
    	}
    	return baselinePerformanceOptions;
    }
    
  
   
    @Override
    public String getMethodName(HttpServletRequest request) {
    	String currentItem = request.getParameter("currentItem");
    	String task = request.getParameter("task");
    	return methodNameMap.get(task + currentItem);
    }
    
    @Override
    protected boolean methodInvocationRequest(HttpServletRequest request) {
    	return org.springframework.web.util.WebUtils.hasSubmitParameter(request, "currentItem") && org.springframework.web.util.WebUtils.hasSubmitParameter(request, "task");
    }
    
    //OBJECT METHODS
    public PriorTherapyDao getPriorTherapyDao() {
		return priorTherapyDao;
	}
    public void setPriorTherapyDao(PriorTherapyDao priorTherapyDao) {
		this.priorTherapyDao = priorTherapyDao;
	}
    public PreExistingConditionDao getPreExistingConditionDao() {
		return preExistingConditionDao;
	}
    public void setPreExistingConditionDao(
			PreExistingConditionDao preExistingConditionDao) {
		this.preExistingConditionDao = preExistingConditionDao;
	}
    public void setConfigurationProperty(ConfigProperty configurationProperty) {
		this.configurationProperty = configurationProperty;
	}
}

