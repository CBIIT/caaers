/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service;

import org.apache.commons.lang.StringUtils;

import gov.nih.nci.cabig.caaers.api.AdeersReportGenerator;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.PersonContact;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.domain.Submitter;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportDelivery;
import gov.nih.nci.cabig.caaers.esb.client.impl.CaaersAdeersMessageBroadcastServiceImpl;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.tools.mail.CaaersJavaMailSender;

import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/**
 * This service is used to withdraw report from external agency, via ServiceMix
 * @author Biju Joseph
 *
 */
@Transactional
public class ReportWithdrawalService {
	
	private AdeersReportGenerator adeersReportGenerator;
	protected CaaersAdeersMessageBroadcastServiceImpl messageBroadcastService;
    protected CaaersJavaMailSender caaersJavaMailSender;
    private MessageSource messageSource;
    private Configuration configuration;
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}


	/**
	 * This method will withdraw the report from external agency, by delegating the call to service mix. 
	 * @param aeReport - Report to withdraw from external agency
     * @param  reportToWithdraw - the report to withdraw
	 */
	public void withdrawExternalReport(ExpeditedAdverseEventReport aeReport , Report reportToWithdraw){
		
		assert !reportToWithdraw.getStatus().equals(ReportStatus.WITHDRAWN) : "Cannot withdraw a report that is already withdrawn";
		//		send message to service mix.
		String caaersXML="";
		try {    		
    		caaersXML = adeersReportGenerator.generateCaaersWithdrawXml(aeReport,reportToWithdraw);
    		//caaersXML = adeersReportGenerator.generateCaaersXml(aeReport,reportToWithdraw);
    		notifyExternalSystems(caaersXML,reportToWithdraw);
    	} catch (Exception e ) {
    		throw new RuntimeException(e);
    	}
	}


    /**
     * This method will notify any external system of the report being generated.
     * @param xml
     * @param report
     * @throws Exception
     */
    public void notifyExternalSystems(String xml,Report report) throws Exception {
        List<ReportDelivery> deliveries = report.getExternalSystemDeliveries();
        int reportId = report.getId();
        StringBuilder sb = new StringBuilder();
        String systemName = "UNKNOWN";
        sb.append("<EXTERNAL_SYSTEMS>");
        //FIXME: Shared code with Report Submision service.
        for (ReportDelivery delivery : deliveries) {
            sb.append(delivery.getEndPoint()).append("::").append(delivery.getUserName()).append("::" ).append(delivery.getPassword());
            systemName = delivery.getReportDeliveryDefinition().getEntityName();
            sb.append("::" ).append(systemName);
        }
        sb.append("</EXTERNAL_SYSTEMS>");
        sb.append(String.format("<CAAERSRID>%s</CAAERSRID>", reportId));

        String submitterEmail = report.getLastVersion().getSubmitter().getContactMechanisms().get(PersonContact.EMAIL);
        sb.append(String.format("<SUBMITTER_EMAIL>%s</SUBMITTER_EMAIL>", submitterEmail));

        SimpleDateFormat msgDF = new SimpleDateFormat("yyyyMMddHHmmss");

        if(StringUtils.isNotEmpty(report.getAeReport().getExternalId())) {
            String msgComboId = report.getAeReport().getExternalId() + "::" + msgDF.format(report.getAeReport().getCreatedAt());
            sb.append(String.format("<MESSAGE_COMBO_ID>%s</MESSAGE_COMBO_ID>", msgComboId));
        }
        String[] coorelationids = report.getCorrelationIds();
        if(coorelationids != null && coorelationids.length > 0) {
            sb.append(String.format("<CORRELATION_ID>%s</CORRELATION_ID>", coorelationids[coorelationids.length - 1]));
        }
        sb.append(String.format("<SYSTEM_NAME>%s</SYSTEM_NAME>", systemName));
        sb.append("<WITHDRAW>true</WITHDRAW>");
        //if there are external systems, send message via service mix
    	String externalXml = xml.replaceAll("<AdverseEventReport>", "<AdverseEventReport>" + sb.toString());
    	

    	try {
    		messageBroadcastService.initialize();
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new Exception (e);
    	}
    	
    	try {
    		messageBroadcastService.broadcast(externalXml);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new Exception (e);
    	}
    }

    public void sendWithdrawEmail(Report report) {
        //CAAERS-6833 - this is a new feature, to send notification on internal withdrawals.
        List<String> emailRecipients = new ArrayList<String>();
        emailRecipients.add(report.getReporter().getEmailAddress());
        String subjectLine = messageSource.getMessage("withdraw.internal.success.subject", new Object[]{report.getLabel()}, Locale.getDefault());
        StringBuilder content = new StringBuilder(messageSource.getMessage("successful.internal.reportWithdraw.message", new Object[]{report.getLabel()}, Locale.getDefault()));
        // append additional report information
        final Submitter submiter = report.getSubmitter();
        String fullName = "UNKNOWN";
        String subEmail = "UNKNOWN";
        if(submiter != null) {
        	fullName = submiter.getFullName();
        	subEmail = submiter.getEmailAddress();
        }
    	String reportDetails = messageSource.getMessage("additional.successful.internal.reportWithdrawl.information",  new Object[] {fullName, 
    		subEmail, report.getAeReport().getStudy().getPrimaryIdentifier().getValue(), report.getAeReport().getParticipant().getPrimaryIdentifierValue(),
			report.getCaseNumber(),String.valueOf(report.getId()),report.getAssignedIdentifer(),report.getLastVersion().getAmendmentNumber(),
			configuration.get(Configuration.SYSTEM_NAME)}, Locale.getDefault());
    	content.append(reportDetails);
        
        // append Help Text message
    	String helpText = messageSource.getMessage("additional.successful.internal.reportWithdrawl.valdiction", new Object[]{}, Locale.getDefault());
    	content.append(helpText);
        
        caaersJavaMailSender.sendMail(emailRecipients.toArray(new String[0]), subjectLine, content.toString(), new String[0]);
    }
    
	public void setAdeersReportGenerator(AdeersReportGenerator adeersReportGenerator) {
		this.adeersReportGenerator = adeersReportGenerator;
	}
	
	
	public void setMessageBroadcastService(
			CaaersAdeersMessageBroadcastServiceImpl messageBroadcastService) {
		this.messageBroadcastService = messageBroadcastService;
	}

    public void setCaaersJavaMailSender(CaaersJavaMailSender caaersJavaMailSender) {
        this.caaersJavaMailSender = caaersJavaMailSender;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
