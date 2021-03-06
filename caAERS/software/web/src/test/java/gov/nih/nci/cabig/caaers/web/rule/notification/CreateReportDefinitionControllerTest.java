/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.rule.notification;

import gov.nih.nci.cabig.caaers.web.WebTestCase;

/**
 * @author Ion C. Olaru
 */
public class CreateReportDefinitionControllerTest extends WebTestCase {

    private CreateReportDefinitionController controller = new CreateReportDefinitionController();
    
    public void testFlow() throws Exception {
        assertEquals("Create Report Definition", controller.getFlowName());
        assertNotNull(controller.getFlowFactory());
    }

}
