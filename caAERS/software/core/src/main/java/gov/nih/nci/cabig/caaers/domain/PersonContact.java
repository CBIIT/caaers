/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;

 
/**
 * The Class PersonContact.
 *
 * @author Krikor Krumlian
 */
@MappedSuperclass
public abstract class PersonContact extends Person {
	
	/** The address. */
	private Address address;
	
    /** The contact mechanisms. */
    private Map<String, String> contactMechanisms = new HashMap<String, String>();

    // TODO: it may be more appropriate to locate these constants somewhere else

    /** The Constant EMAIL. {@link #getContactMechanisms} key for the e-mail address */
    public static final String EMAIL = "e-mail";

    /** The Constant FAX. {@link #getContactMechanisms} key for the fax number */
    public static final String FAX = "fax";

    /** The Constant PHONE. {@link #getContactMechanisms} key for the phone number */
    public static final String PHONE = "phone";
    
    /** The Constant for Alternate Emails. {@link #getContactMechanisms} key for the Alternate emails (Reporter Only) */
    public static final String ALT_EMAIL = "alternate e-mail";

    /** The Constant DEFAULT_CONTACT_MECHANISM_KEYS. */
    public static final List<String> DEFAULT_CONTACT_MECHANISM_KEYS = Arrays.asList(EMAIL, PHONE,
                    FAX, ALT_EMAIL);

    // //// LOGIC

    /**
     * Checks if is transient.
     *
     * @return true, if is transient
     */
    @Transient
    public boolean isTransient() { // TODO: this should go in one of the base classes
        return getId() == null;
    }

    /**
     * Checks if is savable.
     *
     * @return true, if is savable
     */
    @Transient
    public boolean isSavable() {
        return getFirstName() != null && getLastName() != null;
    }

    // //// BOUND PROPERTIES

