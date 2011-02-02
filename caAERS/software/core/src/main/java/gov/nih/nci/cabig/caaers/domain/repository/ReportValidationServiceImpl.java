package gov.nih.nci.cabig.caaers.domain.repository;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.Attribution;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Retireable;
import gov.nih.nci.cabig.caaers.domain.attribution.AdverseEventAttribution;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportTree;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.TreeNode;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.UnsatisfiedProperty;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.service.EvaluationService;
import gov.nih.nci.cabig.caaers.service.ReportSubmittability;
import gov.nih.nci.cabig.ctms.domain.DomainObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

 
/**
 * Provides method to validate the completeness of a {@link gov.nih.nci.cabig.caaers.domain.report.Report}
 * @author Sameer Sawant
 * @author Biju Joseph
 */
public class ReportValidationServiceImpl implements ReportValidationService{
	
	/** The expedited report tree. */
	private ExpeditedReportTree expeditedReportTree;

    /** The evaluation service. */
    private EvaluationService evaluationService;

    /**
     * Checks if the {@link gov.nih.nci.cabig.caaers.domain.report.Report} can be submitted, based on the mandatory section
     * and other instantiated sections.
     *
     * @param report the report
     * @return the report submittability
     */
	public ReportSubmittability isSubmittable(Report report) {

        Map<Integer, Collection<ExpeditedReportSection>> mandatorySectionMap = evaluationService.mandatorySections(report.getAeReport(), report.getReportDefinition());

        return validate(report, Arrays.asList(ExpeditedReportSection.values()), mandatorySectionMap.get(report.getReportDefinition().getId()));
    }
	
	/**
	 * Will tell whether all the mandatory field for this report is duly filled.
	 * Internally this will call the validate method for each element having children in the {@link ExpeditedReportTree}
	 *
	 * @param report the report
	 * @param reportSections - The sections that are to be validated.
	 * @param mandatorySections - The sections which are marked mandatory in mandatory section rules.
	 * @return ErrorMessages
	 */
    public ReportSubmittability validate(Report report, Collection<ExpeditedReportSection> reportSections, Collection<ExpeditedReportSection> mandatorySections) {

        ReportSubmittability messages = new ReportSubmittability();
        ExpeditedAdverseEventReport aeReport = report.getAeReport();

        
        //evaluate mandatoryness
        evaluationService.evaluateMandatoryness(aeReport, report);

        List<String> nonSelfReferencedMandatoryFields = report.getPathOfNonSelfReferencedMandatoryFields();
        List<String> selfReferencedMandatoryFields = report.getPathOfSelfReferencedMandatoryFields();
        for (ExpeditedReportSection section : reportSections) {
            if (section == null)
                throw new NullPointerException("The mandatory sections collection must not contain nulls");
            validate(aeReport, nonSelfReferencedMandatoryFields, selfReferencedMandatoryFields, section, messages);
        }

        //biz rule - Attribution validation should be done if the ReportDefinition says that it is attributable
        if( report.getReportDefinition().getAttributionRequired()){

        	for (AdverseEvent ae : aeReport.getAdverseEvents()) {
        		Attribution max = null;
        		for (AdverseEventAttribution<?> attribution : ae.getAdverseEventAttributions()) {
        			if(attribution.getAttribution() == null) {max = null; break;} //special case when people click save again (after an error).
        			if (max == null || attribution.getAttribution().getCode() > max.getCode()) {
        				max = attribution.getAttribution();
		    		}
		    	}
        		if (max == null || max.getCode() < Attribution.POSSIBLE.getCode()) {
        			messages.addValidityMessage(ExpeditedReportSection.ATTRIBUTION_SECTION,
		    		String.format("The adverse event, '%s, ' is not attributed to a cause. An attribution of possible or higher must be selected for at least one of the causes.", ae.getAdverseEventTerm().getUniversalTerm() != null ? ae.getAdverseEventTerm().getUniversalTerm() : ae.getDetailsForOther()));
        		}
		    }
        }
        
        //biz rule - Physician Sign-Off should be true if the ReportDefinition says that Physician Sign-Off is needed.
        if(report.getReportDefinition().getPhysicianSignOff()){
        	if(report.getPhysicianSignoff() == null || !report.getPhysicianSignoff()){
        		messages.addValidityMessage(ExpeditedReportSection.SUBMIT_REPORT_SECTION, 
        				"Physician sign-off is mandatory for this report.");
        	}
        }

        //additional business rules - if a section is mandatory at least one active child should be present. 
        if(CollectionUtils.isNotEmpty(mandatorySections)) {
           for(ExpeditedReportSection mandatorySecton : mandatorySections){

               //special case - Medical information section has a list but should be ignored
               if(mandatorySecton.equals(ExpeditedReportSection.MEDICAL_INFO_SECTION)) continue;

               boolean sectionFilled = true;
               
               if(mandatorySecton.equals(ExpeditedReportSection.STUDY_INTERVENTIONS)){

                 if(mandatorySections.contains(ExpeditedReportSection.AGENTS_INTERVENTION_SECTION) ||
                    mandatorySections.contains(ExpeditedReportSection.RADIATION_INTERVENTION_SECTION) ||
                    mandatorySections.contains(ExpeditedReportSection.SURGERY_INTERVENTION_SECTION) ||
                    mandatorySections.contains(ExpeditedReportSection.MEDICAL_DEVICE_SECTION)){
                     continue; 
                 }

                 //special case - need to check at least one of its children is present instead.
                   sectionFilled = isElementPresentInSection(aeReport, ExpeditedReportSection.AGENTS_INTERVENTION_SECTION) ||
                                isElementPresentInSection(aeReport, ExpeditedReportSection.RADIATION_INTERVENTION_SECTION) ||
                                isElementPresentInSection(aeReport, ExpeditedReportSection.SURGERY_INTERVENTION_SECTION) ||
                                isElementPresentInSection(aeReport, ExpeditedReportSection.MEDICAL_DEVICE_SECTION);

               }else{
                   sectionFilled = isElementPresentInSection(aeReport, mandatorySecton);
               }

               if(!sectionFilled){
                   messages.addValidityMessage(mandatorySecton,
                           String.format("The section '%s' is mandatory for this report and cannot be empty", mandatorySecton.getDisplayName()));
               }
           }
        }

       return messages;
    }

