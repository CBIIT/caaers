package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.ctms.lang.ComparisonTools;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

 
/**
 * The Class SystemAssignedIdentifier.
 *
 * @author Saurabh Agrawal
 */
@Entity
@DiscriminatorValue("2")
public class SystemAssignedIdentifier extends Identifier {

    /** The Constant MRN_IDENTIFIER_TYPE. */
    public static final String MRN_IDENTIFIER_TYPE = "MRN";

    /** The system name. */
    private String systemName;

    /**
     * Returns the system name.
     * 
     * @return the system name
     */
    @Column(name = "system_name", nullable = true)
    public String getSystemName() {
        return systemName;
    }

    /**
     * Sets the system name.
     *
     * @param systemName the new system name
     */
    public void setSystemName(final String systemName) {
        this.systemName = systemName;
    }

	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.Identifier#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((systemName == null) ? 0 : systemName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.Identifier#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (!super.equals(obj)) return false;
		if (!(obj instanceof SystemAssignedIdentifier)) return false;
		
		SystemAssignedIdentifier other = (SystemAssignedIdentifier) obj;
		return ComparisonTools.nullSafeEquals(getSystemName(), other.getSystemName());
		
	}
    
    
}
