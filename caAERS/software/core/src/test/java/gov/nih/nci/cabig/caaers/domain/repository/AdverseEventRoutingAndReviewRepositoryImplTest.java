package gov.nih.nci.cabig.caaers.domain.repository;

import gov.nih.nci.cabig.caaers.CaaersNoSecurityTestCase;
import gov.nih.nci.cabig.caaers.dao.AdverseEventReportingPeriodDao;
import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.query.AdverseEventReportingPeriodForReviewQuery;
import gov.nih.nci.cabig.caaers.dao.report.ReportDao;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.dto.AdverseEventReportingPeriodDTO;
import gov.nih.nci.cabig.caaers.domain.dto.ExpeditedAdverseEventReportDTO;
import gov.nih.nci.cabig.caaers.domain.dto.ReportDTO;
import gov.nih.nci.cabig.caaers.domain.factory.AERoutingAndReviewDTOFactory;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.workflow.ReportReviewComment;
import gov.nih.nci.cabig.caaers.domain.workflow.ReportingPeriodReviewComment;
import gov.nih.nci.cabig.caaers.domain.workflow.ReviewComment;
import gov.nih.nci.cabig.caaers.domain.workflow.StudySiteWorkflowConfig;
import gov.nih.nci.cabig.caaers.domain.workflow.WorkflowConfig;
import gov.nih.nci.cabig.caaers.service.EvaluationService;
import gov.nih.nci.cabig.caaers.service.ReportSubmittability;
import gov.nih.nci.cabig.caaers.service.workflow.WorkflowService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.classextension.EasyMock;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ProcessInstance;
/**
 * 
 * @author Biju Joseph
 *
 */
public class AdverseEventRoutingAndReviewRepositoryImplTest extends CaaersNoSecurityTestCase {
	
	
	AdverseEventReportingPeriodDao rpDao;
	ExpeditedAdverseEventReportDao rDao;
	ReportDao reportDao;
	AERoutingAndReviewDTOFactory factory;
	WorkflowService wfService;
	AdverseEventRoutingAndReviewRepositoryImpl impl;
	ProcessInstance processInstance;
	ContextInstance contextInstance;
	ReportValidationService reportValidationService;
	
	Map<String, Object> variables = new HashMap<String, Object>();
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rDao = registerDaoMockFor(ExpeditedAdverseEventReportDao.class);
		rpDao = registerDaoMockFor(AdverseEventReportingPeriodDao.class);
		reportDao = registerDaoMockFor(ReportDao.class);
		factory = registerMockFor(AERoutingAndReviewDTOFactory.class);
		wfService = registerMockFor(WorkflowService.class);
		processInstance = registerMockFor(ProcessInstance.class);
		contextInstance = registerMockFor(ContextInstance.class);
		reportValidationService = registerMockFor(ReportValidationService.class);
		
