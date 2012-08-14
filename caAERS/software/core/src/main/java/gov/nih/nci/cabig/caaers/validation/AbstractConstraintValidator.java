package gov.nih.nci.cabig.caaers.validation;

import gov.nih.nci.cabig.caaers.validation.annotation.Validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

public abstract class AbstractConstraintValidator<A extends Annotation, T> implements
		ConstraintValidator<A, T>, Validator<A> {
	
	private CaaersFieldConfigurationManager caaersFieldConfigurationManager;
	
	String fieldPath = null;
	
	Class<?>[] groups = { };
	
	A constraint; 
		
	public void initialize(A constraint) {
		this.constraint = constraint;
		try {
			Method fpmethod = constraint.getClass().getDeclaredMethod("fieldPath", null);
			fieldPath = (String) fpmethod.invoke(constraint, null);
			
			Method gmethod = constraint.getClass().getDeclaredMethod("groups", null);
			groups = (Class[]) gmethod.invoke(constraint, null);
		} catch (Exception e) {
			//do nothing
		}
	}

	public boolean isValid(T object, ConstraintValidatorContext context) {
		//apply restriction on validation based on field configuration manager
		if(!"".equals(fieldPath) && groups.length>0) {
			//TODO: Just looking at the first group value for now. Need to improve to handle multiple groups.
			GroupEnum ge = getValueOf(groups[0].getSimpleName());
			if(ge != null && caaersFieldConfigurationManager.isFieldMandatory(ge.getTabName(), fieldPath)) {
				return validate(object);
			} else {
				return true;
			}
		}
		return validate(object);
	}
	
	public GroupEnum getValueOf(String name) {
		try {
			return GroupEnum.valueOf(name);
		} catch (IllegalArgumentException e) {
			// do nothing;
		}
		return null;
	}

	public CaaersFieldConfigurationManager getCaaersFieldConfigurationManager() {
		return caaersFieldConfigurationManager;
	}
	
	@Autowired
	@Required
	public void setCaaersFieldConfigurationManager(CaaersFieldConfigurationManager caaersFieldConfigurationManager) {
		this.caaersFieldConfigurationManager = caaersFieldConfigurationManager;
	}
	
}
