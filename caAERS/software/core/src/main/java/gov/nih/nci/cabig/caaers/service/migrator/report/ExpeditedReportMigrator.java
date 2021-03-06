/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.migrator.report;

import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.dao.StudyParticipantAssignmentDao;
import gov.nih.nci.cabig.caaers.dao.query.ParticipantQuery;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.migrator.CompositeMigrator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: Biju Joseph
 * Date: 1/8/13
 */
public class ExpeditedReportMigrator extends CompositeMigrator<ExpeditedAdverseEventReport> {
	private static Log logger = LogFactory.getLog(ExpeditedReportMigrator.class);

	private ParticipantDao participantDao;
	private StudyParticipantAssignmentDao studyParticipantAssignmentDao;

	public void setStudyParticipantAssignmentDao(
			StudyParticipantAssignmentDao studyParticipantAssignmentDao) {
		this.studyParticipantAssignmentDao = studyParticipantAssignmentDao;
	}

	public void setParticipantDao(ParticipantDao participantDao) {
		this.participantDao = participantDao;
	}

	@Override
	public void preMigrate(ExpeditedAdverseEventReport src, ExpeditedAdverseEventReport dest, DomainObjectImportOutcome<ExpeditedAdverseEventReport> outcome) {

		if(src.getInvestigationalDeviceAdministered() != null) dest.setInvestigationalDeviceAdministered(src.getInvestigationalDeviceAdministered());

		//Copy the External Id.
		dest.setExternalId(src.getExternalId());

		//set the created date is not present and is available in the source
		if(dest.getCreatedAt() == null && src.getCreatedAt() != null) dest.setCreatedAt(src.getCreatedAt());

		if(src.getReportingPeriod().getTreatmentAssignment() != null){
			StudyParticipantAssignment spa = studyParticipantAssignmentDao.getByStudySubjectIdAndStudyId(src.getReportingPeriod().getAssignment().
					getStudySubjectIdentifier(),  src.getReportingPeriod().getAssignment().getStudySite().getStudy().getPrimaryIdentifier().getValue());
			if(spa == null){
				outcome.addError("ER-SPA-1", "Didn't find Participant Assignment with the combination of Study identifier: " + src.getReportingPeriod().getAssignment().getStudySite().getStudy().getPrimaryIdentifier().getValue()
						+ " and Subject identifier: " + src.getReportingPeriod().getAssignment().
						getStudySubjectIdentifier());
				return;   			
			}
			String code = src.getReportingPeriod().getTreatmentAssignment().getCode();
			if(code != null && code.equalsIgnoreCase("Other")) {
				src.getReportingPeriod().getTreatmentAssignment().setCode(null);
				if(src.getTreatmentInformation()!= null) {
					src.getTreatmentInformation().setTreatmentDescription(src.getReportingPeriod().getTreatmentAssignment().getDescription());
				}
			}
			AdverseEventReportingPeriod arp = spa.findReportingPeriod(null, src.getReportingPeriod().getStartDate(), null, 
					src.getReportingPeriod().getCycleNumber(), null, src.getReportingPeriod().getTreatmentAssignment().getCode());
			if(arp == null){
				outcome.addError("ER-RP-1", "Didn't find Reporting period with TAC: " + src.getReportingPeriod().getTreatmentAssignment().getCode() + 
						(src.getReportingPeriod().getStartDate() != null? " Start date of course: " +  src.getReportingPeriod().getStartDate() :"") +
						(src.getReportingPeriod().getCycleNumber() != null? " Course number: " +  src.getReportingPeriod().getCycleNumber() :""));
				return;

			}

			if(!StringUtils.isBlank(src.getReportingPeriod().getTreatmentAssignmentDescription())){
				arp.setTreatmentAssignmentDescription(src.getReportingPeriod().getTreatmentAssignmentDescription());
	        }
			
			dest.setReportingPeriod(arp);
			return;
		} 

		//identify the reporting period, participant and study to use.

		AdverseEventReportingPeriod  rpSrc = src.getReportingPeriod();
		if(rpSrc == null){
			outcome.addError("ER-RP-1", "Missing Reporting period and Adverse event in input message");
			return;
		}
		StudySite studySiteSrc = rpSrc.getStudySite();
		if(studySiteSrc == null){
			outcome.addError("ER-STU-1", "StudySite information is missing in input message");
			return;
		}
		Study studySrc = rpSrc.getStudy() ;
		if(studySrc == null){
			outcome.addError("ER-STU-2", "Study information is missing in input message");
			return;
		}

		if(studySiteSrc.getOrganization() == null || studySiteSrc.getOrganization().getNciInstituteCode() == null){
			outcome.addError("ER-STU-3", "Missing Study Site details - Organization NCI code");
			return;
		}

		Participant subjectSrc = rpSrc.getParticipant();
		if(subjectSrc == null){
			outcome.addError("ER-SUB-1", "Subject information is missing in input message");
			return;
		}

		StudyParticipantAssignment assignmentSrc = subjectSrc.getAssignments().get(0);

		// DB - Participant Query.
		ParticipantQuery pq = new ParticipantQuery();
		pq.joinStudy();
		pq.filterByStudySubjectIdentifier(assignmentSrc.getStudySubjectIdentifier());
		pq.filterByStudyId(studySrc.getId());
		//pq.filterByStudySiteNciCode(studySiteSrc.getOrganization().getNciInstituteCode());

		List<Participant> dbParticipants = participantDao.searchParticipant(pq);
		if(dbParticipants == null || dbParticipants.isEmpty()){
			outcome.addError("ER-SUB-2", "Subject is not present in caAERS database");
			return;
		}
		StudyParticipantAssignment assignment = dbParticipants.get(0).findAssignemtByStudySubjectIdentifier(assignmentSrc.getStudySubjectIdentifier());
		if(assignment == null){
			outcome.addError("ER-SUB-3", "Subject is not assigned to Study :" + studySrc.getFundingSponsorIdentifierValue() + " at site " + studySiteSrc.getOrganization().getNciInstituteCode());
			return;
		}
		String epochName = rpSrc.getEpoch() != null ? rpSrc.getEpoch().getName() : null;

		AdverseEventReportingPeriod rpFound = assignment.findReportingPeriod(rpSrc.getExternalId(), rpSrc.getStartDate(), rpSrc.getEndDate(), rpSrc.getCycleNumber(), epochName, rpSrc.getTreatmentAssignment() != null ? rpSrc.getTreatmentAssignment().getCode() : null);

		if(rpFound == null){
			outcome.addError("ER-RP-1", "Reporting period not found", studySrc.getFundingSponsorIdentifierValue(),
					studySiteSrc.getOrganization().getNciInstituteCode(),
					assignmentSrc.getStudySubjectIdentifier());
			return;
		}
		dest.setReportingPeriod(rpFound);
		logger.debug(" in migrate; rpFound return; " + rpFound.getTreatmentAssignmentDescription());

	}

}
