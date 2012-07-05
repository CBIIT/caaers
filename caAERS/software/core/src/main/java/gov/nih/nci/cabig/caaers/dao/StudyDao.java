package gov.nih.nci.cabig.caaers.dao;

import edu.nwu.bioinformatics.commons.CollectionUtils;
import gov.nih.nci.cabig.caaers.dao.query.AbstractQuery;
import gov.nih.nci.cabig.caaers.dao.query.StudyQuery;
import gov.nih.nci.cabig.caaers.dao.query.ajax.AbstractAjaxableDomainObjectQuery;
import gov.nih.nci.cabig.caaers.domain.ExpectedAECtcTerm;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.IntegrationLog;
import gov.nih.nci.cabig.caaers.domain.LocalStudy;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyAgent;
import gov.nih.nci.cabig.caaers.domain.StudyAgentINDAssociation;
import gov.nih.nci.cabig.caaers.domain.StudyDevice;
import gov.nih.nci.cabig.caaers.domain.StudyDeviceINDAssociation;
import gov.nih.nci.cabig.caaers.domain.StudyOrganization;
import gov.nih.nci.cabig.caaers.domain.StudyPersonnel;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.Term;
import gov.nih.nci.cabig.caaers.domain.TreatmentAssignment;
import gov.nih.nci.cabig.ctms.dao.MutableDomainObjectDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.semanticbits.coppa.infrastructure.RemoteSession;

/**
 * This class implements the Data access related operations for the Study domain object.
 *
 * @author Sujith Vellat Thayyilthodi
 * @author Rhett Sutphin
 * @author Priyatam
 * @author <a href="mailto:biju.joseph@semanticbits.com">Biju Joseph</a>
 */
@Transactional(readOnly = true)
public class StudyDao extends GridIdentifiableDao<Study> implements MutableDomainObjectDao<Study> {

    private static final List<String> SUBSTRING_MATCH_PROPERTIES = Arrays.asList("shortTitle", "longTitle");
    private static final List<String> EXACT_MATCH_PROPERTIES = Collections.emptyList();
    private static final List<String> EXACT_MATCH_UNIQUE_PROPERTIES = Arrays.asList("longTitle");
    private static final List<String> EMPTY_PROPERTIES = Collections.emptyList();
    private static final List<String> EXACT_MATCH_TITLE_PROPERTIES = Arrays.asList("shortTitle");

    private static final String JOINS = "join o.identifiers as identifier " + "join o.studyOrganizations as ss left outer join ss.studyParticipantAssignments as spa left outer join spa.participant as p left outer join p.identifiers as pIdentifier";
    private static final String QUERY_BY_SHORT_TITLE = "select s from " + Study.class.getName() + " s where shortTitle = :st";
    
    private static final String DUP_PRT_ASSIGNMENT_IDS_IN_STUDY_HQL = "select count(*) from StudyParticipantAssignment spa where spa.studySite.id in (select " +
    		" ss.id from StudySite ss where ss.study.id = :studyId) and spa.studySubjectIdentifier = :identifier";

    private RemoteSession remoteSession;
    
    /**
     * Get the Class representation of the domain object that this DAO is
     * representing.
     *
     * @return Class representation of the domain object that this DAO is
     *         representing.
     */
    @Override
    @Transactional(readOnly = true, propagation= Propagation.NOT_SUPPORTED)
    public Class<Study> domainClass() {
        return Study.class;
    }

	public List<? extends Object> search(final StudyQuery query) {
		return super.search(query);
	}

    @Override
	public List<Object[]> search(final AbstractAjaxableDomainObjectQuery query) {
		return super.search(query);
	}
    /**
     * Get the list of all studies.
     *
     * @return return the list of studies.
     */
    @SuppressWarnings("unchecked")
    public List<Study> getAllStudies() {
        return getHibernateTemplate().find("from Study");
    }


    /**
     * //TODO - Refactor this code with Hibernate Detached objects !!!
     * <p/>
     * This is a hack to load all collection objects in memory. Useful for editing a Study when you know you will be needing all collections
     * To avoid Lazy loading Exception by Hibernate, a call to .size() is done for each collection
     *
     * @param id
     * @return Fully loaded Study
     */
    public Study getStudyDesignById(final int id) {
        Study study = (Study) getHibernateTemplate().get(domainClass(), id);
        initialize(study);
        return study;
    }


    /**
     * //TODO - Refactor this code with Hibernate Detached objects !!!
     * <p/>
     * This is a hack to load all collection objects in memory. Useful for editing a Study when you know you will be needing all collections
     * To avoid Lazy loading Exception by Hibernate, a call to .size() is done for each collection
     *
     * @param Identifier
     * @return Fully loaded Study
     */
    public Study getStudyDesignByIdentifier(final Identifier identifier) {
        Study study = getByIdentifier(identifier);
        if (study != null) {

            initialize(study);
        }
        return study;
    }

