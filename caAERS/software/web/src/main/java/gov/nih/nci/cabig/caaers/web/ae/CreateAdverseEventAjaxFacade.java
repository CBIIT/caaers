/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.ae;

import static gov.nih.nci.cabig.caaers.tools.ObjectTools.reduce;
import static gov.nih.nci.cabig.caaers.tools.ObjectTools.reduceAll;
import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.dao.*;
import gov.nih.nci.cabig.caaers.dao.meddra.LowLevelTermDao;
import gov.nih.nci.cabig.caaers.dao.query.ajax.ParticipantAjaxableDomainObjectQuery;
import gov.nih.nci.cabig.caaers.dao.query.ajax.StudySearchableAjaxableDomainObjectQuery;
import gov.nih.nci.cabig.caaers.dao.report.ReportDao;
import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.ajax.ParticipantAjaxableDomainObject;
import gov.nih.nci.cabig.caaers.domain.ajax.StudyAjaxableDomainObject;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportTree;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.TreeNode;
import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportVersionDTO;
import gov.nih.nci.cabig.caaers.domain.repository.AdverseEventRoutingAndReviewRepository;
import gov.nih.nci.cabig.caaers.domain.repository.ReportRepository;
import gov.nih.nci.cabig.caaers.domain.repository.ReportVersionRepository;
import gov.nih.nci.cabig.caaers.domain.repository.ajax.ParticipantAjaxableDomainObjectRepository;
import gov.nih.nci.cabig.caaers.domain.repository.ajax.StudySearchableAjaxableDomainObjectRepository;
import gov.nih.nci.cabig.caaers.security.SecurityUtils;
import gov.nih.nci.cabig.caaers.service.AdeersIntegrationFacade;
import gov.nih.nci.cabig.caaers.service.InteroperationService;
import gov.nih.nci.cabig.caaers.tools.ObjectTools;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.utils.DateUtils;
import gov.nih.nci.cabig.caaers.utils.Lov;
import gov.nih.nci.cabig.caaers.utils.ranking.RankBasedSorterUtils;
import gov.nih.nci.cabig.caaers.utils.ranking.Serializer;
import gov.nih.nci.cabig.caaers.web.dwr.AjaxOutput;
import gov.nih.nci.cabig.caaers.web.dwr.IndexChange;
import gov.nih.nci.cabig.ctms.domain.DomainObject;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.context.SecurityContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
 * @author Biju Joseph
 */
public class CreateAdverseEventAjaxFacade {

    private static final Log log = LogFactory.getLog(CreateAdverseEventAjaxFacade.class);
    private static Class<?>[] CONTROLLERS = {EditAdverseEventController.class};

    protected StudyDao studyDao;
    protected ParticipantDao participantDao;
    protected StudyParticipantAssignmentDao assignmentDao;
    protected CtcTermDao ctcTermDao;
    protected CtcCategoryDao ctcCategoryDao;
    protected CtcDao ctcDao;
    protected LowLevelTermDao lowLevelTermDao;
    protected ExpeditedAdverseEventReportDao aeReportDao;
    protected AnatomicSiteDao anatomicSiteDao;
    protected InteroperationService interoperationService;
    protected PriorTherapyDao priorTherapyDao;
    protected PreExistingConditionDao preExistingConditionDao;
    protected AgentDao agentDao;
    protected TreatmentAssignmentDao treatmentAssignmentDao;
    protected ReportDefinitionDao reportDefinitionDao;
    protected ExpeditedReportTree expeditedReportTree;
    protected ConfigProperty configProperty;
    protected ReportDao reportDao;
    protected ReportRepository reportRepository;
    protected LabCategoryDao labCategoryDao;
    protected LabTermDao labTermDao;
    protected ChemoAgentDao chemoAgentDao;
    protected InterventionSiteDao interventionSiteDao;
    protected CtepStudyDiseaseDao ctepStudyDiseaseDao;
    protected AdverseEventReportingPeriodDao reportingPeriodDao;
    protected LabLoadDao labLoadDao;
    private StudySearchableAjaxableDomainObjectRepository studySearchableAjaxableDomainObjectRepository;
    private ParticipantAjaxableDomainObjectRepository participantAjaxableDomainObjectRepository;
    private ConditionDao conditionDao;
	private AdverseEventRoutingAndReviewRepository adverseEventRoutingAndReviewRepository;
	private ResearchStaffDao researchStaffDao;
	private InvestigatorDao investigatorDao;
    private OtherInterventionDao otherInterventionDao;
    private StudyDeviceDao studyDeviceDao;
    private AdeersIntegrationFacade proxyWebServiceFacade;
    private SiteInvestigatorDao siteInvestigatorDao;
    private SiteResearchStaffDao siteResearchStaffDao;
    private ReportVersionRepository reportVersionRepository;

    public Class<?>[] controllers() {
        return CONTROLLERS;
    }

    public List<AnatomicSite> matchAnatomicSite(String text) {
        List<AnatomicSite> anatomicSites = anatomicSiteDao.getBySubnames(extractSubnames(text));
        anatomicSites = RankBasedSorterUtils.sort(anatomicSites , text, new Serializer<AnatomicSite>(){
            public String serialize(AnatomicSite object) {
                return object.getName();
            }
        });
        return  anatomicSites;
    }

