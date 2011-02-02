package gov.nih.nci.cabig.caaers.domain.workflow;

import gov.nih.nci.cabig.caaers.domain.PersonRole;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

 
/**
 * Assignee of this type will be associated to a Role.
 * @author Biju Joseph
 *
 */
@Entity
@DiscriminatorValue("r") 
public class RoleAssignee extends Assignee {
	
	/** The user role. */
	private PersonRole userRole;
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.workflow.Assignee#isRole()
	 */
	@Override
	@Transient
	public boolean isRole() {
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.workflow.Assignee#isUser()
	 */
	@Override
	@Transient
	public boolean isUser() {
		return false;
	}
	
	/**
	 * Gets the user role.
	 *
	 * @return the user role
	 */
	@Type(type = "personRoleType")
	@Column(name = "user_role_id")
	public PersonRole getUserRole() {
		return userRole;
	}
	
	/**
	 * Sets the user role.
	 *
	 * @param userRole the new user role
	 */
	public void setUserRole(PersonRole userRole) {
		this.userRole = userRole;
	}
	
}
