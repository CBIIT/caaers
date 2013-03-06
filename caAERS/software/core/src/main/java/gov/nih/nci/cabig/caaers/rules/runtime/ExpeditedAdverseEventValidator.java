/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.rules.runtime;

import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.validation.ValidationErrors;

public abstract class ExpeditedAdverseEventValidator {
    public static boolean SUCCESS = false;

    public static boolean FAIL = true;

    public abstract boolean validate(ExpeditedAdverseEventReport aeReport, ValidationErrors errors);
}