    public AnatomicSite getAnatomicSiteById(String anatomicSiteId) throws Exception {
        return anatomicSiteDao.getById(Integer.parseInt(anatomicSiteId));
    }

    public String buildAnatomicSiteTable(String el, final Map parameterMap, String tableId, HttpServletRequest request) throws Exception {

        try {
            TableModel model = getTableModel(parameterMap, request);
            List<AnatomicSite> anatomicSites = anatomicSiteDao.getAll();

            String onInvokeAction = "showShowAllTable('" + el + "', '" + tableId + "')";
            addTableAndRowToModel(model, tableId, anatomicSites, onInvokeAction);

            Column columnTerm = model.getColumnInstance();
            columnTerm.setProperty("name");
            columnTerm.setTitle("Primary site of disease");
            columnTerm.setCell("gov.nih.nci.cabig.caaers.web.search.link.AnatomicSiteLinkDisplayCell");
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
        List<PriorTherapy> therapies = priorTherapyDao.getBySubnames(extractSubnames(text));
        return therapies;
    }

    public List<PreExistingCondition> matchPreExistingConds(String text) {
        return preExistingConditionDao.getBySubnames(extractSubnames(text));
    }

    public List<LowLevelTerm> matchLowLevelTermsByCode(int version_id, String text) {
        List<LowLevelTerm> terms = lowLevelTermDao.getByVersionSubnames(version_id, extractSubnames(text));
        terms = RankBasedSorterUtils.sort(terms , text, new Serializer<LowLevelTerm>(){
            public String serialize(LowLevelTerm object) {
                return object.getFullName();
            }
        });
        return ObjectTools.reduceAll(terms, "id", "meddraCode", "meddraTerm");
    }

    public List<Condition> matchConditions(String text) {
        List<Condition> conditions = conditionDao.getAllByText(text);
        conditions = RankBasedSorterUtils.sort(conditions , text, new Serializer<Condition>(){
            public String serialize(Condition object) {
                return object.getConditionName();
            }
        });
        return conditions;
    }

    public List<ChemoAgent> matchChemoAgents(String text) {
        String[] excerpts = {text};
        List<ChemoAgent> agents = chemoAgentDao.getBySubname(excerpts);
        agents = RankBasedSorterUtils.sort(agents , text, new Serializer<ChemoAgent>(){
            public String serialize(ChemoAgent object) {
                return object.getFullName();
            }
        });
        return agents;
    }

    public ChemoAgent getChemoAgentById(String chemoAgentId) throws Exception {

        return chemoAgentDao.getById(Integer.parseInt(chemoAgentId));
    }


    public List<InterventionSite> matchInterventionSites(String text) {
        String[] excerpts = {text};
        List<InterventionSite> sites = interventionSiteDao.getBySubname(excerpts);
        sites = RankBasedSorterUtils.sort(sites , text, new Serializer<InterventionSite>(){
            public String serialize(InterventionSite object) {
                return object.getName();
            }
        });
        return sites;
    }

    public List<Agent> matchAgents(String text) {
        List<Agent> agents = agentDao.getBySubnames(extractSubnames(text));
        agents =  RankBasedSorterUtils.sort(agents , text, new Serializer<Agent>(){
            public String serialize(Agent object) {
                return object.getDisplayName();
            }
        });
        return ObjectTools.reduceAll(agents, "id", "name", "nscNumber", "description");
    }

    public Integer getDiseaseFromStudyDisease(String studyDiseaseId) {
        CtepStudyDisease ctepStudyDisease = ctepStudyDiseaseDao.getById(Integer.parseInt(studyDiseaseId));
        return ctepStudyDisease.getTerm().getId();
    }


    public Person getResearchStaffDetails(String userId){
    	Object cmd = extractCommand();
		ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) cmd;
		ResearchStaff researchStaff = researchStaffDao.getById(Integer.parseInt(userId));
		Person user = null;
		SiteResearchStaff siteResearchStaff = null;
		if(researchStaff != null){
            Organization organization = command.getAeReport().getAssignment().getStudySite().getOrganization();
            if(organization != null){
               siteResearchStaff = researchStaff.findActiveSiteResearchStaffByOrganizationId(organization.getId()); 
            }

			user = researchStaff;
		}else{
			user = investigatorDao.getById(Integer.parseInt(userId));
		}
			
    	LocalResearchStaff rstaff = new LocalResearchStaff();
    	rstaff.setId(user.getId());
    	rstaff.setFirstName(user.getFirstName());
    	rstaff.setLastName(user.getLastName());
    	rstaff.setMiddleName(user.getMiddleName());
    	if(siteResearchStaff != null && siteResearchStaff.getEmailAddress() != null)
    		rstaff.setEmailAddress(siteResearchStaff.getEmailAddress());
    	else
    		rstaff.setEmailAddress(user.getEmailAddress());
    	if(siteResearchStaff != null && siteResearchStaff.getPhoneNumber() != null)
    		rstaff.setPhoneNumber(siteResearchStaff.getPhoneNumber());
    	else
    		rstaff.setPhoneNumber(user.getPhoneNumber());
    	if(siteResearchStaff != null && siteResearchStaff.getFaxNumber() != null)
    		rstaff.setFaxNumber(siteResearchStaff.getFaxNumber());
    	else
    		rstaff.setFaxNumber(user.getFaxNumber());
    	if(siteResearchStaff != null && siteResearchStaff.getAddress() != null)
    		rstaff.setAddress(siteResearchStaff.getAddress());
    	return rstaff;
    }
    
