package gov.nih.nci.cabig.caaers.web.rule;

import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;

import com.semanticbits.rules.api.RepositoryService;
import com.semanticbits.rules.api.RuleAuthoringService;
import com.semanticbits.rules.api.RuleDeploymentService;
import com.semanticbits.rules.api.RulesEngineService;
import com.semanticbits.rules.brxml.Column;
import com.semanticbits.rules.brxml.Condition;
import com.semanticbits.rules.brxml.MetaData;
import com.semanticbits.rules.brxml.Rule;
import com.semanticbits.rules.brxml.RuleSet;
import com.semanticbits.rules.impl.RepositoryServiceImpl;
import com.semanticbits.rules.impl.RuleAuthoringServiceImpl;
import com.semanticbits.rules.impl.RuleDeploymentServiceImpl;
import com.semanticbits.rules.impl.RulesEngineServiceImpl;

import gov.nih.nci.cabig.caaers.dao.CtcDao;
import gov.nih.nci.cabig.caaers.dao.NotificationDao;
import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.rules.business.service.CaaersRulesEngineService;
import gov.nih.nci.cabig.caaers.web.DwrFacadeTestCase;
import gov.nih.nci.cabig.caaers.web.rule.author.CreateRuleCommand;
import gov.nih.nci.cabig.caaers.web.rule.author.CreateRuleController;

/**
 * @author Sameer Sawant
 * @author Biju Joseph
 */
public class RuleAjaxFacadeTest extends DwrFacadeTestCase{
	
	private RuleAjaxFacade facade;
	private CaaersRulesEngineService caaersRulesEngineService;
	private ReportDefinitionDao reportDefinitionDao;
	private OrganizationDao organizationDao;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		caaersRulesEngineService = registerMockFor(CaaersRulesEngineService.class);
		reportDefinitionDao = registerDaoMockFor(ReportDefinitionDao.class);
		organizationDao = registerDaoMockFor(OrganizationDao.class);
	
		facade = new RuleAjaxFacade();
		facade.setOrganizationDao(organizationDao);
        facade.setCaaersRulesEngineService(caaersRulesEngineService);
	}
	
	private CreateRuleCommand setupCreateRuleCommand(){
		CreateRuleCommand command = new CreateRuleCommand(caaersRulesEngineService, reportDefinitionDao,  organizationDao);
		command.setRuleSet(new RuleSet());
		List<Rule> rulesList = new ArrayList<Rule>();
		rulesList.add(new Rule());
		rulesList.get(0).setMetaData(new MetaData());
		rulesList.get(0).getMetaData().setName("Rule 1");
		List<Column> columnList1 = new ArrayList<Column>();
		columnList1.add(new Column());
		columnList1.add(new Column());
		columnList1.add(new Column());
		rulesList.get(0).setCondition(new Condition());
		rulesList.get(0).getCondition().setColumn(columnList1);
		
		rulesList.add(new Rule());
		rulesList.get(1).setMetaData(new MetaData());
		rulesList.get(1).getMetaData().setName("Rule 2");
		List<Column> columnList2 = new ArrayList<Column>();
		columnList2.add(new Column());
		columnList2.add(new Column());
		rulesList.get(1).setCondition(new Condition());
		rulesList.get(1).getCondition().setColumn(columnList2);
		command.getRuleSet().setRule(rulesList);
		
		command.getRuleSet().setName("test RuleSet");
		session.setAttribute(CreateRuleController.class.getName() + ".FORM.command", command);
	    expect(webContext.getSession()).andReturn(session).anyTimes();
	    return command;
	}
	
	public void testRemoveCondition(){
		CreateRuleCommand command = setupCreateRuleCommand();
		replayMocks();
		facade.removeCondition(0, 1);
		verifyMocks();
		assertEquals("Error in deleting condition", 2, command.getRuleSet().getRule().get(0).getCondition().getColumn().size());
	}
	
	public void testRemoveRule() throws Exception{
		CreateRuleCommand command = setupCreateRuleCommand();
		facade.getCaaersRulesEngineService().deleteRule("test RuleSet", "Rule 2");
		replayMocks();
		facade.removeRule(1);
		verifyMocks();
		assertEquals("Error in deleting rule", 1, command.getRuleSet().getRule().size());
	}
	
	public void testAddRule() throws Exception{
		CreateRuleCommand command = setupCreateRuleCommand();
		Organization testOrg = Fixtures.createOrganization("test Org");
		ReportDefinition rDef = Fixtures.createReportDefinition("test Report Definition");
		testOrg.addReportDefinition(rDef);
		
		//expect(facade.getOrganizationDao().getByName("test Org")).andReturn(testOrg);
		expect(webContext.forwardToString((String)EasyMock.anyObject())).andReturn("").once();
		
		replayMocks();
		facade.addRule();
		verifyMocks();
		
		assertEquals("Error in adding a new rule", 3, command.getRuleSet().getRule().size());
		assertEquals("Incorrect rule name", "Rule-3", command.getRuleSet().getRule().get(2).getMetaData().getName());
	}

    //checks that report definition is comming out. 
    public void testGetAjaxObjects(){
        CreateRuleCommand command = setupCreateRuleCommand();
        ReportDefinition rd1 = Fixtures.createReportDefinition("test");
        rd1.setId(1);
        command.setReportDefinitions(Arrays.asList(rd1));

        replayMocks();
        List<RuleAjaxObject> list = facade.getAjaxObjects("reportDefinitionName", "");
        verifyMocks();

        assertEquals(1, list.size());
        assertEquals("test", list.get(0).getValue()); 
    }
	
}
