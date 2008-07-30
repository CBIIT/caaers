package gov.nih.nci.cabig.caaers.web.ae;

import static gov.nih.nci.cabig.caaers.tools.ObjectTools.reduce;
import static gov.nih.nci.cabig.caaers.tools.ObjectTools.reduceAll;
import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.dao.AdverseEventReportingPeriodDao;
import gov.nih.nci.cabig.caaers.dao.AgentDao;
import gov.nih.nci.cabig.caaers.dao.AnatomicSiteDao;
import gov.nih.nci.cabig.caaers.dao.ChemoAgentDao;
import gov.nih.nci.cabig.caaers.dao.CtcCategoryDao;
import gov.nih.nci.cabig.caaers.dao.CtcDao;
import gov.nih.nci.cabig.caaers.dao.CtcTermDao;
import gov.nih.nci.cabig.caaers.dao.CtepStudyDiseaseDao;
import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.InterventionSiteDao;
import gov.nih.nci.cabig.caaers.dao.LabCategoryDao;
import gov.nih.nci.cabig.caaers.dao.LabLoadDao;
import gov.nih.nci.cabig.caaers.dao.LabTermDao;
import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.dao.PreExistingConditionDao;
import gov.nih.nci.cabig.caaers.dao.PriorTherapyDao;
import gov.nih.nci.cabig.caaers.dao.ResearchStaffDao;
import gov.nih.nci.cabig.caaers.dao.RoutineAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.TreatmentAssignmentDao;
import gov.nih.nci.cabig.caaers.dao.meddra.LowLevelTermDao;
import gov.nih.nci.cabig.caaers.domain.AbstractAdverseEventTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventCtcTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventMeddraLowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.Agent;
import gov.nih.nci.cabig.caaers.domain.AnatomicSite;
import gov.nih.nci.cabig.caaers.domain.ChemoAgent;
import gov.nih.nci.cabig.caaers.domain.CodedGrade;
import gov.nih.nci.cabig.caaers.domain.ConcomitantMedication;
import gov.nih.nci.cabig.caaers.domain.CourseAgent;
import gov.nih.nci.cabig.caaers.domain.CtcCategory;
import gov.nih.nci.cabig.caaers.domain.CtcTerm;
import gov.nih.nci.cabig.caaers.domain.CtepStudyDisease;
import gov.nih.nci.cabig.caaers.domain.DiseaseHistory;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReportChild;
import gov.nih.nci.cabig.caaers.domain.Grade;
import gov.nih.nci.cabig.caaers.domain.InterventionSite;
import gov.nih.nci.cabig.caaers.domain.LabCategory;
import gov.nih.nci.cabig.caaers.domain.LabLoad;
import gov.nih.nci.cabig.caaers.domain.LabTerm;
import gov.nih.nci.cabig.caaers.domain.MedicalDevice;
import gov.nih.nci.cabig.caaers.domain.OtherCause;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.ParticipantHistory;
import gov.nih.nci.cabig.caaers.domain.PreExistingCondition;
import gov.nih.nci.cabig.caaers.domain.PriorTherapy;
import gov.nih.nci.cabig.caaers.domain.RadiationIntervention;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.domain.RoutineAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.SurgeryIntervention;
import gov.nih.nci.cabig.caaers.domain.Term;
import gov.nih.nci.cabig.caaers.domain.TreatmentAssignment;
import gov.nih.nci.cabig.caaers.domain.attribution.AdverseEventAttribution;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportTree;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.TreeNode;
import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.repository.ReportRepository;
import gov.nih.nci.cabig.caaers.service.InteroperationService;
import gov.nih.nci.cabig.caaers.tools.ObjectTools;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.utils.Lov;
import gov.nih.nci.cabig.caaers.web.dwr.AjaxOutput;
import gov.nih.nci.cabig.caaers.web.dwr.IndexChange;
import gov.nih.nci.cabig.ctms.domain.DomainObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.bean.Row;
import org.extremecomponents.table.bean.Table;
import org.extremecomponents.table.context.Context;
import org.extremecomponents.table.context.HttpServletRequestContext;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.core.TableModelImpl;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * @author Rhett Sutphin
 */
public class CreateAdverseEventAjaxFacade {
	public static final String CAPTURE_ADVERSE_EVENT_INPUT_COMMAND = CaptureAdverseEventController.class.getName()
    + ".FORM.command";
    private static final Log log = LogFactory.getLog(CreateAdverseEventAjaxFacade.class);
    private static Class<?>[] CONTROLLERS = {
    	CaptureAdverseEventController.class,CreateAdverseEventController.class, EditAdverseEventController.class 
    };

    private StudyDao studyDao;
    private ParticipantDao participantDao;
    private CtcTermDao ctcTermDao;
    private CtcCategoryDao ctcCategoryDao;
    private CtcDao ctcDao;
    private LowLevelTermDao lowLevelTermDao;
    private ExpeditedAdverseEventReportDao aeReportDao;
    private RoutineAdverseEventReportDao roReportDao;
    private ResearchStaffDao researchStaffDao;
    private AnatomicSiteDao anatomicSiteDao;
    private InteroperationService interoperationService;
    private PriorTherapyDao priorTherapyDao;
    private PreExistingConditionDao preExistingConditionDao;
    private AgentDao agentDao;
    private TreatmentAssignmentDao treatmentAssignmentDao;
    private ExpeditedReportTree expeditedReportTree;
    private ConfigProperty configProperty;
    private ReportRepository reportRepository;
    private LabCategoryDao labCategoryDao;
    private LabTermDao labTermDao;
    private ChemoAgentDao chemoAgentDao;
    private InterventionSiteDao interventionSiteDao;
    private CtepStudyDiseaseDao ctepStudyDiseaseDao;
    private AdverseEventReportingPeriodDao reportingPeriodDao;
    private LabLoadDao labLoadDao;