    public Person getInvestigator(String text){
    	Person user = investigatorDao.getById(Integer.parseInt(text));
        return reduce(user, "id", "firstName", "lastName", "middleName", "emailAddress", "phoneNumber", "faxNumber");
    }

    public Map getSiteInvestigator(String text) {
        return getSiteResearchStaffOrSiteInvestigator(text, false);
    }

    public Map getSiteResearchStaff(String text) {

        return getSiteResearchStaffOrSiteInvestigator(text, true);
    }

    public Map getSiteResearchStaffOrSiteInvestigator(String text, Boolean fetchSiteResearchStaff) {
        Map user = new HashMap();

        Address address = null;
        Person person = null;
        if (fetchSiteResearchStaff) {
            SiteResearchStaff siteResearchStaff = siteResearchStaffDao.getById(Integer.parseInt(text));
            if (siteResearchStaff != null) {
                user.put("id", siteResearchStaff.getId());
                user.put("emailAddress", siteResearchStaff.getEmailAddress());
                user.put("phoneNumber", siteResearchStaff.getPhoneNumber());
                user.put("faxNumber", siteResearchStaff.getFaxNumber());
                address = siteResearchStaff.getAddress();
                person = siteResearchStaff.getResearchStaff();

            }
        } else {
            SiteInvestigator siteInvestigator = siteInvestigatorDao.getById(Integer.parseInt(text));

            if (siteInvestigator != null) {
                user.put("id", siteInvestigator.getId());
                user.put("emailAddress", siteInvestigator.getEmailAddress());
                user.put("phoneNumber", siteInvestigator.getPhoneNumber());
                user.put("faxNumber", siteInvestigator.getFaxNumber());
                address = siteInvestigator.getAddress();
                person = siteInvestigator.getInvestigator();

            }
        }

        if (address != null) {
            Map addressMap=new HashMap();
            addressMap.put("street", address.getStreet());
            addressMap.put("city", address.getCity());
            addressMap.put("state", address.getState());
            addressMap.put("zip", address.getZip());

            user.put("address", addressMap);
            user.put("address.street", address.getStreet());
            user.put("address.city", address.getCity());
            user.put("address.state", address.getState());
            user.put("address.zip", address.getZip());
        }
        if (person != null) {
            user.put("firstName", person.getFirstName());
            user.put("lastName", person.getLastName());
            user.put("middleName", person.getMiddleName());
            user.put("title", person.getTitle());
        }

        return user;
    }

    public List<ParticipantAjaxableDomainObject> matchParticipants(String text, Integer studyId) {
        ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
        query.filterParticipantsWithMatchingText(text);
        query.filterByStudy(studyId);

        List<ParticipantAjaxableDomainObject> participantAjaxableDomainObjects = participantAjaxableDomainObjectRepository.findParticipants(query);
        participantAjaxableDomainObjects = RankBasedSorterUtils.sort(participantAjaxableDomainObjects , text, new Serializer<ParticipantAjaxableDomainObject>(){
            public String serialize(ParticipantAjaxableDomainObject object) {
                return object.getDisplayName();
            }
        });
        // cut down objects for serialization
        for (ParticipantAjaxableDomainObject o : participantAjaxableDomainObjects) {
            o.setStudySubjectIdentifiersString(o.getStudySubjectIdentifiersCSV());
        }
        return reduceAll(participantAjaxableDomainObjects, "firstName", "lastName", "id", "primaryIdentifierValue", "studySubjectIdentifiersString");
    }


    /*
    * The extra condition "o.status <> 'Administratively Complete'" as fix for bug 9514
    */


    public List<StudyAjaxableDomainObject> matchStudies(String text, Integer participantId, boolean ignoreCompletedStudy) {

        StudySearchableAjaxableDomainObjectQuery domainObjectQuery = new StudySearchableAjaxableDomainObjectQuery();
        domainObjectQuery.filterStudiesWithMatchingText(text);
        if (participantId != null) {
            domainObjectQuery.filterByParticipant(participantId);
        }
        domainObjectQuery.filterByDataEntryStatus(true);
        List<StudyAjaxableDomainObject> studies = studySearchableAjaxableDomainObjectRepository.findStudies(domainObjectQuery);
        studies = RankBasedSorterUtils.sort(studies , text, new Serializer<StudyAjaxableDomainObject>(){
            public String serialize(StudyAjaxableDomainObject object) {
                return object.getDisplayName();
            }
        });
        return reduceAll(studies, "primaryIdentifierValue", "shortTitle" , "id");
    }


