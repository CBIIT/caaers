package gov.nih.nci.cabig.caaers.validation.fields.validators;



/**
 * @author Ion C. Olaru
 *
 */
public class IdentifierValidator extends FieldValidator<IdentifierConstraint, Object> {

    @Override
    public String getMessagePrefix() {
        return "Invalid identifier";
    }

    @Override
    public String getValidatorCSSClassName() {
        return "IDENTIFIER";
    }

    @Override
    public boolean isValid(Object fieldValue) {
        if (fieldValue != null) return fieldValue.toString().matches("^[a-zA-Z0-9#,*()_\\-'\":\\.{}\\[\\]]*$");
        return true;
    }

}