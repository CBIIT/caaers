/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.admin;


import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.esb.client.impl.CaaersAdeersMessageBroadcastServiceImpl;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.tools.mail.CaaersJavaMailSender;
import gov.nih.nci.cabig.caaers.web.listener.Event;
import gov.nih.nci.cabig.caaers.web.listener.EventMonitor;
import gov.nih.nci.cabig.ctms.tools.configuration.ConfigurationProperties;
import gov.nih.nci.cabig.ctms.tools.configuration.ConfigurationProperty;

import java.util.ArrayList;
import java.util.Collection;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.easymock.classextension.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;

public class DiagnosticsControllerTest extends AbstractTestCase {

	public void testFormBackingObjectHttpServletRequest1() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		DiagnosticsController controller = new DiagnosticsController();
		EventMonitor eventMonitor = new EventMonitor();
		controller.setEventMonitor(eventMonitor);
		controller.setConfiguration(new Configuration());
		DiagnosticsCommand command = controller.formBackingObject(request);
		assertNotNull(command);
		assertFalse(command.isSmtpTestResult());
		assertFalse(command.isServiceMixUp());
        assertNotNull(command.getEvents());
	}
	
	public void testFormBackingObjectHttpServletRequest2() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		DiagnosticsController controller = new DiagnosticsController();
		Configuration configuration = new Configuration() {

			@Override
			public <V> V get(ConfigurationProperty<V> property) {
				return null;
			} 
			
		};
		EventMonitor eventMonitor = new EventMonitor();
		CaaersJavaMailSender caaersJavaMailSender = registerMockFor(CaaersJavaMailSender.class);
		CaaersAdeersMessageBroadcastServiceImpl messageBroadcastService = registerMockFor(CaaersAdeersMessageBroadcastServiceImpl.class);
		Session session = null;
		MimeMessage mimeMessage = new MimeMessage(session);
		EasyMock.expect(caaersJavaMailSender.createMimeMessage()).andReturn(mimeMessage);
		mimeMessage.setSubject("Test mail from caAERS Diagnostics");
		mimeMessage.setFrom(new InternetAddress("caaers.app@gmail.com"));
		caaersJavaMailSender.send(EasyMock.isA(MimeMessage.class));
		messageBroadcastService.initialize();
		
		controller.setConfiguration(configuration);
		controller.setEventMonitor(eventMonitor);
		controller.setCaaersJavaMailSender(caaersJavaMailSender);
		controller.setMessageBroadcastService(messageBroadcastService);
		replayMocks();
		DiagnosticsCommand command = controller.formBackingObject(request);
		assertNotNull(command);
		assertTrue(command.isSmtpTestResult());
		assertTrue(command.isServiceMixUp());
		assertNotNull(command.getEvents());
		verifyMocks();
	}

}
