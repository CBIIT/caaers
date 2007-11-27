package gov.nih.nci.cabig.caaers.security;

import gov.nih.nci.cabig.caaers.user.Credential;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.security.passwordpolicy.PasswordPolicy;
import gov.nih.nci.cabig.caaers.security.passwordpolicy.validators.ValidationException;

public interface PasswordPolicyService {
    /**
     * This method will return the stored password poicy 
     * from xml configuration file
     */
    public PasswordPolicy getPasswordPolicy() throws CaaersSystemException;
    
    /**
     * This method serializes the PasswordPolicy Object to xml file
     * and updates any cached PasswordPolicy Object
     */
    public void setPasswordPolicy(PasswordPolicy passwordPolicy) throws CaaersSystemException;
    
    /**
     * This method will return a string in a readble format.
     */
    public String publishPasswordPolicy();
	
    /**
     * This method will apply to xslt to the password policy xml file and
     * return in the desired format. This can be very useful when publishing the 
     * password policy on web pages for users
     */
    public String publishPasswordPolicy(String xsltFileName);
	
    public boolean validatePasswordAgainstCreationPolicy(Credential credential) throws CaaersSystemException;
}
