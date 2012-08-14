package gov.nih.nci.cabig.caaers.validation.fields.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Will check if the number is decimal
 * @author Biju Joseph
 * @author Ion C. Olaru
 *
 */
public class DecimalValidator extends FieldValidator<DecimalConstraint, Object> {

    int integerLength = 0;
    int fractionalLength = 0;
    String pattern;

    public DecimalValidator() {
        this(0, 0);
    }

    public DecimalValidator(int integerLength, int fractionalLength) {
        //BJ : change the regEx for  	 CAAERS-3222
    	this.integerLength = integerLength;
        this.fractionalLength = fractionalLength;
        pattern = String.format("^(-){0,1}(\\d){1,%d}(\\.){0,1}(\\d){0,%d}$", integerLength, fractionalLength);
        
    }
    
    @Override
	public void initialize(DecimalConstraint constraint) {
    	super.initialize(constraint);
    	this.integerLength=constraint.integerLength();
    	this.fractionalLength=constraint.fractionalLength();
    	this.pattern=String.format(constraint.pattern(), integerLength, fractionalLength);
	}

    @Override
    public String getMessagePrefix() {
        return "Invalid";
    }

    @Override
    public String getValidatorCSSClassName() {
        return "DECIMAL";
    }

    @Override
    public boolean isValid(Object fieldValue) {
        if (fieldValue == null) return true;

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(fieldValue.toString());

        if (m.find()) {
            return true;
        }
        
        return false;
    }
}
