package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.domain.LabViewerLab;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.ctms.dao.MutableDomainObjectDao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;


/**
 * This class implements the Data access related operations for the LabViewerLab domain
 * object.
 * 
 * @author Srini Akkala
 */

@Transactional(readOnly = true)
public class LabViewerLabDao extends GridIdentifiableDao<LabViewerLab>
                implements MutableDomainObjectDao<LabViewerLab>{
	
    /**
     * Get the Class representation of the domain object that this DAO is representing.
     * 
     * @return Class representation of the domain object that this DAO is representing.
     */
    @Override
    public Class<LabViewerLab> domainClass() {
        return LabViewerLab.class;
    }
    
    /**
     * Save or update the adverse event lab viewer labs in the db.
     * 
     * @param 
     */
    @Transactional(readOnly = false)
    public void save(final LabViewerLab labViewerLab) {
        getHibernateTemplate().saveOrUpdate(labViewerLab);
    }
    
    /**
     * Get the list of LabViewerLabs based on the Assignment.
     * This is needed to rightly update the side nav bar on addition of a new lab viewer lab data .
     */
    @SuppressWarnings("unchecked")
	public List<LabViewerLab> getByAssignment(StudyParticipantAssignment assignment) {
        List<LabViewerLab> results = getHibernateTemplate().find("from LabViewerLab where assignment_id= ? order by lab_date", assignment.getId());
        return results;
    }
    
    /**
     * Delete report from db.
     * 
     * @param rs
     * The labViewerLab object to be deleted.
     */
    @Transactional(readOnly = false)
    public void delete(LabViewerLab labViewerLab) {
        getHibernateTemplate().delete(labViewerLab);
    }
    
    /**
     * Delete labViewerLab from db
     * 
     * @param id
     *                The ID of the labViewerLab
     * @return True if labViewerLab successfully deleted. False otherwise.
     */
    @Transactional(readOnly = false)
    public boolean deleteById(int id) {
        int count = getHibernateTemplate().bulkUpdate("delete LabViewerLab l where l.id=?",
                        new Object[] { id });
        return count >= 1;
    }
}