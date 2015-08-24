/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers2adeers;


import gov.nih.nci.cabig.open2caaers.exchange.ParticipantODMMessageProcessor;
import junit.framework.Assert;
import junit.framework.TestCase;



import org.apache.camel.Exchange;

/**
 * @author Amarnath-K
 */
public class PartipantODMMessageProcessorTest extends TestCase {
	
	
    public void testProcess_positivScenario() throws Exception {
    	
    	ParticipantODMMessageProcessor newProcessor=new ParticipantODMMessageProcessor();
    	
    	String body = "<ODM><ClinicalData></ClinicalData></ODM>";
    	
    	Exchange exchange = new ExchangeAdapter();
        exchange.getIn().setBody(body);         
    	//newProcessor.process(exchange);
    	
    	Assert.assertEquals(true, newProcessor.isValidOdmXml(exchange));
    	
    	//If the below needs to be tested  checkAuthentication() needs to be commented
    	//Assert.assertEquals("false", exchange.getProperties().get(ParticipantODMMessageProcessor.INVALID_MESSAGE));

    }
    
public void testProcess_negetiveScenario() throws Exception {
    	
    	ParticipantODMMessageProcessor newProcessor1=new ParticipantODMMessageProcessor();
    	
    	String body1="This is Junk";
    	
    	Exchange exchange1 = new ExchangeAdapter();
        exchange1.getIn().setBody(body1);         
    	//newProcessor1.process(exchange1);
    	
    	Assert.assertEquals(false, newProcessor1.isValidOdmXml(exchange1));
    	//If the below needs to be tested  checkAuthentication() needs to be commented
    	//Assert.assertEquals("true", exchange1.getProperties().get(ParticipantODMMessageProcessor.INVALID_MESSAGE));
        
    }

   
}
