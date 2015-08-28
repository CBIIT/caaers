/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.admin;


import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.web.listener.EventMonitor;
import org.springframework.mock.web.MockHttpServletRequest;

public class DiagnosticsControllerTest extends AbstractTestCase {

	public void testFormBackingObjectHttpServletRequest1() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        Configuration cfg = new Configuration();
        EventMonitor monitor = new EventMonitor();
        DiagnosticsController controller = new DiagnosticsController();
        controller.setConfiguration(cfg);
        controller.setEventMonitor(monitor);
        DiagnosticsCommand command = (DiagnosticsCommand)controller.formBackingObject(request);
        assertNotNull(command);
        assertFalse(command.isSmtpTestResult());
        assertFalse(command.isServiceMixUp());
        assertNotNull(command.getEvents());
	}

}
