package gov.nih.nci.cabig.caaers.web.rule.notification;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.dao.ConfigPropertyDao;
import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.query.OrganizationQuery;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportVersionDao;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.report.PlannedNotification;
import gov.nih.nci.cabig.caaers.domain.report.ReportDeliveryDefinition;
import gov.nih.nci.cabig.caaers.domain.report.ReportFormat;
import gov.nih.nci.cabig.caaers.domain.repository.OrganizationRepository;
import gov.nih.nci.cabig.caaers.domain.repository.ReportVersionRepository;
import gov.nih.nci.cabig.caaers.tools.ObjectTools;
import gov.nih.nci.cabig.caaers.utils.ranking.RankBasedSorterUtils;
import gov.nih.nci.cabig.caaers.utils.ranking.Serializer;
import gov.nih.nci.cabig.caaers.web.dwr.AjaxOutput;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.springframework.web.servlet.mvc.AbstractFormController;

public class ReportDefinitionAjaxFacade {

    public static final String AJAX_SUBVIEW_PARAMETER = "subview";
    public static final String AJAX_INDEX_PARAMETER = "index";
    public static final String AJAX_REQUEST_PARAMETER = "isAjax";
    public static final String AJAX_ORIGINAL_INDEX_PARAMETER = "originalIndex";
    public static final String CREATE_FLOW_COMMAND_KEY = CreateReportDefinitionController.class.getName() + ".FORM.command";
    public static final String EDIT_FLOW_COMMAND_KEY = EditReportDefinitionController.class.getName() + ".FORM.command";
    private static final Log log = LogFactory.getLog(ReportDefinitionAjaxFacade.class);
    private OrganizationDao orgDao;
    private ReportDefinitionDao repDefDao;
    private ConfigPropertyDao cpDao;
    private OrganizationRepository organizationRepository;
    private ReportVersionRepository reportVersionRepository;
    
    public void resetReports(){
    	reportVersionRepository.updateInProcessReports();
    }
    // /LOGIC
    public String addReportDeliveryDefinition(int index, int type) {
        WebContext webCtx = WebContextFactory.get();
        HttpServletRequest request = webCtx.getHttpServletRequest();
        ReportDefinitionCommand cmd = getCommand(request);
        List<ReportDeliveryDefinition> deliveries = cmd.getReportDefinition().getDeliveryDefinitions();

        int originalIndex = (deliveries == null) ? 0 : deliveries.size();
        ReportDeliveryDefinition rdd = deliveries.get(originalIndex);
        if (type == ReportDeliveryDefinition.ENTITY_TYPE_SYSTEM) {
            rdd.setFormat(ReportFormat.XML);
            rdd.setEndPointType(ReportDeliveryDefinition.ENDPOINT_TYPE_URL);
        } else {
            rdd.setFormat(ReportFormat.PDF);
            rdd.setEndPointType(ReportDeliveryDefinition.ENDPOINT_TYPE_EMAIL);
        }
        rdd.setEntityType(type);

        // --- add necessary parameters
        request.setAttribute(AJAX_INDEX_PARAMETER, index);
        request.setAttribute(AJAX_SUBVIEW_PARAMETER, "addReportDefinitionSection");
        request.setAttribute(AJAX_REQUEST_PARAMETER, "AJAX");
        request.setAttribute(AJAX_ORIGINAL_INDEX_PARAMETER, originalIndex);
        String url = getCurrentPageContextRelative(webCtx);

        return getOutputFromJsp(url);
    }

    public String addNotification(int index, int indexOnScale) {
        WebContext webCtx = WebContextFactory.get();
        HttpServletRequest request = webCtx.getHttpServletRequest();
        ReportDefinitionCommand cmd = getCommand(request);
        PlannedNotification plannedNotification = cmd.getEmailNotifications().get(index);
        plannedNotification.setIndexOnTimeScale(indexOnScale);
        request.setAttribute(AJAX_INDEX_PARAMETER, index);
        request.setAttribute(AJAX_SUBVIEW_PARAMETER, "addNotificationSection");
        request.setAttribute(AJAX_REQUEST_PARAMETER, "AJAX");
        String url = getCurrentPageContextRelative(webCtx);

        return getOutputFromJsp(url);
    }

