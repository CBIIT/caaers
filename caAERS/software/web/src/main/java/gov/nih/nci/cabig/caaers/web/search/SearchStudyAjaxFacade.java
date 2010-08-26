package gov.nih.nci.cabig.caaers.web.search;

import gov.nih.nci.cabig.caaers.dao.*;
import gov.nih.nci.cabig.caaers.dao.query.InvestigatorQuery;
import gov.nih.nci.cabig.caaers.dao.query.OrganizationQuery;
import gov.nih.nci.cabig.caaers.dao.query.SiteResearchStaffQuery;
import gov.nih.nci.cabig.caaers.dao.query.ajax.ParticipantAjaxableDomainObjectQuery;
import gov.nih.nci.cabig.caaers.dao.query.ajax.StudySearchableAjaxableDomainObjectQuery;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.ajax.ParticipantAjaxableDomainObject;
import gov.nih.nci.cabig.caaers.domain.ajax.StudySearchableAjaxableDomainObject;
import gov.nih.nci.cabig.caaers.domain.repository.*;
import gov.nih.nci.cabig.caaers.domain.repository.ajax.ParticipantAjaxableDomainObjectRepository;
import gov.nih.nci.cabig.caaers.domain.repository.ajax.StudySearchableAjaxableDomainObjectRepository;
import gov.nih.nci.cabig.caaers.tools.ObjectTools;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.web.AbstractAjaxFacade;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.bean.Export;
import org.extremecomponents.table.bean.Row;
import org.extremecomponents.table.bean.Table;
import org.extremecomponents.table.context.Context;
import org.extremecomponents.table.context.HttpServletRequestContext;
import org.extremecomponents.table.core.TableConstants;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.core.TableModelImpl;
import org.extremecomponents.table.view.CsvView;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

import static gov.nih.nci.cabig.caaers.domain.DateValue.stringToDateValue;

public class SearchStudyAjaxFacade extends AbstractAjaxFacade {
    private static final Log log = LogFactory.getLog(SearchStudyAjaxFacade.class);

    private StudyRepository studyRepository;
    private StudyDao studyDao;
    private StudySearchableAjaxableDomainObjectRepository studySearchableAjaxableDomainObjectRepository;
    private ParticipantAjaxableDomainObjectRepository participantAjaxableDomainObjectRepository;
    private ParticipantDao participantDao;
    private OrganizationDao organizationDao;
    private OrganizationRepository organizationRepository;
    private InvestigatorDao investigatorDao;
    private InvestigatorRepository investigatorRepository; 
    private ResearchStaffDao researchStaffDao;
    private ResearchStaffRepository researchStaffRepository;
    private AdverseEventDao adverseEventDao;
    private ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao;
    private InvestigationalNewDrugDao investigationalNewDrugDao;
    private AgentRepository agentRepository;

    private static Class<?>[] CONTROLLERS = {};

    public SearchStudyAjaxFacade() {
    }

    public SearchStudyAjaxFacade(final StudyDao studyDao,
                                 final ParticipantDao participantDoa,
                                 final AdverseEventDao adverseEventDao,
                                 final ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao,
                                 final OrganizationDao organizationDao) {
        this.studyDao = studyDao;
        participantDao = participantDoa;
        this.adverseEventDao = adverseEventDao;
        this.expeditedAdverseEventReportDao = expeditedAdverseEventReportDao;
        this.organizationDao = organizationDao;
    }

    public Object build(final TableModel model, final Collection<StudySearchableAjaxableDomainObject> studies) throws Exception {
        Table table = model.getTableInstance();
        table.setTableId("ajaxTable");
        table.setForm("assembler");
        table.setItems(studies);
        table.setVar("study");
        table.setAction(model.getContext().getContextPath() + "/pages/search/study");
        table.setTitle("");
        table.setShowPagination(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        table.setOnInvokeAction("buildTable('assembler')");
        table.setImagePath(model.getContext().getContextPath() + "/images/table/*.gif");
        //only support filtering & sorting in local authentication mode. 
        table.setFilterable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        table.setSortable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        if(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal()){
        	table.setRowsDisplayed(100);
        }
        table.setSortRowsCallback("gov.nih.nci.cabig.caaers.web.table.SortRowsCallbackImpl");

        
        model.addTable(table);

        Export export = model.getExportInstance();
        export.setView(TableConstants.VIEW_CSV);
        export.setViewResolver(TableConstants.VIEW_CSV);
        export.setImageName(TableConstants.VIEW_CSV);
        export.setText(TableConstants.VIEW_CSV);
        export.addAttribute(CsvView.DELIMITER, "|");
        export.setFileName("caaers_studies.txt");
        model.addExport(export);

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);

        Column columnPrimaryIdentifier = model.getColumnInstance();
        columnPrimaryIdentifier.setSortable(false);
        columnPrimaryIdentifier.setProperty("primaryIdentifierValue");
        columnPrimaryIdentifier.setTitle("Primary ID");
        columnPrimaryIdentifier.setCell("gov.nih.nci.cabig.caaers.web.study.StudyLinkDisplayCell");
        model.addColumn(columnPrimaryIdentifier);

        Column columnShortTitle = model.getColumnInstance();
        columnShortTitle.setProperty("shortTitle");
        model.addColumn(columnShortTitle);

        Column columnSponsorCode = model.getColumnInstance();
        columnSponsorCode.setTitle("Sponsor");
        columnSponsorCode.setProperty("primarySponsorCode");
        model.addColumn(columnSponsorCode);

        Column columnPhaseCode = model.getColumnInstance();
        columnPhaseCode.setTitle("Phase");
        columnPhaseCode.setProperty("phaseCode");
        model.addColumn(columnPhaseCode);

        Column columnStatusCode = model.getColumnInstance();
        columnStatusCode.setProperty("status");
        model.addColumn(columnStatusCode);

        return model.assemble();
    }

