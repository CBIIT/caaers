package gov.nih.nci.cabig.caaers.web.rule.author;

import gov.nih.nci.cabig.caaers.dao.CtcDao;
import gov.nih.nci.cabig.caaers.dao.NotificationDao;
import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.rules.business.service.CaaersRulesEngineService;
import gov.nih.nci.cabig.caaers.web.ae.CaptureAdverseEventInputCommand;
import gov.nih.nci.cabig.caaers.web.rule.AbstractRuleInputController;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;
import gov.nih.nci.cabig.ctms.web.tabs.Tab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.semanticbits.rules.api.RuleAuthoringService;
import com.semanticbits.rules.brxml.RuleSet;

/**
 * 
 * @author Sujith Vellat Thayyilthodi
 * @author Ion C. Olaru
 */
public class CreateRuleController extends AbstractRuleInputController<CreateRuleCommand> {

	public static final String SPONSOR_LEVEL = "Sponsor";

    public static final String INSTITUTIONAL_LEVEL = "Institution";

    public static final String SPONSOR_DEFINED_STUDY_LEVEL = "SponsorDefinedStudy";

    public static final String INSTITUTION_DEFINED_STUDY_LEVEL = "InstitutionDefinedStudy";
    
    private RuleAuthoringService ruleAuthoringService;

    private StudyDao studyDao;

    private NotificationDao notificationDao;

    private ReportDefinitionDao reportDefinitionDao;

    private OrganizationDao organizationDao;

    private CaaersRulesEngineService caaersRulesEngineService;

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
    
    /**
	 * Will return true if we are entering into create rule flow from Manage Rules 
	 */
	@Override
    protected boolean isFormSubmission(HttpServletRequest request) {
		String fromListPage = WebUtils.getStringParameter(request, "from");
		if(StringUtils.isNotEmpty(fromListPage) && StringUtils.equals(fromListPage, "list")) return true;
		return super.isFormSubmission(request);
    }
	
	/**
	 * If the entry to capture adverse event is from Manage reports, we need to handle the invalid submit case, as it the isFormSubmission is flaged 'true'. 
	 */
	
	@Override
	protected ModelAndView handleInvalidSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String fromListPage = WebUtils.getStringParameter(request, "from");
		if(StringUtils.isEmpty(fromListPage)) return  super.handleInvalidSubmit(request, response);
		
		//generate the form, validate , processFormSubmission.
		Object command = formBackingObject(request);
		ServletRequestDataBinder binder = bindAndValidate(request, command);
		BindException errors = new BindException(binder.getBindingResult());
		
		return processFormSubmission(request, response, command, errors);
		
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) {
    	CreateRuleCommand command = new CreateRuleCommand(ruleAuthoringService, studyDao, notificationDao, caaersRulesEngineService, reportDefinitionDao, organizationDao, ctcDao);
    	
    	String sourcePage = (String) findInRequest(request, "from");
    	if(sourcePage != null && sourcePage.equals("list")){
    		String ruleSetId = (String) findInRequest(request, "ruleSetId");
    		List<RuleSet> ruleSets = ruleAuthoringService.getAllRuleSets();
    		RuleSet rs = null;
    		for(RuleSet ruleSet: ruleSets){
    			if(ruleSet.getId().equals(ruleSetId))
    				rs = ruleSet;
    		}
    		if(rs != null){
    			command.setRuleSetName(rs.getDescription());
    			command.setCategoryIdentifier("");
    			if(rs.getSubject().startsWith("Sponsor defined rules for a study")){
    				command.setLevel(SPONSOR_DEFINED_STUDY_LEVEL);
    				command.setCategoryIdentifier(rs.getStudy());
    				command.setSponsorName(rs.getOrganization());
    			}
    			else if(rs.getSubject().startsWith("Sponsor rules")){
    				command.setLevel(SPONSOR_LEVEL);
    				command.setSponsorName(rs.getOrganization());
    			}
    			else if(rs.getSubject().startsWith("Institution rules")){
    				command.setLevel(INSTITUTIONAL_LEVEL);
    				command.setInstitutionName(rs.getOrganization());
    			}
    			else if(rs.getSubject().startsWith("Institution defined rules for a study")){
    				command.setLevel(INSTITUTION_DEFINED_STUDY_LEVEL);
    				command.setCategoryIdentifier(rs.getStudy());
    				command.setInstitutionName(rs.getOrganization());
    			}
    		}
    	}
    	
    	return command;
    }

    @Override
    protected void initFlow() {
        super.initFlow();

    }

    @Override
    protected String getFlowName() {
        return "Manage Rules";
    }

    protected void addTabs() {
        getFlow().addTab(new SelectRuleTypeTab());
        //getFlow().addTab(new DisplayRuleSetsTab());
        getFlow().addTab(new RuleTab());
        getFlow().addTab(new ReviewTab());
    }
    
    /**
     * Returns the value associated with the <code>attributeName</code>, if present in
     * HttpRequest parameter, if not available, will check in HttpRequest attribute map.
     */
    protected Object findInRequest(final ServletRequest request, final String attributName) {

        Object attr = request.getParameter(attributName);
        if (attr == null) {
            attr = request.getAttribute(attributName);
        }
        return attr;
    }

    /*
    *
    * The methos is responsible for the cases when the page validation should be skipped on Submit,
    * in this case when the user goes back in the flow.
    *
    * */
    @Override
    protected boolean suppressValidation(HttpServletRequest httpServletRequest) {
        int curPage = getCurrentPage(httpServletRequest);
        int targetPage = getTargetPage(httpServletRequest, curPage);
        if (targetPage < curPage) return true;
        return super.suppressValidation(httpServletRequest);
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

    public CaaersRulesEngineService getCaaersRulesEngineService() {
        return caaersRulesEngineService;
    }

    public void setCaaersRulesEngineService(CaaersRulesEngineService caaersRulesEngineService) {
        this.caaersRulesEngineService = caaersRulesEngineService;
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