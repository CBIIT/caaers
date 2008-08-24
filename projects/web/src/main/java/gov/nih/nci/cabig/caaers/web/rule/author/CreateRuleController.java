package gov.nih.nci.cabig.caaers.web.rule.author;

import gov.nih.nci.cabig.caaers.dao.CtcDao;
import gov.nih.nci.cabig.caaers.dao.NotificationDao;
import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.rules.author.RuleAuthoringService;
import gov.nih.nci.cabig.caaers.rules.business.service.RulesEngineService;
import gov.nih.nci.cabig.caaers.web.rule.AbstractRuleInputController;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Sujith Vellat Thayyilthodi
 */
public class CreateRuleController extends AbstractRuleInputController<CreateRuleCommand> {

    private RuleAuthoringService ruleAuthoringService;

    private StudyDao studyDao;

    private NotificationDao notificationDao;

    private ReportDefinitionDao reportDefinitionDao;

    private OrganizationDao organizationDao;

    private RulesEngineService rulesEngineService;

    private CtcDao ctcDao;

    public NotificationDao getNotificationDao() {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    public CreateRuleController() {
        super();
        setBindOnNewForm(false);
        addTabs();
    }

    @Override
    protected ModelAndView processFinish(HttpServletRequest arg0, HttpServletResponse arg1, Object oCommand, BindException arg3) throws Exception {

        CreateRuleCommand command = (CreateRuleCommand) oCommand;
        command.save();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("ruleSet", command.getRuleSet());
        return new ModelAndView("redirectToTriggerList", model);

    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) {
        return new CreateRuleCommand(ruleAuthoringService, studyDao, notificationDao, rulesEngineService, reportDefinitionDao, organizationDao, ctcDao);
    }

    @Override
    protected void initFlow() {
        super.initFlow();

    }

    @Override
    protected String getFlowName() {
        return "Author Rule";
    }

    protected void addTabs() {
        getFlow().addTab(new SelectRuleTypeTab());
        getFlow().addTab(new DisplayRuleSetsTab());
        getFlow().addTab(new RuleTab());
        getFlow().addTab(new ReviewTab());
    }

    public RuleAuthoringService getRuleAuthoringService() {
        return ruleAuthoringService;
    }

    public void setRuleAuthoringService(RuleAuthoringService ruleAuthoringService) {
        this.ruleAuthoringService = ruleAuthoringService;
    }

    public StudyDao getStudyDao() {
        return studyDao;
    }

    public void setStudyDao(StudyDao studyDao) {
        this.studyDao = studyDao;
    }

    public RulesEngineService getRulesEngineService() {
        return rulesEngineService;
    }

    public void setRulesEngineService(RulesEngineService rulesEngineService) {
        this.rulesEngineService = rulesEngineService;
    }

    public ReportDefinitionDao getReportDefinitionDao() {
        return reportDefinitionDao;
    }

    public void setReportDefinitionDao(ReportDefinitionDao reportDefinitionDao) {
        this.reportDefinitionDao = reportDefinitionDao;
    }

    public OrganizationDao getOrganizationDao() {
        return organizationDao;
    }

    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    public void setCtcDao(CtcDao ctcDao) {
        this.ctcDao = ctcDao;
    }
}