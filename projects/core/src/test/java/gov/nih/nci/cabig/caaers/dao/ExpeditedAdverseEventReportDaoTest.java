package gov.nih.nci.cabig.caaers.dao;

import static gov.nih.nci.cabig.caaers.CaaersTestCase.*;
import static gov.nih.nci.cabig.caaers.CaaersUseCase.*;
import gov.nih.nci.cabig.caaers.CaaersUseCases;
import gov.nih.nci.cabig.caaers.DaoTestCase;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Lab;
import gov.nih.nci.cabig.caaers.domain.LabValue;
import gov.nih.nci.cabig.caaers.domain.CtcTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.Grade;
import gov.nih.nci.cabig.caaers.domain.Hospitalization;
import gov.nih.nci.cabig.caaers.domain.ConcomitantMedication;
import gov.nih.nci.cabig.caaers.domain.Attribution;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.TreatmentAssignment;
import gov.nih.nci.cabig.caaers.domain.TreatmentInformation;
import gov.nih.nci.cabig.caaers.domain.CourseAgent;
import gov.nih.nci.cabig.caaers.domain.DelayUnits;
import gov.nih.nci.cabig.caaers.domain.AdverseEventResponseDescription;
import gov.nih.nci.cabig.caaers.domain.PostAdverseEventStatus;
import gov.nih.nci.cabig.caaers.domain.Reporter;
import gov.nih.nci.cabig.caaers.domain.ReportPerson;
import gov.nih.nci.cabig.caaers.domain.Physician;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.DiseaseHistory;
import gov.nih.nci.cabig.caaers.domain.ParticipantHistory;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.attribution.ConcomitantMedicationAttribution;
import gov.nih.nci.cabig.ctms.lang.DateTools;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.sql.Timestamp;
import java.math.BigDecimal;

import static edu.nwu.bioinformatics.commons.testing.CoreTestCase.*;
import edu.nwu.bioinformatics.commons.testing.CoreTestCase;
import edu.nwu.bioinformatics.commons.DateUtils;

/**
 * @author Rhett Sutphin
 */
@CaaersUseCases({ CREATE_EXPEDITED_REPORT})
public class ExpeditedAdverseEventReportDaoTest extends DaoTestCase<ExpeditedAdverseEventReportDao> {
    private CtcTermDao ctcTermDao = (CtcTermDao) getApplicationContext().getBean("ctcTermDao");
    private StudyParticipantAssignmentDao assignmentDao
        = (StudyParticipantAssignmentDao) getApplicationContext().getBean("studyParticipantAssignmentDao");
    private AgentDao agentDao = (AgentDao) getApplicationContext().getBean("agentDao");
    private ReportDefinitionDao reportDefinitionDao
        = (ReportDefinitionDao) getApplicationContext().getBean("reportDefinitionDao");

    public void testGet() throws Exception {
        ExpeditedAdverseEventReport loaded = getDao().getById(-1);
        assertEquals("Wrong AE 0", -70, (int) loaded.getAdverseEvents().get(0).getId());
        assertEquals("Wrong AE 1", -11, (int) loaded.getAdverseEvents().get(1).getId());
        assertEquals("Wrong assignment", -14, (int) loaded.getAssignment().getId());
        CoreTestCase.assertDayOfDate("Wrong created at (date)", 2004, Calendar.SEPTEMBER, 4,
            loaded.getCreatedAt());
        CoreTestCase.assertTimeOfDate("Wrong created at (time)", 13, 15, 30, 0,
            loaded.getCreatedAt());
    }

