package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldAttributes;
import gov.nih.nci.cabig.caaers.domain.PostAdverseEventStatus;

import java.util.Map;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * @author Rhett Sutphin
 */
public class DescriptionTab extends AeTab {
    private InputFieldGroup allFields;

    public DescriptionTab() {
        super("Event and response description", "Description", "ae/description");
        allFields = new DefaultInputFieldGroup("desc");
        String baseProp = "aeReport.responseDescription";

        InputField desc = InputFieldFactory.createTextArea(baseProp + ".eventDescription",
            "Description", false);
        InputFieldAttributes.setDetails(desc, "Describe the adverse event(s) and any action prompted by them");
        allFields.getFields().add(desc);

        Map<Object, Object> statusOpts = new LinkedHashMap<Object, Object>();
        statusOpts.put("", "Please select");
        statusOpts.putAll(InputFieldFactory.collectOptions(
            Arrays.asList(PostAdverseEventStatus.values()), null, "displayName"));
        allFields.getFields().add(InputFieldFactory.createSelectField(
            baseProp + ".presentStatus", "Present status", false,
            statusOpts));

        allFields.getFields().add(InputFieldFactory.createDateField(
            baseProp + ".recoveryDate", "Date of recovery or death",  false));
        allFields.getFields().add(InputFieldFactory.createBooleanSelectField(baseProp + ".retreated",
            "Has the particpant been re-treated?", false));
        InputField removedDateField = InputFieldFactory.createDateField(baseProp + ".dateRemovedFromProtocol",
            "Date removed from protocol", false);
        InputFieldAttributes.setDetails(removedDateField, "If the participant was removed from the protocol, enter the date here.  Otherwise, leave it blank.");
        allFields.getFields().add(removedDateField);
    }

    @Override
    public Map<String, InputFieldGroup> createFieldGroups(ExpeditedAdverseEventInputCommand command) {
        return createFieldGroupMap(Arrays.asList(allFields));
    }
}
