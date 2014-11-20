/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.esb.client;

import gov.nih.nci.cabig.caaers.api.AdeersReportGenerator;
import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDao;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.domain.Reporter;
import gov.nih.nci.cabig.caaers.domain.User;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportDelivery;
import gov.nih.nci.cabig.caaers.domain.report.ReportDeliveryDefinition;
import gov.nih.nci.cabig.caaers.domain.report.ReportTracking;
import gov.nih.nci.cabig.caaers.domain.report.ReportVersion;
import gov.nih.nci.cabig.caaers.domain.repository.ReportRepository;
import gov.nih.nci.cabig.caaers.service.ReportSubmissionService;
import gov.nih.nci.cabig.caaers.service.ReportSubmissionService.ReportSubmissionContext;
import gov.nih.nci.cabig.caaers.service.SchedulerService;
import gov.nih.nci.cabig.caaers.service.workflow.WorkflowService;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.tools.mail.CaaersJavaMailSender;
import gov.nih.nci.cabig.caaers.utils.Tracker;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is responsible for the post submission activities (currently only AdEERS-response is assumed)
 * 
 * @author Srini
 * @author Biju Joseph 
 *       
 */
/*
 * BJ : Lacking proper test cases
 * BJ : - Changed the subject in email notification. 
 * BJ : - Reading messages from properties file. 
 * BJ : Bean to use proxy factory as mentioned in  	 https://wiki.nci.nih.gov/x/WoY1AQ
 */

public class MessageNotificationService {
    protected Configuration configuration;

    protected ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao;

    
    private SchedulerService schedulerService;
    
    private ReportRepository reportRepository;

    protected final Log log = LogFactory.getLog(getClass());

    private ReportDao reportDao;
    
    protected CaaersJavaMailSender caaersJavaMailSender;
    
    private MessageSource messageSource;
    
    private AdeersReportGenerator adeersReportGenerator;
    
    private WorkflowService workflowService;
    
    private ReportSubmissionService reportSubmisionService;
    
    
    
    @Transactional
    public void sendWithdrawNotificationToReporter(String submitterEmail, String messages,
            String aeReportId, String reportId, boolean success, String ticketNumber,
            String url,boolean communicationError) throws Exception {
    	
        
        Report r = reportDao.getById(Integer.parseInt(reportId));
        Set<String> emails = reportSubmisionService.getEmailList(r,submitterEmail);
        
        
        if (success) {
        	reportRepository.withdrawReport(r);
        } else {
            r.setStatus(ReportStatus.WITHDRAW_FAILED);
            r.setSubmissionMessage(messages);

            reportDao.save(r);
        }
        
        
        String subject = "";
        String attachment = null;
        if (success) {
            subject = messageSource.getMessage("withdraw.success.subject", new Object[]{r.getLabel(), String.valueOf(r.getLastVersion().getId())}, Locale.getDefault());
            
        } else {
        	subject =  messageSource.getMessage("withdraw.failure.subject", new Object[]{r.getLabel(), String.valueOf(r.getLastVersion().getId())}, Locale.getDefault());
        	// send only to submitter incase of failure
        	emails = new HashSet<String>();
        	emails.add(submitterEmail);
        }
        
        log.debug("send email ");
        try {
        	sendMail(emails.toArray(new String[0]), subject, messages, attachment);
        } catch (Exception  e ) {
        	throw new Exception(" Error in sending email , please check the confiuration " , e);
        }
        
    }
    