    public List<AnatomicSite> matchAnatomicSite(String text) {
        return anatomicSiteDao.getBySubnames(extractSubnames(text));
    }
    public AnatomicSite getAnatomicSiteById(String anatomicSiteId) throws Exception {
       return anatomicSiteDao.getById(Integer.parseInt(anatomicSiteId));
    }

    public String buildAnatomicSiteTable(final Map parameterMap, String tableId, HttpServletRequest request) throws Exception {

        try {
            TableModel model = getTableModel(parameterMap, request);
            List<AnatomicSite> anatomicSites = anatomicSiteDao.getAll();

            String onInvokeAction = "showDiseaseSiteTable('" + tableId + "','" + tableId + "-outer' )";
            addTableAndRowToModel(model, tableId, anatomicSites, onInvokeAction);

            Column columnTerm = model.getColumnInstance();
            columnTerm.setProperty("name");
            columnTerm.setTitle("Primary site of disease");
            columnTerm
					.setCell("gov.nih.nci.cabig.caaers.web.search.link.AnatomicSiteLinkDisplayCell");
            model.addColumn(columnTerm);


            return model.assemble().toString();

        }
        catch (Exception e) {
            log.error("error while retriving the anatomicSites" + e.toString() + " message" + e.getMessage());
        }

        return "";

    }

    private TableModel getTableModel(Map parameterMap, HttpServletRequest request) {
        Context context = null;
        if (parameterMap == null) {
            context = new HttpServletRequestContext(request);
        } else {
            context = new HttpServletRequestContext(request, parameterMap);
        }

        TableModel model = new TableModelImpl(context);
        return model;
    }


    public List<PriorTherapy> matchPriorTherapies(String text) {
        return priorTherapyDao.getBySubnames(extractSubnames(text));
    }

    public List<PreExistingCondition> matchPreExistingConds(String text) {
        return preExistingConditionDao.getBySubnames(extractSubnames(text));
    }

    public List<LowLevelTerm> matchLowLevelTermsByCode(int version_id, String text) {
    	List<LowLevelTerm> terms= lowLevelTermDao.getByVersionSubnames(version_id, extractSubnames(text));
    	return ObjectTools.reduceAll(terms, "id", "meddraCode", "meddraTerm");
    }

    public List<ChemoAgent> matchChemoAgents(String text) {
        String[] excerpts = {text};
        List<ChemoAgent> agents = chemoAgentDao.getBySubname(excerpts);
        return agents;
    }
    public ChemoAgent getChemoAgentById(String chemoAgentId) throws Exception {

           return chemoAgentDao.getById(Integer.parseInt(chemoAgentId));
       }

    public String buildChemoAgentsTable(final Map parameterMap, String tableId, HttpServletRequest request) throws Exception {

        try {
            List<ChemoAgent> chemoAgents = chemoAgentDao.getAll();
            TableModel model = getTableModel(parameterMap, request);

            String onInvokeAction = "showChemoAgentsTable('" + tableId + "','" + tableId + "-outer')";

            addTableAndRowToModel(model, tableId, chemoAgents, onInvokeAction);

            Column columnTerm = model.getColumnInstance();
            columnTerm.setProperty("name");
            columnTerm.setTitle("Agent");
            columnTerm.setCell("gov.nih.nci.cabig.caaers.web.search.link.ChemoAgentLinkDisplayCell");
            model.addColumn(columnTerm);


            return model.assemble().toString();


        }
        catch (Exception e) {
            log.error("error while retriving the ctc terms" + e.toString() + " message" + e.getMessage());
        }

        return "";

    }

    public List<InterventionSite> matchInterventionSites(String text) {
        String[] excerpts = {text};
        List<InterventionSite> sites = interventionSiteDao.getBySubname(excerpts);
        return sites;
    }

    public List<Agent> matchAgents(String text) {
        List<Agent> agents = agentDao.getBySubnames(extractSubnames(text));
        return ObjectTools.reduceAll(agents, "id", "name", "nscNumber", "description");
    }
    
    public Integer getDiseaseFromStudyDisease(String studyDiseaseId) {
    	CtepStudyDisease ctepStudyDisease= ctepStudyDiseaseDao.getById(Integer.parseInt(studyDiseaseId));
        return ctepStudyDisease.getTerm().getId();
    }


    public ResearchStaff getResearchStaff(String text) {
        ResearchStaff researchStaff = researchStaffDao.getById(Integer.parseInt(text));
        return reduce(researchStaff, "id", "firstName", "lastName", "middleName", "emailAddress", "phoneNumber", "faxNumber");
    }

    public List<Participant> matchParticipants(String text, Integer studyId) {
        List<Participant> participants;
        if (studyId == null) {
            participants = participantDao.getBySubnamesJoinOnIdentifier(extractSubnames(text));
        } else {
            participants = participantDao.matchParticipantByStudy(studyId, text);
        }
        // cut down objects for serialization
        return reduceAll(participants, "firstName", "lastName", "id", "primaryIdentifierValue");
    }