    public Object buildInvestigator(final TableModel model, final List<Investigator> investigators) throws Exception {
        Table table = model.getTableInstance();
        table.setTableId("ajaxTable");
        table.setForm("assembler");
        table.setItems(investigators);
        table.setAction(model.getContext().getContextPath() + "/pages/admin/editInvestigator");
        table.setTitle("");
        table.setShowPagination(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        table.setOnInvokeAction("buildTable('assembler')");
        table.setImagePath(model.getContext().getContextPath() + "/images/table/*.gif");
        //only support filtering & sorting in local authentication mode. 
        table.setFilterable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        table.setSortable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        if(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal()){
        	table.setRowsDisplayed(100);
        }
        table.setSortRowsCallback("gov.nih.nci.cabig.caaers.web.table.SortRowsCallbackImpl");

        model.addTable(table);

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);

        Column columnFirstName = model.getColumnInstance();
        columnFirstName.setProperty("firstName");
        columnFirstName.setTitle("First name");
        columnFirstName.setCell("gov.nih.nci.cabig.caaers.web.search.InvestigatorLinkDisplayCell");
        model.addColumn(columnFirstName);
        
        Column columnMiddleName = model.getColumnInstance();
        columnMiddleName.setProperty("middleName");
        columnMiddleName.setTitle("Middle name");
        model.addColumn(columnMiddleName);

        Column columnLastName = model.getColumnInstance();
        columnLastName.setProperty("lastName");
        columnLastName.setTitle("Last name");
        model.addColumn(columnLastName);

        Column columnNciInstituteCode = model.getColumnInstance();
        columnNciInstituteCode.setProperty("nciIdentifier");
        columnNciInstituteCode.setTitle("Investigator number");
        model.addColumn(columnNciInstituteCode);
        
        Column organizations = model.getColumnInstance();
        organizations.setTitle("Organization(s)");
        organizations.setProperty("dummyOrg");
        organizations.setSortable(false);
        organizations.setCell("gov.nih.nci.cabig.caaers.web.search.InvestigatorSiteDisplayCell");
        model.addColumn(organizations);
        
        Column columnStatus = model.getColumnInstance();
        columnStatus.setTitle("Status");
        columnStatus.setProperty("dummyStatus");
        columnStatus.setSortable(false);
        model.addColumn(columnStatus);
        columnStatus.setCell("gov.nih.nci.cabig.caaers.web.search.InvestigatorStatusDisplayCell");

        return model.assemble();
    }

    public Object buildResearchStaff(final TableModel model, final List<SiteResearchStaff> siteResearchStaffs) throws Exception {
    	
    	Map<Integer,ResearchStaff> rsMap = new HashMap<Integer,ResearchStaff>();
    	if(siteResearchStaffs != null && siteResearchStaffs.size() > 0){
    		for(SiteResearchStaff srs : siteResearchStaffs){
    			if(! rsMap.containsKey(srs.getResearchStaff().getId())){
    				rsMap.put(srs.getResearchStaff().getId(), srs.getResearchStaff());
    			}
    		}
    	}
    	List<ResearchStaff> rsList = new ArrayList<ResearchStaff>(rsMap.values());
    	
        Table table = model.getTableInstance();
        table.setTableId("ajaxTable");
        table.setForm("assembler");
        table.setItems(rsList);
        table.setAction(model.getContext().getContextPath() + "/pages/admin/editResearchStaff");
        table.setTitle("");
        table.setShowPagination(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        table.setOnInvokeAction("buildTable('assembler')");
        table.setImagePath(model.getContext().getContextPath() + "/images/table/*.gif");
        //only support filtering & sorting in local authentication mode. 
        table.setFilterable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        table.setSortable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        if(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal()){
        	table.setRowsDisplayed(100);
        }
        table.setSortRowsCallback("gov.nih.nci.cabig.caaers.web.table.SortRowsCallbackImpl");

        model.addTable(table);

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);

        Column columnFirstName = model.getColumnInstance();
        columnFirstName.setProperty("firstName");
        columnFirstName.setTitle("First name");
        columnFirstName.setCell("gov.nih.nci.cabig.caaers.web.search.ResearchStaffLinkDisplayCell");
        model.addColumn(columnFirstName);

        Column columnMiddleName = model.getColumnInstance();
        columnMiddleName.setProperty("middleName");
        columnMiddleName.setTitle("Middle name");
        model.addColumn(columnMiddleName);

        Column columnLastName = model.getColumnInstance();
        columnLastName.setProperty("lastName");
        columnLastName.setTitle("Last name");
        model.addColumn(columnLastName);

        Column organizations = model.getColumnInstance();
        organizations.setTitle("Organization(s)");
        organizations.setProperty("dummyOrg");
        organizations.setSortable(false);
        organizations.setCell("gov.nih.nci.cabig.caaers.web.search.ResearchStaffSiteDisplayCell");
        model.addColumn(organizations);

        Column columnStatus = model.getColumnInstance();
        columnStatus.setTitle("Status");
        columnStatus.setProperty("dummyStatus");
        columnStatus.setSortable(false);
        columnStatus.setCell("gov.nih.nci.cabig.caaers.web.search.ResearchStaffStatusDisplayCell");
        model.addColumn(columnStatus);
        
        return model.assemble();
    }

    public Object buildParticipant(final TableModel model, final Collection participants) throws Exception {
        Table table = model.getTableInstance();
        table.setTableId("ajaxTable");
        table.setForm("assembler");
        table.setItems(participants);
        table.setAction(model.getContext().getContextPath() + "/pages/search/participant");
        table.setTitle("");
        table.setShowPagination(true);
        table.setOnInvokeAction("buildTable('assembler')");
        table.setImagePath(model.getContext().getContextPath() + "/images/table/*.gif");
        table.setFilterable(true);
        table.setSortable(true);
        
        table.setSortRowsCallback("gov.nih.nci.cabig.caaers.web.table.SortRowsCallbackImpl");

        model.addTable(table);

        Export export = model.getExportInstance();
        export.setView(TableConstants.VIEW_CSV);
        export.setViewResolver(TableConstants.VIEW_CSV);
        export.setImageName(TableConstants.VIEW_CSV);
        export.setText(TableConstants.VIEW_CSV);
        export.addAttribute(CsvView.DELIMITER, "|");
        export.setFileName("caaers_participants.txt");
        model.addExport(export);

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);

        Column columnPrimaryIdentifier = model.getColumnInstance();
        columnPrimaryIdentifier.setSortable(false);
        columnPrimaryIdentifier.setProperty("primaryIdentifierValue");
        columnPrimaryIdentifier.setTitle("Primary ID");
        columnPrimaryIdentifier.setCell("gov.nih.nci.cabig.caaers.web.search.ParticipantLinkDisplayCell");
        model.addColumn(columnPrimaryIdentifier);

        Column columnFirstName = model.getColumnInstance();
        columnFirstName.setProperty("firstName");
        model.addColumn(columnFirstName);

        Column columnLastName = model.getColumnInstance();
        columnLastName.setProperty("lastName");
        model.addColumn(columnLastName);

        Column colummGender = model.getColumnInstance();
        colummGender.setProperty("gender");
        model.addColumn(colummGender);

        Column colummRace = model.getColumnInstance();
        colummRace.setProperty("race");
        model.addColumn(colummRace);

        Column colummEthnicity = model.getColumnInstance();
        colummEthnicity.setProperty("ethnicity");
        model.addColumn(colummEthnicity);

        Column columnStudyPrimaryIdentifier = model.getColumnInstance();
        //columnStudyPrimaryIdentifier.setProperty("test");
        //columnStudyPrimaryIdentifier.setSortable(false);
        columnStudyPrimaryIdentifier.setTitle("Associated Study ID(s)");
        columnStudyPrimaryIdentifier.setCell("gov.nih.nci.cabig.caaers.web.search.ParticipantStudyLinkDisplayCell");
        model.addColumn(columnStudyPrimaryIdentifier);

