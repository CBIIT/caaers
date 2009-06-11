package gov.nih.nci.cabig.caaers.domain;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("LOCAL")
public class LocalResearchStaff extends ResearchStaff{
    @Override
    public String getFirstName() {
        return firstName;
    }
    
    @Override
    public String getLastName() {
        return lastName;
    }
    
    @Override
    public String getMiddleName() {
        return middleName;
    }
    
    @Override
    public String getNciIdentifier() {
        return nciIdentifier;
    }
    
	@Transient
	public String getExternalId() {
		return externalId;
	}
	
}
