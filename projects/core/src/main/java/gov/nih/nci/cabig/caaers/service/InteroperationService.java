package gov.nih.nci.cabig.caaers.service;

import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.RoutineAdverseEventReport;
import gov.nih.nci.cabig.caaers.CaaersSystemException;

/**
 * This service class is used to send AE reports to the study calendar.
 * 
 * @author Rhett Sutphin
 */
public interface InteroperationService {
    void pushToStudyCalendar(ExpeditedAdverseEventReport aeReport) throws CaaersSystemException;

    void pushToStudyCalendar(RoutineAdverseEventReport roReport) throws CaaersSystemException;
}
