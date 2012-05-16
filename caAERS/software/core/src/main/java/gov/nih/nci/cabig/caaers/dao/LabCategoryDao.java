package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.domain.LabCategory;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the Data access related operations for the LabCategory domain object.
 * 
 * @author Krikor Krumlian
 */
@Transactional(readOnly = true)
public class LabCategoryDao extends CaaersDao<LabCategory> {
    /**
     * Get the Class representation of the domain object that this DAO is representing.
     * 
     * @return Class representation of the domain object that this DAO is representing.
     */
	@Transactional(readOnly = true, propagation= Propagation.NOT_SUPPORTED)
    public Class<LabCategory> domainClass() {
        return LabCategory.class;
    }

    /**
     * Get the list of all lab categories.
     * 
     * @return return the list of lab categories.
     */
    @SuppressWarnings("unchecked")
    public List<LabCategory> getAll() {
        return getHibernateTemplate().find("from LabCategory");
    }
    
    // return unique element as name is unique across lab categories
    public LabCategory getByName(String name) {
    	Query query = getSessionFactory().getCurrentSession().createQuery("from LabCategory where name like :name");
    	return (LabCategory) query.setParameter("name", name).uniqueResult();
    }

}