    public List<CtcTerm> matchTerms(String text, Integer ctcVersionId, Integer ctcCategoryId, int limit) throws Exception {
        List<CtcTerm> terms = ctcTermDao.getBySubname(extractSubnames(text), ctcVersionId, ctcCategoryId);
        terms = RankBasedSorterUtils.sort(terms , text, new Serializer<CtcTerm>(){
            public String serialize(CtcTerm object) {
                return object.getFullName();
            }
        });
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
        List<CtcTerm> terms = null;

        // from rules UI page , if user selects terms without category a fabricated Id 0 is passed.
        // get all terms incase of this special condition -- srini
        if (ctcCategoryId == 0) {
            terms = ctcTermDao.getAll();
        } else {
            terms = ctcCategoryDao.getById(ctcCategoryId).getTerms();
        }

        List<CtcTerm> termList = new ArrayList<CtcTerm>();
        // cut down objects for serialization
        for (CtcTerm term : terms) {
            CtcTerm t = new CtcTerm();
            t.setSelect(term.getSelect());
            t.setTerm(term.getTerm());
            t.setCtepCode(term.getCtepCode());
            termList.add(t);
        }
        return termList;
    }

    public List<CtcTerm> getTermByTermId(String ctcTermId) throws Exception {
        List<CtcTerm> terms = new ArrayList<CtcTerm>();
        CtcTerm ctcTerm = ctcTermDao.getById(Integer.parseInt(ctcTermId));
        ctcTerm.getCategory().setTerms(null);
        ctcTerm.getCategory().getCtc().setCategories(null);
        terms.add(ctcTerm);

        return terms;
    }

    public List<CtcCategory> getCategories(int ctcVersionId) {
        List<CtcCategory> categories = ctcDao.getById(ctcVersionId).getCategories();
        // cut down objects for serialization
        for (CtcCategory category : categories) {
            category.setTerms(null);
        }
        return categories;
    }

    public List<CtcCategory> getCtcCategoryByStudy(int studyId) {

        Study s = studyDao.getById(studyId);
        Ctc ctc = s.getAeTerminology().getCtcVersion();


        List<CtcCategory> categories = ctc.getCategories();
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
        terms = RankBasedSorterUtils.sort(terms , text, new Serializer<LabTerm>(){
            public String serialize(LabTerm object) {
                return object.getTerm();
            }
        });
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


    private void addTableAndRowToModel(final TableModel model, final String tableId, final Object items, final String onInvokeAction) {
        Table table = model.getTableInstance();
        table.setForm("command");
        table.setTableId(tableId);
        table.setTitle("");
        table.setAutoIncludeParameters(Boolean.FALSE);
        table.setImagePath(model.getContext().getContextPath() + "/images/table/*.gif");
        table.setFilterable(false);
        table.setSortable(false);
        table.setShowPagination(true);
        table.setItems(items);
        table.setSortRowsCallback("gov.nih.nci.cabig.caaers.web.table.SortRowsCallbackImpl");
                
        table.setOnInvokeAction(onInvokeAction);
        model.addTable(table);

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);
    }

    /*
        Given the category ID retrieves the terms having this category as the parent.
    */
    public List<LabTerm> getLabTermsByCategory(Integer labCategoryId) {
        List<LabTerm> terms;
        if (labCategoryId == 0) {
            terms = labTermDao.getAll();
        } else {
            terms = labCategoryDao.getById(labCategoryId).getTerms();
        }
        // cut down objects for serialization
        List<LabTerm> theTerms = new ArrayList<LabTerm>();
        for (LabTerm term : terms) {
            if(term.isRetired()) continue;
            theTerms.add(term);
            term.getCategory().setTerms(null);
            term.getCategory().getLabVersion().setCategories(null);
        }
        Collections.sort(theTerms, new Comparator<LabTerm>() {
            public int compare(LabTerm o1, LabTerm o2) {
                return o1.getTerm().compareTo(o2.getTerm());
            }
        });
        return theTerms;
    }

    public Integer getLabCategory(Integer labTermID) {
        if (labTermID == 0) return null;
        LabTerm lt = labTermDao.getById(labTermID);
        if (lt != null) return lt.getCategory().getId(); else return null;
    }

    public List<LabCategory> getLabCategories() {
        List<LabCategory> categories = labCategoryDao.getAll();
        List<LabCategory> theCategories = new ArrayList<LabCategory>();
        // cut down objects for serialization
        for (LabCategory category : categories) {
            if(category.isRetired()) continue;
            theCategories.add(category);
            category.setTerms(null);
        }
        return theCategories;
    }

    /**
     * Returns the Safety reports to be displayed on the AEReporter Dashboard
     * @param date
     * @return
     */
    public List<ReportVersionDTO> fetchSafetyReports(Date date){
        Date startDate = DateUtils.firstDayOfThisMonth(date);
        Date endDate = DateUtils.lastDayOfThisMonth(date);
        List<ReportVersionDTO> reportVersionDTOs = reportVersionRepository.getReportActivity(startDate, endDate);
        if(reportVersionDTOs != null){
            for(ReportVersionDTO rv: reportVersionDTOs) rv.setRv(null);
        }
        return reportVersionDTOs;
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
        if (true)
            throw new UnsupportedOperationException("No more supported");
        return false;
    }

