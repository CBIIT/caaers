/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.api.AdeersReportGenerator;
import gov.nih.nci.cabig.caaers.dao.AdverseEventReportingPeriodDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.PersonContact;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.domain.Reporter;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.User;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportContent;
import gov.nih.nci.cabig.caaers.domain.report.ReportDelivery;
import gov.nih.nci.cabig.caaers.domain.report.ReportTracking;
import gov.nih.nci.cabig.caaers.domain.report.ReportVersion;
import gov.nih.nci.cabig.caaers.domain.repository.ReportRepository;
import gov.nih.nci.cabig.caaers.esb.client.impl.CaaersAdeersMessageBroadcastServiceImpl;
import gov.nih.nci.cabig.caaers.service.workflow.WorkflowService;
import gov.nih.nci.cabig.caaers.tools.mail.CaaersJavaMailSender;
import gov.nih.nci.cabig.caaers.utils.Tracker;
import gov.nih.nci.cabig.ctms.lang.NowFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

/**
 * This class is responsible for submission of an {@link ExpeditedAdverseEventReport}
 * @author Biju Joseph
 * @author Srini Akkala
 *
 */
@Transactional
public class ReportSubmissionService {
	
    
    protected final Log log = LogFactory.getLog(getClass());
    
    protected final static SimpleDateFormat msgDF = new SimpleDateFormat("yyyyMMddHHmmss");

    private NowFactory nowFactory;
    protected CaaersAdeersMessageBroadcastServiceImpl messageBroadcastService;
    protected CaaersJavaMailSender caaersJavaMailSender;
    private AdeersReportGenerator adeersReportGenerator;
    private SchedulerService schedulerService;
    private ReportRepository reportRepository;
    private WorkflowService workflowService;
    
    private ReportDao reportDao;
    private MessageSource messageSource;
    private AdverseEventReportingPeriodDao adverseEventReportingPeriodDao;
    
    public void setAdverseEventReportingPeriodDao(
			AdverseEventReportingPeriodDao adverseEventReportingPeriodDao) {
		this.adverseEventReportingPeriodDao = adverseEventReportingPeriodDao;
	}

	/**
     * This method will generate the PDF and xml content. 
     * @param context
     */
    public void generateReportContent(ReportSubmissionContext context){
    	Report report = context.report;
    	ReportTracking reportTracking = context.report.getLastVersion().getLastReportTracking();
    	
    	//1. generate caaers xml
    	try {    
    		ExpeditedAdverseEventReport aeReport = report.getAeReport();
    		if (report.isWorkflowEnabled() 
    				&& report.getWorkflowId() != null ) {
    			User user = workflowService.findCoordinatingCenterReviewer(report.getWorkflowId());
    			if(user != null) {
    	        	Reporter r = new Reporter();
    	        	r.copy(user);
    	        	aeReport.setReviewer(r);
            	} else{
            		aeReport.setReviewer(aeReport.getReporter());
            	}
            } else {
            	aeReport.setReviewer(aeReport.getReporter());
            }
    		context.caaersXML = adeersReportGenerator.generateCaaersXml(aeReport,report);
    		Tracker.logXmlGeneration(reportTracking, true, "", nowFactory.getNow());
    	} catch (Exception e ) {
    		Tracker.logXmlGeneration(reportTracking, false, e.getMessage(),nowFactory.getNow());
    		throw new RuntimeException(e);
    	}

        //2. generate pdf
        try {
            context.pdfReportPaths = adeersReportGenerator.generateExternalReports(report, context.caaersXML, report.getLastVersion().getId());
            Tracker.logAttachmentGeneration(reportTracking, true, "", nowFactory.getNow());
        } catch (Exception exp) {
            Tracker.logAttachmentGeneration(reportTracking, false, exp.getMessage(), nowFactory.getNow());
            throw new RuntimeException(exp);
        }
    }
    
