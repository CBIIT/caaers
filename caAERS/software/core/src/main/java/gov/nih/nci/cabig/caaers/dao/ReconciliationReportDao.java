package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.domain.ReconciliationReport;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the Data access related operations for the Reconciliation Report domain object.
 * 
 * @author Ramakrishna Gundala
 */
@Transactional(readOnly = true)
public class ReconciliationReportDao extends CaaersDao<ReconciliationReport> {
	
    @Override
    @Transactional(readOnly = true, propagation= Propagation.NOT_SUPPORTED)
    public Class<ReconciliationReport> domainClass() {
        return ReconciliationReport.class;
    }

    /**
     * Save the Reconciliation Report.
     * 
     * @param event
     *                The event to be saved.
     */
    @Transactional(readOnly = false)
    public void save(final ReconciliationReport reconciliationReport) {
        getHibernateTemplate().saveOrUpdate(reconciliationReport);
    }
    
}