    public AjaxOutput withdrawReportVersion(int aeReportId, int reportId) {
        AjaxOutput out = new AjaxOutput();

        try {
            ExpeditedAdverseEventReport aeReport = aeReportDao.getById(aeReportId);
            Report report = aeReport.findReportById(reportId);
            if(report != null && report.isActive()){
                if(log.isDebugEnabled()) log.debug("Withdrawing report : " + String.valueOf(report));

                //withdraw report.
                reportRepository.withdrawReport(report);
                
                //withdraw the associated report.
                reportRepository.withdrawExternalReport(aeReport, report);
                
                //unamend last amended report.
                Report lastAmendedReport = aeReport.findLastAmendedReport(report.getReportDefinition());
                if(lastAmendedReport != null){
                    reportRepository.unAmendReport(lastAmendedReport);
                }

                aeReportDao.save(aeReport);
            }
            out.setObjectContent("Success");
            
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            out.setError(true);
            out.setErrorMessage(e.getMessage());
        }
        
        return out;
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
        try {
			return ParticipantHistory.bodySuraceArea(ht, htUOM, wt, wtUOM);
		} catch (Exception e) {
			log.error(e);
		}
		return 0.0;
    }
    
    public String addAdverseEvent( int index, Integer aeReportId ){
    	return addNewAdverseEvent("adverseEventFormSection", index, aeReportId, true, null);
    }
    
    public String addAdverseEventWithTerms( int index, Integer aeReportId , Integer termId){
    	return addNewAdverseEvent("adverseEventFormSection", index, aeReportId, true, termId);
    }
    
    public String addAdverseEventMeddra(int index, Integer aeReportId){
    	return addNewAdverseEvent("adverseEventMeddraFormSection", index, aeReportId,false, null);
    }
    
    /**
     * This method will generate the HTML that is to be rendered for a MedDRA form section.
     * Used by the Add AE functionality, in enterBasicsMeddra.jsp
     */
    public String addAdverseEventWithTermsMeddra( int index, Integer aeReportId , Integer termId){
    	return addNewAdverseEvent("adverseEventMeddraFormSection", index, aeReportId, false, termId);
    }
    
