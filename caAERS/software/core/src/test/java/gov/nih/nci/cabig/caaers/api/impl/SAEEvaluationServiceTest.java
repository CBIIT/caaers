/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.api.impl;

import gov.nih.nci.cabig.caaers.CaaersDbNoSecurityTestCase;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.Grade;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.integration.schema.adverseevent.AdverseEventType;
import gov.nih.nci.cabig.caaers.integration.schema.adverseevent.CourseType;
import gov.nih.nci.cabig.caaers.integration.schema.adverseevent.OutComeEnumType;
import gov.nih.nci.cabig.caaers.integration.schema.adverseevent.OutcomeType;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.AdverseEvents;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.Criteria;
import gov.nih.nci.cabig.caaers.integration.schema.saerules.SaveAndEvaluateAEsInputMessage;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;

import org.junit.Test;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class SAEEvaluationServiceTest extends CaaersDbNoSecurityTestCase {
	
	private SAEEvaluationServiceImpl SAEEvaluationService = null;
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        SAEEvaluationService = (SAEEvaluationServiceImpl)getApplicationContext().getBean("SAEEvaluationServiceImpl");
    }
    
    @Test
	public void testSAERules() throws Exception{
		
		// Adverse Event 
		AdverseEventType ae = new AdverseEventType();
		ae.setId(BigInteger.ONE);
		ae.setGrade(Grade.DEATH.getCode());
		List<OutcomeType> listOutcomes = new ArrayList<OutcomeType>();
		
		OutcomeType type = new OutcomeType();
		type.setOutComeEnumType(OutComeEnumType.DEATH);
		type.setOther(null);
		
		listOutcomes.add(type);
		ae.setOutcome(listOutcomes);
		
		
		List<AdverseEventType> aes = new ArrayList<AdverseEventType>();
		aes.add(ae);
		
		AdverseEvents event = new AdverseEvents();
		event.setAdverseEvent(aes);
				
		// Study Assignment
		StudyParticipantAssignment assignment = new StudyParticipantAssignment();
		assignment.setStudySite(Fixtures.createStudySite(Fixtures.createOrganization("Mayo Clinic"), 1));
		assignment.setId(1);
		
		try {
			SAEEvaluationService.processAdverseEvents("12345-ABC", event, assignment, "TAC1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Throwable cause = e;
			while(cause.getCause() != null) {
				cause = cause.getCause();
			}
			String trace = "";
			for(StackTraceElement x : cause.getStackTrace()) {
				trace += x.toString() + "\n$$$\n";
			}
			fail("SAE Service failed with; " + e.getMessage() + "\n---\n" + e.getStackTrace()[0].toString() + "\n---\nCaused By: " + cause.getMessage() + "\n-***-\n" + trace);
		}
		
	}
	
    private Criteria getCriteria(int cycle) {
    	Criteria criteria = new Criteria();
		criteria.setStudyIdentifier("12345-ABC");
		criteria.setStudySubjectIdentifier("TSTSUBJ-14");
		CourseType course = new CourseType();
		course.setCycleNumber(BigInteger.valueOf(cycle));
		course.setStartDateOfFirstCourse(XMLGregorianCalendarImpl.createDate(2015, 1, cycle, DatatypeConstants.FIELD_UNDEFINED));
		course.setTreatmentAssignmentCode("Other");
		course.setOtherTreatmentAssignmentDescription("Unit test created");
		criteria.setCourse(course);
		
		return criteria;
    }
	
	@Test
	public void testProcessAndSave() {
		
		// Adverse Event 
		SaveAndEvaluateAEsInputMessage input = new SaveAndEvaluateAEsInputMessage();
		
		input.setCriteria(getCriteria(1));
		
		SaveAndProcessOutput output = null;

		try {
			output = SAEEvaluationService.saveAndProcessAdverseEvents(input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SAE Service failed with; " + e.getMessage());
		}
		
		if(output == null || output.getPeriod() == null || output.getMsg() == null) {
			fail("There was no ouput.");
		}
		
	}
	
	@Test
	public void testProcessAndInitiateNoRec() {
		fail("WIP");
	}
	
	@Test
	public void testProcessAndInitiateWithRec() {
		fail("WIP");
	}
	
	@Test
	public void testProcessAndInitiateWithdrawal() {
	}

}
