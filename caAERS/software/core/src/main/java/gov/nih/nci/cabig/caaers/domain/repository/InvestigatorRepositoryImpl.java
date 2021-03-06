/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.repository;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.dao.InvestigatorConverterDao;
import gov.nih.nci.cabig.caaers.dao.InvestigatorDao;
import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.SiteInvestigatorDao;
import gov.nih.nci.cabig.caaers.dao.query.InvestigatorQuery;
import gov.nih.nci.cabig.caaers.domain.ConverterInvestigator;
import gov.nih.nci.cabig.caaers.domain.Investigator;
import gov.nih.nci.cabig.caaers.domain.LocalInvestigator;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.RemoteInvestigator;
import gov.nih.nci.cabig.caaers.domain.SiteInvestigator;
import gov.nih.nci.cabig.caaers.event.EventFactory;
import gov.nih.nci.cabig.caaers.security.CaaersSecurityFacade;
import gov.nih.nci.cabig.caaers.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
 

/**
 * This is the repository class for managing investigators.
 *
 * @author Biju Joseph
 */
@Transactional(readOnly = false)
public class InvestigatorRepositoryImpl implements InvestigatorRepository {
	
	/** The investigator dao. */
	private InvestigatorDao investigatorDao;
	
	/** The site investigator dao. */
	private SiteInvestigatorDao siteInvestigatorDao;
	
	/** The investigator converter dao. */
	private InvestigatorConverterDao investigatorConverterDao;
	
	/** The caaers security facade. */
	private CaaersSecurityFacade caaersSecurityFacade;
	
	/** The authentication mode. */
	private String authenticationMode;
	
	/** The organization dao. */
	private OrganizationDao organizationDao;
	
	/** The organization repository. */
	private OrganizationRepository organizationRepository;
    
    /** The event factory. */
    private EventFactory eventFactory;
        
	
	/** The Constant logger. */
	private static final Log logger = LogFactory.getLog(InvestigatorRepositoryImpl.class); 
	 
	 /**
 	 * Creates a new investigator in the system
 	 * OR
 	 * Updates and existing investigator details
 	 * 
 	 * Will also update the corresponding features associated to the CSM groups.
 	 *
 	 * @param investigator the investigator
 	 * @param changeURL the change url
 	 * @return the investigator
 	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, noRollbackFor = MailException.class)
	public Investigator save(Investigator investigator, String changeURL) {
		//save the details in caAERS
		try{
			investigator = investigatorDao.merge(investigator);
		}catch(Exception e){
    		logger.error("error while saving investigator", e);
			throw new CaaersSystemException("Failed to save investigator", e);
		}
		return investigator;
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.repository.InvestigatorRepository#searchInvestigator(gov.nih.nci.cabig.caaers.dao.query.InvestigatorQuery, java.util.HashMap)
	 */
	@SuppressWarnings("unchecked")
	public List<Investigator> searchInvestigator(final InvestigatorQuery query,HashMap searchCriteriaMap){
		
        String firstName = (String)searchCriteriaMap.get("firstName");
        String lastName = (String)searchCriteriaMap.get("lastName");
        String nciIdentifier = (String)searchCriteriaMap.get("personIdentifier");
        String organization = (String)searchCriteriaMap.get("organization");

        if(StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName) && StringUtils.isEmpty(nciIdentifier) && StringUtils.isEmpty(organization)){
        	return investigatorDao.getLocalInvestigator(query);
        }
        
    	if(StringUtils.isNotEmpty(firstName) && firstName.indexOf("%") != -1){
    		return investigatorDao.getLocalInvestigator(query);
    	}
    	if(StringUtils.isNotEmpty(lastName) && lastName.indexOf("%") != -1){
    		return investigatorDao.getLocalInvestigator(query);
    	}
    	if(StringUtils.isNotEmpty(nciIdentifier) && nciIdentifier.indexOf("%") != -1){
    		return investigatorDao.getLocalInvestigator(query);
    	}
		
        Map<String, Object> queryParameterMap = query.getParameterMap();
        for (String key : queryParameterMap.keySet()) {
			if (key.equals("loginId")) {
				return investigatorDao.getLocalInvestigator(query);
			}
        }
        
