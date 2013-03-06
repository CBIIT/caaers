/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.validation.validator;

import gov.nih.nci.cabig.caaers.AbstractNoSecurityTestCase;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.web.CaaersFieldConfigurationManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.classextension.EasyMock;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.sql.Timestamp;

/**
 * This class tests the AdverseEventReportingPeriodValidator class
 * @author Sameer Sawant
 * @author Biju Joseph
 */
public class AdverseEventReportingPeriodValidatorTest extends AbstractNoSecurityTestCase {
	private static final Log log = LogFactory.getLog(AdverseEventReportingPeriodValidator.class);
	
	private AdverseEventReportingPeriodValidator adverseEventReportingPeriodValidator;
	private AdverseEventReportingPeriod adverseEventReportingPeriod;
	private Errors errors;

    private CaaersFieldConfigurationManager confManager;
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();

		adverseEventReportingPeriodValidator = new AdverseEventReportingPeriodValidator();
		adverseEventReportingPeriod = new AdverseEventReportingPeriod();
		errors = new BindException(adverseEventReportingPeriod, "adverseEventReportingPeriod");
        confManager = registerMockFor(CaaersFieldConfigurationManager.class);
        adverseEventReportingPeriodValidator.setCaaersFieldConfigurationManager(confManager);
        EasyMock.expect(confManager.isFieldMandatory((String)EasyMock.anyObject(), (String)EasyMock.anyObject())).andReturn(true).anyTimes();
		replayMocks();
        setupAdverseEventReportingPeriod();
	}

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void setupAdverseEventReportingPeriod(){
		adverseEventReportingPeriod = Fixtures.createReportingPeriod();
		adverseEventReportingPeriod.setStartDate(new Timestamp(100));
		adverseEventReportingPeriod.setEndDate(new Timestamp(200));
		adverseEventReportingPeriod.addAdverseEvent(new AdverseEvent());
		adverseEventReportingPeriod.addAdverseEvent(new AdverseEvent());
		adverseEventReportingPeriod.setTreatmentAssignment(new TreatmentAssignment());
		AdverseEventCtcTerm aeCtcTerm = new AdverseEventCtcTerm();
		aeCtcTerm.setCtcTerm(Fixtures.createCtcTerm("testTerm", "testCode"));
		adverseEventReportingPeriod.getAdverseEvents().get(0).setAdverseEventCtcTerm(aeCtcTerm);
		aeCtcTerm.setCtcTerm(Fixtures.createCtcTerm("testTerm2", "testCode2"));
		adverseEventReportingPeriod.getAdverseEvents().get(1).setAdverseEventCtcTerm(aeCtcTerm);
	}

    //2nd observed ae not graded
	public void testGradeNotNull(){
		adverseEventReportingPeriod.getAdverseEvents().get(0).setGrade(Grade.NORMAL);
		adverseEventReportingPeriodValidator.validate(adverseEventReportingPeriod, errors);
		assertTrue(errors.hasErrors());
		assertEquals("Grade Not Null validation not working.", 1, errors.getErrorCount());
	}

    // making sure that solicited can have grade not evaluated.
    public void testGradeSolicitedHavingNotEvaluated(){
		adverseEventReportingPeriod.getAdverseEvents().get(0).setGrade(Grade.NOT_EVALUATED);
        adverseEventReportingPeriod.getAdverseEvents().get(0).setSolicited(true);
        adverseEventReportingPeriod.getAdverseEvents().get(1).setGrade(Grade.NORMAL);
        adverseEventReportingPeriod.getAdverseEvents().get(1).setSolicited(true);
		adverseEventReportingPeriodValidator.validate(adverseEventReportingPeriod, errors);
		assertFalse(errors.hasErrors());
	}


    // making sure that solicited not evaluated
    public void testGradeSolicitedNotGraded(){
		adverseEventReportingPeriod.getAdverseEvents().get(0).setGrade(null);
        adverseEventReportingPeriod.getAdverseEvents().get(0).setSolicited(true);
        adverseEventReportingPeriod.getAdverseEvents().get(1).setGrade(Grade.NORMAL);
        adverseEventReportingPeriod.getAdverseEvents().get(1).setSolicited(true);
		adverseEventReportingPeriodValidator.validate(adverseEventReportingPeriod, errors);
		assertTrue(errors.hasErrors());
	}


    // making sure that retired ae can be not graded
    public void testNotGradedRetiredAE(){
		adverseEventReportingPeriod.getAdverseEvents().get(0).setGrade(null);
        adverseEventReportingPeriod.getAdverseEvents().get(0).retire();

        adverseEventReportingPeriod.getAdverseEvents().get(1).setGrade(Grade.NORMAL);
        adverseEventReportingPeriod.getAdverseEvents().get(1).setSolicited(true);
		adverseEventReportingPeriodValidator.validate(adverseEventReportingPeriod, errors);
		assertFalse(errors.hasErrors());
	}
	
	public void testValidAttribution(){
		adverseEventReportingPeriod.getAdverseEvents().get(0).setGrade(Grade.MILD);
		adverseEventReportingPeriod.getAdverseEvents().get(1).setGrade(Grade.MILD);
		adverseEventReportingPeriodValidator.validate(adverseEventReportingPeriod, errors);
		assertTrue(errors.hasErrors());
		assertEquals("Attribution needed when grade >= 1 validation not working.", 1, errors.getErrorCount());
	}
	
	public void testValidHospitalization(){
		adverseEventReportingPeriod.getAdverseEvents().get(0).setAttributionSummary(Attribution.POSSIBLE);
		adverseEventReportingPeriod.getAdverseEvents().get(1).setAttributionSummary(Attribution.POSSIBLE);
		adverseEventReportingPeriod.getAdverseEvents().get(0).setGrade(Grade.LIFE_THREATENING);
		adverseEventReportingPeriod.getAdverseEvents().get(1).setGrade(Grade.LIFE_THREATENING);
		adverseEventReportingPeriodValidator.validate(adverseEventReportingPeriod, errors);
		assertTrue(errors.hasErrors());
		assertEquals("Hospitalization needed when Grade >= 2 validation not working.", 1, errors.getErrorCount());
	}
	
	public void testEndDateNotNull(){
		adverseEventReportingPeriod.getAdverseEvents().get(0).setGrade(Grade.NORMAL);
		adverseEventReportingPeriod.getAdverseEvents().get(1).setGrade(Grade.NORMAL);
		adverseEventReportingPeriod.setEndDate(null);
		adverseEventReportingPeriodValidator.validate(adverseEventReportingPeriod, errors);
		assertTrue(errors.hasErrors());
		assertEquals("End date cant be null validation not working", 1, errors.getErrorCount());
	}
}