    public void testGetLabs() throws Exception {
        ExpeditedAdverseEventReport loaded = getDao().getById(-1);
        assertEquals("Wrong number of labs", 3, loaded.getLabs().size());
        assertEquals("Wrong lab 0", -21, (int) loaded.getLabs().get(0).getId());
        assertEquals("Wrong lab 1", -20, (int) loaded.getLabs().get(1).getId());
        assertEquals("Wrong lab 2", -22, (int) loaded.getLabs().get(2).getId());

        Lab l1 = loaded.getLabs().get(1);
        assertSame("Wrong report", loaded, l1.getReport());
        assertEquals("Wrong name", "Extent", l1.getName());
        assertEquals("Wrong units", "hectares/liter", l1.getUnits());
        assertLabValue("Wrong baseline", "3.66", 2003, Calendar.APRIL, 17, l1.getBaseline());
        assertLabValue("Wrong nadir", "0.4", 2007, Calendar.MARCH, 14, l1.getNadir());
        assertLabValue("Wrong nadir", "3.54", 2007, Calendar.MARCH, 19, l1.getRecovery());
    }

    private static void assertLabValue(
        String message, String expectedValue, int expectedYear, int expectedMonth, int expectedDay,
        LabValue actual
    ) {
        assertDayOfDate(prependMessage(message) + "wrong date",
            expectedYear, expectedMonth, expectedDay, actual.getDate());
        assertEquals(prependMessage(message) + "wrong value", expectedValue, actual.getValue());
    }

    public void testGetConcomitantMedications() throws Exception {
        ExpeditedAdverseEventReport loaded = getDao().getById(-1);
        assertEquals("Wrong number of concomitant meds", 2, loaded.getConcomitantMedications().size());
        assertEquals("Wrong con med 0", -31, (int) loaded.getConcomitantMedications().get(0).getId());
        assertEquals("Wrong con med 1", -30, (int) loaded.getConcomitantMedications().get(1).getId());

        ConcomitantMedication cm1 = loaded.getConcomitantMedications().get(1);
        assertSame("Wrong report", loaded, cm1.getReport());
        assertNull("Wrong agent name", cm1.getAgentName());
    }

    public void testGetTreatmentInformation() throws Exception {
        ExpeditedAdverseEventReport loaded = getDao().getById(-1);
        assertNotNull("No treatment info", loaded.getTreatmentInformation());

        TreatmentInformation actual = loaded.getTreatmentInformation();
        assertEquals("Wrong treatment information", -10, (int) actual.getId());
        assertDayOfDate("Wrong first course date", 2005, Calendar.JUNE, 4, actual.getFirstCourseDate());
        assertDayOfDate("Wrong adverse event course date", 2006, Calendar.JULY, 9, actual.getAdverseEventCourse().getDate());
        assertEquals("Wrong adverse event course number", 8, (int) actual.getAdverseEventCourse().getNumber());

        assertEquals("Wrong number of course agents", 2, actual.getCourseAgents().size());
        assertEquals("Wrong course agent 0", -19, (int) actual.getCourseAgents().get(0).getId());
        assertNotNull("Worng association to TreatmentAssignment", actual.getTreatmentAssignment());
        assertEquals("Wrong treatmentAssignment code", "TAC010" ,actual.getTreatmentAssignment().getCode());

        CourseAgent agent1 = actual.getCourseAgents().get(1);
        assertEquals("Wrong course agent 1", -20, (int) agent1.getId());
        assertEquals("Wrong delay in minutes", new BigDecimal(240), agent1.getAdministrationDelay());
        assertEquals("Wrong delay amount", new BigDecimal(4), agent1.getAdministrationDelayAmount());
        assertEquals("Wrong delay units", DelayUnits.HOURS, agent1.getAdministrationDelayUnits());
        assertEquals("Wrong dose amount", new BigDecimal("17.4"), agent1.getDose().getAmount());
        assertEquals("Wrong dose units", "mg", agent1.getDose().getUnits());
        assertEquals("Wrong dose route", "aural", agent1.getDose().getRoute());
        assertEquals("Wrong duration", "8 times every third hour", agent1.getDurationAndSchedule());

        assertEquals("Wrong modified dose amount", new BigDecimal("10"), agent1.getModifiedDose().getAmount());
        assertEquals("Wrong modified dose units", "mg", agent1.getModifiedDose().getUnits());
        assertEquals("Wrong modified dose route", "aural", agent1.getModifiedDose().getRoute());

        assertEquals("Wrong total dose", new BigDecimal("7"), agent1.getTotalDoseAdministeredThisCourse());
        assertDayOfDate("Wrong last administered date", 2006, Calendar.JULY, 10, agent1.getLastAdministeredDate());
    }

