package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.domain.PreExistingCondition;
import gov.nih.nci.cabig.caaers.domain.PriorTherapy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class implements the Data access related operations for the PriorTherapy domain object.
 * 
 * @author Krikor Krumlian
 */
public class PriorTherapyDao extends CaaersDao<PriorTherapy> {
    private static final List<String> SUBSTRING_MATCH_PROPERTIES = Arrays.asList("text",
                    "meddraTerm", "meddraCode");

    private static final List<String> EXACT_MATCH_PROPERTIES = Collections.emptyList();

    /**
     * Get the Class representation of the domain object that this DAO is representing.
     * 
     * @return Class representation of the domain object that this DAO is representing.
     */
    public Class<PriorTherapy> domainClass() {
        return PriorTherapy.class;
    }

    /**
     * Get the list of prior therapies matching the name fragments.
     * 
     * @param subnames
     *                the name fragments to search on.
     * @return List of matching prior therapies.
     */
    public List<PriorTherapy> getBySubnames(String[] subnames) {
        return findBySubname(subnames, SUBSTRING_MATCH_PROPERTIES, EXACT_MATCH_PROPERTIES);
    }

    /**
     * Get the list of all prior therapies.
     * 
     * @return return the list of prior therapies.
     */
    public List<PriorTherapy> getAll() {
        return getHibernateTemplate().find("from PriorTherapy");
    }
}
