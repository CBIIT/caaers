package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.domain.Attribution;
import gov.nih.nci.cabig.caaers.domain.DiseaseCodeTerm;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Term;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Rhett Sutphin
 * @author <a href="mailto:biju.joseph@semanticbits.com">Biju Joseph</a>
 */
public interface ExpeditedAdverseEventInputCommand extends AdverseEventInputCommand {
    Integer ZERO = new Integer(0);

    String COURSE_AGENT_ATTRIBUTION_KEY = "courseAgent";
    String CONCOMITANT_MEDICATIONS_ATTRIBUTION_KEY = "conMed";
    String OTHER_CAUSES_ATTRIBUTION_KEY = "other";
    String DISEASE_ATTRIBUTION_KEY = "disease";
    String SURGERY_ATTRIBUTION_KEY = "surgery";
    String RADIATION_ATTRIBUTION_KEY = "radiation";
    String DEVICE_ATTRIBUTION_KEY = "device";
    String OTHERINTERVENTION_ATTRIBUTION_KEY = "otherIntervention";
    String BIOLOGICALINTERVENTION_ATTRIBUTION_KEY = "biologicalIntervention";
    String BEHAVIORALINTERVENTION_ATTRIBUTION_KEY = "behavioralIntervention";
    String GENETICINTERVENTION_ATTRIBUTION_KEY = "geneticIntervention";
    String DIETARYINTERVENTION_ATTRIBUTION_KEY = "dietarySupplementIntervention";

    ExpeditedAdverseEventReport getAeReport();
    List<Map<Integer, Boolean>> getOutcomes();
    List<String> getOutcomeOtherDetails();
    void updateOutcomes();
    
    //report definitions selected at a given point in time.(used for context switching)
    void setSelectedReportDefinitions(List<ReportDefinition> selectedReportDefinitions);
    List<ReportDefinition> getSelectedReportDefinitions();
    
    //all the applicable report definitions (used by context switching)
    void setApplicableReportDefinitions(List<ReportDefinition> applicableReportDefinitions);
    List<ReportDefinition> getApplicableReportDefinitions();
    
    //the report definitions, that are to be created
    void setNewlySelectedReportDefinitions(List<ReportDefinition> newlySelectedReportDefinitions);
    List<ReportDefinition> getNewlySelectedReportDefinitions();
    
    
    /*
     * attributionMap[attributionKey][ae index][cause index]; indexes are the same as the equivs in
     * AdverseEventReport and AdverseEvent
     */
    Map<String, List<List<Attribution>>> getAttributionMap();
    Collection<ExpeditedReportSection> getMandatorySections();
    Map<Integer, Collection<ExpeditedReportSection>> getMandatorySectionMap();
    void setMandatorySectionMap(Map<Integer, Collection<ExpeditedReportSection>> mandatorySectionMap);
    boolean isSectionMandatory(ExpeditedReportSection section);

    boolean shouldValidateAttributions();

    boolean isStudyOutOfSync();
    void setStudyOutOfSync(boolean sync);
    /**
     * Pre-initalize the mandatory lazy added fields in mandatory sections. (This is a biz-rule)
     */
    void initializeMandatorySectionFields();
    MandatoryProperties getMandatoryProperties();
    List<ReportDefinition> getInstantiatedReportDefinitions();
   
    void setNextPage(int page);
    int getNextPage();
    Map<Object, Object> getStudyDiseasesOptions(DiseaseCodeTerm diseaseCodingTerm);
    Term getStudyTerminologyTerm();
    /**
     * If true, the add button on AdverseEvents page in expedited flow will be enabled.
     * @return
     */
    boolean isAdditionAllowed();
    void saveReportingPeriod();
    boolean isErrorApplicable(String... fields);
    public HashMap<String, Boolean> getRulesErrors();
    public void setRulesErrors(HashMap<String, Boolean> rulesErrors);
    
    public List<String> getRuleableFields();
    public void setRuleableFields(List<String> fields);
}
