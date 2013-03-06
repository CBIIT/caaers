/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.admin;


import gov.nih.nci.cabig.caaers.api.ParticipantService;
import gov.nih.nci.cabig.caaers.api.StudyProcessor;
import gov.nih.nci.cabig.caaers.api.impl.DefaultInvestigatorMigratorService;
import gov.nih.nci.cabig.caaers.api.impl.DefaultResearchStaffMigratorService;
import gov.nih.nci.cabig.caaers.api.impl.ParticipantServiceImpl;
import gov.nih.nci.cabig.caaers.api.impl.StudyProcessorImpl;
import gov.nih.nci.cabig.caaers.dao.MedDRADao;
import gov.nih.nci.cabig.caaers.dao.MeddraVersionDao;
import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.domain.repository.AgentRepository;
import gov.nih.nci.cabig.caaers.domain.repository.InvestigatorRepository;
import gov.nih.nci.cabig.caaers.domain.repository.OrganizationRepository;
import gov.nih.nci.cabig.caaers.domain.repository.ResearchStaffRepository;
import gov.nih.nci.cabig.caaers.domain.repository.StudyRepository;
import gov.nih.nci.cabig.caaers.event.EventFactory;
import gov.nih.nci.cabig.caaers.validation.validator.DomainObjectValidator;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;


/**
 * @author Sameer Sawant
 */
public class ImporterFactory{
	
	private static Logger log = Logger.getLogger(ImporterFactory.class);
	private static final String STUDY_IMPORT = "study";
	private static final String SUBJECT_IMPORT = "participant";
	private static final String RESEARCH_STAFF_IMPORT = "researchStaff";
	private static final String INVESTIGATOR_IMPORT = "investigator";
	private static final String ORGANIZATION_IMPORT = "organization";
	private static final String AGENT_IMPORT = "agent";
	private static final String MEDDRA_IMPORT = "medDRA";
	private static final String ASAEL_IMPORT = "agentSpecificAEList";

	private DomainObjectValidator domainObjectValidator;
	private StudyProcessor studyProcessor;
	private ParticipantService participantService;
	private	DefaultInvestigatorMigratorService investigatorMigratorService;
	private DefaultResearchStaffMigratorService researchStaffMigratorService;
	private StudyRepository studyRepository;
	private ParticipantDao participantDao;
	private ResearchStaffRepository researchStaffRepository;
	private InvestigatorRepository investigatorRepository;
	private OrganizationRepository organizationRepository;
	private AgentRepository agentRepository;
	private MeddraVersionDao meddraVersionDao;
	private MedDRADao medDRADao;
	private MessageSource messageSource;

    private ASAELImporter asaelImporter;

    private EventFactory eventFactory;

	public Importer createImporterInstance(String type){
		if(type.equals(STUDY_IMPORT)){
			StudyImporter studyImporter = new StudyImporter();
			studyImporter.setDomainObjectValidator(domainObjectValidator);
			studyImporter.setStudyRepository(studyRepository);
			studyImporter.setMessageSource(messageSource);
			studyImporter.setStudyProcessorImpl((StudyProcessorImpl)studyProcessor);
            studyImporter.setEventFactory(getEventFactory());
			return studyImporter;
		}else if (type.equals(SUBJECT_IMPORT)){
			SubjectImporter subjectImporter = new SubjectImporter();
			subjectImporter.setDomainObjectValidator(domainObjectValidator);
			subjectImporter.setParticipantDao(participantDao);
			subjectImporter.setMessageSource(messageSource);
            subjectImporter.setEventFactory(getEventFactory());
			subjectImporter.setParticipantServiceImpl((ParticipantServiceImpl)participantService);
			return subjectImporter;
		}else if (type.equals(RESEARCH_STAFF_IMPORT)){
			ResearchStaffImporter researchStaffImporter = new ResearchStaffImporter();
			researchStaffImporter.setDomainObjectValidator(domainObjectValidator);
			researchStaffImporter.setResearchStaffMigratorService(researchStaffMigratorService);
			researchStaffImporter.setResearchStaffRepository(researchStaffRepository);
            researchStaffImporter.setEventFactory(getEventFactory());
			researchStaffImporter.setMessageSource(messageSource);
			return researchStaffImporter;
		}else if (type.equals(INVESTIGATOR_IMPORT)){
			InvestigatorImporter investigatorImporter = new InvestigatorImporter();
			investigatorImporter.setDomainObjectValidator(domainObjectValidator);
			investigatorImporter.setInvestigatorMigratorService(investigatorMigratorService);
			investigatorImporter.setInvestigatorRepository(investigatorRepository);
            investigatorImporter.setEventFactory(getEventFactory());
			investigatorImporter.setMessageSource(messageSource);
			return investigatorImporter;
		}else if(type.equals(ORGANIZATION_IMPORT)){
			OrganizationImporter organizationImporter = new OrganizationImporter();
			organizationImporter.setOrganizationRepository(organizationRepository);
            organizationImporter.setEventFactory(getEventFactory());
			return organizationImporter;
		}else if(type.equals(AGENT_IMPORT)){
			AgentImporter agentImporter = new AgentImporter();
			agentImporter.setAgentRepository(agentRepository);
			return agentImporter;
		}else if(type.equals(MEDDRA_IMPORT)){
			MeddraImporter meddraImporter = new MeddraImporter();
			meddraImporter.setmedDRADao(medDRADao);
			meddraImporter.setMeddraVersionDao(meddraVersionDao);
			return meddraImporter;
		} else if (type.equals(ASAEL_IMPORT)) {
            return asaelImporter;
        } else return null;
	}
	