    public void testGetOtherCauses() throws Exception {
        ExpeditedAdverseEventReport loaded = getDao().getById(-1);

        assertEquals("Wrong number of causes", 3, loaded.getOtherCauses().size());
        assertEquals("Wrong cause 0", -81, (int) loaded.getOtherCauses().get(0).getId());
        assertEquals("Wrong cause 1", -80, (int) loaded.getOtherCauses().get(1).getId());
        assertEquals("Wrong cause 2", -82, (int) loaded.getOtherCauses().get(2).getId());

        assertEquals("Wrong text for cause 1", "Crossed against light",
            loaded.getOtherCauses().get(1).getText());
    }

    public void testGetResponseDescription() throws Exception {
        AdverseEventResponseDescription actual = getDao().getById(-1).getResponseDescription();
        assertEquals("Wrong present status", PostAdverseEventStatus.RECOVERED_WITH_SEQUELAE,
            actual.getPresentStatus());
        assertEquals("Wrong event description", "It was real bad", actual.getEventDescription());
        assertEquals("Wrong retreated flag", Boolean.FALSE, actual.getRetreated());
        assertDayOfDate("Wrong date removed", 2012, Calendar.MARCH, 4, actual.getRecoveryDate());
    }

    public void testGetReports() throws Exception {
        List<Report> actual = getDao().getById(-1).getReports();
        assertNotNull(actual);
        assertEquals("Wrong number of reports", 2, actual.size());
        assertEquals("Wrong report 0", -40, (int) actual.get(0).getId());

        Report actualReport1 = actual.get(1);
        assertNotNull(actualReport1);
        assertEquals("Wrong report 1", -41, (int) actualReport1.getId());
        assertEquals("Wrong def for report 1", -30,
            (int) actualReport1.getReportDefinition().getId());
        assertDayOfDate("Wrong due date for report 1", 2007, Calendar.MAY, 5, actualReport1.getDueOn());
    }

    public void testGetReporter() throws Exception {
        Reporter actual = getDao().getById(-1).getReporter();
        assertNotNull("No reporter", actual);
        assertEquals("Wrong reporter", -100, (int) actual.getId());
        assertEquals("DiMaggio", actual.getLastName());
        assertEquals("Wrong number of contact mechanisms", 2, actual.getContactMechanisms().size());
        assertEquals("joltin@joe.com", actual.getContactMechanisms().get(ReportPerson.EMAIL));
        assertEquals("212 555-1212", actual.getContactMechanisms().get(ReportPerson.PHONE));
    }

    public void testGetPhysician() throws Exception {
        Physician actual = getDao().getById(-1).getPhysician();
        assertNotNull("No physician", actual);
        assertEquals("Wrong reporter", -101, (int) actual.getId());
        assertEquals("Sandpiper", actual.getLastName());
        assertEquals("Wrong number of contact mechanisms", 0, actual.getContactMechanisms().size());
    }

    public void testGetDiseaseHistory() throws Exception {
        DiseaseHistory actual = getDao().getById(-1).getDiseaseHistory();
        assertNotNull("No disease history", actual);
        assertEquals("Wrong history", -53, (int) actual.getId());
        assertEquals("Wrong primary disease site", -1, (int) actual.getCodedPrimaryDiseaseSite().getId());
        assertEquals("Wrong disease from study", -4, (int) actual.getCtepStudyDisease().getId());
        assertDayOfDate("Wrong diagnosis date", 2007, Calendar.JANUARY, 4, actual.getDiagnosisDate());
        assertEquals("Wrong number of metastatic disease sites", 1, actual.getMetastaticDiseaseSites().size());
        assertEquals("Wrong metastatic disease site", -5, (int) actual.getMetastaticDiseaseSites().get(0).getId());
    }

