package gov.nih.nci.cabig.caaers.validation.fields.validators;


public class NotNullValidator extends FieldValidator<NotNullConstraint, Object> {

    @Override
    public boolean isValid(Object fieldValue) {
        return fieldValue != null;
    }

    @Override
    public String getMessagePrefix() {
        return "Missing";
    }

    public String getValidatorCSSClassName() {
        return "NOTEMPTY";
    }
}