    /**
     * This method will do the pre submission initializations. 
     *  - Will generate the report deliveries
     *  - Will start the report tracking. 
     *  - Will generate the PDF and XMLs of the report. 
     *  - Will attach report content to report version.
     *  - Will associate AEs to report version.
     * @param context
     */
    public void doPreSubmitReport(ReportSubmissionContext context){
    	
    	Report report = context.report;
    	
    	if(CollectionUtils.isEmpty(report.getReportDeliveries())){
    		List<ReportDelivery> deliveries = reportRepository.findReportDeliveries(report);
    		for(ReportDelivery delivery : deliveries){
    			report.addReportDelivery(delivery);
    		}
    	}
    	
    	// start tracking.
    	ReportTracking reportTracking = new ReportTracking();
    	Tracker.logInitiation(reportTracking, true, "",nowFactory.getNow());
    	context.report.getLastVersion().addReportTracking(reportTracking);

    	generateReportContent(context);
    }
    
    /**
     * This method will do the post submission activities. 
     * This method also could be invoked from ESB response processing (if submission was successful). 
     * @param context
     */
    public void doPostSubmitReport(ReportSubmissionContext context){

        Report report = context.report;
        ReportVersion reportVersion = report.getLastVersion();

    	try {
            generateReportContent(context);

    		//clear off debris from previous submission.
    		reportVersion.clear();

    		// part1 - add the report content to report version
    		for(String pdfFilePath : context.pdfReportPaths){
            	File f = new File(pdfFilePath);
            	if(f.exists() && f.canRead()){
            		ReportContent reportContent = new ReportContent("application/pdf", FileCopyUtils.copyToByteArray(f));
            		reportVersion.addReportContent(reportContent);
            	}
            }
            reportVersion.addReportContent(new ReportContent("text/xml", context.caaersXML.getBytes()));

            // part2 - update the adverse events being reported
            for(AdverseEvent ae : report.getAeReport().getActiveAdverseEvents()){
            	reportVersion.addReportedAdverseEvent(ae);
            }
            
           //update the signatures of adverse events
           report.getAeReport().updateSignatureOfAdverseEvents();
            
            // update signature of duplicate AEs that are not part of the data collection but part of the reporting period.
           ExpeditedAdverseEventReport aereport = report.getAeReport();
           AdverseEventReportingPeriod reportingPeriod = aereport.getReportingPeriod();
           List<AdverseEvent> reportAes = aereport.getAdverseEvents();
           
           List<AdverseEvent> duplicateUnReportedAes = new ArrayList<AdverseEvent>();
           for(AdverseEvent reportAe : reportAes){
        	   duplicateUnReportedAes.addAll(reportingPeriod.findDuplicateAesByAeCtcTerms(reportAe));
           }
           // update signature of all the unreported duplicate AEs and save them
           
           for(AdverseEvent dupAe : duplicateUnReportedAes){
        	   dupAe.setSignature(dupAe.getCurrentSignature());
           }
           
           adverseEventReportingPeriodDao.save(reportingPeriod);
           

    	} catch (Exception e ) {
    		throw new RuntimeException(e);
    	}

    	
    	//un-schedule all the notifications
    	schedulerService.unScheduleNotification(report);
    	
    	//now update the post submission updated date on submitted adverse events. 
    	report.getAeReport().clearPostSubmissionUpdatedDate();
    	
    	//update the reported flag on the adverse events.
    	report.getAeReport().updateReportedFlagOnAdverseEvents();
    	
    	//create child reports
    	reportRepository.createChildReports(report);
    }
    
    

