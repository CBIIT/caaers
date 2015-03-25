/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.participant;

import gov.nih.nci.cabig.caaers.dao.*;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.security.CaaersSecurityFacade;
import gov.nih.nci.cabig.caaers.security.SecurityUtils;
import gov.nih.nci.cabig.caaers.tools.spring.tabbedflow.AutomaticSaveAjaxableFormController;
import gov.nih.nci.cabig.caaers.web.ControllerTools;
import gov.nih.nci.cabig.caaers.web.ListValues;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;
import gov.nih.nci.cabig.caaers.web.ae.AbstractAdverseEventInputController;
import gov.nih.nci.cabig.ctms.web.tabs.Flow;
import gov.nih.nci.cabig.ctms.web.tabs.FlowFactory;
import gov.nih.nci.cabig.ctms.web.tabs.Tab;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.context.SecurityContext;

public class AssignParticipantController extends AutomaticSaveAjaxableFormController<AssignParticipantStudyCommand, Participant, ParticipantDao> {

    private static Log log = LogFactory.getLog(AssignParticipantController.class);
    private ParticipantDao participantDao;
    private StudySiteDao studySiteDao;
    protected ListValues listValues;

    protected PriorTherapyDao priorTherapyDao;
    protected AnatomicSiteDao anatomicSiteDao;
    protected PreExistingConditionDao preExistingConditionDao;
    protected AbstractStudyDiseaseDao abstractStudyDiseaseDao;
    protected ChemoAgentDao chemoAgentDao;
    protected AgentDao agentDao;

    private ResearchStaffDao rsDao;
    private InvestigatorDao investigatorDao;

    private CaaersSecurityFacade csf;

    @Override
    @SuppressWarnings(value = {"unchecked"})
    protected Map referenceData(HttpServletRequest request, Object command, Errors errors, int page) throws Exception {
        Map referenceData = super.referenceData(request, command, errors, page);
        referenceData.put("participantSearchType", listValues.getParticipantSearchType());
        referenceData.put("studySearchType", listValues.getStudySearchType());
        referenceData.put("assignType", "participant");
        return referenceData;
    }

    /**
     * Build the flow pages
     * */
    @Override
    public FlowFactory<AssignParticipantStudyCommand> getFlowFactory() {
        return new FlowFactory<AssignParticipantStudyCommand>() {
            public Flow<AssignParticipantStudyCommand> createFlow(AssignParticipantStudyCommand cmd) {
                Flow<AssignParticipantStudyCommand> flow = new Flow<AssignParticipantStudyCommand>("Assign Subject to Study");
                flow.addTab(new AssignParticipantTab());
                flow.addTab(new AssignStudyTab());
                flow.addTab(new AssignSubjectMedHistoryTab());
                flow.addTab(new ReviewAssignmentTab());
                return flow;
            }
        };
    }

    public AssignParticipantController() {
        setCommandClass(AssignParticipantStudyCommand.class);
    }

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        AssignParticipantStudyCommand command = new AssignParticipantStudyCommand();

        User user = null;
        SecurityContext context = (SecurityContext) request.getSession().getAttribute("ACEGI_SECURITY_CONTEXT");
        if (context != null) {
            user = (User)context.getAuthentication().getPrincipal();
        }

        command.setHasParUpdate(csf.checkAuthorization(SecurityUtils.getAuthentication(), "gov.nih.nci.cabig.caaers.domain.Participant:UPDATE"));

/*
        command.setLoggedinResearchStaff(rsDao.getByLoginId(user.getUsername()));
        command.setLoggedinInvestigator(investigatorDao.getByLoginId(user.getUsername()));

        if (command.getLoggedinResearchStaff() != null) {
            command.setLoggedInOrganizations(WebUtils.extractOrganizations(command.getLoggedinResearchStaff()));
        }
        
        if (command.getLoggedinInvestigator() != null) {
            command.setLoggedInOrganizations(WebUtils.extractOrganizations(command.getLoggedinInvestigator()));
        }
*/
        
        if(request.getParameter("participantId") !=null ){
        	Participant participant = participantDao.getById(Integer.parseInt(request.getParameter("participantId")));
        	if(participant != null){command.setParticipant(participant); }
        } 
        
