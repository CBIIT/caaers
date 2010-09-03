package gov.nih.nci.cabig.caaers.domain.repository;

import gov.nih.nci.cabig.caaers.CaaersUserProvisioningException;
import gov.nih.nci.cabig.caaers.dao.InvestigationalNewDrugDao;
import gov.nih.nci.cabig.caaers.dao.InvestigatorDao;
import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.ResearchStaffDao;
import gov.nih.nci.cabig.caaers.dao.SiteResearchStaffDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.StudySiteDao;
import gov.nih.nci.cabig.caaers.dao.query.AbstractQuery;
import gov.nih.nci.cabig.caaers.dao.query.StudyQuery;
import gov.nih.nci.cabig.caaers.dao.query.StudySitesQuery;
import gov.nih.nci.cabig.caaers.dao.query.ajax.AbstractAjaxableDomainObjectQuery;
import gov.nih.nci.cabig.caaers.dao.workflow.WorkflowConfigDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.INDHolder;
import gov.nih.nci.cabig.caaers.domain.InvestigationalNewDrug;
import gov.nih.nci.cabig.caaers.domain.Investigator;
import gov.nih.nci.cabig.caaers.domain.InvestigatorHeldIND;
import gov.nih.nci.cabig.caaers.domain.LocalStudy;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.OrganizationAssignedIdentifier;
import gov.nih.nci.cabig.caaers.domain.OrganizationHeldIND;
import gov.nih.nci.cabig.caaers.domain.RemoteStudy;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.domain.SiteInvestigator;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyCoordinatingCenter;
import gov.nih.nci.cabig.caaers.domain.StudyFundingSponsor;
import gov.nih.nci.cabig.caaers.domain.StudyInvestigator;
import gov.nih.nci.cabig.caaers.domain.StudyOrganization;
import gov.nih.nci.cabig.caaers.domain.StudyPersonnel;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.workflow.StudySiteWorkflowConfig;
import gov.nih.nci.cabig.caaers.domain.workflow.WorkflowConfig;
import gov.nih.nci.cabig.caaers.event.EventFactory;
import gov.nih.nci.cabig.caaers.resolver.CoppaConstants;
import gov.nih.nci.cabig.caaers.security.CaaersSecurityFacade;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author Biju Joseph
 * @author Monish Dombla
 */
@Transactional(readOnly = true)
public class StudyRepository {
	
	private static Log log = LogFactory.getLog(StudyRepository.class);
	
    private StudyDao studyDao;
    private StudySiteDao studySiteDao;
    private ResearchStaffDao researchStaffDao;
    private OrganizationDao organizationDao;
    private OrganizationRepository organizationRepository;
    private InvestigatorDao investigatorDao;
    private WorkflowConfigDao workflowConfigDao;
    private InvestigationalNewDrugDao investigationalNewDrugDao;
    private SiteResearchStaffDao siteResearchStaffDao;
    private CaaersSecurityFacade caaersSecurityFacade;
    private EventFactory eventFactory;
    
    //nci_institute_code for National Cancer Institute. 
    private static final String INSTITUTE_CODE = "NCI";


    /**
     * Search the study
     * @param query
     * @param type
     * @param text
     * @return
     */
    @Transactional(readOnly = false)
    public List<Study> search(StudyQuery query,String type, String text){
      return search(query, type, text);
    }

    /**
     * Search the study 
     * @param query
     * @param type
     * @param text
     * @param searchInCOPPA
     * @return
     */
    @SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
    public List<Study> search(StudyQuery query,String type, String text, boolean searchInCOPPA){
        if(searchInCOPPA) searchAndSaveRemoteStudies(type,text);
        List<Study> studyList = (List<Study>)studyDao.search(query);
    	if(studyList != null){
    		log.info(studyList.size() + " :::: Studies is being displayed");
    	}
        return studyList;
    }

    /**
     * Will issue a search in the local database only.
     * @param query
     * @param type
     * @param text
     * @return
     */
    public List<Object[]> search(AbstractAjaxableDomainObjectQuery query,String type,String text){
        return this.search(query, type, text, false);
    }

    /**
     * Will issue a search against the db and COPPA db.
     * @param query
     * @param type
     * @param text
     * @param searchInCOPPA - true, will invoke search against COPPA
     * @return
     */
    @Transactional(readOnly = false)
    public List<Object[]> search(AbstractAjaxableDomainObjectQuery query, String type, String text, boolean searchInCOPPA){

        if (searchInCOPPA) searchAndSaveRemoteStudies(type, text);

        //Perform normal search on caAERS DB & return results. 
    	List<Object[]> objectArray = (List<Object[]>) studyDao.search(query);
    	if(objectArray != null){
    		log.info(objectArray.size() + " :::: Studies is being displayed");
    	}
        return objectArray;
    }
    
