package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.domain.User;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.ctms.dao.MutableDomainObjectDao;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Jared Flatow
 */
@Transactional(readOnly=true)
public class UserDao extends GridIdentifiableDao<User> implements MutableDomainObjectDao<User> {
    
    /**
	 * Get the Class representation of the domain object that this DAO is
	 * representing.
	 * 
	 * @return Class representation of the domain object that this DAO is
	 *         representing.
	 */
	@Override
    public Class<User> domainClass() {
	return User.class;
    }
	/**
	 * Save or update the user in the db.
	 * 
	 * @param The user.
	 */
    @Transactional(readOnly=false)
    public void save(final User user) {
	getHibernateTemplate().saveOrUpdate(user);
    }
    /**
     * Get the user who has specified email address.
     * @param emailAddress The email address of the user.
     * @return The user.
     */
    public User getByEmailAddress(String emailAddress) {
	List<User> results = getHibernateTemplate().find("from ResearchStaff where emailAddress= ?", emailAddress);
	return results.size() > 0 ? results.get(0) : null;
    }
}