    /**
     * This will initialize a lazy collection, consisting of study objects.
     *
     * @param study The study to be initialized.
     */

    public Study initialize(final Study study) {
        HibernateTemplate ht = getHibernateTemplate();
        ht.initialize(study.getIdentifiers());
        ht.initialize(study.getStudyOrganizations());

        if(study.getAeTerminology() != null && study.getAeTerminology().getTerm() != null ) {
            if(study.getAeTerminology().getTerm().equals(Term.CTC)) {
                ht.initialize(study.getAeTerminology().getCtcVersion().getCategories());
            }
        }
        
        for (StudyOrganization studyOrg : study.getStudyOrganizations()) {
            if (studyOrg == null) {
                continue;
            }
            ht.initialize(studyOrg.getStudyInvestigatorsInternal());
            ht.initialize(studyOrg.getStudyPersonnelsInternal());
            for (StudyPersonnel sp : studyOrg.getStudyPersonnels()) {
            	if(sp.getSiteResearchStaff() != null){
            		ht.initialize(sp.getSiteResearchStaff());
                    ht.initialize(sp.getSiteResearchStaff().getSiteResearchStaffRoles());
            	}
            }
            if(studyOrg instanceof StudySite)
                ht.initialize(((StudySite)studyOrg).getStudySiteWorkflowConfigs());
        }
        ht.initialize(study.getStudyConditions());
        ht.initialize(study.getMeddraStudyDiseases());
        ht.initialize(study.getCtepStudyDiseases());
        ht.initialize(study.getStudyAgentsInternal());
        ht.initialize(study.getTreatmentAssignmentsInternal());
        ht.initialize(study.getReportFormats());
        ht.initialize(study.getEpochs());
        ht.initialize(study.getExpectedAEMeddraLowLevelTerms());
        ht.initialize(study.getExpectedAECtcTerms());
        ht.initialize(study.getCtcCategories());
        ht.initialize(study.getOtherInterventions());
        ht.initialize(study.getStudyDevices());
        
        for (StudyDevice sd : study.getStudyDevices()) {
            ht.initialize(sd.getStudyDeviceINDAssociationsInternal());
            for(StudyDeviceINDAssociation saa : sd.getStudyDeviceINDAssociationsInternal()){
            	ht.initialize(saa.getInvestigationalNewDrug());
            }
        }
        
        for (ExpectedAECtcTerm sctct: study.getExpectedAECtcTerms()) {
            if (sctct.isOtherRequired()) {
                if (sctct.getOtherMeddraTerm() != null)
                    ht.initialize(sctct.getOtherMeddraTerm().getMeddraTerm());
  //                ht.initialize(sctct.getOtherMeddraTerm());
            }
        }
        
        for (StudyAgent sa : study.getStudyAgents()) {
            ht.initialize(sa.getStudyAgentINDAssociationsInternal());
            for(StudyAgentINDAssociation saa : sa.getStudyAgentINDAssociationsInternal()){
            	ht.initialize(saa.getInvestigationalNewDrug());
            }
        }

        for (TreatmentAssignment ta : study.getTreatmentAssignments()) {
            ht.initialize(ta.getTreatmentAssignmentStudyInterventions());
            log.debug("Treatment Assignments Study Interventions initialized.");
        }

        return study;
    }

    /**
     * Save or update the study in the db.
     *
     * @param study.
     */
    @Transactional(readOnly = false)
    public void save(final Study study) {
        getHibernateTemplate().saveOrUpdate(study);
    }

    /**
     * Save or update the study in the db.
     * This method is called by StudyParticipantAssignmentMigrator , this method is create for securtuty policy.
     * subject coordinator should be able to add study site while creating the subject. This method is called and grated permissions on to subject cordinator.
     *
     * @param study.
     */
    @Transactional(readOnly = false)
    public void updateStudyForServiceUseOnly(final Study study) {
        getHibernateTemplate().saveOrUpdate(study);
    }
    
    /**
     * Get the list of studies matching the name fragments.
     *
     * @param subnames the name fragments to search on.
     * @return List of matching studies.
     */
    public List<Study> getBySubnames(final String[] subnames) {
        return findBySubname(subnames, null, null, SUBSTRING_MATCH_PROPERTIES, EXACT_MATCH_PROPERTIES);
    }


    /**
     * Gets the study by id. This initializes the study and loads all
     * the objects.
     *
     * @param id the id.
     * @return the study by id.
     */
    public Study getByIdentifier(final Identifier identifier) {
        return findByIdentifier(identifier);
    }

    /**
     * This will do an exact match on the <code>shortTitle</code>, and will return the first available Study. Note:- Biz rule should be
     * made that short title is unique.
     */
    public Study getByShortTitle(final String shortTitle) {
        List<Study> studies = findBySubname(new String[]{shortTitle}, null, null, null, EXACT_MATCH_TITLE_PROPERTIES);
        if (studies != null && studies.size() > 0) {
            return studies.get(0);
        }
        return null;
    }

