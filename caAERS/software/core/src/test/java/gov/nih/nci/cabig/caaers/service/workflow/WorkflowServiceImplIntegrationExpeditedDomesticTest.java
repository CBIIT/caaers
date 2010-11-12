package gov.nih.nci.cabig.caaers.service.workflow;

import gov.nih.nci.cabig.caaers.CaaersDbTestCase;
import gov.nih.nci.cabig.caaers.domain.ReviewStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;

public class WorkflowServiceImplIntegrationExpeditedDomesticTest extends CaaersDbTestCase {
	WorkflowServiceImpl wfService;
	Map<String, Object > variables = new HashMap<String, Object>();
	protected void setUp() throws Exception {
		super.setUp();
		wfService = (WorkflowServiceImpl)getDeployedApplicationContext().getBean("workflowService");
	}
	
	public void testCreateProcessInstance() {
		
	}
	
	public void _BORKEN_testCreateProcessInstance() {
		ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EXPEDITED_FLOW_DOMESTIC, variables);
		assertNotNull(pInstance);
		assertEquals( "Submit Report To Physician" ,pInstance.getRootToken().getNode().getName());
	}
	
	public void _BROKEN_testNextTransitions() {
		Integer id = null;
		String loginId = "pc@def.com";
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EXPEDITED_FLOW_DOMESTIC, variables);
			Long l = pInstance.getId();
			id = new Integer(l.intValue());
		}
		interruptSession();
		{
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
            System.out.println(nextTransitions);
			assertFalse(nextTransitions.isEmpty());
			assertEquals(2, nextTransitions.size());
			assertEquals("Send to Physician for Review", nextTransitions.get(1).getName());
			assertEquals("Physician Review", nextTransitions.get(1).getTo().getName());
		}
	}
	
	public void _BROKEN_testAdvanceWorkflow_ToSAECoordinatorReview(){
		String loginId = "pc@def.com";
		Integer id = null;
		
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EXPEDITED_FLOW_DOMESTIC, variables);
			Long l = pInstance.getId();
			id = new Integer(l.intValue());
		}
		interruptSession();
		
		{
			ReviewStatus status = wfService.advanceWorkflow(id, "Submit to Central Office Report Reviewer");
			assertEquals(ReviewStatus.CENTRAL_OFFICE_REVIEW, status);
		}
		interruptSession();
	    loginId = "aec@def.com";
        
		{
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
			assertFalse(nextTransitions.isEmpty());
			
			assertEquals("Request Additional Information", nextTransitions.get(0).getName());
			assertEquals("Provide Additional Information To Central Office", nextTransitions.get(0).getTo().getName());
			
			assertEquals("Approve Report", nextTransitions.get(1).getName());
			assertEquals("End Expedited Domestic Flow", nextTransitions.get(1).getTo().getName());
		}
	}
	
	public void _BROKEN_testAdvanceWorkflow_ToPhysicianReview(){
		String loginId = "pc@def.com";
		Integer id = null;
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EXPEDITED_FLOW_DOMESTIC, variables);
			Long l = pInstance.getId();
			id = new Integer(l.intValue());
		}
		interruptSession();
		
		{
			ReviewStatus status = wfService.advanceWorkflow(id, "Send to Physician for Review");
			assertEquals(ReviewStatus.PHYSICIAN_REVIEW, status);
			
		}

		interruptSession();
		loginId = "physician@def.com";
        {
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
			assertFalse(nextTransitions.isEmpty());
			assertEquals(2, nextTransitions.size());
			assertEquals("Approve Report", nextTransitions.get(1).getName());
			assertEquals("Submit Report To Central Office", nextTransitions.get(1).getTo().getName());
			
			assertEquals("Request Additional Information", nextTransitions.get(0).getName());
			assertEquals("Provide Additional Information To Physician", nextTransitions.get(0).getTo().getName());
		}
		
		
	}
	
	public void _BROKEN_testAdvanceWorkflow_ToApproveReportByPhysician(){
		String loginId = "pc@def.com";
		Integer id = null;
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EXPEDITED_FLOW_DOMESTIC, variables);
			Long l = pInstance.getId();
			id = new Integer(l.intValue());
		}
		interruptSession();
		
		{
			ReviewStatus status = wfService.advanceWorkflow(id, "Send to Physician for Review");
			assertEquals(ReviewStatus.PHYSICIAN_REVIEW, status);
			
		}
		interruptSession();
        loginId = "physician@def.com";
		{
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
			assertFalse(nextTransitions.isEmpty());
			assertEquals(2, nextTransitions.size());
			assertEquals("Approve Report", nextTransitions.get(1).getName());
			assertEquals("Submit Report To Central Office", nextTransitions.get(1).getTo().getName());
			
			assertEquals("Request Additional Information", nextTransitions.get(0).getName());
			assertEquals("Provide Additional Information To Physician", nextTransitions.get(0).getTo().getName());
		}
		interruptSession();
		{
			ReviewStatus status = wfService.advanceWorkflow(id, "Approve Report");
			assertEquals(ReviewStatus.PHYSICIAN_APPROVED, status);
		}
		interruptSession();
        loginId = "pc@def.com";
		{
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
			assertFalse(nextTransitions.isEmpty());
			assertEquals(2, nextTransitions.size());
			assertEquals("Submit to Central Office Report Reviewer", nextTransitions.get(1).getName());
			assertEquals("Central Office Report Review", nextTransitions.get(1).getTo().getName());
			assertEquals("Send to Physician for Review", nextTransitions.get(0).getName());
		}
	}
	
	public void _BROKEN_testAdvanceWorkfow_toSendToPhysicianForReviewMultipleTimes(){
		String loginId = "pc@def.com";
		Integer id = null;
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EXPEDITED_FLOW_DOMESTIC, variables);
			Long l = pInstance.getId();
			id = new Integer(l.intValue());
		}
		interruptSession();
		
		{
			ReviewStatus status = wfService.advanceWorkflow(id, "Send to Physician for Review");
			assertEquals(ReviewStatus.PHYSICIAN_REVIEW, status);
		}
		interruptSession();
        loginId = "physician@def.com";
		{
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
			assertFalse(nextTransitions.isEmpty());
			assertEquals(2, nextTransitions.size());
			assertEquals("Approve Report", nextTransitions.get(1).getName());
			assertEquals("Submit Report To Central Office", nextTransitions.get(1).getTo().getName());
			
			assertEquals("Request Additional Information", nextTransitions.get(0).getName());
			assertEquals("Provide Additional Information To Physician", nextTransitions.get(0).getTo().getName());
		}
		interruptSession();
		{
			ReviewStatus status = wfService.advanceWorkflow(id, "Approve Report");
			assertEquals(ReviewStatus.PHYSICIAN_APPROVED, status);
		}
		interruptSession();
        loginId = "pc@def.com";
		{
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
			assertFalse(nextTransitions.isEmpty());
			assertEquals(2, nextTransitions.size());
			assertEquals("Submit to Central Office Report Reviewer", nextTransitions.get(1).getName());
			assertEquals("Central Office Report Review", nextTransitions.get(1).getTo().getName());
			assertEquals("Send to Physician for Review", nextTransitions.get(0).getName());
		}
		interruptSession();
		{
			ReviewStatus status = wfService.advanceWorkflow(id, "Send to Physician for Review");
			assertEquals(ReviewStatus.PHYSICIAN_REVIEW, status);
		}
		interruptSession();
        loginId = "physician@def.com";
		{
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
			assertFalse(nextTransitions.isEmpty());
			assertEquals(2, nextTransitions.size());
			assertEquals("Approve Report", nextTransitions.get(1).getName());
			assertEquals("Submit Report To Central Office", nextTransitions.get(1).getTo().getName());
			
			assertEquals("Request Additional Information", nextTransitions.get(0).getName());
			assertEquals("Provide Additional Information To Physician", nextTransitions.get(0).getTo().getName());
		}
	}
	
	public void _BROKEN_testAdvanceWorkflow_ToCentralOfficeSAECoordinatorReview(){
		String loginId = "pc@def.com";
		Integer id = null;
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EXPEDITED_FLOW_DOMESTIC, variables);
			Long l = pInstance.getId();
			id = new Integer(l.intValue());
		}
		interruptSession();
		
		{
			ReviewStatus status = wfService.advanceWorkflow(id, "Submit For Physician Review");
			assertEquals(ReviewStatus.PHYSICIAN_REVIEW, status);
			
		}
		interruptSession();
		{
			ReviewStatus status = wfService.advanceWorkflow(id, "Approve Report");
			assertEquals(ReviewStatus.PHYSICIAN_APPROVED, status);
			
			status = wfService.advanceWorkflow(id, "Submit to Central Office SAE Coordinator");
			assertEquals(ReviewStatus.CENTRAL_OFFICE_REVIEW, status);
		}
		interruptSession();
        loginId = "aec@def.com";
		{
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
			assertFalse(nextTransitions.isEmpty());
			assertEquals(2, nextTransitions.size());
			assertEquals("Approve Report", nextTransitions.get(1).getName());
			assertEquals("End Expedited Domestic Flow", nextTransitions.get(1).getTo().getName());
			
			assertEquals("Request Additional Information", nextTransitions.get(0).getName());
			assertEquals("Provide Additional Information To Central Office", nextTransitions.get(0).getTo().getName());
		}
	}
	
	public void _BROKEN_testAdvanceWorkflow_ToSubmitToSponsor(){
		String loginId = "pc@def.com";
		Integer id = null;
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EXPEDITED_FLOW_DOMESTIC, variables);
			Long l = pInstance.getId();
			id = new Integer(l.intValue());
		}
		interruptSession();
		
		{
			ReviewStatus status = wfService.advanceWorkflow(id, "Submit For Physician Review");
			assertEquals(ReviewStatus.PHYSICIAN_REVIEW, status);
			
			status = wfService.advanceWorkflow(id, "Approve Report");
			assertEquals(ReviewStatus.PHYSICIAN_APPROVED, status);
			
			status = wfService.advanceWorkflow(id, "Submit to Central Office SAE Coordinator");
			assertEquals(ReviewStatus.CENTRAL_OFFICE_REVIEW, status);
			
			status = wfService.advanceWorkflow(id, "Approve Report");
			assertEquals(ReviewStatus.SUBMIT_TO_SPONSOR, status);
		}
		interruptSession();
		{
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
			assertTrue(nextTransitions.isEmpty());
			assertEquals(0, nextTransitions.size());
			
		}
	}
	
	public void _BROKEN_testAdvanceWorkflow_ToSubmittedToSponsor(){
		String loginId = loginId = "pc@def.com";
		Integer id = null;
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EXPEDITED_FLOW_DOMESTIC, variables);
			Long l = pInstance.getId();
			id = new Integer(l.intValue());
		}
		interruptSession();
		
		{
			ReviewStatus status = wfService.advanceWorkflow(id, "Submit For Physician Review");
			assertEquals(ReviewStatus.PHYSICIAN_REVIEW, status);
			
			status = wfService.advanceWorkflow(id, "Approve Report");
			assertEquals(ReviewStatus.PHYSICIAN_APPROVED, status);
			
			status = wfService.advanceWorkflow(id, "Submit to Central Office SAE Coordinator");
			assertEquals(ReviewStatus.CENTRAL_OFFICE_REVIEW, status);
			
			status = wfService.advanceWorkflow(id, "Approve Report");
			assertEquals(ReviewStatus.SUBMIT_TO_SPONSOR, status);
			
		}
		interruptSession();
		{
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
			assertTrue(nextTransitions.isEmpty());
		}
	}
}
