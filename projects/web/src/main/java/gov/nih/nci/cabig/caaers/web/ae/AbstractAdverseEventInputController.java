package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.dao.AgentDao;
import gov.nih.nci.cabig.caaers.dao.AnatomicSiteDao;
import gov.nih.nci.cabig.caaers.dao.CtcCategoryDao;
import gov.nih.nci.cabig.caaers.dao.CtcTermDao;
import gov.nih.nci.cabig.caaers.dao.CtepStudyDiseaseDao;
import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.MeddraStudyDiseaseDao;
import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.dao.PreExistingConditionDao;
import gov.nih.nci.cabig.caaers.dao.PriorTherapyDao;
import gov.nih.nci.cabig.caaers.dao.RoutineAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.StudyAgentDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.LabTermDao;
import gov.nih.nci.cabig.caaers.dao.LabCategoryDao;
import gov.nih.nci.cabig.caaers.dao.StudyParticipantAssignmentDao;
import gov.nih.nci.cabig.caaers.dao.TreatmentAssignmentDao;
import gov.nih.nci.cabig.caaers.dao.meddra.LowLevelTermDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.dao.ChemoAgentDao;
import gov.nih.nci.cabig.caaers.dao.InterventionSiteDao;
import gov.nih.nci.cabig.caaers.domain.Attribution;
import gov.nih.nci.cabig.caaers.domain.Availability;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Grade;
import gov.nih.nci.cabig.caaers.domain.Hospitalization;
import gov.nih.nci.cabig.caaers.domain.PostAdverseEventStatus;
import gov.nih.nci.cabig.caaers.domain.RadiationAdministration;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportTree;
import gov.nih.nci.cabig.caaers.service.EvaluationService;
import gov.nih.nci.cabig.caaers.web.ControllerTools;
import gov.nih.nci.cabig.ctms.lang.NowFactory;
import gov.nih.nci.cabig.ctms.web.tabs.AutomaticSaveFlowFormController;
import gov.nih.nci.cabig.ctms.web.tabs.FlowFactory;
import gov.nih.nci.cabig.ctms.web.tabs.Tab;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Rhett Sutphin
 */
