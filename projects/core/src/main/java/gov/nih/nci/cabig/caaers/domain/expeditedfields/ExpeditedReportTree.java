package gov.nih.nci.cabig.caaers.domain.expeditedfields;

import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.ADDITIONAL_INFO_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.ADVERSE_EVENT_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.ATTRIBUTION_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.BASICS_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.CHECKPOINT_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.CONCOMITANT_MEDICATION_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.DESCRIPTION_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.LABS_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.MEDICAL_DEVICE_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.MEDICAL_INFO_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.OTHER_CAUSE_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.PRE_EXISTING_CONDITION_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.PRIOR_THERAPIES_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.RADIATION_INTERVENTION_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.REPORTER_INFO_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.SUBMIT_REPORT_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.SURGERY_INTERVENTION_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.TREATMENT_INFO_SECTION;
import static gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection.OUTCOME_SECTION;
import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.ReportPerson;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;

/**
 * Tree representing most of the properties in the
 * {@link gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport} model.
 * <p/>
 * Internal nodes in the tree may represent a subproperty of their parent node,
 * or may indicate a logical grouping (section) of their children.  In the latter case,
 * the {@link #getPropertyName propertyName} property will be null.
 *
 * @author Rhett Sutphin
 */
public class ExpeditedReportTree extends PropertylessNode {
    private Map<ExpeditedReportSection, TreeNode> sections;

