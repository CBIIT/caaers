package gov.nih.nci.cabig.caaers.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Dec 17, 2007
 * Time: 5:28:28 PM
 * To change this template use File | Settings | File Templates.
 */
@Documented
@ValidatorClass(UniqueObjectInCollectionValidator.class)
@Target({METHOD, FIELD, ElementType.PARAMETER})
@Retention(RUNTIME)
public @interface UniqueObjectInCollection

{

    public abstract String message() default "Duplicate..!";

}