public abstract class AbstractAdverseEventInputController
                extends
                AutomaticSaveFlowFormController<ExpeditedAdverseEventInputCommand, ExpeditedAdverseEventReport, ExpeditedAdverseEventReportDao> {

    public static final String AJAX_SUBVIEW_PARAMETER = "subview";

    private static final int SUBMISSION_PAGE = 16;

    private static final String UNFILLED_TAB_KEY = "UNFILLED_TABS";

    private static final String MANDATORY_TAB_KEY = "MANDATORY_TABS";

    protected final Log log = LogFactory.getLog(getClass());

    protected ParticipantDao participantDao;

    protected StudyDao studyDao;

    protected StudyParticipantAssignmentDao assignmentDao;

    protected CtcTermDao ctcTermDao;

    protected LowLevelTermDao lowLevelTermDao;

    protected AgentDao agentDao;

    protected ExpeditedAdverseEventReportDao reportDao;

    protected RoutineAdverseEventReportDao routineReportDao;

    protected StudyAgentDao studyAgentDao;

    protected CtepStudyDiseaseDao ctepStudyDiseaseDao;

    protected MeddraStudyDiseaseDao meddraStudyDiseaseDao;

    protected AnatomicSiteDao anatomicSiteDao;

    protected PriorTherapyDao priorTherapyDao;

    protected CtcCategoryDao ctcCategoryDao;

    protected PreExistingConditionDao preExistingConditionDao;

    protected TreatmentAssignmentDao treatmentAssignmentDao;

    protected LabTermDao labTermDao;

    protected ChemoAgentDao chemoAgentDao;

    protected LabCategoryDao labCategoryDao;

    protected InterventionSiteDao interventionSiteDao;

    protected NowFactory nowFactory;

    protected EvaluationService evaluationService;

    protected ReportDefinitionDao reportDefinitionDao;

    protected ExpeditedReportTree expeditedReportTree;

    protected AbstractAdverseEventInputController() {
        setAllowDirtyBack(false);
        setAllowDirtyForward(false);
        setFlowFactory(createFlowFactory());
    }

    protected abstract FlowFactory<ExpeditedAdverseEventInputCommand> createFlowFactory();

    @Override
    @SuppressWarnings( { "unchecked" })
    protected void onBind(HttpServletRequest request, Object oCommand, BindException errors)
                    throws Exception {
        super.onBind(request, oCommand, errors);
        ((ExpeditedAdverseEventInputCommand) oCommand).setNextPage(getTargetPage(request,
                        getCurrentPage(request)));
    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
                    throws Exception {
        ControllerTools.registerDomainObjectEditor(binder, "participant", participantDao);
        ControllerTools.registerDomainObjectEditor(binder, "study", studyDao);
        ControllerTools.registerDomainObjectEditor(binder, "aeReport", reportDao);
        ControllerTools.registerDomainObjectEditor(binder, ctcTermDao);
        ControllerTools.registerDomainObjectEditor(binder, lowLevelTermDao);
        ControllerTools.registerDomainObjectEditor(binder, agentDao);
        ControllerTools.registerDomainObjectEditor(binder, studyAgentDao);
        ControllerTools.registerDomainObjectEditor(binder, ctepStudyDiseaseDao);
        ControllerTools.registerDomainObjectEditor(binder, meddraStudyDiseaseDao);
        ControllerTools.registerDomainObjectEditor(binder, anatomicSiteDao);
        ControllerTools.registerDomainObjectEditor(binder, priorTherapyDao);
        ControllerTools.registerDomainObjectEditor(binder, preExistingConditionDao);
        ControllerTools.registerDomainObjectEditor(binder, ctcCategoryDao);
        ControllerTools.registerDomainObjectEditor(binder, treatmentAssignmentDao);
        ControllerTools.registerDomainObjectEditor(binder, reportDefinitionDao);
        ControllerTools.registerDomainObjectEditor(binder, labTermDao);
        ControllerTools.registerDomainObjectEditor(binder, chemoAgentDao);
        ControllerTools.registerDomainObjectEditor(binder, interventionSiteDao);
        binder.registerCustomEditor(Date.class, ControllerTools.getDateEditor(false));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        ControllerTools.registerEnumEditor(binder, Grade.class);
        ControllerTools.registerEnumEditor(binder, Hospitalization.class);
        ControllerTools.registerEnumEditor(binder, Attribution.class);
        ControllerTools.registerEnumEditor(binder, PostAdverseEventStatus.class);
        ControllerTools.registerEnumEditor(binder, RadiationAdministration.class);
        ControllerTools.registerEnumEditor(binder, Availability.class);
    }

    @Override
    protected int getInitialPage(HttpServletRequest request) {
        boolean isActionAvailable = request.getParameter("action") != null ? true : false;

        if (isActionAvailable && request.getParameter("action").equals("reportSubmission")) {
            log.debug("This is a report Submission");
            return SUBMISSION_PAGE;
        }
        if (isActionAvailable && request.getParameter("action").equals("create")) {
            log.debug("This is a Create where the StudyParticipantAssignment is already defined");
            return 1;
        }
        // default behaviour
        return super.getInitialPage(request);
    }

    @Override
    @SuppressWarnings( { "unchecked", "RawUseOfParameterizedType" })
    protected Map referenceData(HttpServletRequest request, Object oCommand, Errors errors, int page)
                    throws Exception {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand) oCommand;
        Map<String, Object> refdata = super.referenceData(request, cmd, errors, page);
        StringBuffer sb = new StringBuffer("notab");
        StringBuffer sbSections = new StringBuffer();
        for (Tab<ExpeditedAdverseEventInputCommand> tab : getFlow(cmd).getTabs()) {
            if (tab instanceof AeTab) {
                AeTab aeTab = (AeTab) tab;
                sbSections.append(",").append(aeTab.isMandatory(cmd) ? tab.getShortTitle() : "");
                sb.append(",")
                                .append(
                                                aeTab.hasEmptyMandatoryFields(cmd) ? tab
                                                                .getShortTitle() : "");
            }
        }
        refdata.put(MANDATORY_TAB_KEY, sbSections.toString());
        refdata.put(UNFILLED_TAB_KEY, sb.toString());
        if (displaySummary(page)) {
            refdata.put("aesummary", cmd.getAeReport().getSummary());
        }
        return refdata;
    }

    @Override
    protected boolean suppressValidation(HttpServletRequest request, Object command) {
        return request.getParameter(AJAX_SUBVIEW_PARAMETER) != null;
    }

    @Override
    protected boolean shouldSave(HttpServletRequest request,
                    ExpeditedAdverseEventInputCommand command,
                    Tab<ExpeditedAdverseEventInputCommand> tab) {
        return super.shouldSave(request, command, tab)
                        && request.getParameter(AJAX_SUBVIEW_PARAMETER) == null;
    }

    protected boolean displaySummary(int page) {
        return true;
    }

    /**
     * Adds ajax sub-page view capability. TODO: factor this into main tabbed flow controller.
     */
    @Override
    protected String getViewName(HttpServletRequest request, Object command, int page) {
        String subviewName = request.getParameter(AJAX_SUBVIEW_PARAMETER);
        if (subviewName != null) {
            // for side-effects:
            super.getViewName(request, command, page);
            return "ae/ajax/" + subviewName;
        } else {
            return super.getViewName(request, command, page);
        }
    }

    @Override
    protected Object currentFormObject(HttpServletRequest request, Object oCommand)
                    throws Exception {
        oCommand = super.currentFormObject(request, oCommand);
        ((ExpeditedAdverseEventInputCommand) oCommand).reassociate();
        return oCommand;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response,
                    Object oCommand, BindException errors) throws Exception {
        ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) oCommand;
        save(command, errors);
        Map<String, Object> model = new ModelMap("participant", command.getParticipant().getId());
        model.put("study", command.getStudy().getId());
        return new ModelAndView("redirectToAeList", model);
    }

    @Override
    protected ExpeditedAdverseEventReportDao getDao() {
        return reportDao;
    }

    @Override
    protected ExpeditedAdverseEventReport getPrimaryDomainObject(
                    ExpeditedAdverseEventInputCommand command) {
        return command.getAeReport();
    }

    // //// CONFIGURATION

    public void setParticipantDao(ParticipantDao participantDao) {
        this.participantDao = participantDao;
    }

    public void setStudyDao(StudyDao studyDao) {
        this.studyDao = studyDao;
    }

    public void setAssignmentDao(StudyParticipantAssignmentDao assignmentDao) {
        this.assignmentDao = assignmentDao;
    }

    public void setCtcTermDao(CtcTermDao ctcTermDao) {
        this.ctcTermDao = ctcTermDao;
    }

    public void setAgentDao(AgentDao agentDao) {
        this.agentDao = agentDao;
    }

    public void setReportDao(ExpeditedAdverseEventReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public void setStudyAgentDao(StudyAgentDao studyAgentDao) {
        this.studyAgentDao = studyAgentDao;
    }

    public void setCtepStudyDiseaseDao(CtepStudyDiseaseDao ctepStudyDiseaseDao) {
        this.ctepStudyDiseaseDao = ctepStudyDiseaseDao;
    }

    public void setAnatomicSiteDao(AnatomicSiteDao anatomicSiteDao) {
        this.anatomicSiteDao = anatomicSiteDao;
    }

    public void setPriorTherapyDao(PriorTherapyDao priorTherapyDao) {
        this.priorTherapyDao = priorTherapyDao;
    }

    public CtcCategoryDao getCtcCategoryDao() {
        return ctcCategoryDao;
    }

    public void setCtcCategoryDao(CtcCategoryDao ctcCategoryDao) {
        this.ctcCategoryDao = ctcCategoryDao;
    }

    public NowFactory getNowFactory() {
        return nowFactory;
    }

    public void setNowFactory(NowFactory nowFactory) {
        this.nowFactory = nowFactory;
    }

    public RoutineAdverseEventReportDao getRoutineReportDao() {
        return routineReportDao;
    }

    public void setRoutineReportDao(RoutineAdverseEventReportDao routineReportDao) {
        this.routineReportDao = routineReportDao;
    }

    public PreExistingConditionDao getPreExistingConditionDao() {
        return preExistingConditionDao;
    }

    public void setPreExistingConditionDao(PreExistingConditionDao preExistingConditionDao) {
        this.preExistingConditionDao = preExistingConditionDao;
    }

    public EvaluationService getEvaluationService() {
        return evaluationService;
    }

    public void setEvaluationService(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    public ReportDefinitionDao getReportDefinitionDao() {
        return reportDefinitionDao;
    }

    public void setReportDefinitionDao(ReportDefinitionDao reportDefinitionDao) {
        this.reportDefinitionDao = reportDefinitionDao;
    }

    public LowLevelTermDao getLowLevelTermDao() {
        return lowLevelTermDao;
    }

    public void setLowLevelTermDao(LowLevelTermDao lowLevelTermDao) {
        this.lowLevelTermDao = lowLevelTermDao;
    }

    public TreatmentAssignmentDao getTreatmentAssignmentDao() {
        return treatmentAssignmentDao;
    }

    public void setTreatmentAssignmentDao(TreatmentAssignmentDao treatmentAssignmentDao) {
        this.treatmentAssignmentDao = treatmentAssignmentDao;
    }

    public MeddraStudyDiseaseDao getMeddraStudyDiseaseDao() {
        return meddraStudyDiseaseDao;
    }

    public void setMeddraStudyDiseaseDao(MeddraStudyDiseaseDao meddraStudyDiseaseDao) {
        this.meddraStudyDiseaseDao = meddraStudyDiseaseDao;
    }

    public void setExpeditedReportTree(ExpeditedReportTree expeditedReportTree) {
        this.expeditedReportTree = expeditedReportTree;
    }

    public LabTermDao getLabTermDao() {
        return labTermDao;
    }

    public void setLabTermDao(LabTermDao labTermDao) {
        this.labTermDao = labTermDao;
    }

    public LabCategoryDao getLabCategoryDao() {
        return labCategoryDao;
    }

    public void setLabCategoryDao(LabCategoryDao labCategoryDao) {
        this.labCategoryDao = labCategoryDao;
    }

    public ChemoAgentDao getChemoAgentDao() {
        return chemoAgentDao;
    }

    public void setChemoAgentDao(ChemoAgentDao chemoAgentDao) {
        this.chemoAgentDao = chemoAgentDao;
    }

    public InterventionSiteDao getInterventionSiteDao() {
        return interventionSiteDao;
    }

    public void setInterventionSiteDao(InterventionSiteDao interventionSiteDao) {
        this.interventionSiteDao = interventionSiteDao;
    }
}