    public ExpeditedReportTree() {
        sections = new LinkedHashMap<ExpeditedReportSection, TreeNode>();
        add(section(BASICS_SECTION),
            section(ADVERSE_EVENT_SECTION,
                // TODO: figure out how to handle the MedDRA alternative here
                list("adverseEvents", new AdverseEventsDisplayNameCreator(),
                    property("grade", "Grade"),
                    property("startDate", "Start date"),
                    property("endDate", "End date"),
                    property("attributionSummary", "Attribution to study"),
                    property("hospitalization", "Hospitalization"),
                    property("expected", "Expected"),
                    property("comments", "Comments"),
                    property("adverseEventCtcTerm",
                        property("term", "CTC term")
                    ),
                    property("detailsForOther", "Other (specify)")
                )
            ),
            section(REPORTER_INFO_SECTION,
                createPersonBlock("reporter"),
                createPersonBlock("physician")
            ),
            section(CHECKPOINT_SECTION), // so that ordering lines up
            section(RADIATION_INTERVENTION_SECTION,
                list("radiationInterventions","RadiationIntervention",
                    property("treatmentArm", "Treatment arm"),
                    property("description", "Treatment arm description"),
                    property("administration", "Type of radiation administration"),

                    // TODO: these should be a component instead
                    property("dosage", "Dosage"),
                    property("dosageUnit", "Dosage unit"),

                    property("lastTreatmentDate", "Last treatment date"),
                    property("fractionNumber", "Schedule number of fractions"),
                    property("daysElapsed", "Elapsed days"),
                    property("adjustment", "Adjustment")
                )
            ),
            section(SURGERY_INTERVENTION_SECTION,
                list("surgeryInterventions", "SurgeryIntervention",
                    property("treatmentArm", "Treatment arm"),
                    property("description", "Treatment arm description"),
                    property("anatomicSite", "Intervention site"),
                    property("interventionDate", "Intervention date")
                )
            ),
            section(MEDICAL_DEVICE_SECTION,
                list("medicalDevices","MedicalDevice",
                    property("brandName", "Brand name"),
                    property("commonName", "Common name"),
                    property("deviceType", "Device type"),
                    property("manufacturerName", "Manufacturer name"),
                    property("manufacturerCity", "Manufacturer city"),
                    property("manufacturerState", "Manufacturer state"),
                    property("modelNumber", "Model number"),
                    property("lotNumber", "Lot number"),
                    property("catalogNumber", "Catalog number"),
                    property("expirationDate", "Expiration date"),
                    property("serialNumber", "Serial number"),
                    property("otherNumber", "Other number"),
                    property("deviceOperator", "Device operator"),
                    property("otherDeviceOperator", "Other device operator"),
                    property("implantedDate", "If implanted give a date"),
                    property("explantedDate", "IF explanted give a date"),
                    property("deviceReprocessed", "Device reprocessed"),
                    property("reprocessorName", "Reprocessor name"),
                    property("reprocessorAddress", "Reprocessor address"),
                    property("evaluationAvailability", "Evaluation availability"),
                    property("returnedDate", "Returned date")
                )
            ),
            section(DESCRIPTION_SECTION,
                property("responseDescription",
                    property("eventDescription", "Description"),
                    property("presentStatus", "Present status"),
                    property("recoveryDate", "Date of recovery or death"),
                    property("retreated", "Has the particpant been re-treated?"),
                    property("dateRemovedFromProtocol", "Date removed from protocol")
                )
            ),
            section(MEDICAL_INFO_SECTION,
                property("participantHistory",
                    participantMeasure("height"),
                    participantMeasure("weight"),
                    property("baselinePerformanceStatus", "Baseline performance")
                ),
                property("diseaseHistory",
                    codedOrOther(
                        "ctepStudyDisease", "Disease name",
                        "otherPrimaryDisease", "Other (disease)"
                    ),
                    codedOrOther(
                        "codedPrimaryDiseaseSite", "Primary site of disease",
                        "otherPrimaryDiseaseSite", "Other (site of primary disease)"
                    ),
                    property("diagnosisDate", "Date of initial diagnosis"),
                    list("metastaticDiseaseSites", "Metastatic disease site",
                        codedOrOther(
                            "codedSite", "Site name",
                            "otherSite", "Other(site of metastatic disease)"
                        )
                    )
                )
            ),
            section(TREATMENT_INFO_SECTION,
                property("treatmentInformation",
                    property("treatmentAssignment", "Treatment assignment code"),
                    property("treatmentAssignmentDescription", "Description of treatment assignment or dose level"),
                    property("firstCourseDate", "Start date of first course"),
                    // TODO: these should be a component instead
                    property("adverseEventCourse",
                        property("date", "Start date of course associated with expedited report"),
                        property("number", "Course number on which event occurred")
                    ),
                    property("totalCourses", "Total number of courses to date"),
                    //TODO : Need a display name creator????
                    list("courseAgents", "Study Agent",
                        property("studyAgent", "Study Agent"),
                        property("dose",
                        		property("amount", "Total dose administered this course"),
                        		property("units", "Unit of measure")),
                        //property("durationAndSchedule", "Duration and schedule"),
                        property("lastAdministeredDate", "Date last administered"),
                        //dosage("dose", "Dose"), //old Dose
                        //TODO: this is a component
                        property("administrationDelayAmount", "Administration Delay Amount"),
                        property("administrationDelayUnits", "Administration Delay Units"),
                        property("comments", "Comments"),
                        dosage("modifiedDose", "Modified dose")

                    )
                )
            ),
            section(LABS_SECTION,
                list("labs", new LabsDisplayNameCreator(),
               		codedOrOther("name", "Lab test name", "other", "Other test name"),
                    property("units", "Units"),
                    labValue("baseline", "Baseline"),
                    labValue("nadir", "Worst"),
                    labValue("recovery", "Recovery")
                )
            ),
            section(PRIOR_THERAPIES_SECTION,
                list("adverseEventPriorTherapies", "Prior Therapy",
                    property("priorTherapy", "Prior therapy"),
                    property("other", "Comments (prior therapy)"),        //TODO: change the name of other to "comments"
                    property("startDate", "Therapy start Date"),
                    property("endDate", "Therapy end Date")
                )
            ),
            section(PRE_EXISTING_CONDITION_SECTION,
                list("adverseEventPreExistingConds", "AdverseEventPreExistingCond",
                    codedOrOther(
                        "preExistingCondition", "Pre-existing condition",
                        "other", "Other (pre-existing)"
                    )
                )
            ),
            section(CONCOMITANT_MEDICATION_SECTION,
                list("concomitantMedications", "ConcomitantMedication",
                    property("agentName", "Medication")
                )
            ),
            section(OTHER_CAUSE_SECTION,
                list("otherCauses", "OtherCauses",
                    property("text", "Cause")
                )
            ),
            section(ATTRIBUTION_SECTION), //TODO: how to fill this??
            section(ADDITIONAL_INFO_SECTION),//TODO: additional info section
            section(SUBMIT_REPORT_SECTION),// TODO: just a space filler section
            section(OUTCOME_SECTION)// TODO: just a space filler section
        );
    }