    public void testGetParticipantHistory() throws Exception {
        ParticipantHistory actual = getDao().getById(-1).getParticipantHistory();
        assertNotNull("No participant history", actual);
        assertEquals("Wrong history", -57, (int) actual.getId());
        assertEquals("Wrong height", new BigDecimal("134.3"), actual.getHeight().getQuantity());
        assertEquals("Wrong height unit", "cm", actual.getHeight().getUnit());
        assertEquals("Wrong weight", new BigDecimal("54.2"), actual.getWeight().getQuantity());
        assertEquals("Wrong weight unit", "kg", actual.getWeight().getUnit());
        assertEquals("Wrong baseline", "About here", actual.getBaselinePerformanceStatus());
    }

    public void testSaveBasics() throws Exception {
        doSaveTest(new SaveTester() {
            public void setupReport(ExpeditedAdverseEventReport report) {
                CtcTerm term = ctcTermDao.getById(3012);
                AdverseEvent event0 = new AdverseEvent();
                event0.setGrade(Grade.MILD);
                event0.setAdverseEventCtcTerm(Fixtures.createAdverseEventCtcTerm(event0, term));
                event0.setExpected(Boolean.FALSE);
                event0.setHospitalization(Hospitalization.PROLONGED_HOSPITALIZATION);
                event0.setStartDate(new Timestamp(DateUtils.createDate(2004, Calendar.APRIL, 25).getTime() + 600000));

                AdverseEvent event1 = new AdverseEvent();
                event1.setGrade(Grade.SEVERE);
                event1.setAdverseEventCtcTerm(Fixtures.createAdverseEventCtcTerm(event0, term));
                event1.setExpected(Boolean.FALSE);
                event1.setHospitalization(Hospitalization.HOSPITALIZATION);

                report.getAdverseEvents().clear();
                report.addAdverseEvent(event0);
                report.addAdverseEvent(event1);
                report.setAssignment(assignmentDao.getById(-14));

            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                assertEquals("Wrong assignment", -14, (int) loaded.getAssignment().getId());

                assertEquals("Wrong number of AEs", 2, loaded.getAdverseEvents().size());
                AdverseEvent loadedEvent0 = loaded.getAdverseEvents().get(0);
                assertNotNull("Cascaded AE not found", loadedEvent0);
                assertEquals("Wrong grade", Grade.MILD, loadedEvent0.getGrade());
                assertEquals("Wrong CTC term", 3012, (int) loadedEvent0.getAdverseEventCtcTerm().getCtcTerm().getId());
                assertNotNull("No report", loadedEvent0.getReport());
                assertEquals("Wrong hospitalization", Hospitalization.PROLONGED_HOSPITALIZATION, loadedEvent0.getHospitalization());
                assertEquals("Wrong expectedness", Boolean.FALSE, loadedEvent0.getExpected());
                assertNotNull("Second cascaded AE not found", loaded.getAdverseEvents().get(1));
            }
        });
    }

    public void testSaveNewReportWithConMedWithAttribution() throws Exception {
        doSaveTest(new SaveTester() {
            public void setupReport(ExpeditedAdverseEventReport report) {
                ConcomitantMedication conMed = report.getConcomitantMedications().get(0);
                conMed.setAgentName("agentName");
                AdverseEvent ae0 = report.getAdverseEvents().get(0);
                report.getAdverseEvents().get(0).getConcomitantMedicationAttributions()
                    .add(new ConcomitantMedicationAttribution());
                ConcomitantMedicationAttribution conMedAttrib
                    = ae0.getConcomitantMedicationAttributions().get(0);
                conMedAttrib.setCause(conMed);
                conMedAttrib.setAttribution(Attribution.PROBABLE);
            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                assertNotNull("Report not found", loaded);

                assertEquals(1, loaded.getConcomitantMedications().size());

                List<ConcomitantMedicationAttribution> attribs
                    = loaded.getAdverseEvents().get(0).getConcomitantMedicationAttributions();
                assertEquals(1, attribs.size());
                assertEquals("Wrong number of con med attribs", "agentName",
                     attribs.get(0).getCause().getAgentName());
            }
        });
    }