    public String addNewAdverseEvent(String section, int index, Integer aeReportId, boolean isCTC, Integer aeTermId){
    	try {
			Object cmd = extractCommand();
			ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) cmd;
			
			//create a new adverse event.
			AdverseEvent ae = new AdverseEvent();
			ae.setReport(command.getAeReport());
			ae.setGradedDate(new Date());
			command.getAeReport().getReportingPeriod().addAdverseEvent(ae);
			command.getAeReport().addAdverseEvent(ae);
			
			//set the ae term
			if(aeTermId != null){
				if(isCTC){
					CtcTerm term = ctcTermDao.getById(aeTermId);
					ae.getAdverseEventCtcTerm().setTerm(term);
				}else{
					LowLevelTerm term = lowLevelTermDao.getById(aeTermId);
					ae.getAdverseEventMeddraLowLevelTerm().setTerm(term);
				}
			}
			
			
			command.updateOutcomes();
			command.saveReportingPeriod();
			saveIfAlreadyPersistent(command);
			
			return renderIndexedAjaxView(section, index, aeReportId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
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
    	List<IndexChange> changes = null;
    	boolean changesApplied =false;
    	 List<Object> list = null;
    	try {
    	Object cmd = extractCommand();
    	ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) cmd;
    	

        list = (List<Object>) new BeanWrapperImpl(command).getPropertyValue(listProperty);
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
        changesApplied = true;
        
        changes = createMoveChangeList(objectIndex, targetIndex);
        addDisplayNames(listProperty, changes);
        
            saveIfAlreadyPersistent(command);
        } catch (OptimisticLockingFailureException ole) {
            log.error("Error occured while reordering [listProperty :" + listProperty +
                    ", objectIndex :" + targetIndex +
                    ", targetIndex :" + targetIndex + "]", ole);
            return new AjaxOutput("Unable to reorder at this point. The same data is being modified by someone else, please restart the page flow");
        }catch(Exception e){
        	log.error("Error occured while moving", e);
        	//revert the changes if they are applied.
        	if(changesApplied){
        		Object o = list.remove(targetIndex);
        		list.add(objectIndex, o);
        	}
        	return new AjaxOutput("Unable to re-order, please try again after saving the report");
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
        ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) extractCommand();
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
        command.getAeReport().cascaeDeleteToAttributions((DomainObject) removedObject);
        list.remove(indexToDelete);

        if (removedObject instanceof ExpeditedAdverseEventReportChild) {
            ExpeditedAdverseEventReportChild removedAEChild = (ExpeditedAdverseEventReportChild) removedObject;
            removedAEChild.setReport(null);
        }
        
        //update the reported flag when you delete from expedited report.
        if(removedObject instanceof AdverseEvent){
        	((AdverseEvent) removedObject).setReported(false);
        }

        addDisplayNames(listProperty, changes);
        try {
            saveIfAlreadyPersistent(command);
        } catch (DataIntegrityViolationException die) {
            log.error("Error occured while deleting [listProperty :" + listProperty +
                    ", indexToDelete :" + indexToDelete +
                    "]", die);
            return new AjaxOutput("Unable to delete. The object being removed is referenced elsewhere.");
        } catch (OptimisticLockingFailureException ole) {
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

    protected String renderIndexedAjaxView(String viewName, int index, Integer aeReportId) {
        return renderIndexedAjaxView(viewName, index, null, aeReportId);
    }

    protected String renderIndexedAjaxView(String viewName, int index, Integer parentIndex, Integer aeReportId) {
        Map<String, String> params = new LinkedHashMap<String, String>(); // preserve order for testing
        params.put("index", Integer.toString(index));
        if (parentIndex != null) params.put("parentIndex", Integer.toString(parentIndex));
        return renderAjaxView(viewName, aeReportId, params);
    }


    protected String renderAjaxView(String viewName, Integer aeReportId, Map<String, String> params) {
        WebContext webContext = WebContextFactory.get();

        if (aeReportId != null) params.put("aeReport", aeReportId.toString());
        params.put(CaptureAdverseEventController.AJAX_SUBVIEW_PARAMETER, viewName);

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
    
    protected WebContext getWebContext(){
    	return WebContextFactory.get();
    }

    protected Object extractCommand() {
        WebContext webContext = getWebContext();
        Object command = null;
        for (Class<?> controllerClass : controllers()) {
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
    
    // For RoutingAndReview - Report comments.
    // ******************************************************************************************************
    // TODO: These methods have to change to call the repository methods with "Report" object / IDs
    // ******************************************************************************************************
    
    public AjaxOutput addReviewComment(String comment, String reportIdString){
     
    	Integer reportId = Integer.parseInt(reportIdString);
//    	ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) extractCommand();
    	String userId = getUserId();
    	
//    	Report report = null;
//    	for(Report r: command.getAeReport().getActiveReports())
//    		if(r.getId().equals(reportId))
//    			report = r;
		Report report = null;
    	if(reportId != null || !reportId.equals(""))
    		report = reportDao.getById(reportId);		
    	adverseEventRoutingAndReviewRepository.addReportReviewComment(report, comment, userId);
    	
        return fetchPreviousComments(reportId, userId);
    }
    
    public AjaxOutput editReviewComment(String comment, Integer commentId, String reportIdString){
    	Integer reportId = Integer.parseInt(reportIdString);
//    	ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) extractCommand();
    	String userId = getUserId();
    	Report report = null;
    	if(reportId != null || !reportId.equals(""))
    		report = reportDao.getById(reportId);
//    	Report report = null;
//    	for(Report r: command.getAeReport().getActiveReports())
//    		if(r.getId().equals(reportId))
//    			report = r;
    	adverseEventRoutingAndReviewRepository.editReportReviewComment(report, comment, userId, commentId);
    	return fetchPreviousComments(reportId, getUserId());
    }
    
    public AjaxOutput deleteReviewComment(Integer commentId, String reportIdString){
    	Integer reportId = Integer.parseInt(reportIdString);
//    	ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) extractCommand();
    	String userId = getUserId();
    	Report report = null;
    	if(reportId != null || !reportId.equals(""))
    		report = reportDao.getById(reportId);
//    	Report report = null;
//    	for(Report r: command.getAeReport().getActiveReports())
//    		if(r.getId().equals(reportId))
//    			report = r;
    	adverseEventRoutingAndReviewRepository.deleteReportReviewComment(report, commentId);
    	return fetchPreviousComments(reportId, userId);
    }
    
    // ******************************************************************************************************
    // TODO: These methods have to change to call the repository methods with "Report" object / IDs
    // ******************************************************************************************************



    public AjaxOutput updateReviewPageInfo(Boolean physicianSignOff, int reportIndex, String caseNumber){
        AjaxOutput output = new AjaxOutput();

        try {
            ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) extractCommand();
            List<Report> reports = command.getAeReport().getReports();
            if(reportIndex > -1){
               reports.get(reportIndex).setCaseNumber(caseNumber);
            }

            command.getAeReport().setPhysicianSignOff(physicianSignOff);
            saveIfAlreadyPersistent(command);
            Map<String, String> params = new LinkedHashMap<String, String>(); // preserve order for testing
            String html = renderAjaxView("submitReportValidationSection", 0, params);
            output.setHtmlContent(html);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            output.setError(true);
            output.setErrorMessage(e.getMessage());
        }
        return output;
    }
    public AjaxOutput updatePhysicianSignOff(Boolean physicianSignOff){
    	AjaxOutput output = new AjaxOutput();

        try {
            ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) extractCommand();
            command.getAeReport().getReports();
            command.getAeReport().setPhysicianSignOff(physicianSignOff);
            saveIfAlreadyPersistent(command);
            Map<String, String> params = new LinkedHashMap<String, String>(); // preserve order for testing
            String html = renderAjaxView("submitReportValidationSection", 0, params);
            output.setHtmlContent(html);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            output.setError(true);
            output.setErrorMessage(e.getMessage());
        }
        return output;
    }
    
    public AjaxOutput refreshSubmitReportValidationSection(){
    	AjaxOutput out = new AjaxOutput();

        try {
            Map<String, String> params = new LinkedHashMap<String, String>(); // preserve order for testing
            String html = renderAjaxView("submitReportValidationSection", 0, params);
            out.setHtmlContent(html);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            out.setError(true);
            out.setErrorMessage(e.getMessage());
        }

        return out;
    }
    
    public AjaxOutput fetchPreviousComments(Integer entityId, String userId){
		Map params = new HashMap<String, String>();
		params.put(RoutingAndReviewCommentController.AJAX_ENTITY, "report");
        params.put(RoutingAndReviewCommentController.AJAX_ENTITY_ID, entityId.toString());
        params.put("userId", userId);
        params.put(RoutingAndReviewCommentController.AJAX_ACTION, "fetchComments");
        params.put(CaptureAdverseEventController.AJAX_SUBVIEW_PARAMETER, "reviewCommentsList");
        AjaxOutput output = new AjaxOutput();
		String html =  renderCommentsAjaxView(params);
		output.setHtmlContent(html);
		return output;
	}
    
    public AjaxOutput retrieveReviewComments(Report selectedReport){
    	if(selectedReport != null)
    		return fetchPreviousComments(selectedReport.getId(), getUserId());
    	else
    		return new AjaxOutput();
    }
    
    public AjaxOutput retrieveNextTransitions(String reportId){
    	ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) extractCommand();
    	Report selectedReport = null;
    	if(reportId != null)
    		selectedReport = reportDao.getById(Integer.parseInt(reportId));
    	List<String> transitions = new ArrayList<String>();
    	if(selectedReport != null){
    		transitions = adverseEventRoutingAndReviewRepository.nextTransitionNamesForReportWorkflow(selectedReport, getUserId());
    	}
    	AjaxOutput output = new AjaxOutput();
    	output.setObjectContent(transitions.toArray());
    	return output;
    }
    
    public AjaxOutput retrieveReviewCommentsAndActions(String reportId){
    	
    	ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) extractCommand();
    	// Determine the report in context
    	/*Map<Integer, Boolean> selectedReportDefinitionsMap = new HashMap<Integer, Boolean>();
    	for(ReportDefinition rd: command.getSelectedReportDefinitions())
    		selectedReportDefinitionsMap.put(rd.getId(), Boolean.TRUE);
    	Report selectedReport = null;
    	for(Report r: command.getAeReport().getActiveReports())
    		if(selectedReportDefinitionsMap.containsKey(r.getReportDefinition().getId()))
    			selectedReport = r;
    	*/
    	Report report = null;
    	if(reportId != null || !reportId.equals(""))
    		report = reportDao.getById(Integer.parseInt(reportId));
    	AjaxOutput output = retrieveReviewComments(report);
    	AjaxOutput transitionOutput = retrieveNextTransitions(reportId);
    	output.setObjectContent(transitionOutput.getObjectContent());
    	return output;
    	
    }
    
    public AjaxOutput advanceWorkflow(String reportId, String transitionToTake){
        AjaxOutput out = new AjaxOutput();
        try{
        	ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) extractCommand();
			Report report = null;
			if(reportId != null)
				report = reportDao.getById(Integer.parseInt(reportId));
			if(report != null){
				List<String> transitions = adverseEventRoutingAndReviewRepository.advanceReportWorkflow(report.getWorkflowId(), transitionToTake, report, getUserId());
				out.setObjectContent(transitions.toArray());
			}
        } catch (Exception e) {
        	log.error(e.getMessage());
        	e.printStackTrace();
        	out.setError(true);
        	out.setErrorMessage(e.getMessage());
        }
        return out;
    }

