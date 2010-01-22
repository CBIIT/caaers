package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.dao.AdverseEventReportingPeriodDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.web.DwrFacadeTestCase;
import gov.nih.nci.cabig.caaers.web.dwr.AjaxOutput;
import gov.nih.nci.cabig.caaers.web.validation.validator.AdverseEventReportingPeriodValidator;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

import java.util.ArrayList;

import static org.easymock.EasyMock.expect;


/**
 * This class tests RoutingAndReviewAjaxFacade class.
 * @author Sameer Sawant
 * @author Biju Joseph
 */
public class RoutingAndReviewAjaxFacadeTest extends DwrFacadeTestCase{
	
	private RoutingAndReviewAjaxFacade facade;
	private AdverseEventReportingPeriodDao adverseEventReportingPeriodDao;
	private AdverseEventReportingPeriodValidator adverseEventReportingPeriodValidator;
	private MessageSource messageSource;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		adverseEventReportingPeriodDao = registerDaoMockFor(AdverseEventReportingPeriodDao.class);
		facade = new RoutingAndReviewAjaxFacade();
		facade.setAdverseEventReportingPeriodDao(adverseEventReportingPeriodDao);
		facade.setAdverseEventReportingPeriodValidator(adverseEventReportingPeriodValidator);
        messageSource = (MessageSource) getDeployedApplicationContext().getBean("messageSource");
        facade.setMessageSource(messageSource);
	}
		
	/**
	 * This method tests the case when there is a validation error.
	 * The adverseEventReportingPeriodValidator injected into the facade is an instance of the inner Validator class that creates
	 * an error in its validate() method.
	 * The test method checks the AjaxOutput method and confirms that it has an error populated in it correctly.
	 * @throws Exception
	 */
	public void testValidateTransitionWithErrors() throws Exception{
		AdverseEventReportingPeriod reportingPeriod = new AdverseEventReportingPeriod();
		adverseEventReportingPeriodValidator = new AdverseEventReportingPeriodValidator(){
			public void validate(Object obj, Errors e) {
				AdverseEventReportingPeriod adverseEventReportingPeriod = (AdverseEventReportingPeriod) obj;
				e.reject("CAE_007", "test error");
			}
		};
		facade.setAdverseEventReportingPeriodValidator(adverseEventReportingPeriodValidator);
		expect(adverseEventReportingPeriodDao.getById(1)).andReturn(reportingPeriod);
		replayMocks();
		AjaxOutput output = facade.validateTransition(1, "Submit to Data Coordinator");
		assertNotNull("AjaxOutput not populated with errors", output);
		ArrayList<String> errorList = (ArrayList<String>)output.getObjectContent();
		assertEquals("Incorrect number of errors populated in the ajaxOutput object", 1, errorList.size());
        assertEquals("All adverse events should be graded.", errorList.get(0));
		verifyMocks();
	}
	
	/**
	 * This method tests the case when there is no validation error.
	 * The adverseEventReportingPeriodValidator injected into the facade is an instance of the inner Validator class that returns 
	 * no error in its validate() method.
	 * The test method checks that the objectContent attribute of the AjaxOutput object returned is null.
	 * @throws Exception
	 */
	public void testValidateTransitionWithNoErrors() throws Exception{
		AdverseEventReportingPeriod reportingPeriod = new AdverseEventReportingPeriod();
		adverseEventReportingPeriodValidator = new AdverseEventReportingPeriodValidator(){
			public void validate(Object obj, Errors e) {
			}
		};
		facade.setAdverseEventReportingPeriodValidator(adverseEventReportingPeriodValidator);
		expect(adverseEventReportingPeriodDao.getById(1)).andReturn(reportingPeriod);
		replayMocks();
		AjaxOutput output = facade.validateTransition(1, "Submit to Data Coordinator");
		assertNull("ObjectContent populated incorrectly when there were no errors", output.getObjectContent());
		verifyMocks();
	}
}