/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import edu.nwu.bioinformatics.commons.CollectionUtils;
import edu.nwu.bioinformatics.commons.DateUtils;
import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.CaaersUseCases;
import gov.nih.nci.cabig.caaers.domain.attribution.*;
import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.report.Report;

import java.util.*;

import static gov.nih.nci.cabig.caaers.CaaersUseCase.AE_DATA_COLLECTION;

/**
 * @author Biju Joseph
 * @author Ion C. Olaru
 * 
 */
@CaaersUseCases({AE_DATA_COLLECTION})
public class AdverseEventTest extends AbstractTestCase {
    private AdverseEvent adverseEvent;
    private String comments;
    private String detailsForOther;
    private Boolean expected;
    private Boolean participantAtRisk;
    private Boolean requiresReporting;
    private Grade grade;
    private Attribution attributionSummary;
    private TimeValue eventApproximateTime;
    private String eventLocation;
    private Date startDate;
    private Date endDate;
    private Boolean solicited;
    private OutcomeType serious;
    private Hospitalization hospitalization;
    private LowLevelTerm lowLevelTerm;
    private Outcome outcome1, outcome2;
    private AdverseEventReportingPeriod reportingPeriod;
    private ExpeditedAdverseEventReport report;

    private AbstractAdverseEventTerm adverseEventTerm;


    private DeviceAttribution deviceAttribution;
    private ConcomitantMedicationAttribution concomitantMedicationAttribution;
    private DiseaseAttribution diseaseAttribution;
    private CourseAgentAttribution courseAgentAttribution;
    private OtherCauseAttribution otherCauseAttribution;
    private SurgeryAttribution surgeryAttribution;
    private RadiationAttribution radiationAttribution;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        report = new ExpeditedAdverseEventReport();
        outcome1 = new Outcome();
        outcome1.setOutcomeType(OutcomeType.DISABILITY);
        outcome2 = new Outcome();
        outcome2.setOutcomeType(OutcomeType.DEATH);

        adverseEventTerm = new AdverseEventCtcTerm();
        adverseEventTerm.setId(1);
        deviceAttribution = new DeviceAttribution(Fixtures.createMedicalDevice(false , false));
        deviceAttribution.getCause().setId(-1);
        deviceAttribution.setAttribution(Attribution.DEFINITE);

        radiationAttribution = new RadiationAttribution();
        radiationAttribution.setAttribution(Attribution.PROBABLE);

        surgeryAttribution = new SurgeryAttribution();
        surgeryAttribution.setAttribution(Attribution.DEFINITE);

        concomitantMedicationAttribution = new ConcomitantMedicationAttribution();
        concomitantMedicationAttribution.setAttribution(Attribution.PROBABLE);

        diseaseAttribution = new DiseaseAttribution();
        diseaseAttribution.setAttribution(Attribution.DEFINITE);

        otherCauseAttribution = new OtherCauseAttribution();
        otherCauseAttribution.setAttribution(Attribution.PROBABLE);

        courseAgentAttribution = new CourseAgentAttribution();
        courseAgentAttribution.setAttribution(Attribution.DEFINITE);


