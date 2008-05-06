package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.domain.StudySite;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the Data access related operations for the StudySite domain object.
 * 
 * @author Padmaja Vedula
 */
@Transactional(readOnly = true)
public class StudySiteDao extends CaaersDao<StudySite> {
    /**
     * Get the Class representation of the domain object that this DAO is representing.
     * 
     * @return Class representation of the domain object that this DAO is representing.
     */
    public Class<StudySite> domainClass() {
        return StudySite.class;
    }

    /*
     * @See ParticipantService
     */
    public StudySite matchByStudyAndOrg(final String organizationName,
                    final String identifierValue, final String identifierType) {

        String joins = " join o.study as study join study.identifiers as identifier ";

        List<Object> params = new ArrayList<Object>();
        StringBuilder queryBuf = new StringBuilder(" select distinct o from ").append(
                        domainClass().getName()).append(" o ").append(joins);

        queryBuf.append(" where ");
        queryBuf.append("LOWER(").append("identifier.value").append(") = ? ");
        params.add(identifierValue.toLowerCase());

        queryBuf.append(" and ");
        queryBuf.append("LOWER(").append("identifier.type").append(") = ? ");
        params.add(identifierType.toLowerCase());

        queryBuf.append(" and ");
        queryBuf.append("LOWER(").append("o.organization.name").append(") = ? ");
        params.add(organizationName.toLowerCase());

        log.debug("matchStudyByParticipant : " + queryBuf.toString());
        getHibernateTemplate().setMaxResults(5);
        List<StudySite> studySites = getHibernateTemplate().find(queryBuf.toString(),
                        params.toArray());
        return studySites.size() == 1 ? studySites.get(0) : null;
    }
    
    /*
     * @See ParticipantService
     */
    public StudySite findByStudyAndOrganization(final Integer studyId,
                    final Integer orgId) {

           StringBuilder queryBuf = new StringBuilder(" select distinct ss from StudySite ss " +
        		"where ss.organization.id=? and ss.study.id=? ");


        log.debug("findByStudyAndOrganization : " + queryBuf.toString());
        getHibernateTemplate().setMaxResults(5);
        List<StudySite> studySites = getHibernateTemplate().find(queryBuf.toString(),
        		new Object[]{orgId,studyId});
        
        
        return studySites.size() == 1 ? studySites.get(0) : null;
    }
}
