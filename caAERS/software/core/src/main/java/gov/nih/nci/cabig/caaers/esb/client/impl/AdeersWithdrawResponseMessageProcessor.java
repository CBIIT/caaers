/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.esb.client.impl;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.esb.client.ResponseMessageProcessor;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;

import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.transaction.annotation.Transactional;
/**
 * Processes withdraw message responses. 
 * 
 * 
 * @author Srini
 * @author Biju Joseph
 *
 */

/*
 * Note : BJ : TODO modify the testcases checked in at r10219 to suite this class. 
 * 
 * BJ : Made to read text messages from properties file. 
 * BJ : changed all exception printing to log statements
 * BJ : changed to call at the end to "sendWithdrawNotificationToReporter"
 * BJ : Removed unwanted TO-DO comments. 
 */
public class AdeersWithdrawResponseMessageProcessor extends ResponseMessageProcessor{
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	@Transactional
	public void processMessage(String message) throws CaaersSystemException {

        if(log.isDebugEnabled()) {
            log.debug("message received :\n" + message );
        }

        
        Element cancelInfo = this.getResponseElement(message,"withdrawAEReportResponse","AEReportCancelInfo");
        Namespace emptyNS=null;
        Namespace ctepNS = null;
        for (Object obj:cancelInfo.getChildren()) {
				Element e = (Element)obj;
				if (e.getName().equals("CAEERS_AEREPORT_ID")) {
					emptyNS = e.getNamespace();
				}
 				if (e.getName().equals("reportStatus")) {
 					ctepNS = e.getNamespace();
 				} 	
		}
        
        String caaersAeReportId = cancelInfo.getChild("CAEERS_AEREPORT_ID",emptyNS).getValue();
        String reportId = cancelInfo.getChild("CAAERSRID",emptyNS).getValue();
        String submitterEmail = cancelInfo.getChild("SUBMITTER_EMAIL",emptyNS).getValue();
        Report r = reportDao.getById(Integer.parseInt(reportId));
        
        //FIXME: When updating Caaers to send to multiple systems the below must also be changed.
        //Can just use the first system as that is the only one that is used.
        String sysName = r.getExternalSystemDeliveries().get(0).getReportDeliveryDefinition().getEntityName();
//      buld error messages
        StringBuffer sb = new StringBuffer();

        boolean success = true;
        boolean communicationError = false;
        String ticketNumber = "";
        String url = "";

        try {

            List<Element> exceptions = cancelInfo.getChildren("jobExceptions",ctepNS);
            Element reportStatus = cancelInfo.getChild("reportStatus",ctepNS);
            
            if (reportStatus.getValue().equals("SUCCESS")) {
            	ticketNumber = cancelInfo.getChild("ticketNumber",ctepNS).getValue();
                String withdrawSuccessMessage = messageSource.getMessage("successful.reportWithdraw.message", new Object[]{String.valueOf(r.getLastVersion().getId()), ticketNumber}, Locale.getDefault());
                sb.append(withdrawSuccessMessage);
                
            }else{
            	StringBuffer exceptionMsgBuffer = new StringBuffer();
            	if (CollectionUtils.isNotEmpty(exceptions)) {
                    success = false;
                    for (Element ex : exceptions) {
                    	exceptionMsgBuffer.append(ex.getChild("code").getValue()).append( "  -  ").append(ex.getChild("description").getValue()).append("\n");
                       
                    	if (ex.getChild("code").getValue().equals("caAERS-adEERS : COMM_ERR")) {
                    		communicationError=true;
                        }
                    }
                }
            	
            	String withdrawFailureMessage = messageSource.getMessage("failed.reportWithdraw.message", new Object[]{String.valueOf(r.getLastVersion().getId()), exceptionMsgBuffer.toString(), sysName}, Locale.getDefault());
                sb.append(withdrawFailureMessage);
            	
            }
            
         // append additional report information
        	String reportDetails = messageSource.getMessage("additional.successful.reportSubmission.information",  new Object[] {r.getSubmitter().getFullName(), 
   				 r.getSubmitter().getEmailAddress(), r.getAeReport().getStudy().getPrimaryIdentifier().getValue(), r.getAeReport()
				 .getParticipant().getPrimaryIdentifierValue(), r.getCaseNumber(),String.valueOf(r.getId()),ticketNumber, configuration.get(Configuration.SYSTEM_NAME)}, Locale.getDefault());
        	sb.append(reportDetails);

            
            if (cancelInfo.getChild("comments",ctepNS) != null) {
            	 String commentsMessage = messageSource.getMessage("comments.reportWithdraw.message", new Object[]{cancelInfo.getChild("comments",ctepNS).getValue()}, Locale.getDefault());
                 sb.append(commentsMessage);
            }

        } catch (Exception e) {
           log.error("Error while retrieving data from AdEERS response message",  e);
        }

        String emailMessage = sb.toString();

        // Notify submitter

        try {
        	
            if(log.isDebugEnabled() ) {
                log.debug("Sending notification to [" + submitterEmail + "] : \n" + emailMessage);
            }
            getMessageNotificationService().sendWithdrawNotificationToReporter(submitterEmail, emailMessage, caaersAeReportId, reportId, success, ticketNumber, url, communicationError);
            
        } catch (Exception e) {
           log.error("Error while sending notification", e);
        }       
	}




}