        reportingPeriod = new AdverseEventReportingPeriod();
        reportingPeriod.setId(1);
        serious = OutcomeType.DEATH;
        comments = "comments";
        expected = true;
        participantAtRisk = true;
        solicited = true;
        eventLocation = "eventLocation";
        requiresReporting = true;
        attributionSummary = Attribution.POSSIBLE;
        detailsForOther = "detailsForOther";
        grade = Grade.DEATH;
        hospitalization = Hospitalization.YES;
        eventApproximateTime = new TimeValue();
        eventApproximateTime.setHour(3);
        eventApproximateTime.setMinute(2);
        lowLevelTerm = new LowLevelTerm();
        lowLevelTerm.setIcd9Code("icd code");
        adverseEvent = new AdverseEvent();
        adverseEvent.setHospitalization(hospitalization);
        adverseEvent.setSerious(serious);
        adverseEvent.setGrade(grade);
        adverseEvent.addOutcome(outcome1);
        adverseEvent.addOutcome(outcome2);
        adverseEvent.setId(1);
        adverseEvent.setVersion(2);
        adverseEvent.setLowLevelTerm(lowLevelTerm);
        adverseEvent.setEventApproximateTime(eventApproximateTime);
        adverseEvent.setAttributionSummary(attributionSummary);
        adverseEvent.setGridId("grid id");
        adverseEvent.setComments(comments);
        adverseEvent.setDetailsForOther(detailsForOther);
        adverseEvent.setExpected(expected);
        adverseEvent.setParticipantAtRisk(participantAtRisk);
        adverseEvent.setSolicited(solicited);
        adverseEvent.setEventLocation(eventLocation);
        adverseEvent.setRequiresReporting(requiresReporting);
        startDate = DateUtils.createDate(2008, Calendar.NOVEMBER, 2);
        endDate = DateUtils.createDate(2008, Calendar.NOVEMBER, 3);
        startDate.setHours(0); // The Time needs to set to zero as time should be 0.
        endDate.setHours(0);
        adverseEvent.setStartDate(startDate);
        adverseEvent.setEndDate(endDate);
        adverseEvent.setReport(report);
        adverseEvent.setReportingPeriod(reportingPeriod);
        adverseEvent.addAttribution(deviceAttribution, adverseEvent.getDeviceAttributions());
        adverseEvent.addAttribution(radiationAttribution, adverseEvent.getRadiationAttributions());
        adverseEvent.addAttribution(concomitantMedicationAttribution, adverseEvent.getConcomitantMedicationAttributions());
        adverseEvent.addAttribution(otherCauseAttribution, adverseEvent.getOtherCauseAttributions());
        adverseEvent.addAttribution(diseaseAttribution, adverseEvent.getDiseaseAttributions());
        adverseEvent.addAttribution(surgeryAttribution, adverseEvent.getSurgeryAttributions());
        adverseEvent.addAttribution(courseAgentAttribution, adverseEvent.getCourseAgentAttributions());


    }
    
    public void testDefaultExpectedness() throws Exception {
        adverseEvent = new AdverseEvent();
        assertNull(adverseEvent.getExpected());
    }

    public void testDefaultParticipantAtRisk() throws Exception {
        adverseEvent = new AdverseEvent();
        assertNull(adverseEvent.getParticipantAtRisk());
    }
    
    public void testGetStartDateAsString(){
    	assertEquals("11/02/2008", adverseEvent.getStartDateAsString());
    }
    
    public void testSetDateAsString(){
    	adverseEvent.setStartDateAsString("x");
    	assertEquals("11/02/2008", adverseEvent.getStartDateAsString());
    }
    
    public void testGetStartDateAsStringWhenDateIsNull(){
    	adverseEvent.setStartDate(null);
    	adverseEvent.getStartDateAsString();
    }
    
    public void testToString(){
    	assertEquals("{id :null, Grade : null, Hospitalization: null, attribution : null, expected : null}",new AdverseEvent().toString());
    	AdverseEvent e = new AdverseEvent();
    	e.setId(5);
    	assertEquals("{id :5, Grade : null, Hospitalization: null, attribution : null, expected : null}",e.toString());
    	
    }
    
   /*
    *
    * get the signature of an AE
    * 
    **/
    public void testGetCurrentSignature() {
    	AdverseEvent ae = new AdverseEvent();
    	assertEquals("$$$$$$$$$$$$$$$$$$$$$$$$$$$$", ae.getCurrentSignature());
    	assertEquals("detailsForOther$$DEATH$$POSSIBLE$$YES$$true$$$$true$$11/02/2008$$11/03/2008$$03$$02$$eventLocation$$$$DISABILITYDEATH$$", adverseEvent.getCurrentSignature());
    }
    
    public void testSigantureFieldValueFullSignature() {
        String s = adverseEvent.getCurrentSignature(); 
    	assertEquals("DEATH", adverseEvent.signatureFieldValue("grade", s));
    	assertEquals("true", adverseEvent.signatureFieldValue("expectedness", s));
    	assertEquals("POSSIBLE", adverseEvent.signatureFieldValue("attributionSummary", s));
    	assertEquals("YES", adverseEvent.signatureFieldValue("hospitalization", s));
    	assertEquals("true", adverseEvent.signatureFieldValue("participantAtRisk", s));
    	assertEquals("DISABILITYDEATH", adverseEvent.signatureFieldValue("outcomeIdentifier", s));
    }

    public void testSigantureFieldValueEmptySignature() {
        AdverseEvent ae = new AdverseEvent();
        String s = ae.getCurrentSignature();
    	assertEquals("", adverseEvent.signatureFieldValue("grade", s));
    	assertEquals("", adverseEvent.signatureFieldValue("expectedness", s));
    	assertEquals("", adverseEvent.signatureFieldValue("attributionSummary", s));
    	assertEquals("", adverseEvent.signatureFieldValue("hospitalization", s));
    	assertEquals("", adverseEvent.signatureFieldValue("participantAtRisk", s));
    	assertEquals("", adverseEvent.signatureFieldValue("outcomeIdentifier", s));
    }

    public void testSigantureFieldValueEmptySignatureOneField() {
        String s = "comments$$v1$$$$$$$$$$$$$$$$$$";
    	assertEquals("v1", adverseEvent.signatureFieldValue("grade", s));
        assertEquals("", adverseEvent.signatureFieldValue("expectedness", s));
        assertEquals("", adverseEvent.signatureFieldValue("attributionSummary", s));
        assertEquals("", adverseEvent.signatureFieldValue("hospitalization", s));
        assertEquals("", adverseEvent.signatureFieldValue("participantAtRisk", s));
        assertEquals("", adverseEvent.signatureFieldValue("outcomeIdentifier", s));
    }

    public void testIsRuleableFieldsModifiedChangeARuleableField() {
        List<String> ruleableFields = new ArrayList<String>();
        ruleableFields.add("grade");
        
        adverseEvent.setGrade(Grade.MILD);
        String s = adverseEvent.getCurrentSignature();

        adverseEvent.setSignature(s);
        assertFalse(adverseEvent.isRuleableFieldsModified(ruleableFields));

        adverseEvent.setGrade(Grade.NORMAL);
        assertTrue(adverseEvent.isRuleableFieldsModified(ruleableFields));
    }

    public void testIsRuleableFieldsModifiedChangeANonRuleableField() {
        List<String> ruleableFields = new ArrayList<String>();
        ruleableFields.add("grade");

        adverseEvent.setHospitalization(Hospitalization.YES);
        String s = adverseEvent.getCurrentSignature();
        adverseEvent.setSignature(s);
        assertFalse(adverseEvent.isRuleableFieldsModified(ruleableFields));

        adverseEvent.setHospitalization(Hospitalization.NO);
        assertFalse(adverseEvent.isRuleableFieldsModified(ruleableFields));
    }

    public void testInitializeGradedDate(){
    	AdverseEvent ae = new AdverseEvent();
    	assertNull(ae.getGradedDate());
    	ae.initailzeGradedDate();
    	assertNull(ae.getGradedDate());
    }
    
    public void testInitializeGradedDate_WhenGraded(){
    	assertNull(adverseEvent.getGradedDate());
    	adverseEvent.initailzeGradedDate();
    	assertNotNull(adverseEvent.getGradedDate());
    }
    
    public void testInitializeGradedDate_WhenGradedNotEvaluated(){
    	assertNull(adverseEvent.getGradedDate());
    	
    	adverseEvent.setGrade(Grade.NOT_EVALUATED);
    	adverseEvent.initailzeGradedDate();
    	assertNotNull(adverseEvent.getGradedDate());
    }
    
    public void testGetAssocitatedReportNames(){
    	assertTrue(adverseEvent.getAssociatedReportNames().isEmpty());
    	Report r = Fixtures.createReport("test");
    	r.setStatus(ReportStatus.PENDING);
    	report.addReport(r);
    	assertFalse(adverseEvent.getAssociatedReportNames().isEmpty());
    	assertEquals(1, adverseEvent.getAssociatedReportNames().size());
    	assertEquals("test", adverseEvent.getAssociatedReportNames().get(0));
    }
    
    
    public void testInitializePostSubmissionUpdatedDate(){
    	AdverseEvent ae = new AdverseEvent();
    	assertNull(ae.getPostSubmissionUpdatedDate());
    	ae.initializePostSubmissionUpdatedDate();
    	assertNull(ae.getPostSubmissionUpdatedDate());
    }
    
    public void testInitializePostSubmissionUpdatedDate_WhenAEModified(){
    	AdverseEvent ae = new AdverseEvent();
    	ae.setGrade(Grade.NORMAL);
    	assertNull(ae.getPostSubmissionUpdatedDate());
    	assertTrue(ae.isModified());
    	ae.initializePostSubmissionUpdatedDate();
    	assertNull(ae.getPostSubmissionUpdatedDate());
    }
    
    public void testInitializePostSubmissionUpdatedDate_WhenGraded(){
    	AdverseEvent ae = new AdverseEvent();
    	ae.setGrade(Grade.LIFE_THREATENING);
    	assertNull(ae.getPostSubmissionUpdatedDate());
    	assertTrue(ae.isModified());
    	ae.initializePostSubmissionUpdatedDate();
    	assertNotNull(ae.getPostSubmissionUpdatedDate());
    }
    
    
    public void testInitializePostSubmissionUpdatedDate_Same(){
    	AdverseEvent ae = new AdverseEvent();
    	Date d = new Date();
    	ae.setPostSubmissionUpdatedDate(d);
    	ae.setGrade(Grade.LIFE_THREATENING);
    	assertTrue(ae.isModified());
    	ae.initializePostSubmissionUpdatedDate();
    	assertNotNull(ae.getPostSubmissionUpdatedDate());
    	assertSame(d, ae.getPostSubmissionUpdatedDate());
    }


    public void testIsAttributtedWith(){
        AdverseEvent newAE = new AdverseEvent();
        assertFalse(newAE.isAttributedWith(Attribution.DEFINITE));
        assertTrue(adverseEvent.isAttributedWith(Attribution.DEFINITE));
    }
    
