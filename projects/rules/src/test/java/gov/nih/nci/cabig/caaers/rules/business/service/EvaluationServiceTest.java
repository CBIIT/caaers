package gov.nih.nci.cabig.caaers.rules.business.service;

import gov.nih.nci.cabig.caaers.CaaersTestCase;
import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.service.ReportSubmittability;
import gov.nih.nci.cabig.caaers.service.ReportService;
import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluationServiceTest extends CaaersTestCase {

	AdverseEventEvaluationService adverseEventEvaluationService;
    ReportDefinitionDao reportDefinitionDao;
    ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao;
    ReportService reportService;
    OrganizationDao  organizationDao;

    EvaluationServiceImpl service;

    @Override
	protected void setUp() throws Exception {
		super.setUp();
		reportDefinitionDao = registerDaoMockFor(ReportDefinitionDao.class);
		expeditedAdverseEventReportDao = registerDaoMockFor(ExpeditedAdverseEventReportDao.class);
		organizationDao = registerDaoMockFor(OrganizationDao.class);

		adverseEventEvaluationService = registerMockFor(AdverseEventEvaluationService.class);
		reportService = registerMockFor(ReportService.class);

		service =  new EvaluationServiceImpl();

		service.setExpeditedAdverseEventReportDao(expeditedAdverseEventReportDao);
		service.setReportDefinitionDao(reportDefinitionDao);

		service.setReportService(reportService);
		service.setAdverseEventEvaluationService(adverseEventEvaluationService);

	}

    public void testFindRequiredReportDefinitions() throws Exception {
    	String n1 = "24 Hr report";
    	String n2 =  "55 day report";

    	ReportDefinition rd1 = new ReportDefinition();
    	rd1.setName(n1);

    	ReportDefinition rd2 = new ReportDefinition();
    	rd2.setName(n2);

    	List<String> reportNames = new ArrayList<String>();
    	reportNames.add(n1);
    	reportNames.add(n2);
    	Map<String, List<String>> map = new HashMap<String, List<String>>();
    	map.put("junk", reportNames);

    	ExpeditedAdverseEventReport aereport = new ExpeditedAdverseEventReport();
    	expect(adverseEventEvaluationService.evaluateSAEReportSchedule(aereport)).andReturn(map);

    	expect(reportDefinitionDao.getByName(n1)).andReturn(rd1);
    	expect(reportDefinitionDao.getByName(n2)).andReturn(rd2);
    	replayMocks();
    	List<ReportDefinition> actualDefList = service.findRequiredReportDefinitions(aereport);
    	verifyMocks();

    	assertEquals("incorrect number of report definitions", 2, actualDefList.size());
    	assertEquals("report definition name is incorrect" , n1, actualDefList.get(0).getName());



    }

    public void testIsSubmittable() throws Exception{
        ExpeditedAdverseEventReport aeReport = new ExpeditedAdverseEventReport();
        List<ExpeditedReportSection> mandatorySections = Arrays.asList(ExpeditedReportSection.MEDICAL_INFO_SECTION, ExpeditedReportSection.SURGERY_INTERVENTION_SECTION);
        ReportSubmittability messages = new ReportSubmittability();
        Report report = new Report();
        report.setAeReport(aeReport);
        aeReport.addReport(report);
        expect(adverseEventEvaluationService.mandatorySectionsForReport(report)).andReturn(mandatorySections);
        expect(reportService.validate(report, mandatorySections)).andReturn(messages);
        replayMocks();
        ReportSubmittability msgs = service.isSubmittable(report);
        verifyMocks();
        assertEquals("ErrorMessage object is not same",messages, msgs);
    }
    
    public void testLoadingFromApplicationContextXml(){
    	EvaluationServiceImpl es = (EvaluationServiceImpl)getDeployedApplicationContext().getBean("evaluationService");
    	AdverseEventEvaluationServiceImpl aes = (AdverseEventEvaluationServiceImpl)es.getAdverseEventEvaluationService();
    	assertNotNull("business rules execution service should not be null", aes.getBusinessRulesExecutionService());
    	assertNotNull("rules engine service should not be null", aes.getRulesEngineService());
    }

}
