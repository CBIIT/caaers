package gov.nih.nci.cabig.caaers.validation.annotation;

import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.query.OrganizationQuery;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.repository.OrganizationRepository;
import gov.nih.nci.cabig.caaers.validation.AbstractConstraintValidator;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * @author Biju Joseph, Created on December,6th, 2007
 */
public class UniqueNciIdentifierForOrganizationValidator extends AbstractConstraintValidator<UniqueNciIdentifierForOrganization, Object> implements
                Validator<UniqueNciIdentifierForOrganization> {

    String message;

    private OrganizationDao organizationDao;
    private OrganizationRepository organizationRepository;

    public boolean validate(final Object value) {
        if (value instanceof String) {
            OrganizationQuery organizationQuery = new OrganizationQuery();
            organizationQuery.filterByNciCodeExactMatch((String) value);
            List<Organization> organizationList = organizationRepository
                            .searchOrganization(organizationQuery);
            return (organizationList == null || organizationList.isEmpty()) ? true : false;
        }
        return true;
    }

    public void initialize(UniqueNciIdentifierForOrganization uniqueNciIdentifierForResearchStaff) {
    	super.initialize(uniqueNciIdentifierForResearchStaff);
        message = uniqueNciIdentifierForResearchStaff.message();
    }

    public String message() {
        return message;
    }

    @Required
    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

	public OrganizationRepository getOrganizationRepository() {
		return organizationRepository;
	}

	@Required
	public void setOrganizationRepository(
			OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}
    
    
}