package gov.nih.nci.cabig.caaers.dao.workflow;

import java.util.List;

import gov.nih.nci.cabig.caaers.dao.CaaersDao;
import gov.nih.nci.cabig.caaers.dao.GridIdentifiableDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.workflow.WorkflowConfig;
import gov.nih.nci.cabig.ctms.dao.MutableDomainObjectDao;
import gov.nih.nci.cabig.ctms.domain.DomainObject;


/**
 * This class implements the Data access related operations for the WorkflowConfig domain object.
 * 
 * @author Sameer Sawant
 */

public class WorkflowConfigDao extends GridIdentifiableDao<WorkflowConfig>
implements MutableDomainObjectDao<WorkflowConfig>{	
	/**
     * Get the Class representation of the domain object that this DAO is representing.
     * 
     * @return Class representation of the domain object that this DAO is representing.
     */
    public Class<WorkflowConfig> domainClass() {
        return WorkflowConfig.class;
    }
    
    /**
     * Get the list of WorkflowConfigs based on the workflowDefinitionName.
     */
    @SuppressWarnings("unchecked")
	public  WorkflowConfig getByWorkflowDefinitionName(String workflowDefinitionName) {
        List<WorkflowConfig> results = getHibernateTemplate().find("from WorkflowConfig where workflowDefinitionName= ?", workflowDefinitionName);
        return (results == null || results.size() < 1) ? null : results.get(0);
    }
    
    /**
     * This method will return the workflow configured against a give domain object type.
     * @param klass
     * @return
     */
    @SuppressWarnings("unchecked")
	public WorkflowConfig getByDomainObject(Class<? extends DomainObject> klass){
    	List<WorkflowConfig> configs = getHibernateTemplate().find("from WorkflowConfig where domainObject= ?" , klass.getName());
    	if(configs != null && configs.size() > 0) return configs.get(0);
    	return null;
    }

}