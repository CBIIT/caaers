/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers2adeers;


import gov.nih.nci.cabig.open2caaers.exchange.ParticipantODMMessageProcessor;
import gov.nih.nci.cabig.rave2caaers.exchange.RaveIntegrationHeaderProcessor;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;

/**
 * @author Amarnath-K
 */
public class PartipantODMMessageProcessorTest extends TestCase {
	
	
    public void testProcess() throws Exception {
    	ExchangeAdapter exchange = new ExchangeAdapter();
    	ParticipantODMMessageProcessor newProcessor=new ParticipantODMMessageProcessor();
    	newProcessor.setCaaersWSUser("SYSTEM");
    	newProcessor.setCaaersWSPassword("system_admin");
    	
    	String body = "<ODM><ClinicalData></ClinicalData></ODM>";
    	String body1="This is only JUNK";
    	
        exchange.getIn().setBody(body1); 
        
    	//new ParticipantODMMessageProcessor().process(exchange);
    	Assert.assertEquals(true, newProcessor.isValidOdmXml(exchange));
    	 //Assert.assertEquals("true", exchange.getProperties().get(ParticipantODMMessageProcessor.INVALID_MESSAGE));
        
    }

   
}