    /**
     * Will do the following.
     *  1. Do the pre submission initializations
     *  2a. Notify the report to external system
     *  2b. Notify email recipients
     *  3. Do post submit activities.
     */
    public void submitReport(Report report) {
    	//create the context
    	ReportSubmissionContext context = ReportSubmissionContext.getSubmissionContext(report);
    	
    	try {
			//do Pre-submission activities
			doPreSubmitReport(context);
			
			//update the reportVersion
			report.setStatus(ReportStatus.INPROCESS);
			report.setSubmittedOn(nowFactory.getNow());
			
			ReportTracking reportTracking = report.getLastVersion().getLastReportTracking();
			
			//now sent the report to the report recipients
			boolean hasSystemDeliveries = report.hasSystemDeliveries();
			if(hasSystemDeliveries){
				//notify first external systems
				try {
					notifyExternalSystems(context);			
				} catch (Exception e) {
					log.error("Error while sending message to service mix ", e);
					report.setStatus(ReportStatus.FAILED);
			        report.setSubmissionMessage("Problem communicating with ESB <br> Please try to resubmit the report <br>" + e.getMessage());
				}
			}else{
                
				//notify email recipients
				try {
					notifyEmailRecipients(context);
					report.setStatus(ReportStatus.COMPLETED);
					//do the post submission
					doPostSubmitReport(context);
				 } catch (Exception e) {
			     	Tracker.logEmailNotification(reportTracking, false, e.getMessage(),nowFactory.getNow());
					log.error("Error while sending email ", e);
					report.setStatus(ReportStatus.FAILED);
					report.setSubmissionMessage("Error  sending email " + e.getMessage());
			     } 
			}
		} catch (Exception e) {
			log.error("Error while trying to submit report",e);
			throw new CaaersSystemException("Unable to submit report", e);
		}
    	
    	//save the report
    	reportDao.save(context.report);
     }
    
    public Set<String> getEmailList(Report r , String submitterEmail){
    	Set<String> emails = new LinkedHashSet<String>();
    	
    	for(String email : r.getEmailRecipients()) {
    		if(email != null && !email.isEmpty()) {
    			emails.add(email.trim());
    		}
    	}
    	
        if(submitterEmail != null && !submitterEmail.isEmpty()) {
        	emails.add(submitterEmail.trim());
        }
        
        emails.remove(""); //remove the empty email if it was added.
        return emails;
    }
    
    /**
     * This method will generate the message content and forwards it to the caaers mail sender.
     * @param context - The submission context
     * @throws Exception
     */
    public void notifyEmailRecipients(ReportSubmissionContext context) throws Exception {
    	Report report = context.report;
    	String[] pdfFilePaths = context.pdfReportPaths;
    	ReportTracking reportTracking = report.getLastVersion().getLastReportTracking();
    	
    	Set<String> emailRecipients = getEmailList(report, null);
    	if(!emailRecipients.isEmpty()){
    		 //if email recipents are there, notify them.
        	ExpeditedAdverseEventReport expeditedAdverseEventReport = report.getAeReport();
            Participant participant = expeditedAdverseEventReport.getAssignment().getParticipant();
            String firstName = participant.getFirstName();
            String lastName = participant.getLastName();
            String pid = participant.getPrimaryIdentifierValue();
            
            Study study = expeditedAdverseEventReport.getStudy();
            String shortTitle = study.getShortTitle();
            String sid = study.getPrimaryIdentifierValue();
            
            String content = messageSource.getMessage("email.submission.content", new Object[]{report.getLabel(), firstName, lastName, pid, shortTitle, sid}, Locale.getDefault());
            String subjectLine = messageSource.getMessage("submission.success.subject", new Object[]{report.getLabel(), pid}, Locale.getDefault());
            caaersJavaMailSender.sendMail(emailRecipients.toArray(new String[0]), subjectLine, content, pdfFilePaths);
            String msg = "Notified to : " ;
        	for (String e:emailRecipients) {
        		msg = msg + "," + e;
        	}
            Tracker.logEmailNotification(reportTracking, true, msg,nowFactory.getNow());
            
    	}
    }

  
    /**
     * This method will notify the external systems.
     * @param context - The report submission context
     * @throws Exception
     */
    