    @Transactional
    public void sendNotificationToReporter(String submitterEmail, String messages,
                    String aeReportId, String reportId, boolean success, String ticketNumber,
                    String url,boolean communicationError) throws Exception {

        Report report = reportDao.getById(Integer.parseInt(reportId));
        reportDao.initialize(report.getScheduledNotifications());
        ReportVersion reportVersion = report.getLastVersion();

        Set<String> emails = reportSubmisionService.getEmailList(report,submitterEmail);


        ReportTracking rtToUpdate = reportVersion.getLastReportTracking();

        boolean ableToSubmitToWS = true;
        String submissionMessage = "";
        if (communicationError) {
        	ableToSubmitToWS = false;
        	submissionMessage = messages;
        }

        Tracker.logConnectionToExternalSystem(rtToUpdate, ableToSubmitToWS, submissionMessage, new Date());

        reportDao.save(report);

        log.debug("Saving data into report versions table");
        if (success) {
        	report.setAssignedIdentifer(ticketNumber);
        	report.setSubmissionUrl(url);
        	report.setSubmittedOn(new Date());
        	report.setStatus(ReportStatus.COMPLETED);

        	reportVersion.setAssignedIdentifer(ticketNumber);
        	reportVersion.setSubmissionUrl(url);
        	reportVersion.setSubmittedOn(new Date());
        	reportVersion.setReportStatus(ReportStatus.COMPLETED);
        	ReportSubmissionContext context = ReportSubmissionContext.getSubmissionContext(report);
        	reportSubmisionService.doPostSubmitReport(context);

        	Tracker.logSubmissionToExternalSystem(rtToUpdate, true, messages, new Date());

        } else {
        	report.setSubmittedOn(new Date());
        	report.setStatus(ReportStatus.FAILED);

            reportVersion.setSubmittedOn(new Date());
            reportVersion.setReportStatus(ReportStatus.FAILED);
            if (ableToSubmitToWS) {

            	Tracker.logSubmissionToExternalSystem(rtToUpdate, false, messages, new Date());

            }
            //reportTrackingDao.save(rtToUpdate);
        }
        reportVersion.setSubmissionMessage(messages);
        report.setSubmissionMessage(messages);

        reportDao.save(report);
        
        
        String subject = "";
        String attachment = null;
        if (success) {
           // messages = messages + url;
            subject = messageSource.getMessage("submission.success.subject", new Object[]{report.getLabel(),String.valueOf(report.getLastVersion().getId())}, Locale.getDefault());  
            
            //          generating pdf again to get PDF with ticket number ....
            ExpeditedAdverseEventReport aeReport = report.getAeReport();
            if (report.isWorkflowEnabled() && report.getLastVersion().getReportStatus().equals(ReportStatus.COMPLETED)
            		&& report.getWorkflowId() != null) {
            	User user = workflowService.findCoordinatingCenterReviewer(report.getWorkflowId());
            	if(user != null) {
    	        	Reporter r = new Reporter();
    	        	r.copy(user);
    	        	aeReport.setReviewer(r);
            	}
            } else {
            	aeReport.setReviewer(aeReport.getReporter());
            }
            String caaersXML = adeersReportGenerator.generateCaaersXml(aeReport,report);
            String[] pdfReportPaths = adeersReportGenerator.generateExternalReports(report, caaersXML,report.getLastVersion().getId()); 
           
            // CAAERS-6938 do not include attachment if one of the recipients of the report is a system 
            boolean includeAttachment = true;
            
            for(ReportDeliveryDefinition rdd : report.getReportDefinition().getDeliveryDefinitions()){
            	// check whether end point is a system
            	if(rdd.getEntityType() == 1){
            		includeAttachment = false;
            		break;
            	}
            }
            
            if(includeAttachment) {
            	attachment = pdfReportPaths[0] ; //tempDir + "/expeditedAdverseEventReport-" + reportVersion.getId() + ".pdf";
            }
            
           // String tempDir = System.getProperty("java.io.tmpdir");
           // attachment = tempDir + "/expeditedAdverseEventReport-" + reportVersion.getId() + ".pdf";
        } else {
        	subject = messageSource.getMessage("submission.failure.subject", new Object[]{report.getLabel(),String.valueOf(report.getLastVersion().getId())}, Locale.getDefault());
        	// send only to submitter incase of failure
        	emails = new HashSet<String>();
        	emails.add(submitterEmail);
        }
        
        log.debug("send email ");
        try {
        	sendMail(emails.toArray(new String[0]), subject, messages, attachment);
        	String msg = "Notified to : " ;
        	for (String e:emails) {
        		msg = msg + "," + e;
        	}

        	Tracker.logEmailNotificationToSubmitter(rtToUpdate, true, msg, new Date());

        	reportDao.save(report);
        } catch (Exception  e ) {

        	Tracker.logEmailNotificationToSubmitter(rtToUpdate, false, e.getMessage(), new Date());

        	reportDao.save(report);
        	throw new Exception(" Error in sending email , please check the confiuration " , e);
        }
        
    }

	public void sendMail(String[] to, String subject, String content, String attachment) throws Exception {
		
            
		    MimeMessage message = caaersJavaMailSender.createMimeMessage();
		    message.setSubject(subject);
		    message.setFrom(new InternetAddress(configuration.get(Configuration.SYSTEM_FROM_EMAIL)));
		
		    // use the true flag to indicate you need a multipart message
		    MimeMessageHelper helper = new MimeMessageHelper(message, true);
		    helper.setTo(to);
		    helper.setText(content);
		    
			if (attachment != null) {
			    File f = new File(attachment);
			    FileSystemResource file = new FileSystemResource(f);
			    helper.addAttachment(file.getFilename(), file);
			}
		    
		    caaersJavaMailSender.send(message);

	
	 }

	public void setCaaersJavaMailSender(CaaersJavaMailSender caaersJavaMailSender) {
		this.caaersJavaMailSender = caaersJavaMailSender;
	}
	
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setExpeditedAdverseEventReportDao(
                    ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao) {
        this.expeditedAdverseEventReportDao = expeditedAdverseEventReportDao;
    }

    
    public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}


	public void setReportDao(ReportDao reportDao) {
        this.reportDao = reportDao;
    }


	public void setReportRepository(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
	}
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setAdeersReportGenerator(AdeersReportGenerator adeersReportGenerator) {
		this.adeersReportGenerator = adeersReportGenerator;
	}

	public WorkflowService getWorkflowService() {
		return workflowService;
	}
	@Required
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
	

	public ReportSubmissionService getReportSubmisionService() {
		return reportSubmisionService;
	}

	@Required
	public void setReportSubmisionService(ReportSubmissionService reportSubmisionService) {
		this.reportSubmisionService = reportSubmisionService;
	}
}