    public List<Organization> matchOrganization(String text) {
    	OrganizationQuery query = new OrganizationQuery();
    	query.filterByOrganizationNameOrNciCode(text);
        List<Organization> orgs = orgDao.getBySubnames(query);
        orgs = RankBasedSorterUtils.sort(orgs , text, new Serializer<Organization>(){
            public String serialize(Organization object) {
                return object.getFullName();
            }
        });
        return ObjectTools.reduceAll(orgs, "id", "name", "nciInstituteCode");
    }
    public List<Organization> restrictOrganization(String text) {
        List<Organization> orgs = organizationRepository.restrictBySubnames(new String[] { text });
        orgs = RankBasedSorterUtils.sort(orgs , text, new Serializer<Organization>(){
            public String serialize(Organization object) {
                return object.getFullName();
            }
        });
        return ObjectTools.reduceAll(orgs, "id", "name", "nciInstituteCode","externalId");
    }

    // / HELPER METHODS
    public ReportDefinitionCommand getCommand(HttpServletRequest request) {
        String commandName = CREATE_FLOW_COMMAND_KEY;
        ReportDefinitionCommand cmd = (ReportDefinitionCommand) request.getSession().getAttribute(commandName);
        if (cmd == null) {
            commandName = EDIT_FLOW_COMMAND_KEY;
            cmd = (ReportDefinitionCommand) request.getSession().getAttribute(commandName);
        }
        request.setAttribute(AbstractFormController.DEFAULT_COMMAND_NAME, cmd);
        return cmd;
    }

    private String getOutputFromJsp(String jspResource) {
        String html = "Error in rendering...";
        try {
            html = WebContextFactory.get().forwardToString(jspResource);
        } catch (ServletException e) {
            throw new CaaersSystemException(e.getMessage(), e);
        } catch (IOException e) {
            throw new CaaersSystemException(e.getMessage(), e);
        }
        return html;
    }

    private String getCurrentPageContextRelative(WebContext webContext) {
        String contextPath = webContext.getHttpServletRequest().getContextPath();
        String page = webContext.getCurrentPage();
        if (contextPath == null) {
            log.debug("context path not set");
            return page;
        } else if (!page.startsWith(contextPath)) {
            log.debug(page + " does not start with context path " + contextPath);
            return page;
        } else {
            return page.substring(contextPath.length());
        }
    }
    
    /**
     * This method will fetch the report definitions to be displayed in the parent list. 
     * @param organizationID
     * @return
     */
    public AjaxOutput fetchParentReportDefinitions(int organizationID){
    	WebContext webCtx = WebContextFactory.get();
        HttpServletRequest request = webCtx.getHttpServletRequest();
        ReportDefinitionCommand cmd = getCommand(request);
    	cmd.refreshParentOptions(organizationID);
    	AjaxOutput out = new AjaxOutput();
    	out.setObjectContent(cmd.getParentOptions());
    	return out;
    }
    
    /**
     * This method will refresh the report groups in the command, and will return the new set of groups. 
     * @return
     */
    public AjaxOutput fetchReportGroups(){
    	WebContext webCtx = WebContextFactory.get();
        HttpServletRequest request = webCtx.getHttpServletRequest();
        ReportDefinitionCommand cmd = getCommand(request);
        cmd.refreshGroupOptions();
        
    	AjaxOutput out = new AjaxOutput();
    	out.setObjectContent(cmd.getGroupOptions());
    	return out;
    }
    
    // /BEAN PROPERTIES
    public void setOrganizationDao(OrganizationDao orgDao) {
        this.orgDao = orgDao;
    }

    public OrganizationDao getOrganizationDao() {
        return orgDao;
    }

	public void setOrganizationRepository(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

    public ReportDefinitionDao getRepDefDao() {
        return repDefDao;
    }

    public void setRepDefDao(ReportDefinitionDao repDefDao) {
        this.repDefDao = repDefDao;
    }

    public ConfigPropertyDao getCpDao() {
        return cpDao;
    }

    public void setCpDao(ConfigPropertyDao cpDao) {
        this.cpDao = cpDao;
    }

	public void setReportVersionRepository(
			ReportVersionRepository reportVersionRepository) {
		this.reportVersionRepository = reportVersionRepository;
	}
}
