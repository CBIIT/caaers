package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.Study;


/**
 * @author Krikor Krumlian
 */
public interface AdverseEventInputCommandInterface {
    
    StudyParticipantAssignment getAssignment();

    Participant getParticipant();

    Study getStudy();

}