    /**
     * TODO kkk
     *
     * @param study
     * @param isWildCard
     * @return
     */
    @Override
    // TODO - Need to refactor the below into CaaersDao along with identifiers
    public List<Study> searchByExample(final Study study, final boolean isWildCard) {
        Example example = Example.create(study).excludeZeroes().ignoreCase();
        Criteria studyCriteria = getSession().createCriteria(LocalStudy.class);
        studyCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (isWildCard) {
            example.excludeProperty("multiInstitutionIndicator").enableLike(MatchMode.ANYWHERE);
            studyCriteria.add(example);
            if (study.getIdentifiers().size() > 0) {
                studyCriteria.createCriteria("identifiers").add(Restrictions.ilike("value", study.getIdentifiers().get(0).getValue() + "%"));
            }
            if (study.getStudyOrganizations().size() > 0) {
                studyCriteria.createCriteria("studyOrganizations").add(Restrictions.eq("organization.id", study.getStudyOrganizations().get(0).getOrganization().getId()));
            }
            return studyCriteria.list();
        }
        return studyCriteria.add(example).list();
    }

    /**
     * TODO kkk
     *
     * @param query
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public List<Study> find(final AbstractQuery query) {
        String queryString = query.getQueryString();
        log.debug("::: " + queryString.toString());
        return (List<Study>) super.search(query);
    }
    
    public List<Study> find(final AbstractQuery query, final Integer firstResult, final Integer maxResults) {
        String queryString = query.getQueryString();
        log.debug(">>> " + queryString.toString());
        return (List<Study>) super.search(query, firstResult, maxResults);
    }

    /**
     * This method return all the StudyOrganiations for a given Organization ID.
     * @param query
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<StudyOrganization> getStudyOrganizations(final AbstractQuery query){
    	String queryString = query.getQueryString();
    	log.debug("::: " + queryString.toString());
    	
    	return (List<StudyOrganization>) super.search(query);
    }
    
	/**Gets by the unique Identifier
	 * @param externalId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Study getByExternalIdentifier(String externalId) {
		if("".equals(externalId)){
			return null;
		}
		return CollectionUtils.firstElement((List<Study>) getHibernateTemplate()
					.find("from RemoteStudy s where s.externalId = ?", externalId));
	}
    
	
    /**
     * 
     * @param exampleStudy
     * @return
     */
    @Transactional(readOnly = false)
	public List<Study> getExternalStudiesByExampleFromResolver(Study exampleStudy) {
		List<Object> objectList = new ArrayList<Object>();
		try {
			objectList = remoteSession.find(exampleStudy);
		} catch (Exception e) {
			log.warn("Error while invoking COPPA",e);
		}
		List<Study> studyList = new ArrayList<Study>();

		for (Object object : objectList) {
			studyList.add((Study) object);
		}
		return studyList;
	}
	

    /**
     * Delete the study.
     *
     * @param study The study to be deleted.
     */
    @Transactional(readOnly = false)
    public void delete(Study study) {
        getHibernateTemplate().delete(study);
    }
    
    /**
     * This utility method is used to lock the study. 
     * @param studyOrg
     */
    public void reassociateStudyOrganizations(List<StudyOrganization> studyOrgs){
    	for(StudyOrganization studyOrg : studyOrgs){
    		getHibernateTemplate().lock(studyOrg, LockMode.NONE);
    	}
    	
    }

	public void setRemoteSession(RemoteSession remoteSession) {
		this.remoteSession = remoteSession;
	}

    @Transactional(readOnly = false)
    public void deleteAllExpectedTerms() {
        final String HQL = "delete from AbstractExpectedAE";
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(final Session session) throws HibernateException, SQLException {
                Query hQuery = session.createQuery(HQL);
                hQuery.executeUpdate();
                return null;
            }
        });
    }
    
    // repitionCount - number of repetitions of study subject Id that has to be checked
    public Long getNumberOfStudySubjectsInStudyWithGivenAssignmentIdentifier(Study study, String studySubjectIdentifier){
    	if(study == null || study.getId() == null ) return 0L;
    	if(StringUtils.isBlank(studySubjectIdentifier)) return 0L;
    	Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(DUP_PRT_ASSIGNMENT_IDS_IN_STUDY_HQL);
    	query.setParameter("studyId", study.getId());
    	query.setParameter("identifier", studySubjectIdentifier);
    	
    	Long count = (Long) query.uniqueResult();
    	return count;
    }
    
    public boolean hasLogDetails(IntegrationLog il){
    	if (il == null || il.getCorrelationId() == null) {
    		return false;
    	}
    	String queryString = "from IntegrationLog il where il.correlationId = :correlationId and il.integrationLogDetails is not empty";
    	Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(queryString);
    	query.setParameter("correlationId", il.getCorrelationId());
    	IntegrationLog log = (IntegrationLog)query.uniqueResult();
    	if (log != null){
    		return true;
    	}
    	return false;
    }
    
    
}