	public DomainObjectValidator getDomainObjectValidator(){
		return domainObjectValidator;
	}
	
	public void setDomainObjectValidator(DomainObjectValidator domainObjectValidator){
		this.domainObjectValidator = domainObjectValidator;
	}
	
	public StudyProcessor getStudyProcessor(){
		return studyProcessor;
	}
	
	public void setStudyProcessor(StudyProcessor studyProcessor){
		this.studyProcessor = studyProcessor;
	}
	
	public ParticipantService getParticipantService(){
		return participantService;
	}
	
	public void setParticipantService(ParticipantService participantService){
		this.participantService = participantService;
	}
	
	public DefaultInvestigatorMigratorService getInvestigatorMigratorService(){
		return investigatorMigratorService;
	}
	
	public void setInvestigatorMigratorService(DefaultInvestigatorMigratorService investigatorMigratorService){
		this.investigatorMigratorService = investigatorMigratorService;
	}
	
	public DefaultResearchStaffMigratorService getResearchStaffMigratorService(){
		return researchStaffMigratorService;
	}
	
	public void setResearchStaffMigratorService(DefaultResearchStaffMigratorService researchStaffMigratorService){
		this.researchStaffMigratorService = researchStaffMigratorService;
	}
	
	public void setStudyRepository(StudyRepository studyRepository){
		this.studyRepository = studyRepository;
	}
	
	public void setParticipantDao(ParticipantDao participantDao){
		this.participantDao = participantDao;
	}
	
	public void setResearchStaffRepository(ResearchStaffRepository researchStaffRepository){
		this.researchStaffRepository = researchStaffRepository;
	}
	
	public void setInvestigatorRepository(InvestigatorRepository investigatorRepository){
		this.investigatorRepository = investigatorRepository;
	}
	
	public void setOrganizationRepository(
			OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	public MeddraVersionDao getMeddraVersionDao() {
		return meddraVersionDao;
	}

	public void setMeddraVersionDao(MeddraVersionDao meddraVersionDao) {
		this.meddraVersionDao = meddraVersionDao;
	}

	public MedDRADao getMedDRADao() {
		return medDRADao;
	}

	public void setMedDRADao(MedDRADao medDRADao) {
		this.medDRADao = medDRADao;
	}
	
	public void setMessageSource(MessageSource messageSource){
		this.messageSource = messageSource;
	}
	
	public MessageSource getMessageSource(){
		return messageSource;
	}

	public void setAgentRepository(AgentRepository agentRepository) {
		this.agentRepository = agentRepository;
	}

    public ASAELImporter getAsaelImporter() {
        return asaelImporter;
    }

    public void setAsaelImporter(ASAELImporter asaelImporter) {
        this.asaelImporter = asaelImporter;
    }

    public EventFactory getEventFactory() {
        return eventFactory;
    }

    public void setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }
}