    /* Depracated and replace by a hql based query to enhance performance
    public List<Participant> matchParticipants(String text, Integer studyId) {
        List<Participant> participants = participantRepository.getBySubnames(extractSubnames(text));
        if (studyId != null) {
            for (Iterator<Participant> it = participants.iterator(); it.hasNext();) {
                Participant participant = it.next();
                if (!onStudy(participant, studyId)) it.remove();
            }
        }
        // cut down objects for serialization
        return reduceAll(participants, "firstName", "lastName", "id");
    }
    */

    private boolean onStudy(Participant participant, Integer studyId) {
        boolean onStudy = false;
        for (StudyParticipantAssignment assignment : participant.getAssignments()) {
            if (assignment.getStudySite().getStudy().getId().equals(studyId)) {
                onStudy = true;
                break;
            }
        }
        return onStudy;
    }

    /* Depracated and replace by a hql based query to enhance performance
    public List<Study> matchStudies(String text, Integer participantId) {
        List<Study> studies = studyDao.getBySubnames(extractSubnames(text));
        if (participantId != null) {
            for (Iterator<Study> it = studies.iterator(); it.hasNext();) {
                Study study = it.next();
                if (!onStudy(study, participantId)) it.remove();
            }
        }
        // cut down objects for serialization
        return reduceAll(studies, "id", "shortTitle");
    }
    */
    /*
     * The extra condition "o.status <> 'Administratively Complete'" as fix for bug 9514
     */
    public List<Study> matchStudies(String text, Integer participantId, boolean ignoreCompletedStudy) {
        List<Study> studies;
        if (participantId == null) {
            studies = studyDao.getBySubnamesJoinOnIdentifier(extractSubnames(text),
                    (ignoreCompletedStudy) ? "o.status <> '" + Study.STATUS_ADMINISTRATIVELY_COMPLETE + "'" : null);
        } else {
            studies = studyDao.matchStudyByParticipant(participantId, text,
                    (ignoreCompletedStudy) ? "o.status <> '" + Study.STATUS_ADMINISTRATIVELY_COMPLETE + "'" : null);
        }
        // cut down objects for serialization
        return reduceAll(studies, "id", "shortTitle", "primaryIdentifierValue");
    }

    private boolean onStudy(Study study, Integer participantId) {
        boolean onStudy = false;
        for (StudySite studySite : study.getStudySites()) {
            for (StudyParticipantAssignment assignment : studySite.getStudyParticipantAssignments()) {
                if (assignment.getParticipant().getId().equals(participantId)) {
                    onStudy = true;
                    break;
                }
            }
        }
        return onStudy;
    }

    public List<CtcTerm> matchTerms(String text, Integer ctcVersionId, Integer ctcCategoryId, int limit) throws Exception {
        List<CtcTerm> terms = ctcTermDao.getBySubname(extractSubnames(text), ctcVersionId, ctcCategoryId);
        // cut down objects for serialization
        for (CtcTerm term : terms) {
            term.getCategory().setTerms(null);
            term.getCategory().getCtc().setCategories(null);
        }
        while (terms.size() > limit) {
            terms.remove(terms.size() - 1);
        }
        return terms;
    }

    public List<CtcTerm> getTermsByCategory(Integer ctcCategoryId) throws Exception {
        List<CtcTerm> terms = ctcCategoryDao.getById(ctcCategoryId).getTerms();
        // cut down objects for serialization
        for (CtcTerm term : terms) {
            term.getCategory().setTerms(null);
            term.getCategory().getCtc().setCategories(null);
        }
        return terms;
    }

    public List<CtcTerm> getTermByTermId(String ctcTermId) throws Exception {
        List<CtcTerm> terms = new ArrayList<CtcTerm>();
        CtcTerm ctcTerm = ctcTermDao.getById(Integer.parseInt(ctcTermId));
        ctcTerm.getCategory().setTerms(null);
        ctcTerm.getCategory().getCtc().setCategories(null);
        terms.add(ctcTerm);

        return terms;
    }

    public String buildTermsTableByCategory(final Map parameterMap,Integer ctcCategoryId, String tableId, HttpServletRequest request) throws Exception {
        if (ctcCategoryId == null || ctcCategoryId == 0) {
            return "";
        }

        try {
            List<CtcTerm> terms = getTermsByCategory(ctcCategoryId);
            TableModel model = getTableModel(parameterMap, request);
            String onInvokeAction = "buildTable('command'," + ctcCategoryId.intValue() + ",'" + tableId + "')";

            addTableAndRowToModel(model, tableId, terms, onInvokeAction);

            Column columnTerm = model.getColumnInstance();
            columnTerm.setProperty("fullName");
            columnTerm.setTitle("CTC term");
            columnTerm.setCell("gov.nih.nci.cabig.caaers.web.search.CtcTermLinkDisplayCell");
            model.addColumn(columnTerm);


            return model.assemble().toString();


        }
        catch (Exception e) {
            log.error("error while retriving the ctc terms" + e.toString() + " message" + e.getMessage());
        }

        return "";

    }