    @Transactional(readOnly = false)
    private void searchAndSaveRemoteStudies(String type, String text) {
        try {

            if (text.indexOf("%") == -1 && StringUtils.isNotEmpty(text)) {
                Study study = new RemoteStudy();
                Organization nciOrg = organizationDao.getByNCIcode(INSTITUTE_CODE);

                //populate the critera in the Query
                if (StringUtils.isNotEmpty(text)) {

                    if (StringUtils.equals("st", type)) {
                        study.setShortTitle(text);
                    }

                    if (StringUtils.equals("idtf", type)) {
                        OrganizationAssignedIdentifier orgAssignedIdentifier = new OrganizationAssignedIdentifier();
                        orgAssignedIdentifier.setType(CoppaConstants.NCI_ASSIGNED_IDENTIFIER);
                        orgAssignedIdentifier.setValue(text.toUpperCase());
                        study.addIdentifier(orgAssignedIdentifier);
                    }

                }

                //Fetch studies from COPPA matching shortTitle or Identifier
                List<Study> remoteStudies = studyDao.getExternalStudiesByExampleFromResolver(study);

                if (remoteStudies != null & remoteStudies.size() > 0) {
                    for (Study remoteStudy : remoteStudies) {
                        remoteStudy.getNciAssignedIdentifier().setOrganization(nciOrg);
                        verifyAndSaveOrganizations(remoteStudy);
                        verifyAndSaveInvestigators(remoteStudy);
                        verifyAndSaveIND((RemoteStudy) remoteStudy);
                    }
                    //Save the studies returned from COPPA
                    saveRemoteStudies(remoteStudies);
                    log.info(remoteStudies.size() + " :::: Studies fetched from PA");
                }
            }

        } catch (Exception e) {
            log.error("Error saving Remote Studies -- " + e.getMessage());
        }
      //publish event
        eventFactory.publishEntityModifiedEvent(new LocalStudy(), false);
    }

    @Transactional(readOnly = false)
    public List<StudySite> search(StudySitesQuery query,String type,String text, boolean searchInCOPPA){
        if (searchInCOPPA) searchAndSaveRemoteStudies(type, text);
    	List<StudySite> studySites = (List<StudySite>) studySiteDao.search(query);
        return studySites;
    }


    /**
     * This method saves all the RemoteStudies provided in the list.
     * @param remoteStudies
     */
    @Transactional(readOnly = false)
	public void saveRemoteStudies(List<Study> remoteStudies) {
    	try{
    		for (Study remoteStudy : remoteStudies) {
    			if(remoteStudy != null){
    				Study studyFromDatabase = studyDao.getByExternalIdentifier(((RemoteStudy)remoteStudy).getExternalId());
    				//If studyFromDatabase is not null then it already exists as a remoteStudy
    				if (studyFromDatabase == null) {
    					//If studyFromDatabase is null then it does'nt exists as a remoteStudy, hence save it.
    					if(validateRemoteStudy((RemoteStudy)remoteStudy)){
    						save((RemoteStudy)remoteStudy);
    					}else{
    						log.info("Study with ID "+ remoteStudy.getNciAssignedIdentifier() + " was not created in caAERS. Missing Coordinating Center or Funding Sponsor");
    					}
    				}
    				studyDao.flush();
    			} else {
    				log.error("Null Remote Study in the list");
    			}
    		}
    	}catch(Exception ex){
    		log.error(ex.getMessage());
    	}
	}
    
    /**
     * This methods validates if study has Co-ordinating center & funding sponsor.
     * @param remoteStudy
     * @return
     */
    private boolean validateRemoteStudy(RemoteStudy remoteStudy){
    	if(remoteStudy.getStudyCoordinatingCenters() != null){
    		if(remoteStudy.getStudyCoordinatingCenters().size() == 0){
    			return false;
    		}
    	}else{
    		return false;
    	}
    	
    	if(remoteStudy.getStudyFundingSponsors() != null){
    		if(remoteStudy.getStudyFundingSponsors().size() == 0){
    			return false;
    		}
    	}else{
    		return false;
    	}
    	return true;
    }
    
