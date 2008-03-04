package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.domain.Ctc;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the Data access related operations for the Ctc domain object.
 * 
 * @author Rhett Sutphin
 */
@Transactional(readOnly = true)
public class CtcDao extends CaaersDao<Ctc> {
    /**
     * Get the Class representation of the domain object that this DAO is representing.
     * 
     * @return Class representation of the domain object that this DAO is representing.
     */
    public Class<Ctc> domainClass() {
        return Ctc.class;
    }

    /**
     * Get the list of all CTC terms.
     * 
     * @return return the list of CTC terms.
     */
    @SuppressWarnings("unchecked")
    public List<Ctc> getAll() {
        return getHibernateTemplate().find("from Ctc");
    }

    /**
     * TODO
     * 
     * @return
     */
    public Ctc getCtcV2() {
        return getById(2);
    }

    /**
     * TODO
     * 
     * @return
     */
    public Ctc getCtcaeV3() {
        return getById(3);
    }
}
