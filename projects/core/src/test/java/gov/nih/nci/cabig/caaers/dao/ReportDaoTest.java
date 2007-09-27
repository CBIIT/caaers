package gov.nih.nci.cabig.caaers.dao;

import static gov.nih.nci.cabig.caaers.CaaersTestCase.*;
import static gov.nih.nci.cabig.caaers.CaaersUseCase.*;
import gov.nih.nci.cabig.caaers.CaaersUseCases;
import gov.nih.nci.cabig.caaers.DaoTestCase;
import gov.nih.nci.cabig.caaers.dao.report.ReportDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.report.DeliveryStatus;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.domain.report.ReportDelivery;
import gov.nih.nci.cabig.caaers.domain.report.ReportDeliveryDefinition;
import gov.nih.nci.cabig.caaers.domain.report.ScheduledEmailNotification;
import gov.nih.nci.cabig.caaers.domain.report.ScheduledNotification;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:biju.joseph@semanticbits.com">Biju Joseph</a>
 * @author Rhett Sutphin
 */
@CaaersUseCases({CREATE_NOTIFICATION_RULES, CREATE_REPORT_FORMAT,GENERATE_REPORT_FORM})
public class ReportDaoTest extends DaoTestCase<ReportDao> {
	private ReportDao rsDao;
    private TransactionTemplate transactionTemplate;
    private ExpeditedAdverseEventReportDao aeDao;
    private ReportDefinitionDao rpDefDao;

    @Override
    protected void setUp() throws Exception {
		super.setUp();
		rsDao = getDao();

        transactionTemplate = (TransactionTemplate) getApplicationContext().getBean("transactionTemplate");
        aeDao = (ExpeditedAdverseEventReportDao) getApplicationContext().getBean("expeditedAdverseEventReportDao");
        rpDefDao = (ReportDefinitionDao)getApplicationContext().getBean("reportDefinitionDao");
    }

    public void testDomainClass() {
		log.debug("domainClass :" + rsDao.domainClass().getName());
		assertEquals(Report.class.getName(), rsDao.domainClass().getName());
	}

    public void testGetById() throws Exception {
        Report actual = getDao().getById(-223);
        assertDayOfDate("Wrong created on", 2007, Calendar.MAY, 15, actual.getCreatedOn());
        assertDayOfDate("Wrong due on", 2007, Calendar.MAY, 16, actual.getDueOn());
        assertTrue("Should be required", actual.isRequired());
        assertEquals("Wrong def", -222, (int) actual.getReportDefinition().getId());
    }

    public void testSave() {

		Report rs = new Report();
		rs.setAeReport(null);
		//rs.setName("My Sample Report");
		rs.setCreatedOn(new Date());
		rs.setDueOn(new Date());
		rs.setSubmittedOn(new Date());
		rs.setGridId("ADEDR99393939");
		Fixtures.createReportVersion(rs);
		//add deliveries.

		rsDao.save(rs);

		assertNotNull("report id is null",rs.getId());
		assertNotNull("report version id is null",rs.getReportVersions().get(0).getId());
    }


	public void testGetAllByDueDate(){
		List<Report> list = rsDao.getAllByDueDate(new Date());
		log.debug("size::::" + String.valueOf(list));
		for(Report s : list){
			log.debug(s.getId());
		}
	}

