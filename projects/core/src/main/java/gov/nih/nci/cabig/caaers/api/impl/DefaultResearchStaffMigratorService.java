package gov.nih.nci.cabig.caaers.api.impl;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.api.ResearchStaffMigratorService;
import gov.nih.nci.cabig.caaers.dao.ResearchStaffDao;
import gov.nih.nci.cabig.caaers.dao.query.ResearchStaffQuery;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.domain.UserGroupType;
import gov.nih.nci.cabig.caaers.domain.repository.ResearchStaffRepository;
import gov.nih.nci.cabig.caaers.integration.schema.common.OrganizationRefType;
import gov.nih.nci.cabig.caaers.integration.schema.common.ServiceResponse;
import gov.nih.nci.cabig.caaers.integration.schema.common.Status;
import gov.nih.nci.cabig.caaers.integration.schema.common.WsError;
import gov.nih.nci.cabig.caaers.integration.schema.researchstaff.ResearchStaffType;
import gov.nih.nci.cabig.caaers.integration.schema.researchstaff.Role;
import gov.nih.nci.cabig.caaers.integration.schema.researchstaff.Staff;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome.Severity;
import gov.nih.nci.security.acegi.csm.authorization.AuthorizationSwitch;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.TestingAuthenticationToken;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@WebService(endpointInterface="gov.nih.nci.cabig.caaers.api.ResearchStaffMigratorService", serviceName="ResearchStaffMigratorService")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class DefaultResearchStaffMigratorService extends DefaultMigratorService implements
		ResearchStaffMigratorService,ApplicationContextAware {
	
	private static final Log logger = LogFactory.getLog(DefaultResearchStaffMigratorService.class);
	private ResearchStaffDao researchStaffDao;
	private ApplicationContext applicationContext;
	protected ResearchStaffRepository researchStaffRepository;
	
	private List<DomainObjectImportOutcome<ResearchStaff>> importableResearchStaff = new ArrayList<DomainObjectImportOutcome<ResearchStaff>>();
	private List<DomainObjectImportOutcome<ResearchStaff>> nonImportableResearchStaff = new ArrayList<DomainObjectImportOutcome<ResearchStaff>>();

	/**
     * Fetches the research staff from the DB
     * 
     * @param nciCode
     * @return
     */
    ResearchStaff fetchResearchStaff(String email) {//String nciIdentifier) {
    	ResearchStaffQuery rsQuery = new ResearchStaffQuery();
        if (StringUtils.isNotEmpty(email)) {
        	//rsQuery.filterByNciIdentifier(nciIdentifier);
        	rsQuery.filterByEmailAddress(email);
        }
        List<ResearchStaff> rsList = researchStaffDao.searchResearchStaff(rsQuery);
        
        if (rsList == null || rsList.isEmpty()) {
            return null;
        }
        return rsList.get(0);
    }
    public ServiceResponse saveResearchStaff(Staff staff) throws RemoteException {
    	List<ResearchStaffType> researchStaffList = staff.getResearchStaff();
    	ResearchStaff researchStaff = null;//buildInvestigator(investigatorType);
    	getImportableResearchStaff().clear();
    	getNonImportableResearchStaff().clear();
    	
    	List<WsError> wsErrors = new ArrayList<WsError>();
    	ServiceResponse response = new ServiceResponse();
    	response.setStatus(Status.FAILED_TO_PROCESS);
    	
    	for (ResearchStaffType researchStaffType:researchStaffList) {

    		try {
    			researchStaff = buildResearchStaff(researchStaffType);
    			saveResearchStaff(researchStaff);
    			DomainObjectImportOutcome<ResearchStaff> researchStaffImportOutcome = new DomainObjectImportOutcome<ResearchStaff>();
    			researchStaffImportOutcome.setImportedDomainObject(researchStaff);
    			addImportableResearchStaff(researchStaffImportOutcome);
    		} catch (CaaersSystemException e) {
    			researchStaff = new ResearchStaff();
    			researchStaff.setNciIdentifier(researchStaffType.getNciIdentifier());
    			researchStaff.setFirstName(researchStaffType.getFirstName());
    			researchStaff.setLastName(researchStaffType.getLastName());
            	DomainObjectImportOutcome<ResearchStaff> researchStaffImportOutcome = new DomainObjectImportOutcome<ResearchStaff>();
            	researchStaffImportOutcome.setImportedDomainObject(researchStaff);
            	researchStaffImportOutcome.addErrorMessage(e.getMessage(), Severity.ERROR);
            	addNonImportableResearchStaff(researchStaffImportOutcome);
 
    			WsError err = new WsError();
    			err.setErrorDesc("Failed to process ResearchStaff ::: nciIdentifier : "+researchStaffType.getNciIdentifier() + " ::: firstName : "+researchStaffType.getFirstName()+ " ::: lastName : "+researchStaffType.getLastName()) ;
    			err.setException(e.getMessage());
    			wsErrors.add(err);           	
    			//throw new RemoteException("Unable to import investigator", e);
    		}
    	}
    	response.setWsError(wsErrors);
    	if (wsErrors.size() == 0) {
    		response.setStatus(Status.PROCESSED);
    	}
    	return response;
    }
    
    public ResearchStaff buildResearchStaff(ResearchStaffType researchStaffDto) throws CaaersSystemException {
    	  try {
              logger.info("Begining of ResearchStaffMigrator : buildResearchStaff");
               
             // if (researchStaffDto == null) throw getInvalidResearchStaffException("null input");
              String nciIdentifier = researchStaffDto.getNciIdentifier();
              String email = researchStaffDto.getEmailAddress();
              ResearchStaff researchStaff = fetchResearchStaff(email);
              if (researchStaff == null ) {
              	// build new 
              	researchStaff = new ResearchStaff();
              	researchStaff.setNciIdentifier(nciIdentifier);
              	researchStaff.setEmailAddress(researchStaffDto.getEmailAddress());
              } 
              researchStaff.setFirstName(researchStaffDto.getFirstName());
              researchStaff.setLastName(researchStaffDto.getLastName());
              researchStaff.setMiddleName(researchStaffDto.getMiddleName());              
              researchStaff.setFaxNumber(researchStaffDto.getFaxNumber());
              researchStaff.setPhoneNumber(researchStaffDto.getPhoneNumber());
              researchStaff.getUserGroupTypes().clear();
              
              List<Role> roles = researchStaffDto.getRole();
              
              for (Role role:roles) {
            	  researchStaff.addUserGroupType(UserGroupType.valueOf(role.value()));
              }
              
              //get Organizations 
              OrganizationRefType organizationRef = researchStaffDto.getOrganizationRef();
              String nciInstituteCode = organizationRef.getNciInstituteCode();
              Organization organization = fetchOrganization(nciInstituteCode);
              researchStaff.setOrganization(organization);
              
              return researchStaff;

          } catch (Exception e) {
              logger.error("Error while creating research staff", e);
              throw new CaaersSystemException(e.getMessage(), e);
          }	  	
    	
    }
	public void saveResearchStaff(ResearchStaff researchStaff) throws CaaersSystemException {

        try {
            logger.info("Begining of ResearchStaffMigrator : saveResearchStaff");             
            
            //save 
            researchStaffRepository.save(researchStaff,"URL");
            logger.info("Created the research staff :" + researchStaff.getId());
            logger.info("End of ResearchStaffMigrator : saveResearchStaff");

        } catch (Exception e) {
            logger.error("Error while creating research staff", e);
            throw new CaaersSystemException("Unable to create research staff : "+ e.getMessage(), e);
        }	
        
	}
	
	//CONFIGURATION

    @Required
    public void setResearchStaffDao(ResearchStaffDao researchStaffDao) {
		this.researchStaffDao = researchStaffDao;
	}

    @Required
	public ResearchStaffDao getResearchStaffDao() {
		return researchStaffDao;
	}

	public List<DomainObjectImportOutcome<ResearchStaff>> getImportableResearchStaff() {
		return importableResearchStaff;
	}

	public List<DomainObjectImportOutcome<ResearchStaff>> getNonImportableResearchStaff() {
		return nonImportableResearchStaff;
	}

	private void addImportableResearchStaff(DomainObjectImportOutcome<ResearchStaff> domainObjectImportResearchStaff) {
		this.getImportableResearchStaff().add(domainObjectImportResearchStaff);
	}

	private void addNonImportableResearchStaff(DomainObjectImportOutcome<ResearchStaff> domainObjectImportResearchStaff) {
		this.getNonImportableResearchStaff().add(domainObjectImportResearchStaff);
	}
	
	private void switchUser(String userName, String... roles) {
        GrantedAuthority[] authorities = new GrantedAuthority[roles.length];
        for (int i = 0; i < roles.length; i++) {
            authorities[i] = new GrantedAuthorityImpl(roles[i]);
        }
        Authentication auth = new TestingAuthenticationToken(userName, "ignored", authorities);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
	
	private boolean enableAuthorization(boolean on) {
        AuthorizationSwitch sw = (AuthorizationSwitch) this.applicationContext.getBean("authorizationSwitch");
        if (sw == null) throw new RuntimeException("Authorization switch not found");
        boolean current = sw.isOn();
        sw.setOn(on);
        return current;
    }

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
		
	}
	public void setResearchStaffRepository(
			ResearchStaffRepository researchStaffRepository) {
		this.researchStaffRepository = researchStaffRepository;
	}

}
