package gov.nih.nci.cabig.caaers.api.impl;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.api.StudyProcessor;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.StudyImportServiceImpl;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome.Severity;
import gov.nih.nci.cabig.caaers.service.migrator.StudyConverter;
import gov.nih.nci.cabig.caaers.service.synchronizer.StudySynchronizer;
import gov.nih.nci.security.acegi.csm.authorization.AuthorizationSwitch;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.TestingAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@WebService(endpointInterface="gov.nih.nci.cabig.caaers.api.StudyProcessor", serviceName="StudyService")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class StudyProcessorImpl implements StudyProcessor,ApplicationContextAware {
	
	
private static Log logger = LogFactory.getLog(StudyProcessorImpl.class);
	
	//Injected through spring
	private StudyImportServiceImpl studyImportService;
	private StudyDao studyDao;
	private StudyConverter studyConverter;
	private StudySynchronizer studySynchronizer;
	private ApplicationContext applicationContext;
	
	public StudyImportServiceImpl getStudyImportService() {
		return studyImportService;
	}

	public void setStudyImportService(StudyImportServiceImpl studyImportService) {
		this.studyImportService = studyImportService;
	}

	public StudyDao getStudyDao() {
		return studyDao;
	}

	public void setStudyDao(StudyDao studyDao) {
		this.studyDao = studyDao;
	}

	public StudyConverter getStudyConverter() {
		return studyConverter;
	}

	public void setStudyConverter(StudyConverter studyConverter) {
		this.studyConverter = studyConverter;
	}

	public StudySynchronizer getStudySynchronizer() {
		return studySynchronizer;
	}

	public void setStudySynchronizer(StudySynchronizer studySynchronizer) {
		this.studySynchronizer = studySynchronizer;
	}

	/**
	 * Method exisits only to be called from ImportController for testing
	 * until the Webservice is up and running 
	 * @param studyDto
	 */
	public DomainObjectImportOutcome<Study> processStudy(gov.nih.nci.cabig.caaers.webservice.Study studyDto){
		logger.info("Entering createStudy() in StudyProcessorImpl");
		
		DomainObjectImportOutcome<Study> studyImportOutcome = null;
		Study study = new Study();
		
		//Convert JAXB StudyType to Domain Study
		try{
			studyConverter.convertStudyDtoToStudyDomain(studyDto, study);
			logger.info("StudyDto converted to Study");
		}catch(CaaersSystemException caEX){
			studyImportOutcome = new DomainObjectImportOutcome<Study>();
			logger.error("StudyDto to StudyDomain Conversion Failed " , caEX);
			studyImportOutcome.addErrorMessage("StudyDto to StudyDomain Conversion Failed " , DomainObjectImportOutcome.Severity.ERROR);
		}
		
		if(studyImportOutcome == null){
			studyImportOutcome = studyImportService.importStudy(study);
			if(studyImportOutcome.isSavable()){
				//Check if Study Exists; If Exists then update
				Study dbStudy = fetchStudy(studyImportOutcome.getImportedDomainObject());
				if(dbStudy != null){
					logger.info("Study with Long Title -- "+ dbStudy.getLongTitle() + " -- Exists in caAERS trying to Update");
					studySynchronizer.migrate(dbStudy, studyImportOutcome.getImportedDomainObject(), studyImportOutcome);
					studyImportOutcome.setImportedDomainObject(dbStudy);
					logger.info("Study "+ dbStudy.getLongTitle() + " in caAERS Updated");
				}else{
					logger.info("New Study with Long Title -- "+ studyImportOutcome.getImportedDomainObject().getLongTitle() + " -- being Created");
				}
			}
		}
		logger.info("Leaving createStudy() in StudyProcessorImpl");
		return studyImportOutcome;
	}
	
	public void createStudy(gov.nih.nci.cabig.caaers.webservice.Study studyDto) {
		
		boolean authorizationOnByDefault = enableAuthorization(false);
		switchUser("SYSTEM_ADMIN", "ROLE_caaers_super_user");
		logger.info("Swith User Done ");
		logger.info("Inside createStudy ");
		logger.info("Study Short Title --- " + studyDto.getShortTitle());
		logger.info("Study Long Title --- " + studyDto.getLongTitle());
		
		DomainObjectImportOutcome<Study> studyImportOutcome = null;
		Study study = new Study();
		
		//Convert JAXB StudyType to Domain Study
		try{
			logger.info("Converting StudyDto to Study");
			studyConverter.convertStudyDtoToStudyDomain(studyDto, study);
			logger.info("StudyDto converted to Study");
		}catch(CaaersSystemException caEX){
			studyImportOutcome = new DomainObjectImportOutcome<Study>();
			logger.error("StudyDto to StudyDomain Conversion Failed " , caEX);
			studyImportOutcome.addErrorMessage("StudyDto to StudyDomain Conversion Failed " , DomainObjectImportOutcome.Severity.ERROR);
		}
		
		if(studyImportOutcome == null){
			studyImportOutcome = studyImportService.importStudy(study);
			//Check if Study Exists
			Study dbStudy = fetchStudy(studyImportOutcome.getImportedDomainObject());
			if(dbStudy != null){
				studyImportOutcome.addErrorMessage(study.getClass().getSimpleName() + " identifier already exists. ", Severity.ERROR);
			}
			if(studyImportOutcome.isSavable()){
				studyDao.save(studyImportOutcome.getImportedDomainObject());
				logger.info("Study Created");
			}
		}
		enableAuthorization(authorizationOnByDefault);
		switchUser(null);		
		logger.info("Leaving createStudy() in StudyProcessorImpl");

	}

	public void updateStudy(gov.nih.nci.cabig.caaers.webservice.Study studyDto) {
		boolean authorizationOnByDefault = enableAuthorization(false);
		switchUser("SYSTEM_ADMIN", "ROLE_caaers_super_user");
		logger.info("Inside updateStudy ");
		logger.info("Study Short Title --- " + studyDto.getShortTitle());
		logger.info("Study Long Title --- " + studyDto.getLongTitle());
		
		DomainObjectImportOutcome<Study> studyImportOutcome = null;
		Study study = new Study();
		
		//Convert JAXB StudyType to Domain Study
		try{
			studyConverter.convertStudyDtoToStudyDomain(studyDto, study);
			logger.info("StudyDto converted to Study");
		}catch(CaaersSystemException caEX){
			studyImportOutcome = new DomainObjectImportOutcome<Study>();
			logger.error("StudyDto to StudyDomain Conversion Failed " , caEX);
			studyImportOutcome.addErrorMessage("StudyDto to StudyDomain Conversion Failed " , DomainObjectImportOutcome.Severity.ERROR);
		}
		
		if(studyImportOutcome == null){
			studyImportOutcome = studyImportService.importStudy(study);
			if(studyImportOutcome.isSavable()){
				//Check if Study Exists
				Study dbStudy = fetchStudy(studyImportOutcome.getImportedDomainObject());
				if(dbStudy != null){
					studySynchronizer.migrate(dbStudy, studyImportOutcome.getImportedDomainObject(), studyImportOutcome);
					studyImportOutcome.setImportedDomainObject(dbStudy);
					studyDao.save(studyImportOutcome.getImportedDomainObject());
					logger.info("Study Updated");
				}
			}
		}
		enableAuthorization(authorizationOnByDefault);
		switchUser(null);
		logger.info("Leaving updateStudy() in StudyProcessor");
	}
	
	/**
	 * This method fetches a Study from the DB based identifiers.
	 * @param importedStudy
	 * @return
	 */
	private Study fetchStudy(Study importedStudy){
		Study dbStudy = null;
		for (Identifier identifier : importedStudy.getIdentifiers()) {
            dbStudy = studyDao.getStudyDesignByIdentifier(identifier);
            if(dbStudy != null){
            	break;
            }
            studyDao.evict(dbStudy);
        }
		return dbStudy;
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

}