	public void testUpdate() {
        final Integer id;
        {
            //obtain a previously saved report
            final Report report = rsDao.getById(-223);
            //obtain a calendar template
            ReportDefinitionDao rctDao = (ReportDefinitionDao)getApplicationContext().getBean("reportDefinitionDao");
            ReportDefinition rc = rctDao.getById(-222);
            report.setReportDefinition(rc);

            //set the scheduled email notification
            ScheduledEmailNotification sen = new ScheduledEmailNotification();
            sen.setBody("Hi this is body content");
            sen.setCreatedOn(new Date());
            sen.setDeliveryStatus(DeliveryStatus.ERROR);
            sen.setFromAddress("from@from.com");
            sen.setGridId("ggg9d9d9d9d");
            sen.setScheduledOn(new Date());
            sen.setToAddress("to@to.com");
            sen.setSubjectLine("This is my subject");
            sen.setPlanedNotificaiton(rc.getPlannedNotifications().get(0));
            report.addScheduledNotification(sen);

            //obtain an AE report
            ExpeditedAdverseEventReport aeReport = aeDao.getById(-1);
            aeReport.addReport(report);
            report.setStatus(ReportStatus.PENDING);
            report.setAeReport(aeReport);

            //save the report
            id = (Integer) transactionTemplate.execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    rsDao.save(report);
                    assertNotNull("ID still null after save", report.getId());
                    return report.getId();
                }
            });
        }

        interruptSession();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Report reloaded = rsDao.getById(id);
                if(reloaded.getScheduledNotifications() != null && reloaded.getScheduledNotifications().size() > 0){
                    ScheduledNotification sn = reloaded.getScheduledNotifications().get(0);
                    assertEquals("ScheduledNotification Body is not the same", sn.getBody(), "Hi this is body content");
                    assertEquals("Subject should be same", "This is my subject", ((ScheduledEmailNotification)sn).getSubjectLine());
                }

                // fetch AE report and see if we can get hold of the report schedule.
                ExpeditedAdverseEventReport aeReport = aeDao.getById(-1);
                Report fromAeReport = aeReport.getReports().get(0);
                assertNotNull(fromAeReport);
                assertEquals("Report obtained from AEReport is not correct", reloaded.getId(), fromAeReport.getId());
                assertEquals(ReportStatus.PENDING, ReportStatus.PENDING);
            }
        });
    }

    public void testDeleteByID(){
		Report rs = new Report();
		rs.setAeReport(null);
		//rs.setName("My Sample Report");
		rs.setCreatedOn(new Date());
		rs.setDueOn(new Date());
		rs.setSubmittedOn(new Date());
		rs.setGridId("ADEDR99393939");

		rsDao.save(rs);
		Integer id = rs.getId();

        interruptSession();

        boolean deleted = rsDao.deleteById(id);
        assertTrue("unable to delete report schedule", deleted);
        assertNull("Deleted report still present", rsDao.getById(id));
    }

    public void testDeleteExistingById() {
        //delete existing object
        Report rs = rsDao.getById(-223);
        boolean deleted = rsDao.deleteById(rs.getId());
        assertTrue("unable to delete report schedule", deleted);
        interruptSession();
        assertNull("Deleted report still present", rsDao.getById(-223));
    }

    public void saveWithReportDelivery(){
    	int id = 0;
    	{
    		ReportDefinition rpDef = rpDefDao.getById(-222);
        	assertNotNull("report definition should not be null", rpDef);
        	List<ReportDeliveryDefinition> deliveries = rpDef.getDeliveryDefinitions();
        	assertNotNull("delivery definitions should not be null", deliveries);
        	assertEquals("there should exist 2 delivery definitions", 2, deliveries.size());
        	final Report report = rpDef.createReport();
        	report.setDueOn(new Date());
        	report.setCreatedOn(new Date());

        	//populate delivery definitions.
        	ReportDelivery rd0 = deliveries.get(0).createReportDelivery();
        	rd0.setEndPoint("100000");
        	report.addReportDelivery(rd0);

        	ReportDelivery rd1 = deliveries.get(1).createReportDelivery();
        	rd1.setEndPoint("111111");
        	report.addReportDelivery(rd1);

        	//populate scheduled notifications.

            ScheduledEmailNotification sen = new ScheduledEmailNotification();
            sen.setBody("Hi this is body content");
            sen.setCreatedOn(new Date());
            sen.setDeliveryStatus(DeliveryStatus.ERROR);
            sen.setFromAddress("from@from.com");
            sen.setGridId("ggg9d9d9d9d");
            sen.setScheduledOn(new Date());
            sen.setToAddress("to@to.com");
            sen.setSubjectLine("This is my subject");
            sen.setPlanedNotificaiton(rpDef.getPlannedNotifications().get(0));
            List<ScheduledNotification> snfList = new ArrayList<ScheduledNotification>();
            snfList.add(sen);
            report.setScheduledNotifications(snfList);

            //obtain an AE report
            ExpeditedAdverseEventReport aeReport = aeDao.getById(-1);
            aeReport.addReport(report);
            report.setStatus(ReportStatus.PENDING);
            report.setAeReport(aeReport);

            //save in transaction.
            id = (Integer) transactionTemplate.execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    rsDao.save(report);
                    assertNotNull("ID still null after save", report.getId());
                    return report.getId();
                }
            });

            assertTrue("Report ID should be greater than 0", id > 0);

    	}
    	final int id2 = id;
        interruptSession();
    	{
    		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    Report reloaded = rsDao.getById(id2);
                    List<ScheduledNotification> snList = reloaded.getScheduledNotifications();
                    assertNotNull("Scheduled Notifications should not be null", snList);
                    assertEquals("Scheduled Notifiction size", 1, snList.size());

                    ScheduledNotification sn = snList.get(0);
                    assertEquals("ScheduledNotification Body is not the same", sn.getBody(), "Hi this is body content");
                    assertEquals("Subject should be same", "This is my subject", ((ScheduledEmailNotification)sn).getSubjectLine());

                    // fetch AE report and see if we can get hold of the report schedule.
                    ExpeditedAdverseEventReport aeReport = aeDao.getById(-1);
                    Report fromAeReport = aeReport.getReports().get(0);
                    assertNotNull(fromAeReport);
                    assertEquals("Report obtained from AEReport is not correct", reloaded.getId(), fromAeReport.getId());
                    assertEquals(ReportStatus.PENDING, ReportStatus.PENDING);

                    //report delivery
                    List<ReportDelivery> deliveries = reloaded.getReportDeliveries();
                    assertNotNull("report delivery should not be null", deliveries);
                    assertEquals("report delivery size wrong", 2, deliveries.size());

                    assertEquals("report delivery end point should be same", "111111" , deliveries.get(1).getEndPoint());
                }
            });
    	}
    }
}
