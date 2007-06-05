package gov.nih.nci.cabig.caaers.scheduler.runtime;

import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.activemq.thread.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import gov.nih.nci.cabig.caaers.CaaersTestCase;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.ContactMechanism;
import gov.nih.nci.cabig.caaers.domain.Reporter;
import gov.nih.nci.cabig.caaers.domain.notification.ContactMechanismBasedRecipient;
import gov.nih.nci.cabig.caaers.domain.notification.DeliveryStatus;
import gov.nih.nci.cabig.caaers.domain.notification.NotificationBodyContent;
import gov.nih.nci.cabig.caaers.domain.notification.PlannedEmailNotification;
import gov.nih.nci.cabig.caaers.domain.notification.PlannedNotification;
import gov.nih.nci.cabig.caaers.domain.notification.Recipient;
import gov.nih.nci.cabig.caaers.domain.notification.ReportCalendarTemplate;
import gov.nih.nci.cabig.caaers.domain.notification.ReportSchedule;
import gov.nih.nci.cabig.caaers.domain.notification.RoleBasedRecipient;
import gov.nih.nci.cabig.caaers.domain.notification.ScheduledNotification;
import gov.nih.nci.cabig.caaers.domain.notification.TimeScaleUnit;

public class SchedulerServiceImplTest extends CaaersTestCase {
	
	SchedulerServiceImpl service;
	public static ApplicationContext getApplicationContext(){
		String[] locations = new String[] {
				"classpath*:gov/nih/nci/cabig/caaers/applicationContext-*.xml",
	            "classpath*:gov/nih/nci/cabig/caaers/applicationContext-scheduler*.xml"
	        };
		return new ClassPathXmlApplicationContext(locations);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		service = (SchedulerServiceImpl)getApplicationContext().getBean("schedulerService");
		replayMocks();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		verifyMocks();
	}

	

	public void testGetScheduler() {
		assertNotNull("Scheduler should not be null", service.getScheduler());
	}

	public void testGetJobClassMapping() {
		assertNotNull("Scheduler Mapping should not be null", service.getJobClassMapping());
	}

	public void testGetReportScheduleDao() {
		assertNotNull("ReportScheduleDao should not be null", service.getReportScheduleDao());
		System.out.println(service.getReportScheduleDao().getClass().getName());
	}
	public void testScheduleNotification() throws Exception{
		scheduleJobForReport(-444);
		//verify the deliver status. 
		
		ReportSchedule rs = service.getReportScheduleDao().getById(-444);
		for(ScheduledNotification snf : rs.getScheduledNotifications()){
			assertEquals("Delivery status should be delivered", snf.getDeliveryStatus(), DeliveryStatus.DELIVERED);
		}
	}
	public void testScheduleNotificationWithJobDelete() throws Exception{
		scheduleJobForReport(-885);
		//verify delivery status
		ReportSchedule rs = service.getReportScheduleDao().getById(-885);
		int i = 0;
		for(ScheduledNotification snf : rs.getScheduledNotifications()){
			if(i == 0)
				assertEquals("Delivery status should be delivered for fist Scheduled Notificaiton", snf.getDeliveryStatus(), DeliveryStatus.DELIVERED);
			else
				assertEquals("Delivery status should be recalled", snf.getDeliveryStatus(), DeliveryStatus.RECALLED);
		i++;
		}
	}
	
	public void scheduleJobForReport(int reportId) throws Exception{
		assertNotNull( "SchedulerService is not valid",service);
		final ReportSchedule reportSchedule = service.getReportScheduleDao().getById(reportId);
		System.out.println("========== going to schedule ==================== [reportId :" + reportId +"]");
		 service.scheduleNotification(reportSchedule);
		Thread t = new Thread(){
			public void run(){
				try {
					Thread.sleep(1000 * 2); //delay 20 seconds, so that the job properly setup in quartz
					while(true){
						
						String[] jobNames = service.getScheduler().getJobNames("JG-" + reportSchedule.getId());
						
						if(jobNames == null || jobNames.length < 1) //if no scheduled jobs, quit!!!!
							break;
						System.out.println(Arrays.asList(jobNames));
						Thread.sleep(1000 * 30); //poll every 30 seconds, for the scheduled jobs in the Scheduler
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//run
		};
		
		t.start();
		System.out.println("========= started waiter thread ===================");
		t.join();
	}
	
	
}