    /**
     * This method checks if the Investigator already in caAERS. If exists it uses it else creates new investigator in caAERS
     * @param remoteStudy
     */
    @Transactional(readOnly = false)
    private void verifyAndSaveInvestigators(Study remoteStudy){
    	
		for(StudyOrganization studyOrg : remoteStudy.getStudyOrganizations()){
			for(StudyInvestigator studyInv : studyOrg.getStudyInvestigatorsInternal()){
				if(studyInv.getSiteInvestigator() != null && studyInv.getSiteInvestigator().getInvestigator() != null){
					Investigator dbInv = investigatorDao.getByExternalId(studyInv.getSiteInvestigator().getInvestigator().getExternalId());
					if(dbInv != null){
						SiteInvestigator dbSiteInvestigator = dbInv.findSiteInvestigator(studyInv.getSiteInvestigator());
						if(dbSiteInvestigator == null){
							dbInv.addSiteInvestigator(studyInv.getSiteInvestigator());
							investigatorDao.save(dbInv);
							dbSiteInvestigator = studyInv.getSiteInvestigator();
						}
						studyInv.setSiteInvestigator(dbSiteInvestigator);
					}else{
						investigatorDao.save(studyInv.getSiteInvestigator().getInvestigator());
					}
				}
			}
		}
    }

    /**
     * This method checks if the Organization in StudyOrganization is already in caAERS. If exists it uses it else creates new organization in caAERS
     * @param remoteStudy
     */
    @Transactional(readOnly = false)
    private void verifyAndSaveOrganizations(Study remoteStudy){
    	Date today = new Date();
    	long todayInMills = today.getTime();
    	
    	Map<String,Organization> orgMap = new HashMap<String,Organization>();
    	Organization dbOrg = null;
    	List<StudyOrganization> studyOrgList = remoteStudy.getStudyOrganizations();
    	
    	for(StudyOrganization studyOrg : studyOrgList){
    		if(studyOrg.getOrganization().getNciInstituteCode() == null || "".equals(studyOrg.getOrganization().getNciInstituteCode())){
    			todayInMills = todayInMills + 1;
    			studyOrg.getOrganization().setNciInstituteCode("**NULL**-" + todayInMills);
    			dbOrg = organizationDao.getByName(studyOrg.getOrganization().getName());
    		}else{
    			dbOrg = organizationDao.getByNCIcode(studyOrg.getOrganization().getNciInstituteCode());
    		}
    		if(dbOrg == null){
    			organizationRepository.createOrUpdate(studyOrg.getOrganization());
    		}else{
    			studyOrg.setOrganization(dbOrg);
    		}
    		if(studyOrg instanceof StudyFundingSponsor){
				orgMap.put(CoppaConstants.PROTOCOL_AUTHORITY_IDENTIFIER, studyOrg.getOrganization());
			}
    		if(studyOrg instanceof StudyCoordinatingCenter){
				orgMap.put(CoppaConstants.COORDINATING_CENTER_IDENTIFER, studyOrg.getOrganization());
			}
    		for(StudyInvestigator studyInvestigator : studyOrg.getStudyInvestigatorsInternal()){
    			if(studyInvestigator.getSiteInvestigator() != null){
    				studyInvestigator.getSiteInvestigator().setOrganization(studyOrg.getOrganization());
    			}
    		}
    	}
    	//Associate db org's to Identifiers.
    	for(OrganizationAssignedIdentifier identifier : remoteStudy.getOrganizationAssignedIdentifiers()){
    		if(CoppaConstants.PROTOCOL_AUTHORITY_IDENTIFIER.equals(identifier.getType())){
    			identifier.setOrganization(orgMap.get(CoppaConstants.PROTOCOL_AUTHORITY_IDENTIFIER));
    		}
    		if(CoppaConstants.COORDINATING_CENTER_IDENTIFER.equals(identifier.getType())){
    			identifier.setOrganization(orgMap.get(CoppaConstants.COORDINATING_CENTER_IDENTIFER));
    		}
		}
    	
    	if(remoteStudy.getFundingSponsorIdentifier() != null && remoteStudy.getFundingSponsorIdentifier().getValue() != null){
    		if(remoteStudy.getFundingSponsorIdentifier().getValue().indexOf("**NULL**-") != -1){
    			remoteStudy.getFundingSponsorIdentifier().setValue(remoteStudy.getCoordinatingCenterIdentifier().getValue());
    		}
    	}
    }
    
