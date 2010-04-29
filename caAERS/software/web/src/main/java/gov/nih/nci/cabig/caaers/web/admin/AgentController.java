package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.dao.*;
import gov.nih.nci.cabig.caaers.dao.meddra.LowLevelTermDao;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.repository.AgentRepository;
import gov.nih.nci.cabig.caaers.domain.repository.TerminologyRepository;
import gov.nih.nci.cabig.caaers.service.AgentSpecificAdverseEventListService;
import gov.nih.nci.cabig.caaers.tools.spring.tabbedflow.AutomaticSaveAjaxableFormController;
import gov.nih.nci.cabig.caaers.web.AbstractAjaxFacade;
import gov.nih.nci.cabig.caaers.web.ControllerTools;
import gov.nih.nci.cabig.ctms.web.tabs.Flow;
import gov.nih.nci.cabig.ctms.web.tabs.FlowFactory;
import gov.nih.nci.cabig.ctms.web.tabs.Tab;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/*
* @author Ion C. Olaru
* 
* */

public class AgentController extends AutomaticSaveAjaxableFormController<AgentCommand, Agent, AgentDao> {

    protected static final Log log = LogFactory.getLog(AgentController.class);

    protected AgentRepository agentRepository;
    protected AgentSpecificAdverseEventListService service;
    protected TerminologyRepository termRepository;
    protected CtcTermDao ctcTermDao;
    protected AgentSpecificTermDao agentSpecificTermDao;
    protected CtcCategoryDao ctcCategoryDao;
    protected CtcDao ctcDao;
    protected MeddraVersionDao meddraVersionDao;
    protected LowLevelTermDao lowLevelTermDao;
    protected AgentDao agentDao;

    @Override
    protected Agent getPrimaryDomainObject(AgentCommand command) {
        return command.getAgent();
    }

    @Override
    protected AgentDao getDao() {
        return agentDao;
    }

    @Override
    protected ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        return null;
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        request.getSession().removeAttribute(getReplacedCommandSessionAttributeName(request));
        request.getSession().removeAttribute(AgentEditController.class.getName() + ".FORM.command");
        request.getSession().removeAttribute(AgentCreateController.class.getName() + ".FORM.command");
        return null;
    }

    @Override
    protected AgentCommand save(AgentCommand command, Errors errors) {
        getDao().save(getPrimaryDomainObject(command));
        return command;
    }

    @Override
    protected boolean shouldSave(final HttpServletRequest request, final AgentCommand command, final Tab<AgentCommand> tab) {
        if (request.getParameter(AbstractAjaxFacade.AJAX_REQUEST) != null) return false;
        return true;
    }

    @Override
    protected String getViewName(final HttpServletRequest request, final Object command, final int page) {
        String suvbiew = request.getParameter(AbstractAjaxFacade.AJAX_SUBVIEW_PARAMETER);
        if (suvbiew != null) {
            return "admin/ajax/" + suvbiew;
        } else {
            return super.getViewName(request, command, page);
        }
    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
        ControllerTools.registerDomainObjectEditor(binder, ctcDao);
        ControllerTools.registerDomainObjectEditor(binder, meddraVersionDao);
        ControllerTools.registerDomainObjectEditor(binder, lowLevelTermDao);
    }

    public AgentRepository getAgentRepository() {
        return agentRepository;
    }

    public void setAgentRepository(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public AgentSpecificAdverseEventListService getService() {
        return service;
    }

    public void setService(AgentSpecificAdverseEventListService service) {
        this.service = service;
    }

    public TerminologyRepository getTermRepository() {
        return termRepository;
    }

    public void setTermRepository(TerminologyRepository termRepository) {
        this.termRepository = termRepository;
    }

    public CtcTermDao getCtcTermDao() {
        return ctcTermDao;
    }

    public void setCtcTermDao(CtcTermDao ctcTermDao) {
        this.ctcTermDao = ctcTermDao;
    }

    public AgentSpecificTermDao getAgentSpecificTermDao() {
        return agentSpecificTermDao;
    }

    public void setAgentSpecificTermDao(AgentSpecificTermDao agentSpecificTermDao) {
        this.agentSpecificTermDao = agentSpecificTermDao;
    }

    public CtcCategoryDao getCtcCategoryDao() {
        return ctcCategoryDao;
    }

    public void setCtcCategoryDao(CtcCategoryDao ctcCategoryDao) {
        this.ctcCategoryDao = ctcCategoryDao;
    }

    public CtcDao getCtcDao() {
        return ctcDao;
    }

    public void setCtcDao(CtcDao ctcDao) {
        this.ctcDao = ctcDao;
    }

    public MeddraVersionDao getMeddraVersionDao() {
        return meddraVersionDao;
    }

    public void setMeddraVersionDao(MeddraVersionDao meddraVersionDao) {
        this.meddraVersionDao = meddraVersionDao;
    }

    public LowLevelTermDao getLowLevelTermDao() {
        return lowLevelTermDao;
    }

    public void setLowLevelTermDao(LowLevelTermDao lowLevelTermDao) {
        this.lowLevelTermDao = lowLevelTermDao;
    }

    public AgentDao getAgentDao() {
        return agentDao;
    }

    public void setAgentDao(AgentDao agentDao) {
        this.agentDao = agentDao;
    }
}