		impl = new AdverseEventRoutingAndReviewRepositoryImpl();
		impl.setAdverseEventReportingPeriodDao(rpDao);
		impl.setRoutingAndReviewFactory(factory);
		impl.setExpeditedAdverseEventReportDao(rDao);
		impl.setReportDao(reportDao);
		impl.setWorkflowService(wfService);
		impl.setReportValidationService(reportValidationService);
	}
	
	public void testFetchReviewCommentsForReport() {
		Report report = Fixtures.createReport("testReport");
		List<ReportReviewComment> reviewComments = new ArrayList<ReportReviewComment>();
		report.setReviewComments(reviewComments);
		Integer reportId = 10;

		EasyMock.expect(reportDao.getById(reportId)).andReturn(report);
		replayMocks();
		List<? extends  ReviewComment> comments = impl.fetchReviewCommentsForReport(reportId);
		verifyMocks();
		assertSame(reviewComments, comments);
	}

	public void testFetchReviewCommentsForReportingPeriod() {
		AdverseEventReportingPeriod rp = Fixtures.createReportingPeriod();
		List<ReportingPeriodReviewComment> reviewComments = new ArrayList<ReportingPeriodReviewComment>();
		rp.setReviewComments(reviewComments);
		Integer rpId = 5;
		EasyMock.expect(rpDao.getById(rpId)).andReturn(rp);
		replayMocks();
		List<? extends  ReviewComment> comments = impl.fetchReviewCommentsForReportingPeriod(rpId);
		verifyMocks();
		assertSame(reviewComments, comments);
	}

	public void testAddReportReviewComment() {
		Integer reportId = 10;
		String comment = "mycomment";
		String userId = "userId";
		
		Report report = Fixtures.createReport("testReport");
		report.setReviewComments(new ArrayList<ReportReviewComment>());
		EasyMock.expect(reportDao.getById(reportId)).andReturn(report);
		reportDao.save(report);
		replayMocks();
		impl.addReportReviewComment(reportId, comment, userId);
		verifyMocks();
	}
	
	public void testAddReportingPeriodReviewCommentWithObject(){
		String comment = "mycomment";
		String userId = "userId";
		AdverseEventReportingPeriod rp = Fixtures.createReportingPeriod();
		rp.setReviewComments(new ArrayList<ReportingPeriodReviewComment>());
		impl.addReportingPeriodReviewComment(rp, comment, userId);
		assertEquals("Incorrect number of comments", 1, rp.getReviewComments().size());
	}

	public void testAddReportingPeriodReviewCommentWithId() {
		Integer reportingPeriodId = 5;
		String comment = "mycomment";
		String userId = "userId";
		
		AdverseEventReportingPeriod rp = Fixtures.createReportingPeriod();
		rp.setReviewComments(new ArrayList<ReportingPeriodReviewComment>());
		EasyMock.expect(rpDao.getById(reportingPeriodId)).andReturn(rp);
		rpDao.modifyOrSaveReviewStatusAndComments(rp);
		replayMocks();
		impl.addReportingPeriodReviewComment(reportingPeriodId, comment, userId);
		verifyMocks();
	}
	
	public void testEditReportingPeriodReviewCommentWithoutObject(){
		String newComment = "new Comment";
		String userId = "userId";
		Integer commentId = 2;
		AdverseEventReportingPeriod rp = Fixtures.createReportingPeriod();
		ReportingPeriodReviewComment comment = Fixtures.createReportingPeriodReviewComment(1, "comment 1");
		ArrayList<ReportingPeriodReviewComment> commentsList = new ArrayList<ReportingPeriodReviewComment>();
		commentsList.add(comment);
		comment = Fixtures.createReportingPeriodReviewComment(2, "comment 2");
		commentsList.add(comment);
		rp.setReviewComments(commentsList);
		impl.editReportingPeriodReviewComment(rp, newComment, userId, commentId);
		assertEquals("Edit comment isnt working correctly", "new Comment", rp.getReviewComments().get(1).getUserComment());
	}
	
	public void testDeleteReportingPeriodReviewComment(){
		AdverseEventReportingPeriod rp = Fixtures.createReportingPeriod();
		ArrayList<ReportingPeriodReviewComment> commentsList = new ArrayList<ReportingPeriodReviewComment>();
		commentsList.add(Fixtures.createReportingPeriodReviewComment(1, "comment 1"));
		commentsList.add(Fixtures.createReportingPeriodReviewComment(2, "comment 2"));
		commentsList.add(Fixtures.createReportingPeriodReviewComment(3, "comment 3"));
		rp.setReviewComments(commentsList);
		rp.setId(1);
		EasyMock.expect(rpDao.getById(1)).andReturn(rp);
		rpDao.modifyOrSaveReviewStatusAndComments(rp);
		replayMocks();
		impl.deleteReportingPeriodReviewComment(1, 2);
		verifyMocks();
		assertEquals("Comment not deleted from comments list", 2, rp.getReviewComments().size());
	}
	
	public void testDeleteReportReviewComment(){
		Report report = Fixtures.createReport("testReport");
		ArrayList<ReportReviewComment> commentsList = new ArrayList<ReportReviewComment>();
		commentsList.add(Fixtures.createReportReviewComment(1, "comment 1"));
		commentsList.add(Fixtures.createReportReviewComment(2, "comment 2"));
		commentsList.add(Fixtures.createReportReviewComment(3, "comment 3"));
		report.setReviewComments(commentsList);
		EasyMock.expect(reportDao.getById(10)).andReturn(report);
		reportDao.save(report);
		replayMocks();
		impl.deleteReportReviewComment(10, 2);
		verifyMocks();
		assertEquals("Comment not deleted from comments list", 2, report.getReviewComments().size());
 	}
	
	public void testEditReportingPeriodReviewCommentWithId(){
		Integer reportingPeriodId = 5;
		String newComment = "new Comment";
		String userId = "userId";
		Integer commentId = 2;
		
		AdverseEventReportingPeriod rp = Fixtures.createReportingPeriod();
		ReportingPeriodReviewComment comment = Fixtures.createReportingPeriodReviewComment(1, "comment 1");
		ArrayList<ReportingPeriodReviewComment> commentsList = new ArrayList<ReportingPeriodReviewComment>();
		commentsList.add(comment);
		comment = Fixtures.createReportingPeriodReviewComment(2, "comment 2");
		commentsList.add(comment);
		rp.setReviewComments(commentsList);
		EasyMock.expect(rpDao.getById(reportingPeriodId)).andReturn(rp);
		rpDao.modifyOrSaveReviewStatusAndComments(rp);
		replayMocks();
		impl.editReportingPeriodReviewComment(reportingPeriodId, newComment, userId, commentId);
		verifyMocks();
		
		assertEquals("Edit comment isnt working correctly", "new Comment", rp.getReviewComments().get(1).getUserComment());
	}

	public void testFindAdverseEventReportingPeriods() {
		String userId = "tester";
		AdverseEventReportingPeriod rp = Fixtures.createReportingPeriod();
		ExpeditedAdverseEventReport aeReport = Fixtures.createSavableExpeditedReport();
		Report report = Fixtures.createReport("test report");
		report.setStatus(ReportStatus.INPROCESS);
		report.setReviewStatus(ReviewStatus.DRAFT_INCOMPLETE);
		report.setWorkflowId(1);
		rp.addAeReport(aeReport);
		aeReport.addReport(report);
		
		List<AdverseEventReportingPeriod> reportingPeriods = new ArrayList<AdverseEventReportingPeriod>();
		reportingPeriods.add(rp);
		
		rp.setReviewStatus(ReviewStatus.DRAFT_INCOMPLETE);
		rp.setWorkflowId(1);
		
		
		AdverseEventReportingPeriodDTO rpDto = new AdverseEventReportingPeriodDTO();
		ExpeditedAdverseEventReportDTO rDto = new ExpeditedAdverseEventReportDTO();
		ReportDTO reportDto = new ReportDTO();
		rDto.addReportDTO(reportDto);
		List<String> possibleActions = new ArrayList<String>();
		possibleActions.add("abc");
		reportDto.setPossibleActions(possibleActions);
		EasyMock.expect(rpDao.findAdverseEventReportingPeriods((AdverseEventReportingPeriodForReviewQuery) EasyMock.anyObject())).andReturn(reportingPeriods);
	
		EasyMock.expect(factory.createAdverseEventEvalutionPeriodDTO(rp, userId, true)).andReturn(rpDto);
		EasyMock.expect(factory.createAdverseEventReportDTO(aeReport, userId)).andReturn(rDto);
		replayMocks();
		
		Participant participant = Fixtures.createParticipant("Joel", "biju");
		Study study = Fixtures.createStudy("Hello");
		Organization org = Fixtures.createOrganization("test org");
		ReviewStatus reviewStatus = null;
		ReportStatus reportStatus = null;
		
		List<AdverseEventReportingPeriodDTO> dtos = impl.findAdverseEventReportingPeriods(participant, study, org, reviewStatus, reportStatus, userId, true);
		
		verifyMocks();
		
		assertEquals(1, dtos.size());
		assertEquals(1, dtos.get(0).getAeReports().size());
	}

	public void testIsReportingPeriodHavingSpecifiedReviewStatus() {
		AdverseEventReportingPeriod rp = Fixtures.createReportingPeriod();
		boolean result = impl.isReportingPeriodHavingSpecifiedReviewStatus(rp, null);
		assertTrue(result);
	}
	
	public void testIsReportingPeriodHavingReportsWithSpecifiedStatusPositive() {
		AdverseEventReportingPeriod rp = Fixtures.createReportingPeriod();
		Report report = Fixtures.createReport("test report");
		report.setStatus(ReportStatus.COMPLETED);
		ExpeditedAdverseEventReport aeReport = new ExpeditedAdverseEventReport();
		aeReport.addReport(report);
		rp.addAeReport(aeReport);
		assertTrue(impl.isReportingPeriodHavingReportsWithSpecifiedStatus(rp, ReportStatus.COMPLETED));
	}
	
	public void testIsReportingPeriodHavingReportsWithSpecifiedStatusNegative(){
		AdverseEventReportingPeriod rp = Fixtures.createReportingPeriod();
		Report report = Fixtures.createReport("test report");
		report.setStatus(ReportStatus.INPROCESS);
		ExpeditedAdverseEventReport aeReport = new ExpeditedAdverseEventReport();
		aeReport.addReport(report);
		rp.addAeReport(aeReport);
		assertFalse(impl.isReportingPeriodHavingReportsWithSpecifiedStatus(rp, ReportStatus.COMPLETED));
	}

	public void testIsEntityHavingSpecifiedReviewStatus() {
		Report report = Fixtures.createReport("testReport");
		report.setReviewStatus(ReviewStatus.DRAFT_INCOMPLETE);
		
		boolean result = impl.isEntityHavingSpecifiedReviewStatus(null, report);
		assertTrue(result);
		
		report.setReviewStatus(null);
		result = impl.isEntityHavingSpecifiedReviewStatus(ReviewStatus.DRAFT_INCOMPLETE, report);
		assertFalse(result);
		
		report.setReviewStatus(ReviewStatus.DRAFT_INCOMPLETE);
		result = impl.isEntityHavingSpecifiedReviewStatus(ReviewStatus.DRAFT_INCOMPLETE, report);
		assertTrue(result);
	}
	
	
	
	public void testAdvanceReportWorkflow(){
		Integer id = 10;
		Integer wfId = 5;
		String transitionToTake = "abcd";
		String loginId = "SYSTEM_ADMIN";
		ReviewStatus reviewStatus = ReviewStatus.DRAFT_INCOMPLETE;
		Report report = Fixtures.createReport("testReport");
		report.setStatus(ReportStatus.INPROCESS);
		report.setReviewComments(new ArrayList<ReportReviewComment>());
		report.setWorkflowId(wfId);
		List<String> transitions = new ArrayList<String>();

        ReportSubmittability errorMessagesMock = registerMockFor(ReportSubmittability.class);
        EasyMock.expect(errorMessagesMock.isSubmittable()).andReturn(true);
		EasyMock.expect(reportValidationService.isSubmittable(report)).andReturn(errorMessagesMock);
		EasyMock.expect(wfService.nextTransitionNames(wfId, loginId)).andReturn(transitions);
		EasyMock.expect(wfService.advanceWorkflow(wfId, transitionToTake)).andReturn(reviewStatus);
		EasyMock.expect(reportDao.getById(id)).andReturn(report);
		reportDao.save(report);
		replayMocks();
		List<String> transitionsNames = impl.advanceReportWorkflow(wfId, transitionToTake, id, loginId);
		
		verifyMocks();
		assertEquals("A review comment for the action of advancing workflow was not added", 1, report.getReviewComments().size());
	}
	
	public void testAdvanceReportingPeriodWorkflow(){
		Integer id = 5;
		Integer wfId = 5;
		String loginId = "SYSTEM_ADMIN";
		String transitionToTake = "abcd";
		List<String> transitions = new ArrayList<String>();
		ReviewStatus rs = ReviewStatus.DRAFT_INCOMPLETE;
		AdverseEventReportingPeriod rp = Fixtures.createReportingPeriod();
		rp.setReviewComments(new ArrayList<ReportingPeriodReviewComment>());
		EasyMock.expect(wfService.advanceWorkflow(wfId, transitionToTake)).andReturn(rs);
		EasyMock.expect(wfService.nextTransitionNames(wfId, loginId)).andReturn(transitions);
		EasyMock.expect(rpDao.getById(id)).andReturn(rp);
		rpDao.modifyOrSaveReviewStatusAndComments(rp);
		replayMocks();
		List<String> transitionNames = impl.advanceReportingPeriodWorkflow(wfId, transitionToTake, id, loginId);
		
		verifyMocks();
		assertEquals("A review comment for the action of advancing workflow was not added", 1, rp.getReviewComments().size());
	}
	
	public void testEnactReportingPeriodWorkflow() {
		long processId = 5;
		StudyParticipantAssignment assignment = Fixtures.createAssignment();
		AdverseEventReportingPeriod reportingPeriod = Fixtures.createReportingPeriod();
		reportingPeriod.setId(44);
		WorkflowConfig workflowConfig = Fixtures.createWorkflowConfig("test");
		StudySite site = assignment.getStudySite();
		StudySiteWorkflowConfig ssWfCfg = new StudySiteWorkflowConfig("reportingPeriod", site, workflowConfig);
		site.addStudySiteWorkflowConfig(ssWfCfg);
		reportingPeriod.setAssignment(assignment);
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(WorkflowService.VAR_STUDY_ID, site.getStudy().getId());
		variables.put(WorkflowService.VAR_WF_TYPE, AdverseEventReportingPeriod.class.getName());
		variables.put(WorkflowService.VAR_REPORTING_PERIOD_ID, reportingPeriod.getId());
		
		EasyMock.expect(wfService.createProcessInstance("test", variables)).andReturn(processInstance);
	    EasyMock.expect(processInstance.getId()).andReturn(processId).anyTimes();
	    rpDao.modifyOrSaveReviewStatusAndComments(reportingPeriod);
		replayMocks();
		impl.enactReportingPeriodWorkflow(reportingPeriod);
		verifyMocks();
	}
	
	public void testEnactReportWorkflow(){
		long processId = 5;
		StudyParticipantAssignment assignment = Fixtures.createAssignment();
		ExpeditedAdverseEventReport aeReport = Fixtures.createSavableExpeditedReport();
		Report report = Fixtures.createReport("testReport");
		aeReport.setId(55);
		AdverseEventReportingPeriod reportingPeriod = Fixtures.createReportingPeriod();
		WorkflowConfig workflowConfig = Fixtures.createWorkflowConfig("test");
		StudySite site = assignment.getStudySite();
		StudySiteWorkflowConfig ssWfCfg = new StudySiteWorkflowConfig("report", site, workflowConfig);
		site.addStudySiteWorkflowConfig(ssWfCfg);
		reportingPeriod.addAeReport(aeReport);
		aeReport.setAssignment(assignment);
		aeReport.addReport(report);
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(WorkflowService.VAR_STUDY_ID, site.getStudy().getId());
		variables.put(WorkflowService.VAR_WF_TYPE, Report.class.getName());
		variables.put(WorkflowService.VAR_REPORT_ID, report.getId());
		variables.put(WorkflowService.VAR_EXPEDITED_REPORT_ID, aeReport.getId());
		
		EasyMock.expect(wfService.createProcessInstance("test", variables)).andReturn(processInstance);
	    EasyMock.expect(processInstance.getId()).andReturn(processId).anyTimes();
	    reportDao.save(report);
		replayMocks();
		impl.enactReportWorkflow(report);
		verifyMocks();
	}
	
	public void testNextTransitionsForAeReportWithIncompleteReports() throws Exception{
		Report report = Fixtures.createReport("testReport");
		report.setWorkflowId(1);
		ReportSubmittability errorMessagesMock = registerMockFor(ReportSubmittability.class);
		report.setStatus(ReportStatus.PENDING);
		List<String> transitions = new ArrayList<String>();
		transitions.add("test action");
		transitions.add("Submit to Central Office Report Reviewer");
		
		EasyMock.expect(wfService.nextTransitionNames(1, "SYSTEM_ADMIN")).andReturn(transitions);
		EasyMock.expect(reportValidationService.isSubmittable(report)).andReturn(errorMessagesMock);
		EasyMock.expect(errorMessagesMock.isSubmittable()).andReturn(false);
		replayMocks();
		List<String> filteredTransitions = impl.nextTransitionNamesForReportWorkflow(report, "SYSTEM_ADMIN");
		verifyMocks();
		assertEquals(1, filteredTransitions.size());
	}

	public void testNextTransitionsForAeReportWithCompleteReports() throws Exception{
		Report report = Fixtures.createReport("testReport");
		report.setWorkflowId(1);
		ReportSubmittability errorMessagesMock = registerMockFor(ReportSubmittability.class);
		report.setStatus(ReportStatus.PENDING);
		List<String> transitions = new ArrayList<String>();
		transitions.add("test action");
		transitions.add("Submit to Central Office Report Reviewer");
		
		EasyMock.expect(wfService.nextTransitionNames(1, "SYSTEM_ADMIN")).andReturn(transitions);
		EasyMock.expect(reportValidationService.isSubmittable(report)).andReturn(errorMessagesMock);
		EasyMock.expect(errorMessagesMock.isSubmittable()).andReturn(true);
		replayMocks();
		List<String> filteredTransitions = impl.nextTransitionNamesForReportWorkflow(report, "SYSTEM_ADMIN");
		verifyMocks();
		assertEquals(2, filteredTransitions.size());
	}

    //test the transition,when physician do not have login.
	public void testNextTransitionsForAeReportWithIncompleteReportsAndInvestigatorHasNoLogin() throws Exception{
		Report report = Fixtures.createReport("testReport");
        Investigator investigator = Fixtures.createInvestigator("tester");
        investigator.setLoginId(null);
        report.getPhysician().setUser(investigator);
		report.setWorkflowId(1);
		ReportSubmittability errorMessagesMock = registerMockFor(ReportSubmittability.class);
		report.setStatus(ReportStatus.PENDING);
		List<String> transitions = new ArrayList<String>();
		transitions.add("test action");
		transitions.add("Submit to Central Office Report Reviewer");
        transitions.add("Send to Physician for Review");

		EasyMock.expect(wfService.nextTransitionNames(1, "SYSTEM_ADMIN")).andReturn(transitions);
		EasyMock.expect(reportValidationService.isSubmittable(report)).andReturn(errorMessagesMock);
		EasyMock.expect(errorMessagesMock.isSubmittable()).andReturn(false);
		replayMocks();
		List<String> filteredTransitions = impl.nextTransitionNamesForReportWorkflow(report, "SYSTEM_ADMIN");
		verifyMocks();
		assertEquals(1, filteredTransitions.size());
	}

    //test the transition, were the physician can login to the system.
	public void testNextTransitionsForAeReportWithAnInvestigatorHavingLogin() throws Exception{
		Report report = Fixtures.createReport("testReport");
        Investigator investigator = Fixtures.createInvestigator("tester");
        investigator.setLoginId("hai");
        report.getPhysician().setUser(investigator);
		report.setWorkflowId(1);
		ReportSubmittability errorMessagesMock = registerMockFor(ReportSubmittability.class);
		report.setStatus(ReportStatus.PENDING);
		List<String> transitions = new ArrayList<String>();
		transitions.add("test action");
		transitions.add("Send to Physician for Review");

		EasyMock.expect(wfService.nextTransitionNames(1, "SYSTEM_ADMIN")).andReturn(transitions);
		EasyMock.expect(reportValidationService.isSubmittable(report)).andReturn(errorMessagesMock);
		EasyMock.expect(errorMessagesMock.isSubmittable()).andReturn(true);
		replayMocks();
		List<String> filteredTransitions = impl.nextTransitionNamesForReportWorkflow(report, "SYSTEM_ADMIN");
		verifyMocks();
		assertEquals(2, filteredTransitions.size());
	}
	
	public void testAeReportHasWorkflowOnActiveReportsWithActiveReports(){
		Report report = Fixtures.createReport("test report");
		report.setStatus(ReportStatus.COMPLETED);
		report.setWorkflowId(1);
		ExpeditedAdverseEventReport aeReport = new ExpeditedAdverseEventReport();
		aeReport.addReport(report);
		assertTrue("aeReportHasWorkflowOnActive reports should have returned true", impl.aeReportHasWorkflowOnActiveReports(aeReport));
	}
	
	public void testAeReportHasWorkflowOnActiveReportsWithInactiveReports(){
		Report report = Fixtures.createReport("test report");
		report.setStatus(ReportStatus.AMENDED);
		report.setWorkflowId(2);
		ExpeditedAdverseEventReport aeReport = new ExpeditedAdverseEventReport();
		aeReport.addReport(report);
		assertFalse("aeReportHasWorkflowOnActive reports should have returned false", impl.aeReportHasWorkflowOnActiveReports(aeReport));
	}
}