//
//    public void testCopyAdverseEventTerm() {
//
//        AdverseEvent copiedAdverseEvent = adverseEvent.copy();
//        assertNull("must not create ctc term  and medra term by default", copiedAdverseEvent.getAdverseEventTerm());
//
//        adverseEvent.setAdverseEventTerm(adverseEventTerm);
//        copiedAdverseEvent = adverseEvent.copy();
//        assertNotNull(copiedAdverseEvent.getAdverseEventTerm());
//        assertNotSame("adverse event term object must not be same", adverseEventTerm, copiedAdverseEvent.getAdverseEventTerm());
//
//        assertNotEquals("adverse event term object must not be same", adverseEventTerm, copiedAdverseEvent.getAdverseEventTerm());
//
//        assertSame("adverse must be same", copiedAdverseEvent, copiedAdverseEvent.getAdverseEventTerm().getAdverseEvent());
//    }
//
//    public void testCopyAdverseEventAttribution() {
//
//        AdverseEvent copiedAdverseEvent = adverseEvent.copy();
//        assertEquals("number of adverse events attributions must be same", 7, adverseEvent.getAdverseEventAttributions().size());
//
//        assertEquals("number of adverse events attributions must be same", 7, copiedAdverseEvent.getAdverseEventAttributions().size());
//
//        assertEquals("number of deviceAttribution1 must be same", adverseEvent.getDeviceAttributions().size(), copiedAdverseEvent.getDeviceAttributions().size());
//        assertEquals("attribute of deviceAttribution1 must have same value", deviceAttribution.getAttribution(), copiedAdverseEvent.getDeviceAttributions().get(0).getAttribution());
//
//        assertNotSame("deviceAttribution object must not refer to same object", deviceAttribution, copiedAdverseEvent.getDeviceAttributions().get(0));
//        assertNotEquals("deviceAttribution object must not refer to same object", deviceAttribution, copiedAdverseEvent.getDeviceAttributions().get(0));
//        assertSame("adverse must be same", copiedAdverseEvent, copiedAdverseEvent.getDeviceAttributions().get(0).getAdverseEvent());
//
//        assertNotSame("radiationAttribution object must not refer to same object", radiationAttribution, copiedAdverseEvent.getRadiationAttributions().get(0));
//        assertNotEquals("radiationAttribution object must not refer to same object", radiationAttribution, copiedAdverseEvent.getRadiationAttributions().get(0));
//        assertSame("adverse must be same", copiedAdverseEvent, copiedAdverseEvent.getRadiationAttributions().get(0).getAdverseEvent());
//
//        assertNotSame("surgeryAttribution object must not refer to same object", surgeryAttribution, copiedAdverseEvent.getSurgeryAttributions().get(0));
//        assertNotEquals("surgeryAttribution object must not refer to same object", surgeryAttribution, copiedAdverseEvent.getSurgeryAttributions().get(0));
//        assertSame("adverse must be same", copiedAdverseEvent, copiedAdverseEvent.getSurgeryAttributions().get(0).getAdverseEvent());
//
//        assertNotSame("diseaseAttribution object must not refer to same object", diseaseAttribution, copiedAdverseEvent.getDiseaseAttributions().get(0));
//        assertNotEquals("diseaseAttribution object must not refer to same object", diseaseAttribution, copiedAdverseEvent.getDiseaseAttributions().get(0));
//        assertSame("adverse must be same", copiedAdverseEvent, copiedAdverseEvent.getDiseaseAttributions().get(0).getAdverseEvent());
//
//        assertNotSame("courseAgentAttribution object must not refer to same object", courseAgentAttribution, copiedAdverseEvent.getCourseAgentAttributions().get(0));
//        assertNotEquals("courseAgentAttribution object must not refer to same object", radiationAttribution, copiedAdverseEvent.getCourseAgentAttributions().get(0));
//        assertSame("adverse must be same", copiedAdverseEvent, copiedAdverseEvent.getCourseAgentAttributions().get(0).getAdverseEvent());
//
//
//        assertNotSame("concomitantMedicationAttribution object must not refer to same object", concomitantMedicationAttribution, copiedAdverseEvent.getConcomitantMedicationAttributions().get(0));
//        assertNotEquals("concomitantMedicationAttribution object must not refer to same object", concomitantMedicationAttribution, copiedAdverseEvent.getConcomitantMedicationAttributions().get(0));
//        assertSame("adverse must be same", copiedAdverseEvent, copiedAdverseEvent.getConcomitantMedicationAttributions().get(0).getAdverseEvent());
//
//
//    }
//
//    public void testCopyForRoutineReport() {
//
//        AdverseEvent copiedAdverseEvent = adverseEvent.copy();
//        assertSame("routineReport must refer to same object", routineReport, copiedAdverseEvent.getRoutineReport());
//        assertEquals("routineReport must refer to same object", routineReport, copiedAdverseEvent.getRoutineReport());
//
//    }
//
//    public void testCopyForReportingPeriod() {
//
//        AdverseEvent copiedAdverseEvent = adverseEvent.copy();
//        assertSame("reportingPeriod must refer to same object", reportingPeriod, copiedAdverseEvent.getReportingPeriod());
//        assertEquals("reportingPeriod must refer to same object", reportingPeriod, copiedAdverseEvent.getReportingPeriod());
//
//    }
//
//    public void testCopyForReport() {
//
//        AdverseEvent copiedAdverseEvent = adverseEvent.copy();
//        assertNotNull(adverseEvent.getReport());
//        assertNull("must not copy the report", copiedAdverseEvent.getReport());
//
//    }
//
//    public void testCopyForOutCome() {
//
//        AdverseEvent copiedAdverseEvent = adverseEvent.copy();
//
//        assertEquals("number of outcomes must be same", adverseEvent.getOutcomes().size(), copiedAdverseEvent.getOutcomes().size());
//        assertEquals("attribute of outcomes must have same value", outcome1.getOutcomeType(), copiedAdverseEvent.getOutcomes().get(0).getOutcomeType());
//
//        assertNotSame("outcome object must not refer to same object", outcome1, copiedAdverseEvent.getOutcomes().get(0));
//        assertNotEquals("outcome object must not refer to same object", outcome1, copiedAdverseEvent.getOutcomes().get(0));
//
//    }
//
//    public void testMustNotCopyIdGridIdAndVersionNumber() {
//
//        AdverseEvent copiedAdverseEvent = adverseEvent.copy();
//
//        assertNotNull(adverseEvent.getId());
//
//        assertNotNull(adverseEvent.getGridId());
//        assertNotNull(adverseEvent.getVersion());
//
//        assertNull(copiedAdverseEvent.getId());
//        assertNull(copiedAdverseEvent.getGridId());
//        assertNull("version number must be null", copiedAdverseEvent.getVersion());
//    }
//
//    public void testCopyForBasicProperties() {
//
//        AdverseEvent copiedAdverseEvent = adverseEvent.copy();
//
//
//        assertEquals("hospitalization must be same", hospitalization, copiedAdverseEvent.getHospitalization());
//
//        assertEquals("comments must be same", comments, copiedAdverseEvent.getComments());
//        assertEquals("details must be same", detailsForOther, copiedAdverseEvent.getDetailsForOther());
//        assertEquals("expected must be same", expected, copiedAdverseEvent.getExpected());
//        assertEquals("start date must be same", startDate, copiedAdverseEvent.getStartDate());
//        assertEquals("end date must be same", endDate, copiedAdverseEvent.getEndDate());
//        assertEquals("serious must be same", serious, copiedAdverseEvent.getSerious());
//
//        assertEquals("solicited must be same", solicited, copiedAdverseEvent.getSolicited());
//        assertEquals("eventLocation must be same", eventLocation, copiedAdverseEvent.getEventLocation());
//        assertEquals("requiresReporting must be same", requiresReporting, copiedAdverseEvent.getRequiresReporting());
//
//        assertEquals("attributionSummary must be same", attributionSummary, copiedAdverseEvent.getAttributionSummary());
//
//
//        assertEquals("grade must be same", grade, copiedAdverseEvent.getGrade());
//
//        assertEquals("low level term must be equal", adverseEvent.getLowLevelTerm(), copiedAdverseEvent.getLowLevelTerm());
//        assertSame("low level term must refer to same object", lowLevelTerm, copiedAdverseEvent.getLowLevelTerm());
//
//        assertNotSame("both adverse event must not refer to same object", copiedAdverseEvent, adverseEvent);
//        assertNotEquals("both adverse event must not refer to same object", copiedAdverseEvent, adverseEvent);
//
//    }
//
//    public void testCopyForEventApproximationtime() {
//
//        AdverseEvent copiedAdverseEvent = adverseEvent.copy();
//
//
//        assertSame("eventApproximateTime must   refer same objects", eventApproximateTime, copiedAdverseEvent.getEventApproximateTime());
//
//        assertEquals("eventApproximateTime must  refer same object ", eventApproximateTime, copiedAdverseEvent.getEventApproximateTime());
//
//
//    }
    public void testCollectionUtils(){
    	List<Object> l1 = null;
    	List<Object> l2 = new ArrayList<Object>();
    	List<Object> l3 = new ArrayList<Object>();
    	l3.add(2);
    	assertTrue(CollectionUtils.isEmpty(l1));
    	assertTrue(CollectionUtils.isEmpty(l2));
    	assertFalse(CollectionUtils.isEmpty(l3));
    }
    
    public void testGetDisplayName(){
    	AdverseEvent ae = Fixtures.createAdverseEvent(1, Grade.DEATH);
    	ae.getAdverseEventCtcTerm().getTerm().setOtherRequired(true);
    	assertEquals("abcd", ae.getDisplayName());
    	ae.setDetailsForOther("hello");
    	assertEquals("abcd, hello", ae.getDisplayName());
    }

    public void testToReadableString(){
       String s =  AdverseEvent.toReadableString(adverseEvent);
       assertEquals(" ID : 1 Grade : DEATH Start date : 11/02/2008", s);
    }
   
    public void testAddOutcomeIfNecessary(){
        Outcome o = new Outcome();
        o.setOutcomeType(OutcomeType.DEATH);
        adverseEvent.getOutcomes().clear();
        assertTrue(adverseEvent.getOutcomes().isEmpty());
        adverseEvent.addOutComeIfNecessary(o);
        
        Outcome o2 = new Outcome();
        o2.setOutcomeType(OutcomeType.LIFE_THREATENING);
        adverseEvent.addOutComeIfNecessary(o2);
        
        assertEquals(2, adverseEvent.getOutcomes().size());

        Outcome o3 = new Outcome();
        o3.setOutcomeType(OutcomeType.OTHER_SERIOUS);
        o3.setOther("x");
        adverseEvent.addOutComeIfNecessary(o3);
        assertEquals(3, adverseEvent.getOutcomes().size());

        Outcome o4 = new Outcome();
        o4.setOutcomeType(OutcomeType.OTHER_SERIOUS);
        o4.setOther("x");
        adverseEvent.addOutComeIfNecessary(o4);
        assertEquals(3, adverseEvent.getOutcomes().size());
        assertEquals("Other serious:x", o4.getDisplayName());

        Outcome o5 = adverseEvent.getOutcomeOfType(OutcomeType.LIFE_THREATENING) ;
        assertNotNull(o5);
        adverseEvent.removeOtherOutcomes(Arrays.asList(OutcomeType.DEATH));
        assertEquals(1, adverseEvent.getOutcomes().size());

    }
    public void testGetOutcomeOfType(){
        {
            Outcome o = adverseEvent.getOutcomeOfType(OutcomeType.DEATH);
            assertNotNull(o);
        }
        
        {
            Outcome o1 = new Outcome();
            o1.setOutcomeType(OutcomeType.DEATH);
            adverseEvent.addOutComeIfNecessary(o1);
        }
        
        {
            Outcome o1 = new Outcome();
            o1.setOutcomeType(OutcomeType.LIFE_THREATENING);
            adverseEvent.addOutcome(o1);
        }

        Outcome o = adverseEvent.getOutcomeOfType(OutcomeType.DEATH);
        assertNotNull(o);
        assertEquals("Death", o.getDisplayName());

    }

    public void testGetDisplaySerious(){
        Outcome outcome = Fixtures.createOutcome(1001, OutcomeType.CONGENITAL_ANOMALY);
        AdverseEvent ae = Fixtures.createAdverseEvent(1, Grade.DEATH);
        ae.getAdverseEventCtcTerm().getTerm().setOtherRequired(true);
        assertEquals("No", ae.getDisplaySerious());
        ae.addOutcome(outcome);
        assertEquals("Yes", ae.getDisplaySerious());
    }

}