    public List<CtcCategory> getCategories(int ctcVersionId) {
        List<CtcCategory> categories = ctcDao.getById(ctcVersionId).getCategories();
        // cut down objects for serialization
        for (CtcCategory category : categories) {
            category.setTerms(null);
        }
        return categories;
    }

    public List<? extends CodedGrade> getTermGrades(int ctcTermId) {
        List<CodedGrade> list = ctcTermDao.getById(ctcTermId).getGrades();
        // have to detect whether it's a collection of Grade or CtcGrade;
        // if the latter, need to call reduce
        if (list.size() == 0) {
            return list;
        } else if (list.get(0) instanceof Grade) {
            return list;
        } else {
            return reduceAll(list, "grade", "text");
        }
    }

    public List<LabTerm> matchLabTerms(String text, Integer labCategoryId) {
        List<LabTerm> terms = labTermDao.getBySubname(extractSubnames(text), labCategoryId);
        // cut down objects for serialization
        for (LabTerm term : terms) {
            term.getCategory().setTerms(null);
            term.getCategory().getLabVersion().setCategories(null);
        }
        return terms;
    }

    public LabTerm getLabTermById(String labTermId) throws Exception {
        LabTerm labTerm = labTermDao.getById(Integer.parseInt(labTermId));
        // cut down objects for serialization
        labTerm.getCategory().setTerms(null);
        labTerm.getCategory().getLabVersion().setCategories(null);

        return labTerm;
    }
    public String buildLabTermsTable(final Map parameterMap, String labCategoryId, String tableId, HttpServletRequest request) throws Exception {

        if (labCategoryId == null || labCategoryId.equalsIgnoreCase("")) {
            return "";
        }


        try {
            TableModel model = getTableModel(parameterMap, request);
            List<LabTerm> terms = getLabTermsByCategory(Integer.parseInt(labCategoryId));

            String onInvokeAction = "showLabsTable('" + labCategoryId + "','" + tableId + "')";

            addTableAndRowToModel(model, tableId, terms, onInvokeAction);

            Column columnTerm = model.getColumnInstance();
            columnTerm.setProperty("term");
            columnTerm.setTitle("Lab test name");
            columnTerm.setCell("gov.nih.nci.cabig.caaers.web.search.link.LabTermLinkDisplayCell");
            model.addColumn(columnTerm);


            return model.assemble().toString();

        }
        catch (Exception e) {
            log.error("error while retriving the lab terms" + e.toString() + " message" + e.getMessage());
        }

        return "";

    }


