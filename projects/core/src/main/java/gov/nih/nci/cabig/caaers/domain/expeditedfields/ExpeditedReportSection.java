package gov.nih.nci.cabig.caaers.domain.expeditedfields;

public enum ExpeditedReportSection {
    BASICS_SECTION("Enter AEs", true), 
    ADVERSE_EVENT_SECTION("Adverse events"), 
    REPORTER_INFO_SECTION("Reporter"), 
    CHECKPOINT_SECTION("Select Report"), 
    RADIATION_INTERVENTION_SECTION("Radiation"), 
    SURGERY_INTERVENTION_SECTION("Surgery"),
    MEDICAL_DEVICE_SECTION("Device", true), 
    DESCRIPTION_SECTION("Describe Event", true), 
    MEDICAL_INFO_SECTION("Patient Details", true), 
    TREATMENT_INFO_SECTION("Course and Agent", true), 
    LABS_SECTION("Labs", true), 
    PRIOR_THERAPIES_SECTION("Prior Therapies", true), 
    PRE_EXISTING_CONDITION_SECTION("Pre-existing Conditions", true), 
    CONCOMITANT_MEDICATION_SECTION("Conmeds"), 
    OTHER_CAUSE_SECTION("Other Causes"), 
    ATTRIBUTION_SECTION("Attribution", true), 
    ADDITIONAL_INFO_SECTION("Attachments"), 
    SUBMIT_REPORT_SECTION("Submit", true), 
    OUTCOME_SECTION("Outcome"),
    PATIENT_DETAILS_SECTION("Patient Medical History");

    private String displayName;

    private boolean associatedToBusinessRules;

    private ExpeditedReportSection(String displayName) {
        this(displayName, false);
    }

    private ExpeditedReportSection(String displayName, boolean associatedToBusinessRules) {
        this.displayName = displayName;
        this.associatedToBusinessRules = associatedToBusinessRules;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAssociatedToBusinessRules() {
        return associatedToBusinessRules;
    }

    public static ExpeditedReportSection getByDisplayName(String displayName) {
        for (ExpeditedReportSection section : values()) {
            if (section.displayName.equals(displayName)) return section;
        }
        return null;
    }
}