    /**
     * Gets the contact mechanisms.
     *
     * @return the contact mechanisms
     */
    @CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name = "contact_mechanisms", joinColumns = @JoinColumn(name = "person_id"))
    @MapKey(columns = @Column(name = "type"))
    @Column(name = "value")
    public Map<String, String> getContactMechanisms() {
        return contactMechanisms;
    }

    /**
     * Sets the contact mechanisms.
     *
     * @param contactMechanisms the contact mechanisms
     */
    public void setContactMechanisms(Map<String, String> contactMechanisms) {
        this.contactMechanisms = contactMechanisms;
    }
    
    /**
     * Gets the address.
     *
     * @return the address
     */
    @Embedded
	public Address getAddress() {
    	if(address == null) address = new Address();
		return address;
	}
	
	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(Address address) {
		this.address = address;
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.Person#getTitle()
	 */
	@Override
	public String getTitle() {
		return super.getTitle();
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.Person#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.Person#setPhoneNumber(java.lang.String)
	 */
	@Transient
	public void setPhoneNumber(String phoneNumber){
		contactMechanisms.put(PHONE, phoneNumber);
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.Person#getPhoneNumber()
	 */
	@Transient
	public String getPhoneNumber(){
		return contactMechanisms.get(PHONE);
	}
	
	/**
	 * Sets the fax.
	 *
	 * @param fax the new fax
	 */
	@Transient
	public void setFax(String fax){
		contactMechanisms.put(FAX, fax);
	}
	
	/**
	 * Gets the fax.
	 *
	 * @return the fax
	 */
	@Transient
	public String getFax(){
		return contactMechanisms.get(FAX);
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.Person#setEmailAddress(java.lang.String)
	 */
	@Transient
	public void setEmailAddress(String emailAddress){
		contactMechanisms.put(EMAIL, emailAddress);
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.Person#getEmailAddress()
	 */
	@Transient
	public String getEmailAddress(){
		return contactMechanisms.get(EMAIL);
	}
	
	/**
	 * Sets the Backup email addesses
	 * @param emailAddress the email addresses to set.
	 */
	@Transient
	public void setAlternateEmailAddress(String emailAddress){
		contactMechanisms.put(ALT_EMAIL, emailAddress);
	}
	
	/**
	 * Gets the backup email addresses
	 */
	@Transient
	public String getAlternateEmailAddress(){
		return contactMechanisms.get(ALT_EMAIL);
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.Person#getFirstName()
	 */
	@Override
	public String getFirstName() {
		return firstName;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.Person#getLastName()
	 */
	@Override
	public String getLastName() {
		return lastName;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.Person#getMiddleName()
	 */
	@Override
	public String getMiddleName() {
		return middleName;
	}


    /**
     * This method will copy a user, and set it as the referenced user of this reporter.
     *
     * @param person the person
     */
    public void copy(Person person){
    	if(person == null) return;

    	if(person.getFirstName() != null) this.setFirstName(person.getFirstName());
    	if(person.getLastName() != null) this.setLastName(person.getLastName());
    	if(person.getMiddleName() != null) this.setMiddleName(person.getMiddleName());
    	if(person.getTitle() != null) this.setTitle(person.getTitle());
    	if(person.getFaxNumber() != null) this.setFax(person.getFaxNumber());
    	if(person.getFaxNumber() != null) this.setFaxNumber(person.getFaxNumber());
    	if(person.getPhoneNumber() != null) this.setPhoneNumber(person.getPhoneNumber());
    	if(person.getEmailAddress() != null) this.setEmailAddress(person.getEmailAddress());
    	if(person instanceof PersonContact) {
    		final String email = ((PersonContact) person).getAlternateEmailAddress();
    		if(email != null) this.setAlternateEmailAddress(email);
    	}
        if(person.isUser()) setCaaersUser(person.getCaaersUser());

    }

    /**
     * This method will copy details from the given SiteResearchStaff into the reporter.
     *
     * @param siteResearchStaff the person
     */
    public void copy(SiteResearchStaff siteResearchStaff){
    	if(siteResearchStaff == null) return;

    	if(siteResearchStaff.getFirstName() != null) this.setFirstName(siteResearchStaff.getFirstName());
    	if(siteResearchStaff.getLastName() != null) this.setLastName(siteResearchStaff.getLastName());
    	if(siteResearchStaff.getMiddleName() != null) this.setMiddleName(siteResearchStaff.getMiddleName());
    	if(siteResearchStaff.getTitle() != null) this.setTitle(siteResearchStaff.getTitle());
    	if(siteResearchStaff.getFaxNumber() != null) this.setFax(siteResearchStaff.getFaxNumber());
    	if(siteResearchStaff.getFaxNumber() != null) this.setFaxNumber(siteResearchStaff.getFaxNumber());
    	if(siteResearchStaff.getPhoneNumber() != null) this.setPhoneNumber(siteResearchStaff.getPhoneNumber());
    	if(siteResearchStaff.getEmailAddress() != null) this.setEmailAddress(siteResearchStaff.getEmailAddress());
    	if(siteResearchStaff.getAddress() != null) this.setAddress(siteResearchStaff.getAddress());
        if(siteResearchStaff.getResearchStaff() != null && siteResearchStaff.getResearchStaff().isUser()) setCaaersUser(siteResearchStaff.getResearchStaff().getCaaersUser());
    }

    /**
     * This method will copy details from the given SiteInvestigator into the reporter.
     *
     * @param siteInvestigator the person
     */
    public void copy(SiteInvestigator siteInvestigator){
        if(siteInvestigator == null) return;

        this.setFirstName(siteInvestigator.getFirstName());
        this.setLastName(siteInvestigator.getLastName());
        this.setMiddleName(siteInvestigator.getMiddleName());
        this.setTitle(siteInvestigator.getTitle());
        this.setFax(siteInvestigator.getFaxNumber());
        this.setFaxNumber(siteInvestigator.getFaxNumber());
        this.setPhoneNumber(siteInvestigator.getPhoneNumber());
        this.setEmailAddress(siteInvestigator.getEmailAddress());
        this.setAddress(siteInvestigator.getAddress());
        if(siteInvestigator.getInvestigator() != null && siteInvestigator.getInvestigator().isUser()) setCaaersUser(siteInvestigator.getInvestigator().getCaaersUser());
    }


    /**
     * Will copy the details from the supplied user.
     * @param user - A user in caAERS.
     */
    public void copy(User user) {
        if(user == null) return;
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
    	this.setMiddleName(user.getMiddleName());
    	this.setTitle(user.getTitle());
    	this.setFax(user.getFaxNumber());
    	this.setFaxNumber(user.getFaxNumber());
    	this.setPhoneNumber(user.getPhoneNumber());
    	this.setEmailAddress(user.getEmailAddress());
        this.setCaaersUser(user);
    }
}
