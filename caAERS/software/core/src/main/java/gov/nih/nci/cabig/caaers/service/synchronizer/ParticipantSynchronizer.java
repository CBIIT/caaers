/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.synchronizer;

import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ParticipantSynchronizer extends CompositeSynchronizer<Participant>{


    private List<String> context = new ArrayList<String>();
    @Override
    public List<String> contexts() {
        return context;
    }
	@Override
	public void preMigrate(Participant dbParticipant, Participant xmlParticipant,
			DomainObjectImportOutcome<Participant> outcome) {
		
		dbParticipant.setFirstName(xmlParticipant.getFirstName());
		dbParticipant.setLastName(xmlParticipant.getLastName());
		if(xmlParticipant.getMiddleName() != null &&  StringUtils.isNotEmpty(xmlParticipant.getMiddleName())){
			dbParticipant.setMiddleName(xmlParticipant.getMiddleName());
		}
		if(xmlParticipant.getMaidenName() != null &&  StringUtils.isNotEmpty(xmlParticipant.getMaidenName())){
			dbParticipant.setMaidenName(xmlParticipant.getMaidenName());
		}
		dbParticipant.setDateOfBirth(xmlParticipant.getDateOfBirth());
		if(xmlParticipant.getGender() != null &&  StringUtils.isNotEmpty(xmlParticipant.getGender())){
			dbParticipant.setGender(xmlParticipant.getGender());
		}
		if(xmlParticipant.getEthnicity() != null &&  StringUtils.isNotEmpty(xmlParticipant.getEthnicity())){
			dbParticipant.setEthnicity(xmlParticipant.getEthnicity());
		}
		if(xmlParticipant.getRace() != null &&  StringUtils.isNotEmpty(xmlParticipant.getRace())){
			dbParticipant.setRace(xmlParticipant.getRace());
		}
	}
}