    public void testSaveNewReportWithTreatment() throws Exception {
        doSaveTest(new SaveTester() {
            public void setupReport(ExpeditedAdverseEventReport report) {
                TreatmentInformation ti = new TreatmentInformation();
                ti.getAdverseEventCourse().setDate(DateUtils.createDate(2006, Calendar.MAY, 4));
                ti.getAdverseEventCourse().setNumber(4);
                ti.setFirstCourseDate(DateTools.createDate(2005, Calendar.JULY, 30));
                ti.getCourseAgents().get(0).setAdministrationDelay(new BigDecimal(480));
                ti.getCourseAgents().get(0).getDose().setAmount(new BigDecimal("45.2"));
                //TODO: load the treatmentAssignment and add it, before saving.
                report.setTreatmentInformation(ti);
            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                TreatmentInformation ti = loaded.getTreatmentInformation();
                assertNotNull("Should have treatment info", ti);
                assertDayOfDate("Wrong first course date", 2005, Calendar.JULY, 30,
                    ti.getFirstCourseDate());
                assertEquals("Wrong AE course number", 4,
                    (int) ti.getAdverseEventCourse().getNumber());
                assertDayOfDate("Wrong AE course date", 2006, Calendar.MAY, 4,
                    ti.getAdverseEventCourse().getDate());

                assertEquals("Wrong number of course agents", 1, ti.getCourseAgents().size());
                CourseAgent ca = ti.getCourseAgents().get(0);
                assertEquals(new BigDecimal(480), ca.getAdministrationDelay());
                assertEquals(new BigDecimal("45.2"), ca.getDose().getAmount());
            }
        });
    }

    public void testSaveOtherCauses() throws Exception {
        doSaveTest(new SaveTester() {
            public void setupReport(ExpeditedAdverseEventReport report) {
                report.getOtherCauses().get(0).setText("Insomnia");
                report.getOtherCauses().get(1).setText("Bus");
            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                assertEquals("Wrong number of other causes", 2, loaded.getOtherCauses().size());
                assertEquals("Wrong cause 0", "Insomnia", loaded.getOtherCauses().get(0).getText());
                assertEquals("Wrong cause 1", "Bus", loaded.getOtherCauses().get(1).getText());
            }
        });
    }

    public void testSaveNewContactMechanisms() throws Exception {
        doSaveTest(new SaveTester() {
            public void setupReport(ExpeditedAdverseEventReport report) {
                report.getPhysician().getContactMechanisms().put("phone", "312-333-2100");
            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                assertEquals(2, loaded.getPhysician().getContactMechanisms().size());
                assertEquals("312-333-2100", loaded.getPhysician().getContactMechanisms().get("phone"));
            }
        });
    }

    public void testDeleteContactMechanism() throws Exception {
        {
            ExpeditedAdverseEventReport report = getDao().getById(-1);
            assertEquals(2, report.getReporter().getContactMechanisms().size());
            report.getReporter().getContactMechanisms().remove("e-mail");
            assertEquals("Not removed from memory copy", 1, report.getReporter().getContactMechanisms().size());
            getDao().save(report);
        }

        interruptSession();

        ExpeditedAdverseEventReport reloaded = getDao().getById(-1);
        assertEquals("Removal not persisted", 1, reloaded.getReporter().getContactMechanisms().size());
    }

