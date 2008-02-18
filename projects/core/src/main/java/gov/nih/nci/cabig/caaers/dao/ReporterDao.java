package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.domain.Reporter;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Kulasekaran
 */
@Transactional(readOnly=true)
public class ReporterDao extends GridIdentifiableDao<Reporter> {
    /**
	 * Get the Class representation of the domain object that this DAO is
	 * representing.
	 * 
	 * @return Class representation of the domain object that this DAO is
	 *         representing.
	 */
	public Class<Reporter> domainClass() {
        return Reporter.class;
    }
	/**
	 * Save or update the reporter in the db.
	 * 
	 * @param The reporter.
	 */
    @Transactional(readOnly=false)
    public void save(Reporter reporter) {
        getHibernateTemplate().saveOrUpdate(reporter);
    }
}

