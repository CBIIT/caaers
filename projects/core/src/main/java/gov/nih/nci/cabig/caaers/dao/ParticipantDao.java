package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.dao.query.ParticipantQuery;
import gov.nih.nci.cabig.caaers.domain.DateValue;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.LoadStatus;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.SystemAssignedIdentifier;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.ctms.dao.MutableDomainObjectDao;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the Data access related operations for the Participant domain object.
 * 
 * @author Rhett Sutphin
 */
@Transactional(readOnly = true)
public class ParticipantDao extends GridIdentifiableDao<Participant> implements
                MutableDomainObjectDao<Participant> {
    // these are for getBySubnames
    private static final List<String> SUBSTRING_MATCH_PROPERTIES = Arrays.asList("firstName",
                    "lastName");

    private static final List<String> EXACT_MATCH_PROPERTIES = Arrays
                    .asList("institutionalPatientNumber");

    private static final List<String> EXACT_MATCH_UNIQUE_PROPERTIES = Arrays.asList("firstName",
                    "lastName");

    private static final List<String> EMPTY_PROPERTIES = Collections.emptyList();

    private static final String JOINS = "join o.identifiers as identifier "
                    + "join o.assignments as spa join spa.studySite as ss join ss.study as s join s.identifiers as sIdentifier ";

    /**
     * Get the Class representation of the domain object that this DAO is representing.
     * 
     * @return Class representation of the domain object that this DAO is representing.
     */
    @Override
    public Class<Participant> domainClass() {
        return Participant.class;
    }

    /**
     * Save or update the participant in the db.
     * 
     * @param The
     *                participant.
     */
    @Transactional(readOnly = false)
    public void save(final Participant participant) {
        getHibernateTemplate().saveOrUpdate(participant);
    }

    /**
     * TODO
     * 
     * @param domainObjectImportOutcome
     */
    @Transactional(readOnly = false)
    public void batchSave(
                    final List<DomainObjectImportOutcome<Participant>> domainObjectImportOutcome) {
        log.debug("Time now : " + new java.util.Date());
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        for (DomainObjectImportOutcome<Participant> outcome : domainObjectImportOutcome) {
            final Participant participant = outcome.getImportedDomainObject();
            session.merge(participant);
        }
    }

    /**
     * Get the list of all participants.
     * 
     * @return return the list of participants.
     */
    @SuppressWarnings("unchecked")
    public List<Participant> getAll() {
        return getHibernateTemplate().find("from Participant p order by p.lastName, p.firstName");
    }

    /**
     * @param subnames
     *                a set of substrings to match
     * @return a list of participants such that each entry in <code>subnames</code> is a
     *         case-insensitive substring match of the participant's name or other identifier
     */
    @SuppressWarnings("unchecked")
    public List<Participant> getBySubnames(final String[] subnames) {
        return findBySubname(subnames, SUBSTRING_MATCH_PROPERTIES, EXACT_MATCH_PROPERTIES);
    }

    /**
     * TODO
     * 
     * @param subnames
     * @return
     */
    public List<Participant> getBySubnamesJoinOnIdentifier(final String[] subnames) {
        String joins = " join o.identifiers as identifier ";
        List<String> subStringMatchProperties = Arrays.asList("o.firstName", "o.lastName",
                        "identifier.type", "identifier.value");
        return findBySubname(subnames, null, null, subStringMatchProperties, EMPTY_PROPERTIES,
                        joins);
    }

    /**
     * @param subnames
     *                a set of substrings to match
     * @return a list of participants such that each entry in <code>subnames</code> is a
     *         case-insensitive substring match of the participant's name or other identifier
     */
    @SuppressWarnings("unchecked")
    public List<Participant> getByUniqueIdentifiers(final String[] subnames) {
        return findBySubname(subnames, EMPTY_PROPERTIES, EXACT_MATCH_UNIQUE_PROPERTIES);
    }

    /**
     * Gets the participant by id. This initializes the participant and loads all the objects.
     * 
     * @param identifier
     *                the id.
     * 
     * @return the participant by id.
     */
    public Participant getByIdentifier(final Identifier identifier) {
        return findByIdentifier(identifier);
    }
    
    /**
     * This method returns a Fully loaded Participant.
     * @param identifier
     * @return
     */
    public Participant getParticipantDesignByIdentifier(final Identifier identifier){
    	Participant participant = getByIdentifier(identifier);
    	if(participant != null){
    		initialize(participant);
    	}
    	return participant;
    }

    /**
     * TODO
     * 
     * @param studyId
     * @param text
     * @return
     */
    public List<Participant> matchParticipantByStudy(final Integer studyId, final String text) {

        String joins = " join o.identifiers as identifier join o.assignments as spa join spa.studySite as ss join ss.study as s ";

        List<Object> params = new ArrayList<Object>();
        StringBuilder queryBuf = new StringBuilder(" select distinct o from ").append(
                        domainClass().getName()).append(" o ").append(joins);

        queryBuf.append(" where ");
        queryBuf.append("s.id = ?");
        params.add(studyId);

        queryBuf.append(" and ( ");
        queryBuf.append("LOWER(").append("o.firstName").append(") LIKE ?");
        params.add('%' + text.toLowerCase() + '%');

        queryBuf.append(" or ");
        queryBuf.append("LOWER(").append("identifier.type").append(") LIKE ? ");
        params.add('%' + text.toLowerCase() + '%');

        queryBuf.append(" or ");
        queryBuf.append("LOWER(").append("identifier.value").append(") LIKE ? ");
        params.add('%' + text.toLowerCase() + '%');

        queryBuf.append(" or ");
        queryBuf.append("LOWER(").append("o.lastName").append(") LIKE ? ) ");
        params.add('%' + text.toLowerCase() + '%');

        log.debug("matchParticipantByStudy : " + queryBuf.toString());
        getHibernateTemplate().setMaxResults(30);
        return getHibernateTemplate().find(queryBuf.toString(), params.toArray());
    }

    /**
     * Search for participants given search criteria.
     * 
     * @param props
     *                The search criteria.
     * @return The list of participants.
     * @throws ParseException
     */
    @SuppressWarnings( { "unchecked" })
    public List<Participant> searchParticipant(final Map props) throws ParseException {

        List<Object> params = new ArrayList<Object>();
        boolean firstClause = true;
        StringBuilder queryBuf = new StringBuilder(" select distinct o from ").append(
                        domainClass().getName()).append(" o ").append(JOINS);

        if (props.get("studyIdentifier") != null) {
            queryBuf.append(firstClause ? " where " : " and ");
            queryBuf.append("LOWER(").append("sIdentifier.value").append(") LIKE ?");
            String p = (String) props.get("studyIdentifier");
            params.add('%' + p.toLowerCase() + '%');
            firstClause = false;
        }
        if (props.get("studyShortTitle") != null) {
            queryBuf.append(firstClause ? " where " : " and ");
            queryBuf.append("LOWER(").append("s.shortTitle").append(") LIKE ?");
            String p = (String) props.get("studyShortTitle");
            params.add('%' + p.toLowerCase() + '%');
            firstClause = false;
        }
        if (props.get("participantIdentifier") != null) {
            queryBuf.append(firstClause ? " where " : " and ");
            queryBuf.append("LOWER(").append("identifier.value").append(") LIKE ?");
            String p = (String) props.get("participantIdentifier");
            params.add('%' + p.toLowerCase() + '%');
            firstClause = false;
        }
        if (props.get("participantFirstName") != null) {
            queryBuf.append(firstClause ? " where " : " and ");
            queryBuf.append("LOWER(").append("o.firstName").append(") LIKE ?");
            String p = (String) props.get("participantFirstName");
            params.add('%' + p.toLowerCase() + '%');
            firstClause = false;
        }
        if (props.get("participantLastName") != null) {
            queryBuf.append(firstClause ? " where " : " and ");
            queryBuf.append("LOWER(").append("o.lastName").append(") LIKE ?");
            String p = (String) props.get("participantLastName");
            params.add('%' + p.toLowerCase() + '%');
            firstClause = false;
        }
        if (props.get("participantEthnicity") != null) {
            queryBuf.append(firstClause ? " where " : " and ");
            queryBuf.append("LOWER(").append("o.ethnicity").append(") LIKE ?");
            String p = (String) props.get("participantEthnicity");
            params.add(p.toLowerCase());
            firstClause = false;
        }
        if (props.get("participantGender") != null) {
            queryBuf.append(firstClause ? " where " : " and ");
            queryBuf.append("LOWER(").append("o.gender").append(") LIKE ?");
            String p = (String) props.get("participantGender");
            params.add(p.toLowerCase());
            firstClause = false;
        }

        if (props.get("participantDateOfBirth") != null) {
            queryBuf.append(firstClause ? " where " : " and ");
            String p = (String) props.get("participantDateOfBirth");
            DateValue dob = stringToDateValue(p);
            queryBuf.append(" o.dateOfBirth.year").append(" = ? ");
            params.add(dob.getYear());
            if (dob.getMonth() > 0) {
                queryBuf.append(" and o.dateOfBirth.month").append(" = ? ");
                params.add(dob.getMonth());
            }
            if (dob.getDay() > 0) {
                queryBuf.append(" and o.dateOfBirth.day").append(" = ? ");
                params.add(dob.getDay());
            }
            firstClause = false;
        }

        log.debug("::: " + queryBuf.toString());
        getHibernateTemplate().setMaxResults(CaaersDao.DEFAULT_MAX_RESULTS_SIZE);
        return getHibernateTemplate().find(queryBuf.toString(), params.toArray());
    }

    /**
     * Gets the participant by id. This initialize the participant and load all the objects.
     * 
     * @param id
     *                the id
     * 
     * @return the participant by id
     */
    public Participant getParticipantById(final int id) {
        Participant participant = (Participant) getHibernateTemplate().get(domainClass(), id);
        initialize(participant);

        return participant;
    }

    /**
     * This will initialize a lazy collection, consisting of participant objects.
     * 
     * @param participant
     *                The participant object.
     */

    public Participant initialize(final Participant participant) {
        HibernateTemplate ht = getHibernateTemplate();
        ht.initialize(participant.getIdentifiers());
        ht.initialize(participant.getAssignments());
        ht.initialize(participant.getStudies());

        return participant;
    }

    /**
     * Search for participants using query.
     * 
     * @param query
     *                The query used to search for participants
     * @return The list of participants.
     */
    @SuppressWarnings("unchecked")
    public List<Participant> searchParticipant(final ParticipantQuery query) {
        String queryString = query.getQueryString();
        log.debug("::: " + queryString.toString());
        return (List<Participant>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(final Session session) throws HibernateException,
                            SQLException {
                org.hibernate.Query hiberanteQuery = session.createQuery(query.getQueryString());
                Map<String, Object> queryParameterMap = query.getParameterMap();
                for (String key : queryParameterMap.keySet()) {
                    Object value = queryParameterMap.get(key);
                    hiberanteQuery.setParameter(key, value);

                }
                return hiberanteQuery.list();
            }

        });
    }

    /**
     * This method will reassociate the domain object to hibernate session. With a lock mode none.
     * 
     * @param o -
     *                the domain object instance that is to be reassociated.
     */
    public void reassociateUsingLock(Participant o) {
        getHibernateTemplate().lock(o, LockMode.NONE);
    }

    /**
     * Delete the specified participant.
     * 
     * @param p
     *                The participant.
     */
    @Transactional(readOnly = false)
    public void delete(Participant p) {
        getHibernateTemplate().delete(p);
    }

    /*
     * @Transactional(readOnly=false) public void deleteInprogressParticipant(String mrn ){ final
     * Participant p = fetchParticipantIdAndLoadStatusByMRN(mrn); if(p == null) throw new
     * CaaersSystemException("No participants exist with the given mrn :" + mrn);
     * 
     * getHibernateTemplate().execute(new HibernateCallback(){ public Object doInHibernate(Session
     * session) throws HibernateException, SQLException { if(p.getLoadStatus() ==
     * LoadStatus.INPROGRESS.getCode()){
     * 
     * //delete identifiers of participant session.createSQLQuery("delete from identifiers where
     * participant_id = " + p.getId().toString()).executeUpdate();
     * 
     * //delete participant session.createSQLQuery("delete from participants where id = " +
     * p.getId().toString()).executeUpdate();
     *  } //delete assignment, if load_status of assignment is 0 session.createSQLQuery("delete from
     * participant_assignments where participant_id = " + p.getId().toString() ).executeUpdate();
     * return null; } });
     *  }
     * 
     * 
     * @Transactional(readOnly=false) public void commitParticipant(String mrn){ final Participant p =
     * fetchParticipantIdAndLoadStatusByMRN(mrn); if(p == null) throw new CaaersSystemException("No
     * participants exist with the given mrn :" + mrn);
     * 
     * getHibernateTemplate().execute(new HibernateCallback(){ public Object doInHibernate(Session
     * session) throws HibernateException, SQLException { //update participants
     * session.createSQLQuery("update participants set load_status = 1 where id = " +
     * p.getId().toString()).executeUpdate();
     * 
     * //update participants session.createSQLQuery("update participant_assignments set load_status =
     * 1 where participant_id = " + p.getId().toString()).executeUpdate();
     * 
     * return null; } }); }
     * 
     * public boolean isInprogressParticipantExist(String mrn){ Participant p =
     * fetchParticipantIdAndLoadStatusByMRN(mrn); if(p == null) return false; return true; // }
     * 
     * private Participant fetchParticipantIdAndLoadStatusByMRN(final String mrn){ return
     * (Participant)getHibernateTemplate().execute(new HibernateCallback(){ public Object
     * doInHibernate(Session session) throws HibernateException, SQLException { Query query =
     * session.createSQLQuery("select p.id, p.load_status from identifiers i " + " join participants
     * p on i.participant_id = p.id " + " where i.value = '" + mrn + "' and i.type='" +
     * SystemAssignedIdentifier.MRN_IDENTIFIER_TYPE + "'"); Object[] result =
     * (Object[])query.uniqueResult();
     * 
     * if(result == null) return null;
     * 
     * Participant p = new Participant(); p.setId(Integer.valueOf(result[0].toString()));
     * p.setLoadStatus(Integer.valueOf(result[1].toString())); return p; } });
     *  }
     */

}
