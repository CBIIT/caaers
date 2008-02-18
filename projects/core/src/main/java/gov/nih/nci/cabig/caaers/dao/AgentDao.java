package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.domain.Agent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author Krikor Krumlian
 */
public class AgentDao extends GridIdentifiableDao<Agent> {
    
    private static final List<String> SUBSTRING_MATCH_PROPERTIES = Arrays
			.asList("name", "nscNumber");

	private static final List<String> EXACT_MATCH_PROPERTIES = Collections
			.emptyList();
	/**
	 * Get the Class representation of the domain object that this DAO is representing.
	 * @return Class representation of the domain object that this DAO is representing.
	 **/
	public Class<Agent> domainClass() {
        return Agent.class;
    }
/**
 * Get the list of all agents.
 * @return return the list of agents.
 */
    public List<Agent> getAll() {
        return getHibernateTemplate().find("from Agent");
    }    
    /**
     * Get the list of agents matching the name fragments.
     * @param subnames the name fragments to search on.
     * @return List of matching agents.
     */
    public List<Agent> getBySubnames(String[] subnames) {
        return findBySubname(subnames,
            SUBSTRING_MATCH_PROPERTIES, EXACT_MATCH_PROPERTIES);
    }
    /**
     * Get the agent by name
     * @param name Name of the agent
     * @return Agent that matches name.
     */
    public Agent getByName(String name) {
        List<Agent> results = getHibernateTemplate().find("from Agent where name= ?", name);
        return results.size() > 0 ? results.get(0) : null;
    }
    
    /**
     * Get the agent by NSC number.
     * @param nscNumber NSC number of the agent.
     * @return Agent that matches the NSC number.
     */
    public Agent getByNscNumber(String nscNumber) {
        List<Agent> results = getHibernateTemplate().find("from Agent where nsc= ?", nscNumber);
        return results.size() > 0 ? results.get(0) : null;
    }
    
}