    public void testUpdateContactMechanism() throws Exception {
        {
            ExpeditedAdverseEventReport report = getDao().getById(-1);
            assertEquals(2, report.getReporter().getContactMechanisms().size());
            report.getReporter().getContactMechanisms().put("e-mail", "clipper@yankee.com");
            getDao().save(report);
        }

        interruptSession();

        ExpeditedAdverseEventReport reloaded = getDao().getById(-1);
        assertEquals("Wrong number of mechanisms after reload", 2,
            reloaded.getReporter().getContactMechanisms().size());
        assertEquals("Change not persisted", "clipper@yankee.com",
            reloaded.getReporter().getContactMechanisms().get("e-mail"));
    }

    public void testSaveNewAdditionalInformation() throws Exception {
        doSaveTest(new SaveTester() {
            public void setupReport(ExpeditedAdverseEventReport report) {
                report.getAdditionalInformation().setConsults(Boolean.TRUE);
            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                assertEquals(Boolean.TRUE, loaded.getAdditionalInformation().getConsults());
            }
        });
    }

    public void testSaveNewMedicalDevice() throws Exception {
        doSaveTest(new SaveTester() {
            public void setupReport(ExpeditedAdverseEventReport report) {
            	report.getMedicalDevice().setBrandName("IBM");
            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                assertEquals("IBM", loaded.getMedicalDevice().getBrandName());
            }
        });
    }

    public void testSaveNewRadiationIntervention() throws Exception {
        doSaveTest(new SaveTester() {
            public void setupReport(ExpeditedAdverseEventReport report) {
                report.getRadiationIntervention().setTreatmentArm("ARM:");
            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                assertEquals("ARM:", loaded.getRadiationIntervention().getTreatmentArm());
            }
        });
    }

    public void testSaveSkipsPhysicianWhenNotSavable() throws Exception {
        doSaveTest(new SaveTester() {
            public void setupReport(ExpeditedAdverseEventReport report) {
                report.getPhysician().setLastName(null);
            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                assertNull(loaded.getPhysician().getFirstName());
                assertNull(loaded.getPhysician().getLastName());
            }
        });
    }

    public void testSaveSkipsReporterWhenNotSavable() throws Exception {
        doSaveTest(new SaveTester() {
            public void setupReport(ExpeditedAdverseEventReport report) {
                report.getReporter().setFirstName(null);
            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                assertNull(loaded.getReporter().getFirstName());
                assertNull(loaded.getReporter().getLastName());
            }
        });
    }

    public void testSaveSavesReporterWhenSavable() throws Exception {
        doSaveTest(new SaveTester() {
            private static final String FIRST_NAME = "Joe";
            private static final String LAST_NAME = "Arimathea";
            private static final String ADDRESS = "joe@arimatheaonline.net";
            private static final String PHONE = "312-HY-GRAIL";

            public void setupReport(ExpeditedAdverseEventReport report) {
                report.getReporter().setFirstName(FIRST_NAME);
                report.getReporter().setLastName(LAST_NAME);
                report.getReporter().getContactMechanisms().put(ReportPerson.EMAIL, ADDRESS);
                report.getReporter().getContactMechanisms().put(ReportPerson.PHONE, PHONE);
            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                assertEquals(FIRST_NAME, loaded.getReporter().getFirstName());
                assertEquals(LAST_NAME, loaded.getReporter().getLastName());
                assertEquals(ADDRESS, loaded.getReporter().getContactMechanisms().get(ReportPerson.EMAIL));
                assertEquals(PHONE, loaded.getReporter().getContactMechanisms().get(ReportPerson.PHONE));
            }
        });
    }