        command.init();
        return command;
    }

    @Override
    protected String getFormSessionAttributeName() {
        // the entry point to this flow is from AssignController
        return AssignParticipantController.class.getName() + ".FORM." + getCommandName();
    }

    /**
     * Validation needs to be supressed when, - We go back in the flow. - We the page displayed is
     * the result of study/participant search.
     */
    @Override
    protected boolean suppressValidation(HttpServletRequest request, Object command) {
        int curPage = getCurrentPage(request);
        int targetPage = getTargetPage(request, curPage);

        if(isAjaxRequest(request) || targetPage < curPage) return true;
        return super.suppressValidation(request, command);
        // return (request.getParameter("studyType") != null) || (request.getParameter("participantType") != null) || (targetPage < curPage);
    }


    protected void initBinder(HttpServletRequest httpServletRequest, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(httpServletRequest, binder);
        ControllerTools.registerDomainObjectEditor(binder, participantDao);
        ControllerTools.registerDomainObjectEditor(binder, studySiteDao);

        ControllerTools.registerDomainObjectEditor(binder, priorTherapyDao);
        ControllerTools.registerDomainObjectEditor(binder, anatomicSiteDao);
        ControllerTools.registerDomainObjectEditor(binder, preExistingConditionDao);
        ControllerTools.registerDomainObjectEditor(binder, "assignment.diseaseHistory.abstractStudyDisease", abstractStudyDiseaseDao);
        ControllerTools.registerDomainObjectEditor(binder, chemoAgentDao);
        ControllerTools.registerDomainObjectEditor(binder, agentDao);

    }

    @Override
    protected String getViewName(final HttpServletRequest request, final Object command, final int page) {
        Object subviewName = findInRequest(request, AbstractAdverseEventInputController.AJAX_SUBVIEW_PARAMETER);
        if (subviewName != null) {
            return "par/ajax/" + subviewName;
        }
		return super.getViewName(request, command, page);
    }

    /**
     * Returns the value associated with the <code>attributeName</code>, if present in
     * HttpRequest parameter, if not available, will check in HttpRequest attribute map.
     */
    protected Object findInRequest(final HttpServletRequest request, final String attributName) {
        Object attr = request.getParameter(attributName);
        if (attr == null) {
            attr = request.getAttribute(attributName);
        }
        return attr;
    }

    @Override
    protected ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        log.debug("processFinish.");

        AssignParticipantStudyCommand assignParticipantStudyCommand = (AssignParticipantStudyCommand) command;
        Participant participant = assignParticipantStudyCommand.getParticipant();
        participantDao.reassociateUsingLock(participant);
        assignParticipantStudyCommand.getAssignment().setStudySite(assignParticipantStudyCommand.getStudySite());
        assignParticipantStudyCommand.getAssignment().setParticipant(participant);
        participant.addAssignment(assignParticipantStudyCommand.getAssignment());
        assignParticipantStudyCommand.getAssignment().setStudySubjectIdentifier(assignParticipantStudyCommand.getStudySubjectIdentifier());

/*
        assignParticipantStudyCommand.getAssignment().setDateOfEnrollment(new Date());
        assignParticipantStudyCommand.setStudy(assignParticipantStudyCommand.getStudySite().getStudy());
*/


        participantDao.save(participant);

        response.sendRedirect("view?participantId=" + participant.getId() + "&type=edit");

        return null;
    }

    public ParticipantDao getParticipantDao() {
        return participantDao;
    }

    public void setParticipantDao(ParticipantDao participantDao) {
        this.participantDao = participantDao;
    }

    public ListValues getListValues() {
        return listValues;
    }

    public void setListValues(ListValues listValues) {
        this.listValues = listValues;
    }

    protected Participant getPrimaryDomainObject(AssignParticipantStudyCommand command) {
        return command.getParticipant();
    }

    protected ParticipantDao getDao() {
        return participantDao;
    }

    protected boolean shouldSave(HttpServletRequest request, AssignParticipantStudyCommand command, Tab<AssignParticipantStudyCommand> assignParticipantStudyCommandTab) {
        return false;
    }

    public StudySiteDao getStudySiteDao() {
        return studySiteDao;
    }

    public void setStudySiteDao(StudySiteDao studySiteDao) {
        this.studySiteDao = studySiteDao;
    }

    public PriorTherapyDao getPriorTherapyDao() {
        return priorTherapyDao;
    }

    public void setPriorTherapyDao(PriorTherapyDao priorTherapyDao) {
        this.priorTherapyDao = priorTherapyDao;
    }

    public AnatomicSiteDao getAnatomicSiteDao() {
        return anatomicSiteDao;
    }

    public void setAnatomicSiteDao(AnatomicSiteDao anatomicSiteDao) {
        this.anatomicSiteDao = anatomicSiteDao;
    }

    public PreExistingConditionDao getPreExistingConditionDao() {
        return preExistingConditionDao;
    }

    public void setPreExistingConditionDao(PreExistingConditionDao preExistingConditionDao) {
        this.preExistingConditionDao = preExistingConditionDao;
    }

    public AbstractStudyDiseaseDao getAbstractStudyDiseaseDao() {
        return abstractStudyDiseaseDao;
    }

    public void setAbstractStudyDiseaseDao(AbstractStudyDiseaseDao abstractStudyDiseaseDao) {
        this.abstractStudyDiseaseDao = abstractStudyDiseaseDao;
    }

    public ChemoAgentDao getChemoAgentDao() {
        return chemoAgentDao;
    }

    public void setChemoAgentDao(ChemoAgentDao chemoAgentDao) {
        this.chemoAgentDao = chemoAgentDao;
    }

    public AgentDao getAgentDao() {
        return agentDao;
    }

    public void setAgentDao(AgentDao agentDao) {
        this.agentDao = agentDao;
    }

    public ResearchStaffDao getRsDao() {
        return rsDao;
    }

    public void setRsDao(ResearchStaffDao rsDao) {
        this.rsDao = rsDao;
    }

    public InvestigatorDao getInvestigatorDao() {
        return investigatorDao;
    }

    public void setInvestigatorDao(InvestigatorDao investigatorDao) {
        this.investigatorDao = investigatorDao;
    }

    public CaaersSecurityFacade getCsf() {
        return csf;
    }

    public void setCsf(CaaersSecurityFacade csf) {
        this.csf = csf;
    }
}