    public void notifyExternalSystems(ReportSubmissionContext context) throws Exception {
    	Report report = context.report;
    	String xml = context.caaersXML;
    	ReportTracking reportTracking = report.getLastVersion().getLastReportTracking();
    	
        List<ReportDelivery> deliveries = report.getExternalSystemDeliveries();
        int reportId = report.getId();
        StringBuilder sb = new StringBuilder();
        sb.append("<EXTERNAL_SYSTEMS>");
        for (ReportDelivery delivery : deliveries) {
            sb.append(delivery.getEndPoint()).append("::").append(delivery.getUserName()).append("::" ).append(delivery.getPassword());
        }
        sb.append("</EXTERNAL_SYSTEMS>");
        sb.append("<CAAERSRID>" + reportId + "</CAAERSRID>");

        String submitterEmail = report.getLastVersion().getSubmitter().getContactMechanisms().get(PersonContact.EMAIL);
        sb.append("<SUBMITTER_EMAIL>" + submitterEmail + "</SUBMITTER_EMAIL>");
        
        String msgComboId = report.getAeReport().getExternalId() + "::" + msgDF.format(report.getAeReport().getCreatedAt());
        sb.append("<MESSAGE_COMBO_ID>" + msgComboId + "</MESSAGE_COMBO_ID>");
        
        //if there are external systems, send message via service mix
    	String externalXml = xml.replaceAll("<AdverseEventReport>", "<AdverseEventReport>" + sb.toString());
    	
    	try {
    		messageBroadcastService.initialize();
    	} catch (Exception e) {
    		Tracker.logConnectionToESB(reportTracking, false, e.getMessage() + " Error initilizing ESB broadcast", nowFactory.getNow());
    		e.printStackTrace();
    		throw new Exception (e);
    	}
    	
    	try {
    		messageBroadcastService.broadcast(externalXml);
    	} catch (Exception e) {
    		Tracker.logConnectionToESB(reportTracking, false, e.getMessage() + " Error Broadcasting to ESB", nowFactory.getNow());
    		e.printStackTrace();
    		throw new Exception (e);
    	}
    	Tracker.logConnectionToESB(reportTracking, true, "", nowFactory.getNow());
    }

	
	@Required
	public void setSchedulerService(final SchedulerService schedulerService) {
	    this.schedulerService = schedulerService;
	}
	@Required
	public void setAdeersReportGenerator(AdeersReportGenerator adeersReportGenerator) {
		this.adeersReportGenerator = adeersReportGenerator;
	}
	
    @Required
    public void setReportDao(final ReportDao reportDao) {
        this.reportDao = reportDao;
    }
    
    @Required
    public void setMessageBroadcastService(
                    CaaersAdeersMessageBroadcastServiceImpl messageBroadcastService) {
        this.messageBroadcastService = messageBroadcastService;
    }
    
    @Required
	public void setCaaersJavaMailSender(CaaersJavaMailSender caaersJavaMailSender) {
		this.caaersJavaMailSender = caaersJavaMailSender;
	}
    
    @Required
    public void setNowFactory(final NowFactory nowFactory) {
        this.nowFactory = nowFactory;
    }
    
    @Required
    public void setReportRepository(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
	}
    
    @Required
    public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
    
    
    
	public WorkflowService getWorkflowService() {
		return workflowService;
	}
	
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}



	/**
	 * This class maintains the submission context, across various template methods.
	 * @author Biju Joseph
	 *
	 */
	public static class ReportSubmissionContext {
		public final Report report;
		public String[] pdfReportPaths;
		public String caaersXML; 
		public boolean asynchronousResponse = false;
		
		private ReportSubmissionContext(Report report) {
			this.report = report;
			pdfReportPaths = new String[0];
		}
		
		public static ReportSubmissionContext getSubmissionContext(Report report){
			return new ReportSubmissionContext(report);
		}
	}
	
	
}
