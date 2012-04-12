package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.api.impl.ParticipantServiceImpl;
import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.integration.schema.participant.ParticipantType;
import gov.nih.nci.cabig.caaers.integration.schema.participant.Participants;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome.Severity;
import gov.nih.nci.cabig.caaers.validation.validator.DomainObjectValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * @author Sameer Sawant
 */
public class SubjectImporter extends Importer{
	
	private static Logger logger = Logger.getLogger(SubjectImporter.class);
	private DomainObjectValidator domainObjectValidator;
	private ParticipantServiceImpl participantServiceImpl;
	private ParticipantDao participantDao;
	
	public void SubjectImporter(){
	}
	
	public void processEntities(File xmlFile,ImportCommand command){
		boolean valid = validateAgainstSchema(xmlFile , command, getXSDLocation(SUBJECT_IMPORT));
        if (!valid) {
        	return;
        }
		Participants participants;
    	try {
			JAXBContext jaxbContext = JAXBContext.newInstance("gov.nih.nci.cabig.caaers.integration.schema.participant");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				
			Object importObject = unmarshaller.unmarshal(xmlFile);
			if(!validRootElement(importObject, SUBJECT_IMPORT, command))
				return;
				
			participants = (Participants) importObject;
			if(participants != null){
				for(ParticipantType participantDto : participants.getParticipant()){
					DomainObjectImportOutcome<Participant> participantImportOutcome  = participantServiceImpl.processParticipant(participantDto);
					List<String> errors = domainObjectValidator.validate(participantImportOutcome.getImportedDomainObject());
					if (participantImportOutcome.isSavable() && errors.size() == 0) {
						command.addImportableParticipant(participantImportOutcome);
			        } else {
			        	for(String errMsg : errors){
			        		participantImportOutcome.addErrorMessage(errMsg, Severity.ERROR);
			        	}
			            command.addNonImportableParticipant(participantImportOutcome);
			        }
				}
				
				//Remove Duplicate Participants from the ImportableParticipants  List.
				List<DomainObjectImportOutcome<Participant>> dupList = new ArrayList<DomainObjectImportOutcome<Participant>>();
				for(int k=0 ; k < command.getImportableParticipants().size()-1 ; k++){
					Participant par1 = command.getImportableParticipants().get(k).getImportedDomainObject();
					for(int l=k+1 ; l < command.getImportableParticipants().size() ; l++){
						Participant par2 = command.getImportableParticipants().get(l).getImportedDomainObject();
						if(par1.equals(par2)){
							command.getImportableParticipants().get(l).addErrorMessage("Participant Identifier already used", Severity.ERROR);
							command.addNonImportableParticipant(command.getImportableParticipants().get(l));
							dupList.add(command.getImportableParticipants().get(l));
							logger.debug("Duplicate Participant :: " + par2.getFullName());
							break;
						}
					}
				}
				for(DomainObjectImportOutcome<Participant> obj : dupList){
					command.getImportableParticipants().remove(obj);
				}
			}
		} catch (JAXBException e) {
			throw new CaaersSystemException("There was an error converting participant data transfer object to participant domain object", e);
		}
	}
	
	public void save(ImportCommand command, HttpServletRequest request){
		for (DomainObjectImportOutcome<Participant> importOutcome : command.getImportableParticipants()) {
        	participantDao.save(importOutcome.getImportedDomainObject());
        }
        //	CAAERS-4461
        if(CollectionUtils.isNotEmpty(command.getImportableParticipants())) getEventFactory().publishEntityModifiedEvent(new Participant(),true);
	}
	
	public DomainObjectValidator getDomainObjectValidator(){
		return domainObjectValidator;
	}
	
	public void setDomainObjectValidator(DomainObjectValidator domainObjectValidator){
		this.domainObjectValidator = domainObjectValidator;
	}
	
	public ParticipantServiceImpl getParticipantServiceImpl(){
		return participantServiceImpl;
	}
	
	public void setParticipantServiceImpl(ParticipantServiceImpl participantServiceImpl){
		this.participantServiceImpl = participantServiceImpl;
	}
	
	public void setParticipantDao(ParticipantDao participantDao){
		this.participantDao = participantDao;
	}
	
}