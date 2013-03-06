/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service;

import edu.duke.cabig.c3pr.esb.Metadata;
import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.RoutineAdverseEventReport;

/**
 * Contains do-nothing implementations of {@link InteroperationService}, suitable for local
 * testing.
 * 
 * @author Rhett Sutphin
 */
public class MockInteroperationService implements InteroperationService {

    // this implementation throws an exception 25% of the time in order to test exception handling
    public void pushToStudyCalendar(ExpeditedAdverseEventReport aeReport) {
        int rand = (int) Math.floor(Math.random() * 4);
        if (rand == 3) throw new CaaersSystemException("You lose");
    }

    public void pushToStudyCalendar(RoutineAdverseEventReport roReport)
                    throws CaaersSystemException {
        int rand = (int) Math.floor(Math.random() * 4);
        if (rand == 3) throw new CaaersSystemException("You lose");

    }
    
    public String broadcastCOPPA(String message,Metadata metaData) throws CaaersSystemException {
    	return null;
    }
}