    /**
     * Will check if the {@link gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport} has the values for elements
     * associated with the section.
     *
     * @param aeReport the ae report
     * @param section the section
     * @return true, if is element present in section
     */
    protected boolean isElementPresentInSection(ExpeditedAdverseEventReport aeReport, ExpeditedReportSection section){
        TreeNode sectionNode = expeditedReportTree.getNodeForSection(section);
        List<TreeNode> listNodes = new ArrayList<TreeNode>();
        sectionNode.recursivelyCollectListNodes(listNodes);

        if(listNodes.isEmpty()) return true;

        for(TreeNode listNode : listNodes){
            MutablePropertyValues mpvs = listNode.getPropertyValuesFrom(aeReport);
            for(PropertyValue pv : mpvs.getPropertyValues()){
                Object obj = pv.getValue();
                if(obj instanceof DomainObject){
                    if(obj instanceof Retireable && ((Retireable) obj).isRetired()) continue;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Validate.
     *
     * @param aeReport the ae report
     * @param mandatoryFields the mandatory fields
     * @param selfReferencedMandatoryFields the self referenced mandatory fields
     * @param section the section
     * @param messages the messages
     */
    @SuppressWarnings("unchecked")
    private void validate(
            ExpeditedAdverseEventReport aeReport, List<String> mandatoryFields, List<String> selfReferencedMandatoryFields , ExpeditedReportSection section,
            ReportSubmittability messages
    ) {
        TreeNode sectionNode = expeditedReportTree.getNodeForSection(section);
        if (sectionNode == null)
            throw new CaaersSystemException("There is no section node in the report tree for " + section.name() + ".  This shouldn't be possible.");

        List<String> applicableFields = new LinkedList<String>();
        for (String field : mandatoryFields) {
            TreeNode n = sectionNode.find(field);
            if (n == null) continue;
            applicableFields.add(field);
        }
        List<UnsatisfiedProperty> unsatisfied = expeditedReportTree.verifyPropertiesPresent(
                applicableFields, aeReport);
        for (UnsatisfiedProperty uProp : unsatisfied) {
            TreeNode unsatisfiedNode = uProp.getTreeNode();

            messages.addMissingField(
                    section,
                    uProp.getDisplayName(),
                    uProp.getBeanPropertyName());
        }

        //evaluate the self referenced fields
        BeanWrapper bw = new BeanWrapperImpl(aeReport);
        for(String fieldPath : selfReferencedMandatoryFields){
            TreeNode node = expeditedReportTree.find(fieldPath);
            
            if(node != null && sectionNode.isAncestorOf(node)){
                if(bw.getPropertyValue(fieldPath) == null){
                   messages.addMissingField(section, node.getDisplayName(), fieldPath); 
                }
            }

        }

    }

    /**
     * Sets the expedited report tree.
     *
     * @param expeditedReportTree the new expedited report tree
     */
    @Required
    public void setExpeditedReportTree(final ExpeditedReportTree expeditedReportTree) {
        this.expeditedReportTree = expeditedReportTree;
    }

    /**
     * Sets the evaluation service.
     *
     * @param evaluationService the new evaluation service
     */
    @Required
    public void setEvaluationService(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }
}