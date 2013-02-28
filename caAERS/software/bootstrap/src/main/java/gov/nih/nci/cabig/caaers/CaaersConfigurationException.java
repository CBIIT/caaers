/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers;

import gov.nih.nci.cabig.ctms.CommonsConfigurationException;

/**
 * @author Rhett Sutphin
 */
public class CaaersConfigurationException extends CommonsConfigurationException {
    public CaaersConfigurationException(String message, Throwable cause, Object... substitutions) {
        super(message, cause, substitutions);
    }

    public CaaersConfigurationException(Throwable cause) {
        super(cause);
    }

    public CaaersConfigurationException(String message, Object... substitutions) {
        super(message, substitutions);
    }
}
