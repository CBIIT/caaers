/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers2adeers.track;

import gov.nih.nci.cabig.caaers2adeers.exchnage.ExchangePreProcessor;
import gov.nih.nci.cabig.caaers2adeers.track.IntegrationLog.Stage;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * @author Biju Joseph (minor refactoring)
 */
public class Tracker implements Processor{
	
	protected static final Log log = LogFactory.getLog(Tracker.class);
	public static final String SOAP_FAULT_STATUS= "FAILED_TO_PROCESS";
	public static final String CAAERS_RESPONSE_ERROR= "FAILED_TO_PROCESS";
	
 	// progress made by synch request
 	private Stage stage;
 	
 	// details 
 	private String notes;
 	
 	boolean caputureLogDetails = false; 
 	
 	boolean captureLogMessage = false;
 	
 	String[] messageComboIdPaths;
 	
	public Tracker(Stage stage, String notes, boolean caputureLogDetails, String[] messageComboIdPaths) {
		super();
		this.stage = stage;
		this.notes = notes;
		this.caputureLogDetails = caputureLogDetails;
		this.messageComboIdPaths = messageComboIdPaths;
	}
	
	
	public static Tracker track(Stage stage, String notes, boolean caputureLogDetails){
		return new Tracker(stage, notes, caputureLogDetails, null);
    }
	
	public static Tracker track(Stage stage, boolean caputureLogDetails){
        return track(stage, null, caputureLogDetails);
    }
	
    public static Tracker track(Stage stage, String notes){
        return track(stage, notes, false);
    }
    
    
    public static Tracker track(Stage stage){
        return track(stage, false);
    }
    
    public static Tracker track(Stage stage, String[] messageComboIdPaths){
    	return new Tracker(stage, null, false, messageComboIdPaths);
    }
    
	public void process(Exchange exchange) throws Exception {
		try {
            //set the properties in the exchange
			log.debug("Logging with tracker, begin.");
            Map<String,Object> properties = exchange.getProperties();
            String entity = (String) properties.get(ExchangePreProcessor.ENTITY_NAME);
            String operation = (String) properties.get(ExchangePreProcessor.OPERATION_NAME);
            String coorelationId = (String) properties.get(ExchangePreProcessor.CORRELATION_ID);
            Long begin = (Long) properties.get(ExchangePreProcessor.ENTRED_ON);
            long end = 0;
            String timeTookNotes = "";

            if(coorelationId == null || stage == null || entity == null || operation == null){
                throw new RuntimeException("Cannot log in database. Required fields are missing");
            }
            if(begin != null && begin > 0) {
                end = System.currentTimeMillis() - begin;
                if(end > 1000) {
                    timeTookNotes = "Took " + (end/1000) + " seconds.";
                } else {
                    timeTookNotes = "Took " + end + " milliseconds.";
                }
            }

            String status = "";

			try {
                status = StringUtils.substring(XPathBuilder.xpath("//status/text()").evaluate(exchange, String.class), 0, 100);
			} catch (Exception ignore) {
				log.debug("Ignoring invalid status text from response XML", ignore);
			}

            String actualNotes = timeTookNotes + " " + notes + " " + status;
            IntegrationLog integrationLog = new IntegrationLog(coorelationId, stage, entity, operation, actualNotes);
            log.debug("creating new instance of IntegrationLog with [" + coorelationId+", " + stage+", " + entity+", " + operation+", " + actualNotes + "]");

            IntegrationLogDao integrationLogDao = (IntegrationLogDao)exchange.getContext().getRegistry().lookup("integrationLogDao");
            captureLogDetails(exchange, integrationLog);
            captureLogMessage(exchange, integrationLog);

            integrationLogDao.save(integrationLog);
            if(log.isInfoEnabled() && StringUtils.isNotEmpty(timeTookNotes)){
                log.info(coorelationId + " - " + entity+"#" + operation+ " |" + stage + "|" + timeTookNotes);
            }

        } catch(Exception ex) {
            log.error("Error while tracking exchange", ex);
        }
        
	}


	private void captureLogDetails(Exchange exchange,
			IntegrationLog integrationLog) {
		if(caputureLogDetails){
        	//Check for soap fault
        	String faultString = XPathBuilder.xpath("//faultstring/text()").evaluate(exchange, String.class);
        	if(!StringUtils.isBlank(faultString)){
        		integrationLog.setNotes(SOAP_FAULT_STATUS);
        		integrationLog.addIntegrationLogDetail(new IntegrationLogDetail(null, faultString, true));
        	}
        	
        	//check for caaers error message in response
        	String errorString = XPathBuilder.xpath("//error/text()").evaluate(exchange, String.class);
        	if(!StringUtils.isBlank(errorString)){
        		integrationLog.setNotes(CAAERS_RESPONSE_ERROR);
        		integrationLog.addIntegrationLogDetail(new IntegrationLogDetail(null, errorString, true));
        	}
        	
        	//check for 'com:entityProcessingOutcomes'
        	NodeList nodes = XPathBuilder.xpath("//com:entityProcessingOutcomes")
                    .namespace("com", "http://schema.integration.caaers.cabig.nci.nih.gov/common")
                    .nodeResult()
                    .evaluate(exchange, NodeList.class);

        	if(nodes != null){

	        	for(int i=0 ; i<nodes.getLength() ; i++){
	        		Node outcome = nodes.item(i);
	        		if(StringUtils.equals( outcome.getLocalName(),"entityProcessingOutcome")){
		        		NodeList children = outcome.getChildNodes();
		        		String businessIdentifier = null;
		        		String outcomeMsg = null;
		        		boolean failed = false;
		        		for(int j=0 ; j<children.getLength() ; j++){
		            		Node child = children.item(j);
		            		String childLocalName = child.getLocalName();
		            		if(!StringUtils.isBlank(childLocalName) && childLocalName.equals("businessIdentifier")){
		            			businessIdentifier = child.getFirstChild().getNodeValue();
		            		}else if(!StringUtils.isBlank(childLocalName) && childLocalName.equals("message")){
		            			outcomeMsg = child.getFirstChild() != null ? child.getFirstChild().getNodeValue() : null ;
		            		}else if(!StringUtils.isBlank(childLocalName) && childLocalName.equals("failed")){
		            			failed = new Boolean(child.getFirstChild() != null ? child.getFirstChild().getNodeValue() : "false");
		            		}
		        		}
		        		if(businessIdentifier != null){
		        			integrationLog.addIntegrationLogDetail(new IntegrationLogDetail(businessIdentifier, outcomeMsg, failed));
		        		}
	        		}
	        	}
        	}
        }
	}
	
	private void captureLogMessage(Exchange exchange, IntegrationLog integrationLog) {
        if(messageComboIdPaths !=null && messageComboIdPaths.length>0) {
            String correlationId = (String) exchange.getProperty(ExchangePreProcessor.CORRELATION_ID);
            String message = exchange.getIn().getBody(String.class);
            integrationLog.addIntegrationLogMessage(new IntegrationLogMessage(correlationId, message, stage));
        }
	}
    
    
}
