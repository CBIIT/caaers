package gov.nih.nci.cabig.caaers.web.ae;

import org.springframework.beans.BeanWrapper;
import org.springframework.validation.Errors;

import java.util.Map;
import java.util.ListIterator;

import gov.nih.nci.cabig.caaers.domain.AdverseEventPreExistingCond;
import gov.nih.nci.cabig.caaers.web.fields.DefaultTextField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.AutocompleterField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.fields.RepeatingFieldGroupFactory;

/**
 * @author Krikor Krumlian
 */
public class PreExistingConditionsTab extends AeTab {
    private RepeatingFieldGroupFactory fieldFactory;

    public PreExistingConditionsTab() {
        super("Pre-Existing Conditions", "Pre-Existing Conditions", "ae/preExistingConds");
        fieldFactory = new RepeatingFieldGroupFactory("conmed", "aeReport.adverseEventPreExistingConds");
        fieldFactory.setDisplayNameCreator(new RepeatingFieldGroupFactory.DisplayNameCreator() {
            public String createDisplayName(int index) {
                return "Pre-Existing Condition " + (index + 1);
            }
        });
        fieldFactory.addField(new AutocompleterField("preExistingCondition", "Pre-Existing condition", false));
        fieldFactory.addField(new DefaultTextField("other", "Other", false));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, InputFieldGroup> createFieldGroups(ExpeditedAdverseEventInputCommand command) {
        InputFieldGroupMap groups = new InputFieldGroupMap();
        groups.addRepeatingFieldGroupFactory(fieldFactory, command.getAeReport().getAdverseEventPreExistingConds().size());
        return groups;
    }

    @Override
    public boolean isAllowDirtyForward() {
        return false;
    }

    @Override
    protected void validate(
        ExpeditedAdverseEventInputCommand command, BeanWrapper commandBean,
        Map<String, InputFieldGroup> fieldGroups, Errors errors
    ) {
        for (ListIterator<AdverseEventPreExistingCond> it = command.getAeReport().getAdverseEventPreExistingConds().listIterator(); it.hasNext();) {
        	AdverseEventPreExistingCond preCondition = it.next();
            validateAdverseEventPreExistingCond(preCondition, it.previousIndex(), errors);
        }
    }

    private void validateAdverseEventPreExistingCond(AdverseEventPreExistingCond preCondition, int index, Errors errors) {
        if (preCondition.getPreExistingCondition() == null && preCondition.getOther() == null) {
            errors.rejectValue(
                String.format("aeReport.adverseEventPreExistingConds[%d]", index),
                "REQUIRED",
                "Either a known pre Existing Condition or other is required"
            );
        }
    }
}
