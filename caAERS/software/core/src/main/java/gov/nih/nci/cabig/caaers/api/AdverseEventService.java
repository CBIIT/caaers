/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.api;

import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.Lab;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.Study;

import java.util.List;

/**
 * @author Ram Chilukuri
 */
public interface AdverseEventService {
    public static String DEFAULT_SITE_NAME = "Default Site";

    public String createCandidateAdverseEvent(Study study, Participant participant,
                    Organization organization, AdverseEvent ae, List<Lab> labs);
}