    /**
     * Currently there is no validation for AeReport.
     * So we are returning an empty AjaxOutput. But this is the place to call a validator incase some validation needs to be
     * done before we do transition in ExpeditedAdverseEventReport workflow.
     * @param transitionToTake
     * @return
     */
    public AjaxOutput validateAndAdvanceWorkflow(String transitionToTake){
    	return new AjaxOutput();
    }
    
    protected String renderCommentsAjaxView(Map<String, String> params){
    	WebContext webContext = getWebContext();
    	String url = String.format("%s?%s",
    			"/pages/ae/listReviewComments", createQueryString(params));
        try {
            String html = webContext.forwardToString(url);
            return html;
        } catch (ServletException e) {
            throw new CaaersSystemException(e);
        } catch (IOException e) {
            throw new CaaersSystemException(e);
        }
    }

    public AjaxOutput retrieveOtherInterventionDescription(int otherInterventionId){
        AjaxOutput output = new AjaxOutput();
        OtherIntervention oi = otherInterventionDao.getById(otherInterventionId);
        if(oi != null) output.setHtmlContent(oi.getDescription());
        return output;
    }


    public AjaxOutput retrieveStudyDevice(int studyDeviceId){
        StudyDevice sd  = studyDeviceDao.getById(studyDeviceId);
        AjaxOutput output = new AjaxOutput();
        if(sd != null){
            StudyDevice d = new StudyDevice();
            d.setOtherBrandName(sd.getBrandName());
            d.setOtherCommonName(sd.getCommonName());
            d.setOtherDeviceType(sd.getDeviceType());
            d.setCatalogNumber(sd.getCatalogNumber());
            d.setManufacturerCity(sd.getManufacturerCity());
            d.setManufacturerName(sd.getManufacturerName());
            d.setManufacturerState(sd.getManufacturerState());
            d.setModelNumber(sd.getModelNumber());
            output.setObjectContent(d);
        }
        return output;
    }

