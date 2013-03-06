/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.synchronizer;

import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.migrator.Migrator;

import java.util.ArrayList;
import java.util.List;

public class StudyParticipantAssignmentSynchronizer implements Migrator<Participant>{

	public void migrate(Participant dbParticipant, Participant xmlParticipant,
			DomainObjectImportOutcome<Participant> outcome) {
		
		List<StudyParticipantAssignment> newStudyParticipantAssignmentList = new ArrayList<StudyParticipantAssignment>();
		
		//Identify New StudyParticipantAssignment .
		for(StudyParticipantAssignment xmlStudyParticipantAssignment : xmlParticipant.getAssignments()){
			for(StudyParticipantAssignment dbStudyParticipantAssignment : dbParticipant.getAssignments()){
				xmlStudyParticipantAssignment.setId(dbStudyParticipantAssignment.getId());
				List<Identifier> xmlIdentifiers = xmlStudyParticipantAssignment.getStudySite().getStudy().getIdentifiers();
				List<Identifier> dbIdentifiers = dbStudyParticipantAssignment.getStudySite().getStudy().getIdentifiers();
				boolean studyIdentifierMatchFound = matchIdentifiers(xmlIdentifiers, dbIdentifiers);
				String xmlOrgName = xmlStudyParticipantAssignment.getStudySite().getOrganization().getName();
				String dbOrgName = dbStudyParticipantAssignment.getStudySite().getOrganization().getName();
				String xmlNciInstCode = xmlStudyParticipantAssignment.getStudySite().getOrganization().getNciInstituteCode();
				String dbNciInstCode = dbStudyParticipantAssignment.getStudySite().getOrganization().getNciInstituteCode();
				
				if(studyIdentifierMatchFound && (xmlOrgName.equals(dbOrgName) || xmlNciInstCode.equals(dbNciInstCode))){
					dbStudyParticipantAssignment.setStudySubjectIdentifier(xmlStudyParticipantAssignment.getStudySubjectIdentifier());
					break;
				}else{
					xmlStudyParticipantAssignment.setId(null);
				}
			}
			if(xmlStudyParticipantAssignment.getId() == null){
				newStudyParticipantAssignmentList.add(xmlStudyParticipantAssignment);
			}
		}
		
		//Add New StudyParticipantAssignment
		for(StudyParticipantAssignment newStudyParticipantAssignment : newStudyParticipantAssignmentList){
			dbParticipant.getAssignments().add(newStudyParticipantAssignment);
		}
		//Need to set the Study for the update to function
		for(StudyParticipantAssignment studyParticipantAssignment : dbParticipant.getAssignments()){
			studyParticipantAssignment.setParticipant(dbParticipant);
		}
	}
	
	public boolean matchIdentifiers(List<Identifier> xmlIdentifiers ,List<Identifier> dbIdentifiers){
		
		boolean matchFound = false;
		
		for(Identifier xmlIdentifer : xmlIdentifiers){
				for(Identifier dbIdentifer : dbIdentifiers){
						if(xmlIdentifer.equals(dbIdentifer)){
							matchFound = true;
							return matchFound;
						}
				}
		}
		return matchFound;
	}
}
