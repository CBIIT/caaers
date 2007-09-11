package gov.nih.nci.cabig.caaers.web.rule.notification;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.TreeNode;
import gov.nih.nci.cabig.caaers.domain.report.PlannedNotification;
import gov.nih.nci.cabig.caaers.domain.report.ReportFormat;
import gov.nih.nci.cabig.caaers.domain.report.ReportMandatoryFieldDefinition;
import gov.nih.nci.cabig.caaers.domain.report.TimeScaleUnit;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.web.ControllerTools;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.ctms.web.tabs.AbstractTabbedFlowFormController;
import gov.nih.nci.cabig.ctms.web.tabs.Flow;

public abstract class AbstractReportDefinitionController extends AbstractTabbedFlowFormController<ReportDefinitionCommand>{
	public static final String AJAX_SUBVIEW_PARAMETER = "subview";
	public static final String AJAX_REQUEST_PARAMETER = "isAjax";

	protected ReportDefinitionDao reportDefinitionDao;
	private ConfigProperty configurationProperty;
	protected Map<String, String> roles;
	protected OrganizationDao organizationDao;

	public AbstractReportDefinitionController(){
		initFlow();
		super.setAllowDirtyBack(false);
		super.setAllowDirtyForward(false);
	}

	//initializes the flow
    protected void initFlow() {
        setFlow(new Flow<ReportDefinitionCommand>(getFlowName()));
        BasicsTab firstTab = new BasicsTab();
        ReportDeliveryDefinitionTab deliveryDefTab = new ReportDeliveryDefinitionTab();
        ReportMandatoryFieldDefinitionTab mandatoryFieldTab = new ReportMandatoryFieldDefinitionTab();
        NotificationsTab secondTab = new NotificationsTab();
        ReviewTab thirdTab = new ReviewTab();

        getFlow().addTab(firstTab);
        getFlow().addTab(deliveryDefTab);
        getFlow().addTab(mandatoryFieldTab);
        getFlow().addTab(secondTab);
        getFlow().addTab(thirdTab);
    }

    @Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		ControllerTools.registerDomainObjectEditor(binder, organizationDao);
		ControllerTools.registerEnumEditor(binder, ReportFormat.class);
		ControllerTools.registerEnumEditor(binder, TimeScaleUnit.class);
	}

	@Override
	protected ModelAndView processFinish(HttpServletRequest req, HttpServletResponse res, Object cmd, BindException arg3) throws Exception {
		ReportDefinitionCommand rpDefCmd = (ReportDefinitionCommand)cmd;
		reportDefinitionDao.save(rpDefCmd.getReportDefinition());
        Map<String, Object> model = new ModelMap();
        //model.put("study", command.getStudy().getId());
        return new ModelAndView("redirectToNotificationList", model);
	}


	@Override
	protected String getViewName(HttpServletRequest request, Object command, int page) {
        Object subviewName = findInRequest(request, AJAX_SUBVIEW_PARAMETER);
        if (subviewName != null) {
            return "rule/notification/ajax/" + subviewName;
        } else {
            return super.getViewName(request, command, page);
        }
    }

	/** Should return the name of the flow */
	public abstract String getFlowName();

    private Object findInRequest(HttpServletRequest request, String attributName){

    	Object attr = request.getParameter(attributName);
    	if(attr == null) attr = request.getAttribute(attributName);
    	return attr;
    }

	@Override
	protected boolean suppressValidation(HttpServletRequest request, Object command) {
		Object isAjax = findInRequest(request, AJAX_REQUEST_PARAMETER);
		if(isAjax != null) return true;
		return super.suppressValidation(request, command);
	}

	protected void populateMandatoryFields(List<ReportMandatoryFieldDefinition> mfList, TreeNode node) {
		if(StringUtils.isNotEmpty(node.getPropertyPath())){
			ReportMandatoryFieldDefinition mf = new ReportMandatoryFieldDefinition(node.getPropertyPath());
			mfList.add(mf);
		}
		if(node.getChildren() != null){
			for(TreeNode n : node.getChildren())
				populateMandatoryFields(mfList, n);
		}
	}


	protected Map<Object, Object> collectRoleOptions(){
        Map<Object, Object> options = new LinkedHashMap<Object, Object>();
        options.put("" , "Please select");
        options.putAll(InputFieldFactory.collectOptions(configurationProperty.getMap().get("reportingRolesRefData"),
        		"code", "desc"));
        options.putAll(InputFieldFactory.collectOptions(configurationProperty.getMap().get("invRoleCodeRefData"),
        		"code", "desc"));
        options.putAll(InputFieldFactory.collectOptions(configurationProperty.getMap().get("studyPersonnelRoleRefData"),
        		"code", "desc"));

        return options;
    }

	///BEAN PROPERTIES
	public ReportDefinitionDao getReportDefinitionDao() {
		return reportDefinitionDao;
	}

	public void setReportDefinitionDao(ReportDefinitionDao rdDao) {
		this.reportDefinitionDao = rdDao;
	}

	public void setRoles(Map<String,String> roleList){
		this.roles = roleList;
	}

	public Map<String,String> getAllRoles(){
		return roles;
	}

	public OrganizationDao getOrganizationDao(){
		return organizationDao;
	}
	public void setOrganizationDao(OrganizationDao organizationDao){
		this.organizationDao = organizationDao;
	}
	public ConfigProperty getConfigurationProperty() {
		return configurationProperty;
	}
	public void setConfigurationProperty(ConfigProperty configurationProperty) {
		this.configurationProperty = configurationProperty;
	}

}
