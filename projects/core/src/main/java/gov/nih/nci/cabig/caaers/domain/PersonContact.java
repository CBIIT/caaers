package gov.nih.nci.cabig.caaers.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;

/**
 * @author Krikor Krumlian
 */
@MappedSuperclass
public abstract class PersonContact extends Person {
    private Map<String, String> contactMechanisms = new HashMap<String, String>();

    // TODO: it may be more appropriate to locate these constants somewhere else

    /** {@link #getContactMechanisms} key for the e-mail address */
    public static final String EMAIL = "e-mail";
    /** {@link #getContactMechanisms} key for the fax number */
    public static final String FAX = "fax";
    /** {@link #getContactMechanisms} key for the phone number */
    public static final String PHONE = "phone";

    public static final List<String> DEFAULT_CONTACT_MECHANISM_KEYS
        = Arrays.asList(EMAIL, PHONE, FAX); 

    ////// LOGIC

    @Transient
    public boolean isTransient() { // TODO: this should go in one of the base classes
        return getId() == null;
    }

    @Transient
    public boolean isSavable() {
        return getFirstName() != null && getLastName() != null
            && getContactMechanisms().get(EMAIL) != null;
    }

    ////// BOUND PROPERTIES


    @CollectionOfElements
    @JoinTable(
        name="contact_mechanisms",
        joinColumns = @JoinColumn(name="person_id")
    )
    @MapKey(columns=@Column(name="type"))
    @Column(name="value")
    public Map<String, String> getContactMechanisms() {
        return contactMechanisms;
    }

    public void setContactMechanisms(Map<String, String> contactMechanisms) {
        this.contactMechanisms = contactMechanisms;
    }
}