    public void testSaveSavesPhysicianWhenSavable() throws Exception {
        doSaveTest(new SaveTester() {
            private static final String FIRST_NAME = "Henry";
            private static final String LAST_NAME = "Jones";
            private static final String ADDRESS = "jonessr@indianaonline.net";
            private static final String PHONE = "773-LOST-BOK";

            public void setupReport(ExpeditedAdverseEventReport report) {
                report.getPhysician().setFirstName(FIRST_NAME);
                report.getPhysician().setLastName(LAST_NAME);
                report.getPhysician().getContactMechanisms().put(ReportPerson.EMAIL, ADDRESS);
                report.getPhysician().getContactMechanisms().put(ReportPerson.PHONE, PHONE);
            }

            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
                assertEquals(FIRST_NAME, loaded.getPhysician().getFirstName());
                assertEquals(LAST_NAME, loaded.getPhysician().getLastName());
                assertEquals(ADDRESS, loaded.getPhysician().getContactMechanisms().get(ReportPerson.EMAIL));
                assertEquals(PHONE, loaded.getPhysician().getContactMechanisms().get(ReportPerson.PHONE));
            }
        });
    }

//    public void testSaveSavesSubmittableReports() throws Exception {
//        doSaveTest(new SaveTester() {
//            public void setupReport(ExpeditedAdverseEventReport report) {
//                Report submittable = new Report();
//                submittable.setDueOn(new Date());
//                submittable.setCreatedOn(new Date());
//              //  submittable.setName("What is this field for?");
//                submittable.setReportDefinition(reportDefinitionDao.getById(-30));
//                report.addReport(submittable);
//            }
//
//            public void assertCorrect(ExpeditedAdverseEventReport loaded) {
//                assertEquals("Report not saved", 1, loaded.getReports().size());
//                assertEquals("Report has wrong definition",
//                    -30, (int) loaded.getReports().get(0).getReportDefinition().getId());
//            }
//        });
//    }


    public void testSearchExpeditedReportByCtcTermPartial() throws Exception {
    	List<ExpeditedAdverseEventReport> results;
    	Map<String,String> m = new HashMap<String,String>();
    	m.put("ctcTerm", "Auditory/Ear");
    	results = getDao().searchExpeditedReports(m);
    	assertEquals("Wrong number of results", 1, results.size());
    }



    public void testSearchExpeditedReportByParticipantFirstName() throws Exception {
    	List<ExpeditedAdverseEventReport> results;
    	Map<String,String> m = new HashMap<String,String>();
    	m.put("participantFirstName", "Michael");
    	results = getDao().searchExpeditedReports(m);
    	assertEquals("Wrong number of results", 1, results.size());
    }

    public void testSearchExpeditedReportByStudyShortTitle() throws Exception {
    	List<ExpeditedAdverseEventReport> results;
    	Map<String,String> m = new HashMap<String,String>();
    	m.put("studyShortTitle", "That");
    	results = getDao().searchExpeditedReports(m);
    	assertEquals("Wrong number of results", 1, results.size());
    }

    private void doSaveTest(SaveTester tester) {
        Integer savedId;
        {
            ExpeditedAdverseEventReport report = createMinimalAeReport();

            tester.setupReport(report);

            getDao().save(report);
            assertNotNull("No ID for new report", report.getId());
            savedId = report.getId();
        }

        interruptSession();

        {
            ExpeditedAdverseEventReport loaded = getDao().getById(savedId);
            assertNotNull("Saved report not found", loaded);
            tester.assertCorrect(loaded);
        }
    }

    private ExpeditedAdverseEventReport createMinimalAeReport() {
        ExpeditedAdverseEventReport report = Fixtures.createSavableExpeditedReport();
        report.setAssignment(assignmentDao.getById(-14));
        report.getAdverseEvents().get(0).setAdverseEventCtcTerm(Fixtures.createAdverseEventCtcTerm(report.getAdverseEvents().get(0), ctcTermDao.getById(3012)));
        return report;
    }

    private static interface SaveTester {
        void setupReport(ExpeditedAdverseEventReport report);
        void assertCorrect(ExpeditedAdverseEventReport loaded);
    }
}