		//Populate searchCriteria 
		RemoteInvestigator searchCriteria = new RemoteInvestigator(); 
		searchCriteria.setFirstName(firstName);
		searchCriteria.setLastName(lastName);
		searchCriteria.setNciIdentifier(nciIdentifier);
		if(StringUtils.isNotEmpty(organization)){
			SiteInvestigator si = new SiteInvestigator();
			si.setOrganization(this.organizationDao.getById(Integer.parseInt(organization)));
			searchCriteria.addSiteInvestigator(si);
		}
		List<Investigator> remoteInvestigators = null;
		try{
			remoteInvestigators = investigatorDao.getRemoteInvestigators(searchCriteria);
		}catch(Exception e){
			logger.warn("Error searching Investiagators from PO -- " + e.getMessage());
		}
		if(CollectionUtils.isNotEmpty(remoteInvestigators)){
			saveRemoteInvestigators(remoteInvestigators);
			logger.info(remoteInvestigators.size() +" :::: Investigators fetched from PO");
		}
		List<Investigator> invList = investigatorDao.getLocalInvestigator(query);
		if(invList != null){
			logger.info(invList.size() +" :::: Investigators is being displayed");
		}
		return invList;
	}
	
	/**
	 * Save remote investigators.
	 *
	 * @param remoteList the remote list
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, noRollbackFor = MailException.class)
	private void saveRemoteInvestigators(List<Investigator> remoteList){
		for (Investigator remoteInvestigator:remoteList) {

			Investigator inv = investigatorDao.getByExternalId(remoteInvestigator.getExternalId());
    		if (inv == null ) {
    			try {
    				List<SiteInvestigator> siList = remoteInvestigator.getSiteInvestigators();
    				List<SiteInvestigator> siDBList = new ArrayList<SiteInvestigator>();
    				for (SiteInvestigator si:siList) {
    					Organization remoteOrganization = si.getOrganization();
    					Organization organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
    	    			if (organization == null) {
    	    				organizationRepository.create(remoteOrganization);
    	    				organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
    	    			} 
    	    			SiteInvestigator dbSI = new SiteInvestigator();
    	    			dbSI.setOrganization(organization);
    	    			dbSI.setStartDate(DateUtils.today());
    	    			dbSI.setInvestigator(remoteInvestigator);
    	    			siDBList.add(dbSI);
    	    			
    				}
    				remoteInvestigator.getSiteInvestigators().clear();
    				remoteInvestigator.setSiteInvestigators(siDBList);
    				
    				save(remoteInvestigator,"URL");
    				remoteInvestigator = investigatorDao.getByExternalId(remoteInvestigator.getExternalId());
    			} catch (MailException e) {
    				logger.error("Mail send exception --" + e.getMessage());
    			}
        	} else {
        		try {
    				List<SiteInvestigator> siList = remoteInvestigator.getSiteInvestigators();
    				for (SiteInvestigator si:siList) {
    					Organization remoteOrganization = si.getOrganization();
    					Organization organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
    	    			if (organization == null) {
    	    				organizationRepository.create(remoteOrganization);
    	    				organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
    	    			} 
    	    			SiteInvestigator siteInvestigator = new SiteInvestigator();
    	    			siteInvestigator.setOrganization(organization);
    	    			siteInvestigator.setStartDate(DateUtils.today());
    	    			siteInvestigator.setInvestigator(remoteInvestigator);
    	    			List<SiteInvestigator> siDBList = inv.getSiteInvestigators();
    	    			boolean exists = false;
    	    			for (SiteInvestigator sid:siDBList){
    	    				if (sid.getOrganization().getNciInstituteCode().equals(organization.getNciInstituteCode())) {
    	    					exists = true;
    	    					break;
    	    				}
    	    			}
    	    			if (!exists) {
    	    				inv.addSiteInvestigator(siteInvestigator);
    	    			}
    				}
    				save(inv,"URL");
    			} catch (MailException e) {
    				logger.error("Mail send exception --" + e.getMessage());
    			}
        	}
    	}

        eventFactory.publishEntityModifiedEvent(new LocalInvestigator(), false);

	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.repository.InvestigatorRepository#searchInvestigator(gov.nih.nci.cabig.caaers.dao.query.InvestigatorQuery)
	 */
	public List<Investigator> searchInvestigator(final InvestigatorQuery query){
		List<Investigator> localInvestigators = investigatorDao.getLocalInvestigator(query);
		return localInvestigators;
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.repository.InvestigatorRepository#getBySubnames(java.lang.String[], int)
	 */
	public List<SiteInvestigator> getBySubnames(String[] subnames, int siteId) {
		List<SiteInvestigator> siteInvestigators = siteInvestigatorDao.getBySubnames(subnames,siteId);
		return siteInvestigators;
	}
	
	/**
	 * Gets the by id.
	 *
	 * @param id the id
	 * @return the by id
	 */
	public Investigator getById(int id) {
		return getInvestigatorDao().getInvestigatorById(id);
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.cabig.caaers.domain.repository.InvestigatorRepository#convertToRemote(gov.nih.nci.cabig.caaers.domain.Investigator, gov.nih.nci.cabig.caaers.domain.Investigator)
	 */
	@Transactional(readOnly = false)
    public void convertToRemote(Investigator localInvestigator, Investigator remoteInvestigator){
    	ConverterInvestigator conInv = investigatorConverterDao.getById(localInvestigator.getId());
    	conInv.setType("REMOTE");
    	conInv.setExternalId(remoteInvestigator.getExternalId());
    	conInv.setFirstName(remoteInvestigator.getFirstName());
    	conInv.setLastName(remoteInvestigator.getLastName());
    	conInv.setMiddleName(remoteInvestigator.getMiddleName());
    	conInv.setPhoneNumber(remoteInvestigator.getPhoneNumber());
    	conInv.setFaxNumber(remoteInvestigator.getFaxNumber());
    	investigatorConverterDao.save(conInv);
    }
	
    /**
     * Gets the investigator dao.
     *
     * @return the investigator dao
     */
    public InvestigatorDao getInvestigatorDao() {
		return investigatorDao;
	}
    
    /**
     * Sets the investigator dao.
     *
     * @param investigatorDao the new investigator dao
     */
    public void setInvestigatorDao(InvestigatorDao investigatorDao) {
		this.investigatorDao = investigatorDao;
	}
    
    /**
     * Gets the authentication mode.
     *
     * @return the authentication mode
     */
    public String getAuthenticationMode() {
		return authenticationMode;
	}
    
    /**
     * Sets the authentication mode.
     *
     * @param authenticationMode the new authentication mode
     */
    public void setAuthenticationMode(String authenticationMode) {
		this.authenticationMode = authenticationMode;
	}

	/**
	 * Sets the investigator converter dao.
	 *
	 * @param investigatorConverterDao the new investigator converter dao
	 */
	public void setInvestigatorConverterDao(
			InvestigatorConverterDao investigatorConverterDao) {
		this.investigatorConverterDao = investigatorConverterDao;
	}

	/**
	 * Sets the site investigator dao.
	 *
	 * @param siteInvestigatorDao the new site investigator dao
	 */
	public void setSiteInvestigatorDao(SiteInvestigatorDao siteInvestigatorDao) {
		this.siteInvestigatorDao = siteInvestigatorDao;
	}

	/**
	 * Sets the organization dao.
	 *
	 * @param organizationDao the new organization dao
	 */
	public void setOrganizationDao(OrganizationDao organizationDao) {
		this.organizationDao = organizationDao;
	}

	/**
	 * Sets the organization repository.
	 *
	 * @param organizationRepository the new organization repository
	 */
	public void setOrganizationRepository(
			OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	/**
	 * Sets the caaers security facade.
	 *
	 * @param caaersSecurityFacade the new caaers security facade
	 */
	public void setCaaersSecurityFacade(
			CaaersSecurityFacade caaersSecurityFacade) {
		this.caaersSecurityFacade = caaersSecurityFacade;
	}

    /**
     * Gets the event factory.
     *
     * @return the event factory
     */
    public EventFactory getEventFactory() {
        return eventFactory;
    }

    /**
     * Sets the event factory.
     *
     * @param eventFactory the new event factory
     */
    public void setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }
}