    /**
     * This method iterates the IND list in RemoteStudy and saves it in DB.
     * @param remoteStudy
     */
    @Transactional(readOnly = false)
    protected void verifyAndSaveIND(RemoteStudy remoteStudy){
    	Organization dbOrg = null;
    	Investigator dbInv = null;
    	for(InvestigationalNewDrug indInvestigationalNewDrug : remoteStudy.getInvestigationalNewDrugList()){
    		INDHolder holder = indInvestigationalNewDrug.getINDHolder() ;
    		if(holder instanceof OrganizationHeldIND){
    			dbOrg = organizationDao.getByNCIcode(((OrganizationHeldIND)holder).getOrganization().getNciInstituteCode());
    			if(dbOrg == null){
    				dbOrg = organizationDao.getByNCIcode(CoppaConstants.DUMMY_ORGANIZATION_IDENTIFIER);
    			}
    			((OrganizationHeldIND)holder).setOrganization(dbOrg);
        		investigationalNewDrugDao.save(indInvestigationalNewDrug);
    		}else if(holder instanceof InvestigatorHeldIND){
    			dbInv = investigatorDao.getByNciIdentfier(((InvestigatorHeldIND)holder).getInvestigator().getNciIdentifier());
    			if(dbInv == null){
    				dbInv = investigatorDao.getByNciIdentfier(CoppaConstants.DUMMY_INVESTIGATOR_IDENTIFIER);
    			}
    			((InvestigatorHeldIND)holder).setInvestigator(dbInv);
    			investigationalNewDrugDao.save(indInvestigationalNewDrug);
    		}
    	}
    }
    
    /**
     * Search using a sample populate Study object
     *
     * @param study the study object
     * @return List of Study objects based on the sample study object
     * @throws Exception runtime exception object
     */
    public List<Study> search(Study study) throws Exception {
        return studyDao.searchByExample(study, true);
    }

    @Transactional(readOnly = false)
    public void clearStudyPersonnel(StudyOrganization so) {
        so.getStudyPersonnels().clear();
    }

    @Transactional(readOnly = false)
    public void clearStudyInvestigators(StudyOrganization so) {
        so.getStudyInvestigators().clear();
    }

    /**
     * Will merge the study and return the merged study back. 
     * @param study
     * @return
     */
    @Transactional(readOnly=false)
    public Study merge(Study study){
    	associateSiteToWorkflowConfig(study.getStudySites());
    	return studyDao.merge(study);
    }
    
    /**
     * Saves a study object
     *
     * @param study the study object
     * @throws Exception runtime exception object
     */

    @Transactional(readOnly = false)
    public void save(Study study){
    	associateSiteToWorkflowConfig(study.getStudySites());
    	//Provision instances an Investigator or ResearchStaff has access to in CSM
    	provisionStudyTeam(study);
    	//Save the study
        studyDao.save(study);
    }
    
    /**
     * This method provision's the study team members into CSM.
     * @param study
     */
    public  void provisionStudyTeam(Study study){
    	try{
    		List<StudyOrganization> studyOrgs = study.getActiveStudyOrganizations();
    		List<StudyInvestigator> studyInvs = null;
    		List<StudyPersonnel> studyPersonnel = null;
    		for(StudyOrganization studyOrg : studyOrgs){
    			//Remove, add or update what instances an Investigator is entitled to.
    			studyInvs = studyOrg.getStudyInvestigators();
    			if(studyInvs != null){
        			for(StudyInvestigator studyInv : studyInvs){
    					caaersSecurityFacade.provisionStudies(studyInv);
        			}
    			}
    			//Remove, add or update what instances an ResearchStaff is entitled to.
    			studyPersonnel = studyOrg.getStudyPersonnels();
    			if(studyPersonnel != null){
    				for(StudyPersonnel studyPer : studyPersonnel){
						caaersSecurityFacade.provisionStudies(studyPer);
    				}
    			}
    		}
    	}catch(CaaersUserProvisioningException ex){
    		log.error("Exception while provisioning StudyTeam", ex);
    		throw ex;
    	}
    }

    @Required
    public void setStudyDao(final StudyDao studyDao) {
        this.studyDao = studyDao;
    }

    public List<Study> find(final AbstractQuery query) {
        return studyDao.find(query);
    }
    
    /**
     * @param id
     * @return
     */
    public Study getById(int id) {
    	return studyDao.getById(id);
    }
    