    private void addTableAndRowToModel(final TableModel model, final String tableId, final Object items, final String onInvokeAction) {
        Table table = model.getTableInstance();
        table.setForm("command");
        table.setTableId(tableId);
        table.setTitle("");
        table.setAutoIncludeParameters(Boolean.FALSE);
        table.setImagePath(model.getContext().getContextPath() + "/images/table/*.gif");
        table.setFilterable(false);
        table.setSortable(true);
        table.setShowPagination(true);
        table.setItems(items);
        table.setOnInvokeAction(onInvokeAction);
        model.addTable(table);

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);
    }


    public List<LabTerm> getLabTermsByCategory(Integer labCategoryId) {
        List<LabTerm> terms;
        if (labCategoryId == 0) {
            terms = labTermDao.getAll();
        } else {
            terms = labCategoryDao.getById(labCategoryId).getTerms();
        }
        // cut down objects for serialization
        for (LabTerm term : terms) {
            term.getCategory().setTerms(null);
            term.getCategory().getLabVersion().setCategories(null);
        }
        return terms;
    }

    public List<LabCategory> getLabCategories() {
        List<LabCategory> categories = labCategoryDao.getAll();
        // cut down objects for serialization
        for (LabCategory category : categories) {
            category.setTerms(null);
        }
        return categories;
    }

    //will return the labTestNamesRefData Lov's matching the testName.
    public List<Lov> matchLabTestNames(String testName) {
        List<Lov> lovs = new ArrayList<Lov>();
        for (Lov lov : configProperty.getMap().get("labTestNamesRefData")) {
            if (StringUtils.containsIgnoreCase(lov.getDesc(), testName)) lovs.add(lov);
        }
        return ObjectTools.reduceAll(lovs, "code", "desc");
    }

    public List<TreatmentAssignment> matchTreatmentAssignment(String text, int studyId) {
        List<TreatmentAssignment> treatmentAssignments = treatmentAssignmentDao.getAssignmentsByStudyId(text, studyId);
        return ObjectTools.reduceAll(treatmentAssignments, "id", "code", "description");
    }

    private String[] extractSubnames(String text) {
        return text.split("\\s+");
    }

    public boolean pushAdverseEventToStudyCalendar(int aeReportId) {
        ExpeditedAdverseEventReport report = aeReportDao.getById(aeReportId);
        try {
            interoperationService.pushToStudyCalendar(report);
            return true;
        } catch (CaaersSystemException ex) {
            log.warn("Interoperation Service, is not working properly", ex);
            // this happens if the interoperationService isn't correctly configured
            return false;
        } catch (RuntimeException re) {
            log.error("Unexpected error in communicating with study calendar", re);
            return false;
        }
    }

    public boolean pushRoutineAdverseEventToStudyCalendar(int aeReportId) {
        RoutineAdverseEventReport report = roReportDao.getById(aeReportId);
        try {
            interoperationService.pushToStudyCalendar(report);
            return true;
        } catch (CaaersSystemException ex) {
            log.warn("Interoperation Service, is not working properly", ex);
            // this happens if the interoperationService isn't correctly configured
            return false;
        } catch (RuntimeException re) {
            log.error("Unexpected error in communicating with study calendar", re);
            return false;
        }
    }

    public String withdrawReportVersion(int aeReportId, int reportId) {
        ExpeditedAdverseEventReport aeReport = aeReportDao.getById(aeReportId);
        for (Report report : aeReport.getReports()) {
            if (report.getId().equals(reportId) && !report.getLastVersion().getReportStatus().equals(ReportStatus.COMPLETED)) {
                reportRepository.withdrawLastReportVersion(report);
                break;
            }
        }
        aeReportDao.save(aeReport);
        return "Success";
    }

    /**
     * Generic method which returns the contents of a JSP form section for the given
     * named section.
     */
    public String addFormSection(String name, int index, Integer aeReportId) {
        return renderIndexedAjaxView(name + "FormSection", index, aeReportId);
    }

    /**
     * Returns the HTML for the section of the basic AE entry form for
     * the adverse event with the given index
     *
     * @param index
     * @return
     */
    public String addRoutineAeMeddra(int index, Integer aeReportId) {
        return renderIndexedAjaxView("routineAdverseEventMeddraFormSection", index, aeReportId);
    }

    /**
     * Returns the HTML for the section of the other causes form for
     * the other cause with the given index
     *
     * @param index
     * @return
     */
    public String addPriorTherapy(int index, Integer aeReportId) {
        return renderIndexedAjaxView("priorTherapyFormSection", index, aeReportId);
    }

    /**
     * Returns the HTML for the section of the other causes form for
     * the other cause with the given index
     *
     * @param index
     * @return
     */
    public String addPriorTherapyAgent(int index, int parentIndex, Integer aeReportId) {
        return renderIndexedAjaxView("priorTherapyAgentFormSection", index, parentIndex, aeReportId);
    }

    public double calculateBodySurfaceArea(double ht, String htUOM, double wt, String wtUOM) {
        return ParticipantHistory.bodySuraceArea(ht, htUOM, wt, wtUOM);
    }

    /**
     * Reorders the list property of the current session command, moving the element at
     * <code>objectIndex</code> to <code>targetIndex</code> and shifting everything else
     * around as appropriate.
     * <p/>
     * <p>
     * Note that other than the extract command bit, this is entirely non-ae-flow-specific.
     * </p>
     *
     * @return A list of changes indicating which elements of the list were moved and where to.
     *         This list will be empty if the requested change is invalid or if the change is a no-op.
     */
    @SuppressWarnings({"unchecked"})
    public AjaxOutput reorder(String listProperty, int objectIndex, int targetIndex) {
        Object command = extractCommand();
        List<Object> list = (List<Object>) new BeanWrapperImpl(command).getPropertyValue(listProperty);
        if (targetIndex >= list.size()) {
            log.debug("Attempted to move past the end; " + targetIndex + " >= " + list.size());
            return new AjaxOutput("Unable to reorder. Attempted to delete beyond the end; " + targetIndex + " >= " + list.size());
        }
        if (targetIndex < 0) {
            log.debug("Attempted to move past the start; " + targetIndex + " < 0");
            return new AjaxOutput("Unable to reorder. Attempted to move past the start; " + targetIndex + " < 0");
        }
        if (objectIndex == targetIndex) {
            log.debug("No move requested; " + objectIndex + " == " + targetIndex);
            return new AjaxOutput();
        }
        if (0 > objectIndex || objectIndex >= list.size()) {
            log.debug("No " + listProperty + " with index " + objectIndex);
            return new AjaxOutput();
        }
        Object o = list.remove(objectIndex);
        list.add(targetIndex, o);
        List<IndexChange> changes = createMoveChangeList(objectIndex, targetIndex);
        addDisplayNames(listProperty, changes);
        try {
			saveIfAlreadyPersistent((ExpeditedAdverseEventInputCommand) command);
		} catch( OptimisticLockingFailureException ole){
			log.error("Error occured while reordering [listProperty :" + listProperty +
        			", objectIndex :" + targetIndex +
        			", targetIndex :" + targetIndex +"]", ole);
        	return new AjaxOutput("Unable to reorder at this point. The same data is being modified by someone else, please restart the page flow");
		}
        return new AjaxOutput(changes);
    }

    private List<IndexChange> createMoveChangeList(int original, int target) {
        List<IndexChange> list = new ArrayList<IndexChange>();
        if (original < target) {
            list.add(new IndexChange(original, target));
            for (int i = original + 1; i <= target; i++) {
                list.add(new IndexChange(i, i - 1));
            }
        } else {
            for (int i = target; i < original; i++) {
                list.add(new IndexChange(i, i + 1));
            }
            list.add(new IndexChange(original, target));
        }
        return list;
    }

    /**
     * When we delte an element which has been attributed, the attribution also needs to be deleted.
     * @param o
     */
    public void cascaeDeleteToAttributions(DomainObject o, ExpeditedAdverseEventReport aeReport){
    	for(AdverseEvent ae : aeReport.getAdverseEvents()){
    		if(o instanceof RadiationIntervention){
    			deleteAttribution(o, ae.getRadiationAttributions(), ae);
        	}else if(o instanceof MedicalDevice) {
        		deleteAttribution(o, ae.getDeviceAttributions(), ae);
        	}else if(o instanceof SurgeryIntervention) {
        		deleteAttribution(o, ae.getSurgeryAttributions(), ae);
        	}else if(o instanceof CourseAgent) {
        		deleteAttribution(o, ae.getCourseAgentAttributions(), ae);
        	}else if(o instanceof ConcomitantMedication) {
        		deleteAttribution(o, ae.getConcomitantMedicationAttributions(), ae);
        	}else if(o instanceof OtherCause) {
        		deleteAttribution(o, ae.getOtherCauseAttributions(), ae);
        	}else if(o instanceof DiseaseHistory) {
        		deleteAttribution(o, ae.getDiseaseAttributions(), ae);
        	}
    	}
    }

    public void deleteAttribution(DomainObject obj, List<? extends AdverseEventAttribution<? extends DomainObject>> attributions, AdverseEvent ae){
    	AdverseEventAttribution<? extends DomainObject> unwantedAttribution = null;
    	for(AdverseEventAttribution<? extends DomainObject> attribution : attributions){
    		if(obj.getId().equals(attribution.getCause().getId())) {
    			unwantedAttribution = attribution;
    			break;
    		}

    	}
    	if(unwantedAttribution != null){
    		attributions.remove(unwantedAttribution);
    		unwantedAttribution.setAdverseEvent(null);
    	}
    }
    /**
     * Deletes an element in a list property of the current session command, shifting everything
     * else around as appropriate.
     * <p/>
     * <p>
     * Note that other than the extract command bit, this is entirely non-ae-flow-specific.
     * </p>
     *
     * @return A list of changes indicating which elements of the list were moved and where to.
     *         This list will be empty if the requested change is invalid or if the change is a no-op.
     *         The element to remove will be represented by a move to a negative index.
     */
    @SuppressWarnings({"unchecked"})
    public AjaxOutput remove(String listProperty, int indexToDelete) {
        ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand)extractCommand();
        command.reassociate(); //reassociate to session
        command.getStudy(); //this is to fix the LazyInit execption on "Save&Continue" after a delete(GForge #11981, comments has the details) 
        List<Object> list = (List<Object>) new BeanWrapperImpl(command).getPropertyValue(listProperty);
        if (indexToDelete >= list.size()) {
            log.debug("Attempted to delete beyond the end; " + indexToDelete + " >= " + list.size());
            return new AjaxOutput("Unable to delete. Attempted to delete beyond the end; " + indexToDelete + " >= " + list.size());
        }
        if (indexToDelete < 0) {
            log.debug("Attempted to delete from an invalid index; " + indexToDelete + " < 0");
            return new AjaxOutput("Unable to delete. Attempted to delete beyond the end; " + indexToDelete + " >= " + list.size());
        }
        List<IndexChange> changes = createDeleteChangeList(indexToDelete, list.size());
        Object removedObject = list.get(indexToDelete);
        cascaeDeleteToAttributions((DomainObject)removedObject, command.getAeReport());
        list.remove(indexToDelete);

        if(removedObject instanceof ExpeditedAdverseEventReportChild){
        	ExpeditedAdverseEventReportChild removedAEChild = (ExpeditedAdverseEventReportChild) removedObject;
        	removedAEChild.setReport(null);
        }
        
        addDisplayNames(listProperty, changes);
        try{
        	saveIfAlreadyPersistent(command);
        }catch(DataIntegrityViolationException die){
        	log.error("Error occured while deleting [listProperty :" + listProperty +
        			", indexToDelete :" + indexToDelete +
        			"]", die);
        	return new AjaxOutput("Unable to delete. The object being removed is referenced elsewhere.");
        }catch(OptimisticLockingFailureException ole){
        	log.error("Error occured while deleting [listProperty :" + listProperty +
        			", indexToDelete :" + indexToDelete +
        			"]", ole);
        	return new AjaxOutput("Unable to delete. The same data is being modified by someone else, please restart the page flow.");
        }
        return new AjaxOutput(changes);
    }

    private List<IndexChange> createDeleteChangeList(int indexToDelete, int length) {
        List<IndexChange> changes = new ArrayList<IndexChange>();
        changes.add(new IndexChange(indexToDelete, null));
        for (int i = indexToDelete + 1; i < length; i++) {
            changes.add(new IndexChange(i, i - 1));
        }
        return changes;
    }

    private void addDisplayNames(String listProperty, List<IndexChange> changes) {
        TreeNode listNode = expeditedReportTree.find(listProperty.split("\\.", 2)[1]);
        for (IndexChange change : changes) {
            if (change.getCurrent() != null) {
                change.setCurrentDisplayName(listNode.getDisplayName(change.getCurrent()));
            }
        }
    }

    private void saveIfAlreadyPersistent(ExpeditedAdverseEventInputCommand command) {
        if (command.getAeReport().getId() != null) {
            aeReportDao.save(command.getAeReport());
        }
    }

    private String renderIndexedAjaxView(String viewName, int index, Integer aeReportId) {
        return renderIndexedAjaxView(viewName, index, null, aeReportId);
    }

    private String renderIndexedAjaxView(String viewName, int index, Integer parentIndex, Integer aeReportId) {
        Map<String, String> params = new LinkedHashMap<String, String>(); // preserve order for testing
        params.put("index", Integer.toString(index));
        if (parentIndex != null) params.put("parentIndex", Integer.toString(parentIndex));
        return renderAjaxView(viewName, aeReportId, params);
    }
    
    
   
    private String renderAjaxView(String viewName, Integer aeReportId, Map<String, String> params) {
        WebContext webContext = WebContextFactory.get();

        if (aeReportId != null) params.put("aeReport", aeReportId.toString());
        params.put(AbstractAdverseEventInputController.AJAX_SUBVIEW_PARAMETER, viewName);

        String url = String.format("%s?%s",
                getCurrentPageContextRelative(webContext), createQueryString(params));
        log.debug("Attempting to return contents of " + url);
        try {
            String html = webContext.forwardToString(url);
            if (log.isDebugEnabled()) log.debug("Retrieved HTML:\n" + html);
            return html;
        } catch (ServletException e) {
            throw new CaaersSystemException(e);
        } catch (IOException e) {
            throw new CaaersSystemException(e);
        }
    }

    private Object extractCommand() {
        WebContext webContext = WebContextFactory.get();
        Object command = null;
        for (Class<?> controllerClass : CONTROLLERS) {
            String formSessionAttributeName = controllerClass.getName() + ".FORM.command";
            command = webContext.getSession().getAttribute(formSessionAttributeName);
            if (command == null) {
                log.debug("Command not found using name " + formSessionAttributeName);
            } else {
                log.debug("Command found using name " + formSessionAttributeName);
                break;
            }
        }
        if (command == null) {
            throw new CaaersSystemException("Could not find command in session");
        } else {
            return command;
        }
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
    

    // TODO: there's got to be a library version of this somewhere
    private String createQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue())
                    .append('&');
        }
        return sb.toString().substring(0, sb.length() - 1);
    }
    
    //--------------- functionality added for Labviewr integration -------------------------
    public void dismissLab(int labId){
    	LabLoad labLoad = labLoadDao.getById(labId);
    	if(labLoad != null){
    		labLoad.setDismissed(Boolean.TRUE);
    		labLoadDao.save(labLoad);
    	}
    }

    
    //--------------------- functionality added for Reporting period -----------------

    /**
     * This function is called to fetch the content associated to a reporting period
     *   -  after we create a new reporting period
     *   -  after we select a reporting period from the combo box.
     *   
     *   A little bit on the working, 
     *     - Will refresh the assignment object, (to support newly added Reporting period ordering)
     *     - Will fetch the content associated to the reporting period by calling captureAdverseEventDetailSection.jsp
     * @param reportingPeriodId
     * @return
     */
    
    public AjaxOutput refreshReportingPeriodAndGetDetails(int reportingPeriodId){
    	CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand)extractCommand();
    	command.refreshAssignment(reportingPeriodId);
    	
    	List<AdverseEventReportingPeriod> rpList = ObjectTools.reduceAll(command.getAssignment().getReportingPeriods(), "id", "startDate" , "endDate", "name");
    	AjaxOutput output = new AjaxOutput();
    	output.setObjectContent(rpList);
    	
    	//get the content for the below html section. 
    	
    	Map<String, String> params = new LinkedHashMap<String, String>(); // preserve order for testing
    	params.put("adverseEventReportingPeriod", "" + reportingPeriodId);
    	String html = renderAjaxView("captureAdverseEventDetailSection", 0, params);
    	output.setHtmlContent(html);
    	return output;
    }
    /**
     * Create AdverseEvent objects corresponding to the terms(listOfTermIDs).
     *  Add the following parameters to request :- 
     *     1. "index" - corresponds to begin (of AE).
     *     2. "ajaxView" - 'observedAdverseEventSection'
     *  
     * @param listOfTermIDs
     * @return
     */
    public String addObservedAE(int[] listOfTermIDs) {
        
        CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
        int index = command.getAdverseEvents().size();
        
        List<Integer> filteredTermIDs = new ArrayList<Integer>();
        //filter off the terms that are already present
        for(int id : listOfTermIDs){
        	filteredTermIDs.add(id);
        }
        //remove from filteredTermIds, the ones that are avaliable in AE
        for(AdverseEvent ae : command.getAdverseEventReportingPeriod().getAdverseEvents()){
        	filteredTermIDs.remove(ae.getAdverseEventTerm().getTerm().getId());
        }
        
        if(filteredTermIDs.isEmpty()) return "";
        
        boolean isMeddra = command.getStudy().getAeTerminology().getTerm() == Term.MEDDRA;
        for(int id: filteredTermIDs){
        	AdverseEvent ae = new AdverseEvent();
        	ae.setSolicited(false);
        	
        	if(isMeddra){
        		//populate MedDRA term
        		LowLevelTerm llt = lowLevelTermDao.getById(id);
        		AdverseEventMeddraLowLevelTerm aellt = new AdverseEventMeddraLowLevelTerm();
        		aellt.setLowLevelTerm(llt);
        		ae.setAdverseEventMeddraLowLevelTerm(aellt);
        		aellt.setAdverseEvent(ae);
        	}else{
        		//properly set CTCterm
        		CtcTerm ctc =ctcTermDao.getById(id);
        		AdverseEventCtcTerm aeCtc = new AdverseEventCtcTerm();
        		aeCtc.setCtcTerm(ctc);
        		ae.setAdverseEventCtcTerm(aeCtc);
        		aeCtc.setAdverseEvent(ae);
        	}
        	
        	ae.setReportingPeriod(command.getAdverseEventReportingPeriod());
        	command.getAdverseEvents().add(ae);
        }
        Map<String, String> params = new LinkedHashMap<String, String>(); // preserve order for testing
    	params.put("adverseEventReportingPeriod", "" + command.getAdverseEventReportingPeriod());
    	 params.put("index", Integer.toString(index));
        return renderAjaxView("observedAdverseEventSection", 0, params);
    }
    
    public AjaxOutput deleteAdverseEvent(int index){
    	CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
    	command.getAdverseEvents().remove(index);
    	return new AjaxOutput();
    }
    
    ////// CONFIGURATION

    @Required
    public void setStudyDao(StudyDao studyDao) {
        this.studyDao = studyDao;
    }
    @Required
    public void setParticipantDao(final ParticipantDao participantDao) {
        this.participantDao = participantDao;
    }



    @Required
    public void setCtcDao(CtcDao ctcDao) {
        this.ctcDao = ctcDao;
    }

    @Required
    public void setCtcTermDao(CtcTermDao ctcTermDao) {
        this.ctcTermDao = ctcTermDao;
    }

    @Required
    public void setAeReportDao(ExpeditedAdverseEventReportDao aeReportDao) {
        this.aeReportDao = aeReportDao;
    }

    @Required
    public void setResearchStaffDao(ResearchStaffDao researchStaffDao) {
        this.researchStaffDao = researchStaffDao;
    }

    @Required
    public void setInteroperationService(InteroperationService interoperationService) {
        this.interoperationService = interoperationService;
    }

    @Required
    public void setAnatomicSiteDao(AnatomicSiteDao anatomicSiteDao) {
        this.anatomicSiteDao = anatomicSiteDao;
    }

    @Required
    public void setPriorTherapyDao(PriorTherapyDao priorTherapyDao) {
        this.priorTherapyDao = priorTherapyDao;
    }

    @Required
    public void setCtcCategoryDao(CtcCategoryDao ctcCategoryDao) {
        this.ctcCategoryDao = ctcCategoryDao;
    }

    @Required
    public void setLowLevelTermDao(LowLevelTermDao lowLevelTermDao) {
        this.lowLevelTermDao = lowLevelTermDao;
    }

    @Required
    public void setPreExistingConditionDao(
            PreExistingConditionDao preExistingConditionDao) {
        this.preExistingConditionDao = preExistingConditionDao;
    }

    @Required
    public void setAgentDao(AgentDao agentDao) {
        this.agentDao = agentDao;
    }

    @Required
    public void setExpeditedReportTree(ExpeditedReportTree expeditedReportTree) {
        this.expeditedReportTree = expeditedReportTree;
    }

    @Required
    public ConfigProperty getConfigurationProperty() {
        return configProperty;
    }

    public void setConfigurationProperty(ConfigProperty configProperty) {
        this.configProperty = configProperty;
    }

    @Required
    public TreatmentAssignmentDao getTreatmentAssignmentDao() {
        return treatmentAssignmentDao;
    }

    public void setTreatmentAssignmentDao(TreatmentAssignmentDao treatmentAssignmentDao) {
        this.treatmentAssignmentDao = treatmentAssignmentDao;
    }

    @Required
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Required
    public void setRoutineAdverseEventReportDao(RoutineAdverseEventReportDao roReportDao) {
        this.roReportDao = roReportDao;
    }

    @Required
    public LabCategoryDao getLabCategoryDao() {
        return labCategoryDao;
    }

    public void setLabCategoryDao(LabCategoryDao labCategoryDao) {
        this.labCategoryDao = labCategoryDao;
    }

    @Required
    public LabTermDao getLabTermDao() {
        return labTermDao;
    }

    public void setLabTermDao(LabTermDao labTermDao) {
        this.labTermDao = labTermDao;
    }

    @Required
    public ChemoAgentDao getChemoAgentDao() {
        return chemoAgentDao;
    }

    public void setChemoAgentDao(ChemoAgentDao chemoAgentDao) {
        this.chemoAgentDao = chemoAgentDao;
    }

    @Required
    public InterventionSiteDao getInterventionSiteDao() {
        return interventionSiteDao;
    }

    public void setInterventionSiteDao(InterventionSiteDao interventionSiteDao) {
        this.interventionSiteDao = interventionSiteDao;
    }
    @Required
	public CtepStudyDiseaseDao getCtepStudyDiseaseDao() {
		return ctepStudyDiseaseDao;
	}
	public void setCtepStudyDiseaseDao(CtepStudyDiseaseDao ctepStudyDiseaseDao) {
		this.ctepStudyDiseaseDao = ctepStudyDiseaseDao;
	}

	private HttpServletRequest getHttpServletRequest() {
        return WebContextFactory.get().getHttpServletRequest();
    }

	public AdverseEventReportingPeriodDao getReportingPeriodDao() {
		return reportingPeriodDao;
	}
	
	public void setReportingPeriodDao(
			AdverseEventReportingPeriodDao reportingPeriodDao) {
		this.reportingPeriodDao = reportingPeriodDao;
	}

	public void setLabLoadDao(LabLoadDao labLoadDao) {
		this.labLoadDao = labLoadDao;
	}
}