    @Override
    public TreeNode add(TreeNode... subnodes) {
        super.add(subnodes);
        for (TreeNode subnode : subnodes) {
            if (subnode instanceof SectionNode) {
                sections.put(((SectionNode) subnode).getSection(), subnode);
            }
        }
        return this;
    }

    @Override
    public String getPropertyName() {
        return null;
    }

    public List<UnsatisfiedProperty> verifyPropertiesPresent(
        String nodePropertyName, ExpeditedAdverseEventReport report
    ) {
        return verifyPropertiesPresent(Collections.singleton(nodePropertyName), report);
    }

    public List<UnsatisfiedProperty> verifyPropertiesPresent(
        Collection<String> nodePropertyNames, ExpeditedAdverseEventReport report
    ) {
        List<TreeNode> propertyNodes = new LinkedList<TreeNode>();
        for (String propertyName : nodePropertyNames) {
            TreeNode node = find(propertyName);
            // HACK - if there is a property mismatch, node will be null.
            if (node == null) continue; // continue with next property.
            propertyNodes.add(node);
        }
        return verifyNodesSatisfied(propertyNodes, report);
    }

    public List<UnsatisfiedProperty> verifyNodesSatisfied(
        Collection<TreeNode> propertyNodes, ExpeditedAdverseEventReport report
    ) {
        if (log.isDebugEnabled()) {
            log.debug("Examining report for satisfaction of " + propertyNodes);
        }
        List<UnsatisfiedProperty> unsatisfied = new LinkedList<UnsatisfiedProperty>();
        for (TreeNode node : propertyNodes) {
            PropertyValues values = node.getPropertyValuesFrom(report);
            for (PropertyValue pv : values.getPropertyValues()) {
                if (pv.getValue() == null) {
                    unsatisfied.add(new UnsatisfiedProperty(node, pv.getName()));
                }
            }
        }
        return unsatisfied;
    }

    public TreeNode getNodeForSection(ExpeditedReportSection section) {
        TreeNode node = sections.get(section);
        if (node == null && log.isDebugEnabled()) {
            log.debug("No node in the expedited report tree for " + section);
        }
        return node;
    }

    public ExpeditedReportSection getSectionForNode(TreeNode node) {
        if (node == null) throw new NullPointerException("No node provided");
        if (node instanceof SectionNode) return ((SectionNode) node).getSection();
        if (node.getParent() == null) throw new CaaersSystemException(node + " doesn't belong to a section");
        return getSectionForNode(node.getParent());
    }
    
    ////// TREE CONSTRUCTION HELPERS

    private static TreeNode createPersonBlock(String person) {
        return property(
            person, StringUtils.capitalize(person) + " details",
            property("firstName", "First name"),
            property("middleName", "Middle name"),
            property("lastName", "Last name"),
            contactField(ReportPerson.EMAIL, "E-mail address"),
            contactField(ReportPerson.PHONE),
            contactField(ReportPerson.FAX)
        );
    }

    private static TreeNode contactField(String contactType) {
        return contactField(contactType, StringUtils.capitalize(contactType));
    }

    private static TreeNode contactField(
        String contactType, String displayName
    ) {
        return property("contactMechanisms[" + contactType + ']', displayName);
    }

    private static TreeNode participantMeasure(String baseName) {
        return property(baseName, StringUtils.capitalize(baseName),
            property("quantity", "Quantity"),
            property("unit", "Units")
        );
    }

    private static TreeNode dosage(String baseName, String displayName) {
        return property(baseName, displayName,
            property("amount", "Amount"),
            property("units", "Units")
            //,property("route", "Route")
        );
    }

    private static TreeNode labValue(String baseName, String displayName) {
        return property(baseName, displayName,
            property("value", "Value"),
            property("date", "Date")
        );
    }
}