    /**
     *
     * @param id Study databse id
     * @return
     */
    public AjaxOutput syncStudyWithAdEERS(Integer id) {
        AjaxOutput out = new AjaxOutput();
        String _result = "";

        try{
            _result = proxyWebServiceFacade.updateStudy(id, false);
        }catch (Exception e){
            out.setError(true);
            out.setErrorMessage(e.getMessage());
        }

        if(!NumberUtils.isNumber(_result)){
            out.setError(true);
            out.setErrorMessage(_result);
        }  else{
            out.setObjectContent(_result);
        }
        

        Object cmd = extractCommand();
        ExpeditedAdverseEventInputCommand command = (ExpeditedAdverseEventInputCommand) cmd;
        command.setStudyOutOfSync(false);
        return out;
    }

    
    protected String getUserId(){
		WebContext webContext = getWebContext();
		SecurityContext context = (SecurityContext)webContext.getHttpServletRequest().getSession().getAttribute("ACEGI_SECURITY_CONTEXT");
		return SecurityUtils.getUserLoginName(context.getAuthentication());
	}

    // For RoutingAndReview - Report comments ends here.


    // TODO: there's got to be a library version of this somewhere
    protected String createQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue())
                    .append('&');
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    //--------------- functionality added for Labviewr integration -------------------------
    public void dismissLab(int labId) {
        LabLoad labLoad = labLoadDao.getById(labId);
        if (labLoad != null) {
            labLoad.setDismissed(Boolean.TRUE);
            labLoadDao.save(labLoad);
        }
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
    public void setAssignmentDao(StudyParticipantAssignmentDao assignmentDao){
    	this.assignmentDao = assignmentDao;
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
    public void setPreExistingConditionDao(PreExistingConditionDao preExistingConditionDao) {
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
    
    public void setReportDefinitionDao(ReportDefinitionDao reportDefinitionDao){
    	this.reportDefinitionDao = reportDefinitionDao;
    }
    
    public ReportDefinitionDao getReportDefinitionDao(){
    	return reportDefinitionDao;
    }

    public void setLabLoadDao(LabLoadDao labLoadDao) {
        this.labLoadDao = labLoadDao;
    }

    public AdverseEventRoutingAndReviewRepository getAdverseEventRoutingAndReviewRepository() {
		return adverseEventRoutingAndReviewRepository;
    }

    public void setAdverseEventRoutingAndReviewRepository(
			AdverseEventRoutingAndReviewRepository adverseEventRoutingAndReviewRepository) {
		this.adverseEventRoutingAndReviewRepository = adverseEventRoutingAndReviewRepository;
    }

    @Required
    public void setStudySearchableAjaxableDomainObjectRepository(StudySearchableAjaxableDomainObjectRepository studyAjaxableDomainObjectRepository) {
        this.studySearchableAjaxableDomainObjectRepository = studyAjaxableDomainObjectRepository;
    }

    @Required
    public void setParticipantAjaxableDomainObjectRepository(ParticipantAjaxableDomainObjectRepository participantAjaxableDomainObjectRepository) {
        this.participantAjaxableDomainObjectRepository = participantAjaxableDomainObjectRepository;
    }

    public ConditionDao getConditionDao() {
        return conditionDao;
    }
    
    @Required
    public void setConditionDao(ConditionDao conditionDao) {
        this.conditionDao = conditionDao;
    }

    
    @Required
    public void setResearchStaffDao(ResearchStaffDao researchStaffDao){
    	this.researchStaffDao = researchStaffDao;
    }
    
    @Required
    public void setInvestigatorDao(InvestigatorDao investigatorDao){
    	this.investigatorDao = investigatorDao;
    }
    
    @Required
    public void setReportDao(ReportDao reportDao){
    	this.reportDao = reportDao;
    }
    
    @Required
    public OtherInterventionDao getOtherInterventionDao() {
        return otherInterventionDao;
    }

    public void setOtherInterventionDao(OtherInterventionDao otherInterventionDao) {
        this.otherInterventionDao = otherInterventionDao;
    }
    
    @Required
    public StudyDeviceDao getStudyDeviceDao() {
        return studyDeviceDao;
    }

    public void setStudyDeviceDao(StudyDeviceDao studyDeviceDao) {
        this.studyDeviceDao = studyDeviceDao;
    }

    @Required
    public void setProxyWebServiceFacade(AdeersIntegrationFacade proxyWebServiceFacade) {
        this.proxyWebServiceFacade = proxyWebServiceFacade;
    }

    @Required
    public void setSiteInvestigatorDao(SiteInvestigatorDao siteInvestigatorDao) {
        this.siteInvestigatorDao = siteInvestigatorDao;
    }

    @Required
    public void setSiteResearchStaffDao(SiteResearchStaffDao siteResearchStaffDao) {
        this.siteResearchStaffDao = siteResearchStaffDao;
    }

    public ReportVersionRepository getReportVersionRepository() {
        return reportVersionRepository;
    }

    public void setReportVersionRepository(ReportVersionRepository reportVersionRepository) {
        this.reportVersionRepository = reportVersionRepository;
    }
}
