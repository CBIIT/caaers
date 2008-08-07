package gov.nih.nci.cabig.caaers.api.impl;

import gov.nih.nci.cabig.caaers.api.AdverseEventQueryService;
import gov.nih.nci.cabig.caaers.api.AdverseEventSerializer;
import gov.nih.nci.cabig.caaers.dao.AdverseEventDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.Study;

import java.util.List;


public class AdverseEventQueryServiceImpl implements AdverseEventQueryService {

	private AdverseEventDao adverseEventDao;
	
	public List<AdverseEvent> getByParticipant(Participant participant) {
		return adverseEventDao.getByParticipant(participant);
		
	}

	public List<AdverseEvent> getByParticipant(Participant participant, AdverseEvent adverseEvent){
		return adverseEventDao.getByParticipant(participant,adverseEvent);
	}

	
	public List<AdverseEvent> getByStudy(Study study) {
		return adverseEventDao.getByStudy(study);
	}

	public List<AdverseEvent> getByStudy(Study study, AdverseEvent adverseEvent) {
		return adverseEventDao.getByStudy(study, adverseEvent);
	}


	//public List<AdverseEvent> getByStudyParticipantAssignment(StudyParticipantAssignment studyParticipantAssignment) {
		//return adverseEventDao.getByStudyParticipantAssignment(studyParticipantAssignment);
	//}
	
	public void setAdverseEventDao(AdverseEventDao adverseEventDao) {
		this.adverseEventDao = adverseEventDao;
	}

	public String getXML(List<AdverseEvent> adverseEvents) throws Exception {
		// TODO Auto-generated method stub
		AdverseEventSerializer aes = new AdverseEventSerializer();
		
		StringBuilder aeList = new StringBuilder();
		aeList.append("<AdverseEvents>");
		for (AdverseEvent ae:adverseEvents) {
			System.out.println(ae.getId());
			aeList.append(aes.serialize(ae));
		}
		aeList.append("</AdverseEvents>");
		return aeList.toString();
	}
	
}