    //Associate the ResearchStaff to all the Studies 
    public void associateStudyPersonnel(ResearchStaff researchStaff) throws Exception{
    	//Associate to All Studies feature is discontinued hence commenting the below code.
    	/*
    	List<StudyOrganization> studyOrganizations = null;
    	StudyOrganizationsQuery studyOrgsQuery = null;
    	for(SiteResearchStaff siteResearchStaff : researchStaff.getSiteResearchStaffs()){
    		if(BooleanUtils.isTrue(siteResearchStaff.getAssociateAllStudies())){
    			studyOrgsQuery = new StudyOrganizationsQuery();
    			studyOrgsQuery.filterByOrganizationId(siteResearchStaff.getOrganization().getId());
    			studyOrganizations = studyDao.getStudyOrganizations(studyOrgsQuery);
    			for(StudyOrganization studyOrg : studyOrganizations){
    				studyOrg.syncStudyPersonnel(siteResearchStaff);
    				studyDao.save(studyOrg.getStudy());
    				studyDao.flush();
    			}
    		}
    	}
    	*/
    }
    
    @Transactional(readOnly = false)
    public void synchronizeStudyPersonnel(Study study) {
    	
    	//Associate to All Studies feature is discontinued hence commenting the below code.
    	/*
    	
        //Identify newly added StudyOrganizations to associate ResearchStaff
        //whose associateAllStudies flag is set to true.
        List<SiteResearchStaff> siteResearchStaffs = null;
        for(StudyOrganization studyOrganization : study.getStudyOrganizations()) {
        	siteResearchStaffs = siteResearchStaffDao.getOrganizationResearchStaffs(studyOrganization.getOrganization());
        	for(SiteResearchStaff siteResearchStaff : siteResearchStaffs){
        		if(BooleanUtils.isTrue(siteResearchStaff.getAssociateAllStudies())){
        			studyOrganization.syncStudyPersonnel(siteResearchStaff);
        		}
        	}
        }
        */
    }
    
    
    /**
     * This method will associate StudySites to the {@link AdverseEventReportingPeriod} and {@link ExpeditedAdverseEventReport} workflow.
     * The default assigned to {@link AdverseEventReportingPeriod} is <b>reportingperiod_coordinating_center</b>
     * The default assigned to {@link ExpeditedAdverseEventReport} is <b>expedited_domestic</b> 
     * @param sites - A list of {@link StudySite}
     */
    public void associateSiteToWorkflowConfig(List<StudySite> sites){
    	
    	if(CollectionUtils.isEmpty(sites)) return;
    	WorkflowConfig rpWorkflowConfig = null;
    	WorkflowConfig rWorkflowConfig = null;
    	
    	for(StudySite site : sites){
    		if(site.getStudySiteWorkflowConfigs().isEmpty()){
    			if(rpWorkflowConfig == null) rpWorkflowConfig = workflowConfigDao.getByWorkflowDefinitionName("reportingperiod_coordinating_center");
    			site.addStudySiteWorkflowConfig(new StudySiteWorkflowConfig("reportingPeriod", site, rpWorkflowConfig));
    			if(rWorkflowConfig == null) rWorkflowConfig =  workflowConfigDao.getByWorkflowDefinitionName("expedited_domestic");
    			site.addStudySiteWorkflowConfig(new StudySiteWorkflowConfig("report", site, rWorkflowConfig));
        	}
    	}
    	
    }

	public void setResearchStaffDao(ResearchStaffDao researchStaffDao) {
		this.researchStaffDao = researchStaffDao;
	}

	public void setOrganizationDao(OrganizationDao organizationDao) {
		this.organizationDao = organizationDao;
	}

	public void setOrganizationRepository(
			OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	public void setInvestigatorDao(InvestigatorDao investigatorDao) {
		this.investigatorDao = investigatorDao;
	}
	
	public void setWorkflowConfigDao(WorkflowConfigDao workflowConfigDao) {
		this.workflowConfigDao = workflowConfigDao;
	}

	public void setInvestigationalNewDrugDao(
			InvestigationalNewDrugDao investigationalNewDrugDao) {
		this.investigationalNewDrugDao = investigationalNewDrugDao;
	}

	public void setSiteResearchStaffDao(SiteResearchStaffDao siteResearchStaffDao) {
		this.siteResearchStaffDao = siteResearchStaffDao;
	}

	public void setCaaersSecurityFacade(CaaersSecurityFacade caaersSecurityFacade) {
		this.caaersSecurityFacade = caaersSecurityFacade;
	}

    public StudySiteDao getStudySiteDao() {
        return studySiteDao;
    }

    public void setStudySiteDao(StudySiteDao studySiteDao) {
        this.studySiteDao = studySiteDao;
    }
    
    public EventFactory getEventFactory() {
        return eventFactory;
    }

    public void setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }
}