        return model.assemble();
    }

    public Object buildAdverseEvent(final TableModel model, final Collection adverseEvents) throws Exception {
        Table table = model.getTableInstance();
        table.setTableId("ajaxTable");
        table.setForm("assembler");
        table.setItems(adverseEvents);
        table.setAction(model.getContext().getContextPath() + "/pages/search/adverseEvent");
        table.setTitle("");
        table.setShowExports(true);
        table.setOnInvokeAction("buildTable('searchForm')");
        table.setImagePath(model.getContext().getContextPath() + "/images/table/*.gif");
        //only support filtering & sorting in local authentication mode. 
        table.setFilterable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        table.setSortable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        if(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal()){
        	table.setRowsDisplayed(100);
        }
        table.setSortRowsCallback("gov.nih.nci.cabig.caaers.web.table.SortRowsCallbackImpl");

        model.addTable(table);

        Export export = model.getExportInstance();
        export.setView(TableConstants.VIEW_CSV);
        export.setViewResolver(TableConstants.VIEW_CSV);
        export.setImageName(TableConstants.VIEW_CSV);
        export.setText(TableConstants.VIEW_CSV);
        export.addAttribute(CsvView.DELIMITER, "|");
        export.setFileName("caaers_aes.txt");
        model.addExport(export);

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);

        Column columnstudyIdentifier = model.getColumnInstance();
        columnstudyIdentifier.setProperty("test");
        columnstudyIdentifier.setTitle("Study ID");
        columnstudyIdentifier.setSortable(false);
        columnstudyIdentifier.setCell("gov.nih.nci.cabig.caaers.web.search.StudyLinkDisplayCell");
        model.addColumn(columnstudyIdentifier);

        Column columnSponsorId = model.getColumnInstance();
        columnSponsorId.setProperty("test");
        columnSponsorId.setTitle("Sponsor");
        columnSponsorId.setSortable(false);
        columnSponsorId.setCell("gov.nih.nci.cabig.caaers.web.search.SponsorLinkDisplayCell");
        model.addColumn(columnSponsorId);

        Column columnAeType = model.getColumnInstance();
        columnAeType.setProperty("test");
        columnAeType.setTitle("AE Type");
        columnAeType.setStyle("width:8px");
        columnAeType.setSortable(false);
        columnAeType.setCell("gov.nih.nci.cabig.caaers.web.search.AeTypeDisplayCell");
        model.addColumn(columnAeType);

        Column columnCtcCategory = model.getColumnInstance();
        columnCtcCategory.setTitle("CTC Category");
        columnCtcCategory.setAlias("category");
        columnCtcCategory.setProperty("ctcTerm.category.name");
        model.addColumn(columnCtcCategory);

        Column columnCtcTerm = model.getColumnInstance();
        columnCtcTerm.setTitle("CTC Term");
        columnCtcTerm.setAlias("ctcTerm");
        columnCtcTerm.setProperty("ctcTerm.term");
        model.addColumn(columnCtcTerm);

        Column columnGrade = model.getColumnInstance();
        columnGrade.setTitle("Grade");
        columnGrade.setStyle("width:6px");
        columnGrade.setAlias("grade");
        columnGrade.setProperty("grade.code");
        model.addColumn(columnGrade);

        Column medDRACode = model.getColumnInstance();
        medDRACode.setProperty("ctcTerm.ctepCode");
        medDRACode.setAlias("ctepCode");
        medDRACode.setTitle("MedDRA Code");
        model.addColumn(medDRACode);

        Column aeStartDate = model.getColumnInstance();
        aeStartDate.setProperty("test");
        aeStartDate.setSortable(false);
        aeStartDate.setCell("gov.nih.nci.cabig.caaers.web.search.AeDetectionDateDisplayCell");
        aeStartDate.setTitle("Start Date");
        model.addColumn(aeStartDate);

        return model.assemble();
    }

    public Object buildExpeditedReport(final TableModel model, final Collection expeditedReports) throws Exception {
        Table table = model.getTableInstance();
        table.setTableId("ajaxTable");
        table.setForm("assembler");
        table.setItems(expeditedReports);
        table.setAction(model.getContext().getContextPath() + "/pages/search/report");
        table.setTitle("");
        table.setOnInvokeAction("buildTable('assembler')");
        table.setImagePath(model.getContext().getContextPath() + "/images/table/*.gif");
        //only support filtering & sorting in local authentication mode. 
        table.setFilterable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        table.setSortable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        if(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal()){
        	table.setRowsDisplayed(100);
        }
        table.setSortRowsCallback("gov.nih.nci.cabig.caaers.web.table.SortRowsCallbackImpl");

        table.setShowPagination(true);
        model.addTable(table);

        Export export = model.getExportInstance();
        export.setView(TableConstants.VIEW_CSV);
        export.setViewResolver(TableConstants.VIEW_CSV);
        export.setImageName(TableConstants.VIEW_CSV);
        export.setText(TableConstants.VIEW_CSV);
        export.addAttribute(CsvView.DELIMITER, "|");
        export.setFileName("caaers_expedited_reports.txt");
        model.addExport(export);

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);

        Column columnPrimaryAeTerm = model.getColumnInstance();
        columnPrimaryAeTerm.setProperty("adverseEvents[0].ctcTerm.term");
        columnPrimaryAeTerm.setTitle("Primary CTC term");
        columnPrimaryAeTerm.setAlias("term");
        model.addColumn(columnPrimaryAeTerm);

        Column ctcGrade = model.getColumnInstance();
        ctcGrade.setProperty("adverseEvents[0].grade.code");
        ctcGrade.setAlias("grade");
        ctcGrade.setTitle("Grade");
        model.addColumn(ctcGrade);

        Column attributionCode = model.getColumnInstance();
        attributionCode.setProperty("adverseEvents[0].attributionSummary.displayName");
        attributionCode.setTitle("Attribution");
        attributionCode.setAlias("attribution");
        model.addColumn(attributionCode);

        Column aeDetectionDate = model.getColumnInstance();
        aeDetectionDate.setProperty("adverseEvents[0].startDate");
        aeDetectionDate.setTitle("Start Date");
        aeDetectionDate.setCell(TableConstants.DATE);
        aeDetectionDate.setFormat("MM/dd/yyyy");
        model.addColumn(aeDetectionDate);

        Column columnstudyIdentifier = model.getColumnInstance();
        columnstudyIdentifier.setProperty("study.primaryIdentifier.value");
        //columnstudyIdentifier.setSortable(false);
        columnstudyIdentifier.setTitle("Study ID");
        columnstudyIdentifier.setCell("gov.nih.nci.cabig.caaers.web.search.StudyLinkDisplayCellExpedited");
        model.addColumn(columnstudyIdentifier);

        Column columnParticipantIdentifier = model.getColumnInstance();
        //columnParticipantIdentifier.setSortable(false);
        columnParticipantIdentifier.setProperty("participant.primaryIdentifier.value");
        columnParticipantIdentifier.setTitle("Participant ID");
        columnParticipantIdentifier.setCell("gov.nih.nci.cabig.caaers.web.search.ParticipantLinkDisplayCellExpedited");
        model.addColumn(columnParticipantIdentifier);

        /*
           * Column columnPrimaryIdentifier = model.getColumnInstance(); columnPrimaryIdentifier.setProperty("status.displayName");
           * columnPrimaryIdentifier.setTitle("Status");
           * //columnPrimaryIdentifier.setCell("gov.nih.nci.cabig.caaers.web.search.ParticipantLinkDisplayCell");
           * model.addColumn(columnPrimaryIdentifier);
           */
        return model.assemble();
    }

    public Object buildRoutineReport(final TableModel model, final Collection expeditedReports) throws Exception {
        Table table = model.getTableInstance();
        table.setTableId("ajaxTable");
        table.setForm("assembler");
        table.setItems(expeditedReports);
        table.setAction(model.getContext().getContextPath() + "/pages/search/report");
        table.setTitle("");
        table.setOnInvokeAction("buildTable('assembler')");
        table.setImagePath(model.getContext().getContextPath() + "/images/table/*.gif");
        table.setFilterable(true);
        table.setShowPagination(true);
        table.setSortable(true);
        table.setSortRowsCallback("gov.nih.nci.cabig.caaers.web.table.SortRowsCallbackImpl");

        model.addTable(table);

        Export export = model.getExportInstance();
        export.setView(TableConstants.VIEW_CSV);
        export.setViewResolver(TableConstants.VIEW_CSV);
        export.setImageName(TableConstants.VIEW_CSV);
        export.setText(TableConstants.VIEW_CSV);
        export.addAttribute(CsvView.DELIMITER, "|");
        export.setFileName("caaers_routine_reports.txt");
        model.addExport(export);

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);

        /*
           * Column columnPrimaryIdentifier = model.getColumnInstance(); columnPrimaryIdentifier.setProperty("primaryIdentifier");
           * columnPrimaryIdentifier.setCell("gov.nih.nci.cabig.caaers.web.study.StudyLinkDisplayCell");
           * model.addColumn(columnPrimaryIdentifier);
           */

        Column columnPrimaryAeTerm = model.getColumnInstance();
        columnPrimaryAeTerm.setProperty("adverseEvents[0].ctcTerm.term");
        columnPrimaryAeTerm.setTitle("Primary Ctc term");
        columnPrimaryAeTerm.setAlias("term");
        // columnPrimaryIdentifier.setCell("gov.nih.nci.cabig.caaers.web.search.ParticipantLinkDisplayCell");
        model.addColumn(columnPrimaryAeTerm);
        /*
           * Column medDRACode = model.getColumnInstance(); medDRACode.setProperty("ctcTerm.ctepCode"); medDRACode.setTitle("MedDRA Code");
           * model.addColumn(medDRACode);
           */

        Column ctcGrade = model.getColumnInstance();
        ctcGrade.setProperty("adverseEvents[0].grade.code");
        ctcGrade.setTitle("Grade");
        ctcGrade.setAlias("grade");
        model.addColumn(ctcGrade);

        Column attributionCode = model.getColumnInstance();
        attributionCode.setProperty("adverseEvents[0].attributionSummary.displayName");
        attributionCode.setTitle("Attribution");
        attributionCode.setAlias("attribution");
        model.addColumn(attributionCode);

        Column columnObservationDate = model.getColumnInstance();
        columnObservationDate.setProperty("adverseEvents[0].ctcTerm.term");
        columnObservationDate.setSortable(false);
        columnObservationDate.setTitle("Observation Dates");
        columnObservationDate.setCell("gov.nih.nci.cabig.caaers.web.search.ObservationDateDisplayCell");
        model.addColumn(columnObservationDate);

        Column columnstudyIdentifier = model.getColumnInstance();
        columnstudyIdentifier.setSortable(false);
        columnstudyIdentifier.setProperty("study.primaryIdentifier.value");
        columnstudyIdentifier.setTitle("Study ID");
        columnstudyIdentifier.setCell("gov.nih.nci.cabig.caaers.web.search.StudyLinkDisplayCellRoutine");
        model.addColumn(columnstudyIdentifier);

        Column columnParticipantIdentifier = model.getColumnInstance();
        columnParticipantIdentifier.setSortable(false);
        columnParticipantIdentifier.setProperty("participant.primaryIdentifier.value");
        columnParticipantIdentifier.setTitle("Participant ID");
        columnParticipantIdentifier.setCell("gov.nih.nci.cabig.caaers.web.search.ParticipantLinkDisplayCellRoutine");
        model.addColumn(columnParticipantIdentifier);

        /*
           * Column aeDetectionDate = model.getColumnInstance(); aeDetectionDate.setProperty("detectionDate");
           * aeDetectionDate.setTitle("Detection Date"); aeDetectionDate.setCell(TableConstants.DATE);
           * aeDetectionDate.setFormat("MM/dd/yyyy"); model.addColumn(aeDetectionDate); Column columnPrimaryIdentifier =
           * model.getColumnInstance(); columnPrimaryIdentifier.setProperty("status.displayName"); columnPrimaryIdentifier.setTitle("Status");
           * //columnPrimaryIdentifier.setCell("gov.nih.nci.cabig.caaers.web.search.ParticipantLinkDisplayCell");
           * model.addColumn(columnPrimaryIdentifier);
           */
        return model.assemble();
    }

    /**
     * YUI result
     * 
     * */
    @SuppressWarnings("unchecked")
    public List<InvestigationalNewDrug> getINDTable(final Map parameterMap, final String type, final String text,final HttpServletRequest request) throws Exception {
        List<InvestigationalNewDrug> items = new ArrayList<InvestigationalNewDrug>();
        HashMap<String, String> map = new HashMap<String, String>();
        if (type != null && text != null) {
            String[] fields = type.split(",");
            String[] values = text.split(",");
            for (int i = 0; i < fields.length; i++) {
                map.put(fields[i], values[i]);
            }
            items = investigationalNewDrugDao.searchInvestigationalNewDrugs(map);
        }
        return ObjectTools.reduceAll(items, "indNumber", "holderName");
    }

    @SuppressWarnings("finally")
    private List<StudySearchableAjaxableDomainObject> constructExecuteStudyQuery(final String type, final String text) throws ParseException {
        StringTokenizer typeToken = new StringTokenizer(type, ",");
        StringTokenizer textToken = new StringTokenizer(text, ",");
        log.debug("type :: " + type);
        log.debug("text :: " + text);
        String sType, sText;
        Map<String, String> propValue = new HashMap<String, String>();

        StudySearchableAjaxableDomainObjectQuery query = new StudySearchableAjaxableDomainObjectQuery();
        // map the html properties to the model properties
        Map<String, String> m = new HashMap<String, String>();
        m.put("prop0", "studyIdentifier");
        m.put("prop1", "studyShortTitle");
        m.put("prop2", "participantIdentifier");
        m.put("prop3", "participantFirstName");
        m.put("prop4", "participantLastName");
        m.put("prop5", "participantEthnicity");
        m.put("prop6", "participantGender");
        m.put("prop7", "participantDateOfBirth");

        while (typeToken.hasMoreTokens() && textToken.hasMoreTokens()) {
            sType = typeToken.nextToken();
            sText = textToken.nextToken();
            // Create a map of property key ,and search criteria
            propValue.put(m.get(sType), sText);


        }


        query.filterStudiesWithMatchingIdentifierOnly(propValue.get("studyIdentifier"));

        query.filterStudiesWithMatchingShortTitleOnly(propValue.get("studyShortTitle"));
        query.filterByParticipant(propValue.get("participantFirstName"), propValue.get("participantLastName"),
                propValue.get("participantEthnicity"), propValue.get("participantIdentifier"), propValue.get("participantGender"),
                stringToDateValue(propValue.get("participantDateOfBirth")));

        List<StudySearchableAjaxableDomainObject> studySearchableAjaxableDomainObjects = studySearchableAjaxableDomainObjectRepository.findStudies(query);
        return studySearchableAjaxableDomainObjects;


    }

    @SuppressWarnings("finally")
    private List<ParticipantAjaxableDomainObject> constructExecuteParticipantQuery(final String type, final String text) {

        StringTokenizer typeToken = new StringTokenizer(type, ",");
        StringTokenizer textToken = new StringTokenizer(text, ",");
        log.debug("type :: " + type);
        log.debug("text :: " + text);
        String sType, sText;
        Map<String, String> propValue = new HashMap<String, String>();
        
        ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
        List<ParticipantAjaxableDomainObject> participantAjaxableDomainObjects = new ArrayList<ParticipantAjaxableDomainObject>();

        // map the html properties to the model properties
        Map<String, String> m = new HashMap<String, String>();
        m.put("prop0", "studyIdentifier");
        m.put("prop1", "studyShortTitle");
        m.put("prop2", "participantIdentifier");
        m.put("prop3", "participantFirstName");
        m.put("prop4", "participantLastName");
        m.put("prop5", "participantEthnicity");
        m.put("prop6", "participantGender");
        m.put("prop7", "participantDateOfBirth");

        while (typeToken.hasMoreTokens() && textToken.hasMoreTokens()) {
            sType = typeToken.nextToken();
            sText = textToken.nextToken();
            // Create a map of property key ,and search criteria
            propValue.put(m.get(sType), sText);
        }
        
        try {
        	query.filterByPrimaryIdentifiers();
			query.filterParticipants(propValue);
//			query.filterByStudyIdentifierValue(propValue.get("studyIdentifier"));
//			query.filterByStudyShortTitle(propValue.get("studyShortTitle"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Query Parsing Error : constructExecuteParticipantQuery", e);
		}
 
		participantAjaxableDomainObjects = participantAjaxableDomainObjectRepository.findParticipants(query);
        return participantAjaxableDomainObjects;
        /*
        try {
            participants = participantDao.searchParticipant(propValue);
        }
        catch (Exception e) {
            throw new RuntimeException("Formatting Error", e);
        }
        finally {
            return participants;
        }
        */
    }

    @SuppressWarnings("finally")
    private List<Organization> constructExecuteOrganizationQuery(final String type, final String text) {

        StringTokenizer typeToken = new StringTokenizer(type, ",");
        StringTokenizer textToken = new StringTokenizer(text, ",");
        log.debug("type :: " + type);
        log.debug("text :: " + text);
        String sType, sText;
        List<Organization> organizations = new ArrayList<Organization>();

        OrganizationQuery organizationQuery = new OrganizationQuery();
        

        while (typeToken.hasMoreTokens() && textToken.hasMoreTokens()) {
            sType = typeToken.nextToken();
            sText = textToken.nextToken();
            if (sType.equals("name")) {
                organizationQuery.filterByOrganizationName(sText);
            }
            if (sType.equals("nciInstituteCode")) {
                organizationQuery.filterByNciInstituteCode(sText);
            }

        }

        try {
            organizations = organizationRepository.searchOrganization(organizationQuery);
        }
        catch (Exception e) {
            throw new RuntimeException("Formatting Error", e);
        }
        finally {
            return organizations;
        }
    }

    @SuppressWarnings("finally")
    private List<Investigator> constructExecuteInvestigatorQuery(final String type, final String text) {

        StringTokenizer typeToken = new StringTokenizer(type, ",");
        StringTokenizer textToken = new StringTokenizer(text, ",");
        log.debug("type :: " + type);
        log.debug("text :: " + text);
        String sType, sText;
        List<Investigator> investigators = new ArrayList<Investigator>();

        InvestigatorQuery investigatorQuery = new InvestigatorQuery();

        while (typeToken.hasMoreTokens() && textToken.hasMoreTokens()) {
            sType = typeToken.nextToken();
            sText = textToken.nextToken();
            if (sType.equals("firstName")) {
                investigatorQuery.filterByFirstName(sText);
            } else if (sType.equals("nciIdentifier")) {
                investigatorQuery.filterByNciIdentifier(sText);
            } else if (sType.equals("lastName")) {
                investigatorQuery.filterByLastName(sText);
            } else if (sType.equals("organization")) {
            	investigatorQuery.filterByOrganization(sText);
            }
        }

        try {
            investigators = investigatorRepository.searchInvestigator(investigatorQuery,type,text);
        }
        catch (Exception e) {
            throw new RuntimeException("Formatting Error", e);
        }
        finally {
            return investigators;
        }
    }

    @SuppressWarnings("finally")
    private List<SiteResearchStaff> constructExecuteSiteResearchStaffQuery(final String type, final String text) {

        StringTokenizer typeToken = new StringTokenizer(type, ",");
        StringTokenizer textToken = new StringTokenizer(text, ",");
        log.debug("type :: " + type);
        log.debug("text :: " + text);
        String sType, sText;
        List<SiteResearchStaff> siteResearchStaffs = new ArrayList<SiteResearchStaff>();

        SiteResearchStaffQuery query = new SiteResearchStaffQuery();

        while (typeToken.hasMoreTokens() && textToken.hasMoreTokens()) {
            sType = typeToken.nextToken();
            sText = textToken.nextToken();
            if (sType.equals("firstName")) {
                query.filterByFirstName(sText);
            } else if (sType.equals("lastName")) {
                query.filterByLastName(sText);
            } else if (sType.equals("organization")) {
            	query.filterByOrganization(sText);
            }
        }

        try {
            siteResearchStaffs = researchStaffRepository.getSiteResearchStaff(query,type,text);
        }
        catch (Exception e) {
            throw new RuntimeException("Formatting Error", e);
        }
        finally {
            return siteResearchStaffs;
        }
    }

    @SuppressWarnings("finally")
    private List<AdverseEvent> constructExecuteAdverseEventQuery(final String type, final String text) {

        StringTokenizer typeToken = new StringTokenizer(type, ",");
        StringTokenizer textToken = new StringTokenizer(text, ",");
        log.debug("type :: " + type);
        log.debug("text :: " + text);
        String sType, sText;
        Map<String, String> propValue = new HashMap<String, String>();
        List<AdverseEvent> adverseEvents = new ArrayList<AdverseEvent>();

        // map the html properties to the model properties
        Map<String, String> m = new HashMap<String, String>();
        m.put("prop0", "ctcCategory");
        m.put("prop1", "ctcTerm");
        m.put("prop2", "ctcMeddra");
        m.put("prop3", "grade");
        m.put("prop4", "studyIdentifier");
        m.put("prop5", "studyShortTitle");
        m.put("prop6", "participantIdentifier");
        m.put("prop7", "participantFirstName");
        m.put("prop8", "participantLastName");
        m.put("prop9", "participantEthnicity");
        m.put("prop10", "participantGender");
        m.put("prop11", "participantDateOfBirth");

        while (typeToken.hasMoreTokens() && textToken.hasMoreTokens()) {
            sType = typeToken.nextToken();
            sText = textToken.nextToken();
            // Create a map of property key ,and search criteria
            propValue.put(m.get(sType), sText);
        }

        try {
            adverseEvents = adverseEventDao.searchAdverseEvents(propValue);
        }
        catch (Exception e) {
            throw new RuntimeException("Formatting Error", e);
        }
        finally {
            return adverseEvents;
        }
    }

    @SuppressWarnings("finally")
    private List<ExpeditedAdverseEventReport> constructExecuteExpeditedReportQuery(final String type, final String text) {

        StringTokenizer typeToken = new StringTokenizer(type, ",");
        StringTokenizer textToken = new StringTokenizer(text, ",");
        log.debug("type :: " + type);
        log.debug("text :: " + text);
        String sType, sText;
        Map<String, String> propValue = new HashMap<String, String>();
        List<ExpeditedAdverseEventReport> expeditedReports = new ArrayList<ExpeditedAdverseEventReport>();

        // map the html properties to the model properties
        Map<String, String> m = new HashMap<String, String>();
        m.put("prop0", "expeditedDate");
        m.put("prop1", "ctcTerm");
        m.put("prop2", "ctcCategory");
        m.put("prop3", "ctcCtepCode");
        m.put("prop4", "studyIdentifier");
        m.put("prop5", "studyShortTitle");
        m.put("prop6", "participantIdentifier");
        m.put("prop7", "participantFirstName");
        m.put("prop8", "participantLastName");
        m.put("prop9", "participantEthnicity");
        m.put("prop10", "participantGender");
        m.put("prop11", "participantDateOfBirth");

        while (typeToken.hasMoreTokens() && textToken.hasMoreTokens()) {
            sType = typeToken.nextToken();
            sText = textToken.nextToken();
            // Create a map of property key ,and search criteria
            propValue.put(m.get(sType), sText);
        }

        try {
            expeditedReports = expeditedAdverseEventReportDao.searchExpeditedReports(propValue);
        }
        catch (Exception e) {
            throw new RuntimeException("Formatting Error", e);
        }
        finally {
            return expeditedReports;
        }

    }

   

    /*
      * Ajax Call hits this method to generate table
      */
    public String getTable(final Map parameterMap, final String type, final String text,
                           final HttpServletRequest request) {

        // Use this code to view the contents of parameterMap
        if (parameterMap != null) {

            for (Object key : parameterMap.keySet()) {
                log.debug(key.toString() + " -- " + parameterMap.get(key));
            }
        }

        try {
            List<StudySearchableAjaxableDomainObject> studySearchableAjaxableDomainObjects = new ArrayList<StudySearchableAjaxableDomainObject>();
            if (type != null && text != null) {
                studySearchableAjaxableDomainObjects = constructExecuteStudyQuery(type, text);
            }
            log.debug("Studies:?: " + studySearchableAjaxableDomainObjects.size());

            Context context = null;
            if (parameterMap == null) {
                context = new HttpServletRequestContext(request);
            } else {
                context = new HttpServletRequestContext(request, parameterMap);
            }

            TableModel model = new TableModelImpl(context);

            return build(model, studySearchableAjaxableDomainObjects).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /*
      * Ajax Call hits this method to generate table
      */
    public String getParticipantTable(final Map parameterMap, final String type, final String text, final HttpServletRequest request) {

        //List<Participant> participants = new ArrayList<Participant>();
    	List<ParticipantAjaxableDomainObject> participants= new ArrayList<ParticipantAjaxableDomainObject>();
        if (type != null && text != null) {
            participants = constructExecuteParticipantQuery(type, text);
        }
        log.debug("Participants :: " + participants.size());

        Context context = null;
        if (parameterMap == null) {
            context = new HttpServletRequestContext(request);
        } else {
            context = new HttpServletRequestContext(request, parameterMap);
        }

        TableModel model = new TableModelImpl(context);
        // LimitFactory limitFactory = new TableLimitFactory(context);
        // Limit limit = new TableLimit(limitFactory);
        // limit.setRowAttributes(totalRows, DEFAULT_ROWS_DISPLAYED);
        // model.setLimit(limit);

        try {
            return buildParticipant(model, participants).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     *
     * YUI 
     * 
     * */
    public List<Organization> getOrganizationTable(final Map parameterMap, final String type, final String text, final HttpServletRequest request) {
        WebContext webContext = WebContextFactory.get();
        List<Organization> organizations = new ArrayList<Organization>();
        if (type != null && text != null) {
            organizations = constructExecuteOrganizationQuery(type, text);
        }
        return ObjectTools.reduceAll(organizations, "id", "name", "nciInstituteCode");
    }

    public String getInvestigatorTable(final Map parameterMap, final String type, final String text, final HttpServletRequest request) {

        List<Investigator> investigators = new ArrayList<Investigator>();
        if (type != null && text != null) {
            investigators = constructExecuteInvestigatorQuery(type, text);
        }
        log.debug("Investigators :: " + investigators.size());

        Context context = null;
        if (parameterMap == null) {
            context = new HttpServletRequestContext(request);
        } else {
            context = new HttpServletRequestContext(request, parameterMap);
        }

        TableModel model = new TableModelImpl(context);
        
        try {
            return buildInvestigator(model, investigators).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public List<Agent> getAgentsTable(final Map parameterMap, final String text, final String nsc, final HttpServletRequest request) {
        List<Agent> agents = new ArrayList<Agent>();
        if (text != null) {
            agents = agentRepository.getAgentsBySubnames(new String[] {text, nsc});
        }
        return ObjectTools.reduceAll(agents, "id", "name", "nscNumber");
    }

    public String getResearchStaffTable(final Map parameterMap, final String type, final String text, final HttpServletRequest request) {
        
        List<SiteResearchStaff> siteResearchStaffs = new ArrayList<SiteResearchStaff>();
        if (type != null && text != null) {
            siteResearchStaffs = constructExecuteSiteResearchStaffQuery(type, text);
        }
        log.debug("ResearchStaffs :: " + siteResearchStaffs.size());

        Context context = null;
        if (parameterMap == null) {
            context = new HttpServletRequestContext(request);
        } else {
            context = new HttpServletRequestContext(request, parameterMap);
        }

        TableModel model = new TableModelImpl(context);
        try {
            return buildResearchStaff(model, siteResearchStaffs).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /*
      * Ajax Call hits this method to generate table
      */
    public String getAdverseEventTable(final Map parameterMap, final String type, final String text,
                                       final HttpServletRequest request) {

        List<AdverseEvent> adverseEvents = new ArrayList<AdverseEvent>();
        if (type != null && text != null) {
            adverseEvents = constructExecuteAdverseEventQuery(type, text);
        }
        log.debug("AdverseEvents :: " + adverseEvents.size());

        Context context = null;
        if (parameterMap == null) {
            context = new HttpServletRequestContext(request);
        } else {
            context = new HttpServletRequestContext(request, parameterMap);
        }

        TableModel model = new TableModelImpl(context);
        try {
            return buildAdverseEvent(model, adverseEvents).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /*
      * Ajax Call hits this method to generate table
      */
    public String getExpeditedReportTable(final Map parameterMap, final String type, final String text,
                                          final HttpServletRequest request) {
        List<ExpeditedAdverseEventReport> expeditedReports = new ArrayList<ExpeditedAdverseEventReport>();
        if (type != null && text != null) {
            expeditedReports = constructExecuteExpeditedReportQuery(type, text);
        }
        log.debug("AdverseEvents :: " + expeditedReports.size());

        Context context = null;
        if (parameterMap == null) {
            context = new HttpServletRequestContext(request);
        } else {
            context = new HttpServletRequestContext(request, parameterMap);
        }

        TableModel model = new TableModelImpl(context);
        try {
            return buildExpeditedReport(model, expeditedReports).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Builds a table
     *
     * @param items          - A list consisting of item that should be painted in each row
     * @param actionPath     - The path of the actions
     * @param exportFileName - The file to which the table data to be exported
     * @param cvObjects      - A ColumnValue Object, which has the details of the columns in the table.
     */
    @SuppressWarnings("unchecked")
    public Object buildTable(final TableModel model, final Collection items, final String actionPath,
                             final String exportFileName, final ColumnValueObject... cvObjects) throws Exception {
        Table table = model.getTableInstance();
        table.setTableId("ajaxTable");
        table.setForm("assembler");
        table.setItems(items);
        table.setAction(model.getContext().getContextPath() + actionPath);
        table.setTitle("");
        table.setShowPagination(false);
        table.setOnInvokeAction("buildTable('assembler')");
        table.setImagePath(model.getContext().getContextPath() + "/images/table/*.gif");
        table.setFilterable(true);
        table.setSortRowsCallback("gov.nih.nci.cabig.caaers.web.table.SortRowsCallbackImpl");

        table.setSortable(true);
        model.addTable(table);
/*
        if (StringUtils.isNotEmpty(exportFileName)) {
            Export export = model.getExportInstance();
            export.setView(TableConstants.VIEW_CSV);
            export.setViewResolver(TableConstants.VIEW_CSV);
            export.setImageName(TableConstants.VIEW_CSV);
            export.setText(TableConstants.VIEW_CSV);
            export.addAttribute(CsvView.DELIMITER, "|");
            export.setFileName(exportFileName);
            model.addExport(export);
        }
*/
        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);
        for (ColumnValueObject cv : cvObjects) {
            Column col = model.getColumnInstance();
            col.setTitle(cv.title);
            col.setProperty(cv.propertyName);
            col.setSortable(cv.sortable);
            if (StringUtils.isNotEmpty(cv.alias)) {
                col.setAlias(cv.alias);
            }
            if (StringUtils.isNotEmpty(cv.format)) {
                col.setFormat(cv.format);
            }
            if (StringUtils.isNotEmpty(cv.cellDisplay)) {
                col.setCellDisplay(cv.cellDisplay);
            }
            if (StringUtils.isNotEmpty(cv.cellType)) {
                col.setCell(cv.cellType);
            }
            model.addColumn(col);
        }

        return model.assemble();
    }

 

    @SuppressWarnings("finally")
    private List<ParticipantAjaxableDomainObject> getParticipants(final String type, final String text) {

        StringTokenizer typeToken = new StringTokenizer(type, ",");
        StringTokenizer textToken = new StringTokenizer(text, ",");
        log.debug("type :: " + type);
        log.debug("text :: " + text);
        String sType, sText;

        List<ParticipantAjaxableDomainObject> participants = new ArrayList<ParticipantAjaxableDomainObject>();
        
        
        ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
        //ParticipantQuery participantQuery = new ParticipantQuery();
        //participantQuery.leftJoinFetchOnIdentifiers();
        while (typeToken.hasMoreTokens() && textToken.hasMoreTokens()) {
            sType = typeToken.nextToken();
            sText = textToken.nextToken();
            
            if (sType.equals("firstName")) {
            	query.filterByFirstName(sText);
            } else if (sType.equals("identifier")) {
            	query.filterByParticipantIdentifierValue(sText);
            } else if (sType.equals("lastName")) {
            	query.filterByLastName(sText);
            }
        }

        try {
            participants = participantAjaxableDomainObjectRepository.findParticipants(query);
        }
        catch (Exception e) {
            throw new RuntimeException("Formatting Error", e);
        }
        finally {
            return participants;
        }
    }

    public String buildParticipantTable(final Map parameterMap, final String type, final String text, final HttpServletRequest request) {

        List<ParticipantAjaxableDomainObject> participants = new ArrayList<ParticipantAjaxableDomainObject>();
        if (type != null && text != null) {
            participants = getParticipants(type, text);
        }

        Context context = null;
        if (parameterMap == null) {
            context = new HttpServletRequestContext(request);
        } else {
            context = new HttpServletRequestContext(request, parameterMap);
        }

        TableModel model = new TableModelImpl(context);
        // LimitFactory limitFactory = new TableLimitFactory(context);
        // Limit limit = new TableLimit(limitFactory);
        // limit.setRowAttributes(totalRows, DEFAULT_ROWS_DISPLAYED);
        // model.setLimit(limit);
        
        try {
            return buildPartcipantTable(model, participants).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public Object buildPartcipantTable(final TableModel model, final List<ParticipantAjaxableDomainObject> participants) throws Exception {
        Table table = model.getTableInstance();
        table.setTableId("ajaxTable");
        table.setForm("assembler");
        table.setItems(participants);
        table.setAction(model.getContext().getContextPath() + "/pages/participant/search");
        table.setTitle("");
        table.setShowPagination(true);
        table.setOnInvokeAction("buildTable('assembler')");
        table.setImagePath(model.getContext().getContextPath() + "/images/table/*.gif");
        //only support filtering & sorting in local authentication mode. 
        table.setFilterable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());
        table.setSortable(Configuration.LAST_LOADED_CONFIGURATION.isAuthenticationModeLocal());

        table.setSortRowsCallback("gov.nih.nci.cabig.caaers.web.table.SortRowsCallbackImpl");

        model.addTable(table);

/*        Export export = model.getExportInstance();
        export.setView(TableConstants.VIEW_CSV);
        export.setViewResolver(TableConstants.VIEW_CSV);
        export.setImageName(TableConstants.VIEW_CSV);
        export.setText(TableConstants.VIEW_CSV);
        export.addAttribute(CsvView.DELIMITER, "|");
        export.setFileName("caaers_participants.txt");
        model.addExport(export);*/

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);

        Column columnFirstName = model.getColumnInstance();
        columnFirstName.setProperty("firstName");
        columnFirstName.setCell("gov.nih.nci.cabig.caaers.web.search.ParticipantLinkDisplayCell");
        model.addColumn(columnFirstName);

        Column columnLastName = model.getColumnInstance();
        columnLastName.setProperty("lastName");
        model.addColumn(columnLastName);

        Column columnPrimaryIdentifier = model.getColumnInstance();
        columnPrimaryIdentifier.setSortable(false);
        columnPrimaryIdentifier.setProperty("primaryIdentifierValue");
        columnPrimaryIdentifier.setTitle("Primary ID");
        columnPrimaryIdentifier.setCell("gov.nih.nci.cabig.caaers.web.search.ParticipantLinkDisplayCell");
        model.addColumn(columnPrimaryIdentifier);

        Column assignmentIdentifiers = model.getColumnInstance();
        assignmentIdentifiers.setProperty("studySubjectIdentifiersCSV");
        assignmentIdentifiers.setTitle("Study Subject Identifiers");
        assignmentIdentifiers.setCell("gov.nih.nci.cabig.caaers.web.search.ParticipantAssignmentsDisplayCell");
        model.addColumn(assignmentIdentifiers);

        return model.assemble();

    }

    public StudyRepository getStudyRepository() {
        return studyRepository;
    }

    public void setStudyRepository(final StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    public StudyDao getStudyDao() {
        return studyDao;
    }

    public void setStudyDao(final StudyDao studyDao) {
        this.studyDao = studyDao;
    }

    public ParticipantDao getParticipantDao() {
        return participantDao;
    }

    public void setParticipantDao(final ParticipantDao participantDao) {
        this.participantDao = participantDao;
    }

    public void setOrganizationDao(final OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    public void setResearchStaffDao(final ResearchStaffDao researchStaffDao) {
        this.researchStaffDao = researchStaffDao;
    }

    public AdverseEventDao getAdverseEventDao() {
        return adverseEventDao;
    }

    public void setAdverseEventDao(final AdverseEventDao adverseEventDao) {
        this.adverseEventDao = adverseEventDao;
    }

    public ExpeditedAdverseEventReportDao getExpeditedAdverseEventReportDao() {
        return expeditedAdverseEventReportDao;
    }

    public void setExpeditedAdverseEventReportDao(final ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao) {
        this.expeditedAdverseEventReportDao = expeditedAdverseEventReportDao;
    }


    public InvestigationalNewDrugDao getInvestigationalNewDrugDao() {
        return investigationalNewDrugDao;
    }

    public void setInvestigationalNewDrugDao(final InvestigationalNewDrugDao investigationalNewDrugDao) {
        this.investigationalNewDrugDao = investigationalNewDrugDao;
    }

    public void setInvestigatorDao(final InvestigatorDao investigatorDao) {
        this.investigatorDao = investigatorDao;
    }

    @Required
    public void setStudySearchableAjaxableDomainObjectRepository(StudySearchableAjaxableDomainObjectRepository studySearchableAjaxableDomainObjectRepository) {
        this.studySearchableAjaxableDomainObjectRepository = studySearchableAjaxableDomainObjectRepository;
    }
    @Required
	public void setParticipantAjaxableDomainObjectRepository(
			ParticipantAjaxableDomainObjectRepository participantAjaxableDomainObjectRepository) {
		this.participantAjaxableDomainObjectRepository = participantAjaxableDomainObjectRepository;
	}

	public OrganizationRepository getOrganizationRepository() {
		return organizationRepository;
	}
	
	@Required
	public void setOrganizationRepository(
			OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	public ResearchStaffRepository getResearchStaffRepository() {
		return researchStaffRepository;
	}

	@Required
	public void setResearchStaffRepository(
			ResearchStaffRepository researchStaffRepository) {
		this.researchStaffRepository = researchStaffRepository;
	}

	public InvestigatorRepository getInvestigatorRepository() {
		return investigatorRepository;
	}
	
	@Required
	public void setInvestigatorRepository(InvestigatorRepository investigatorRepository) {
		this.investigatorRepository = investigatorRepository;
	}

    public AgentRepository getAgentRepository() {
        return agentRepository;
    }

    public void setAgentRepository(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Override
    public Class<?>[] controllers() {
        return CONTROLLERS; 
    }
}

class ColumnValueObject {
    public String title;

    public String propertyName;

    public String alias;

    public String format;

    public String cellDisplay;

    public String cellType;

    public boolean sortable;

    public ColumnValueObject(final String propertyName, final String title, final String alias, final String format,
                             final String cellDisplay, final String cellType) {
        this.title = title;
        this.propertyName = propertyName;
        this.alias = alias;
        this.format = format;
        this.cellDisplay = cellDisplay;
        this.cellType = cellType;
    }

    public static ColumnValueObject create(final String propertyName) {
        return ColumnValueObject.create(propertyName, null, null);
    }

    public static ColumnValueObject create(final String propertyName, final String title) {
        return ColumnValueObject.create(propertyName, title, null);
    }

    public static ColumnValueObject create(final String propertyName, final String title, final String alias) {
        return new ColumnValueObject(propertyName, title, alias, null, null, null);
    }
}
