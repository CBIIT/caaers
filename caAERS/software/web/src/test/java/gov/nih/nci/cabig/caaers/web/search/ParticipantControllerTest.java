/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.search;

import gov.nih.nci.cabig.caaers.web.ListValues;
import gov.nih.nci.cabig.caaers.web.WebTestCase;

import org.springframework.web.servlet.ModelAndView;

/**
 * @author Krikor Krumlian
 */
public class ParticipantControllerTest extends WebTestCase {
    private ParticipantController controller = new ParticipantController();

    protected void setUp() throws Exception {
        super.setUp();
        controller.setListValues(new ListValues());
    }

    public void testViewOnGoodSubmit() throws Exception {

        ModelAndView mv = controller.handleRequest(request, response);
        assertEquals("search/participant_search", mv.getViewName());
    }
}
