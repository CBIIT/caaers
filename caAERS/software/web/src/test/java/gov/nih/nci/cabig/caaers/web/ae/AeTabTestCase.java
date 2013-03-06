/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldAttributes;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.validation.ObjectError;

/**
 * @author Rhett Sutphin
 */
public abstract class AeTabTestCase extends AeWebTestCase {
    private static final Log log = LogFactory.getLog(AeTabTestCase.class);

    protected AeTab tab;
    protected StaticMessageSource messageSource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        messageSource = new StaticMessageSource();
        messageSource.addMessage("instruction_ae_modification_detected", Locale.getDefault(), "Testing");
        tab = createTab();
        tab.setMessageSource(messageSource);
        tab.setExpeditedReportTree(expeditedReportTree);
        tab.setEvaluationService(evaluationService);
    }

    protected abstract AeTab createTab();

    /**
     * Subclasses should override this to initialize all the components of the command they might
     * use. E.g., if the tab being tested generates fields for a collection, put an object in that
     * collection.
     *
     * Subclasses need not repeat things which are added as part of the minimally valid command.
     */
    protected void fillInUsedProperties(ExpeditedAdverseEventInputCommand cmd) {
    }

    public void testFieldPropertiesExist() {
    	replayMocks();
        fillInUsedProperties(command);
        assertAllFieldPropertiesExist();
    }

    @Override
    protected EditExpeditedAdverseEventCommand createCommand() {
        return createMinimallyValidMockCommand();
    }

    protected InputFieldGroup getFieldGroup(String fieldGroupName) {
        return getTab().createFieldGroups(command).get(fieldGroupName);
    }
    
    protected void assertFieldError(String fieldName, String code, String errorMsg){
    	assertEquals("Wrong number of errors for " + fieldName, 1, errors.getFieldErrorCount(fieldName));
    	 ObjectError fieldError = errors.getFieldError(fieldName);
    	 assertEquals("Wrong code for " + fieldName + " error", code, fieldError.getCode());
    	 assertEquals("Wrong default message for " + fieldName + " error", errorMsg, fieldError.getDefaultMessage());
    }
    
    
    protected void assertNoFieldError(String fieldName){
    	assertEquals("Wrong number of errors for " + fieldName, 0, errors.getFieldErrorCount(fieldName));
    }
    
    protected void assertNoFieldRequiredErrorRaised(String fieldName, String displayName) {
    	assertNoFieldError(fieldName);
    }
    
    protected void assertFieldRequiredErrorRaised(String fieldName, String displayName) {
    	assertFieldError(fieldName, "REQUIRED", "<b>Missing:</b> &quot;" + displayName + "&quot;");
    }

    protected void assertDisplayNameForFieldGroup(String expectedDisplayName, String groupName) {
        String actual = getTab().createFieldGroups(command).get(groupName).getDisplayName();
        assertEquals("Wrong display name for " + groupName, expectedDisplayName, actual);
    }

    protected void assertFieldProperties(String fieldGroupName, String... expectedProperties) {
        InputFieldGroup actualGroup = getFieldGroup(fieldGroupName);
        assertNotNull("There's no group named " + fieldGroupName, actualGroup);
        List<InputField> actualFields = actualGroup.getFields();
        assertEquals("Wrong number of fields in " + fieldGroupName, expectedProperties.length, actualFields.size());
        for (int i = 0; i < expectedProperties.length; i++) {
            assertEquals("Wrong property " + i, expectedProperties[i], actualFields.get(i).getPropertyName());
        }
    }

    protected void assertAllFieldPropertiesExist() {
        Map<String, InputFieldGroup> groups = getTab().createFieldGroups(command);
        BeanWrapper wrappedCommand = new BeanWrapperImpl(command);
        for (String name : groups.keySet()) {
            for (InputField field : groups.get(name).getFields()) {
                String msg = "The property "
                                + field.getPropertyName()
                                + " (group "
                                + name
                                + ") is not present in the command.  Either the command was not properly initialized (override fillInUsedProperties), or one of the tab's field groups is wrong.";
                try {
                    assertNotNull(msg, wrappedCommand.getPropertyType(field.getPropertyName()));
                } catch (InvalidPropertyException ipe) {
                    log.debug("Property not found exception", ipe);
                    fail(msg);
                }
            }
        }
    }

    protected void doValidate() {
        replayMocks();
        getTab().validate(command, errors);
        verifyMocks();
    }

    public AeTab getTab() {
        return tab;
    }

    protected Map<Object, Object> getActualSelectFieldOptions(String fieldGroupName, String propertyName) {
        return getActualSelectFieldOptions(getFieldGroup(fieldGroupName).getFields(), propertyName);
    }

    @SuppressWarnings( { "unchecked" })
    protected Map<Object, Object> getActualSelectFieldOptions(List<InputField> fields, String propertyName) {
        InputField field = findField(fields, propertyName);
        Map<Object, Object> options = InputFieldAttributes.getOptions(field);
        assertNotNull("Field for " + propertyName + " is not a select", options);
        return options;
    }

    protected InputField findField(List<InputField> fields, String propertyName) {
        InputField field = null;
        for (InputField candidate : fields) {
            if (candidate.getPropertyName().equals(propertyName)) {
                field = candidate;
                break;
            }
        }
        assertNotNull("No field for " + propertyName + ": " + fields, field);
        return field;
    }
}
