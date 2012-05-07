package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.dao.IntegrationLogDao;
import gov.nih.nci.cabig.caaers.dao.query.StudyHavingStudySiteQuery;
import gov.nih.nci.cabig.caaers.dao.query.StudyQuery;
import gov.nih.nci.cabig.caaers.dao.query.StudySitesQuery;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.ajax.StudyAjaxableDomainObject;
import gov.nih.nci.cabig.caaers.domain.ajax.StudySiteAjaxableDomainObject;
import gov.nih.nci.cabig.caaers.domain.repository.StudyRepository;
import gov.nih.nci.cabig.caaers.domain.repository.ajax.StudySearchableAjaxableDomainObjectRepository;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.web.participant.AssignParticipantController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.bean.Row;
import org.extremecomponents.table.bean.Table;
import org.extremecomponents.table.core.TableModel;
import org.springframework.beans.factory.annotation.Required;

public class IntegrationLogsAjaxFacade {

    /*private IntegrationLogDao integrationLogDao;
    public void setIntegrationLogDao(IntegrationLogDao integrationLogDao) {
		this.integrationLogDao = integrationLogDao;
	}

	private static final Log log = LogFactory.getLog(IntegrationLogsAjaxFacade.class);

    public Object build(TableModel model, Collection studySearchableAjaxableDomainObjects) throws Exception {
        addTable(model, studySearchableAjaxableDomainObjects);
        addPrimaryIdColumn(model);
        addShorTitleColumn(model);
        addSponsorColumn(model);
        addPhaseCodeColumn(model);
        addStatusColumn(model);
        return model.assemble();
    }

    private void addStatusColumn(TableModel model) {
        Column columnStatusCode = model.getColumnInstance();
        columnStatusCode.setProperty("study.status");
        model.addColumn(columnStatusCode);
        columnStatusCode.setSortable(Boolean.TRUE);
    }

    private void addPhaseCodeColumn(TableModel model) {
        Column columnPhaseCode = model.getColumnInstance();
        columnPhaseCode.setTitle("Phase");
        columnPhaseCode.setProperty("phaseCode");
        model.addColumn(columnPhaseCode);
        columnPhaseCode.setSortable(Boolean.TRUE);
    }

    private void addSponsorColumn(TableModel model) {
        Column columnSponsorCode = model.getColumnInstance();
        columnSponsorCode.setTitle("Funding Sponsor");
        columnSponsorCode.setProperty("primarySponsorCode");
        columnSponsorCode.setSortable(Boolean.TRUE);
        model.addColumn(columnSponsorCode);
    }

    private void addShorTitleColumn(TableModel model) {
        Column columnShortTitle = model.getColumnInstance();
        columnShortTitle.setTitle("Short Title");
        columnShortTitle.setProperty("shortTitle");
        columnShortTitle.setSortable(Boolean.TRUE);
        model.addColumn(columnShortTitle);
    }

    private void addPrimaryIdColumn(TableModel model) {
        Column columnPrimaryIdentifier = model.getColumnInstance();
        columnPrimaryIdentifier.setProperty("primaryIdentifierValue");
        columnPrimaryIdentifier.setTitle("Study ID");
        columnPrimaryIdentifier.setCell("gov.nih.nci.cabig.caaers.web.study.StudyLinkDisplayCell");
        model.addColumn(columnPrimaryIdentifier);
    }

    private void addTable(TableModel model, Collection studySearchableAjaxableDomainObjects) {
        Table table = model.getTableInstance();
        table.setTableId("ajaxTable");
        table.setForm("assembler");
        table.setItems(studySearchableAjaxableDomainObjects);
        table.setAction(model.getContext().getContextPath() + "/assembler.run");
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

        table.setAutoIncludeParameters(false);
        model.addTable(table);

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.TRUE);
        model.addRow(row);
    }

    public List<StudyAjaxableDomainObject> getStudiesTable(Map parameterMap, String type, String text, HttpServletRequest request) {
        StudyQuery sq = new StudyQuery();

        StringTokenizer typeToken = new StringTokenizer(type, ",");
        StringTokenizer textToken = new StringTokenizer(text, ",");
        String sType;
        String sText;
        while (typeToken.hasMoreTokens() && textToken.hasMoreTokens()) {
            sType = typeToken.nextToken();
            sText = textToken.nextToken();

            if ("idtf".equals(sType)) {
                sq.filterByIdentifierValue(sText);
            } else if ("st".equals(sType)) {
                sq.filterByShortTitle(sText);
            }
        }
        
        List<Study> studies = studyRepository.search(sq, type, text, coppaMode);
        List<StudyAjaxableDomainObject> rs = new ArrayList<StudyAjaxableDomainObject>();
        for (Study s : studies) {
            StudyAjaxableDomainObject as = new StudyAjaxableDomainObject();
            as.setId(s.getId());
            as.setStatus(s.getStatus());
            as.setShortTitle(s.getShortTitle());
            as.setPrimaryIdentifierValue(s.getPrimaryIdentifierValue());
            as.setPhaseCode(s.getPhaseCode());
            as.setPrimarySponsorCode(s.getPrimarySponsorCode());
            as.setExternalId(s.getExternalId() != null ? s.getExternalId().trim() : "");
            rs.add(as);
        }
        return rs;
        
    }

    // Create Subject flow
    public List<StudyAjaxableDomainObject> getStudiesForCreateParticipant(Map parameterMap, String type, String text, String nciCode, HttpServletRequest request) {

        List<Study> studies; 
        List<StudyAjaxableDomainObject> rs = new ArrayList<StudyAjaxableDomainObject>();
        
        if (text != null && type != null && !text.equals("")) {
            StudyHavingStudySiteQuery query = new StudyHavingStudySiteQuery();
            query.joinStudyOrganization();
            query.filterByDataEntryStatus(true);
            query.filterByStudySiteNciInstituteCode(nciCode);
            if ("st".equals(type)) {
                query.filterByStudyShortTile(text);
            } else if ("idtf".equals(type)) {
                query.filterByIdentifierValue(text);
            }
            query.filterBySST();
            studies = studyRepository.find(query);

            for (Study s : studies) {
                StudyAjaxableDomainObject sado = new StudyAjaxableDomainObject();
                sado.setId(s.getId());
                sado.setShortTitle(s.getShortTitle());
                sado.setStatus(s.getStatus());
                sado.setPrimaryIdentifierValue(s.getPrimaryIdentifierValue());
                sado.setPhaseCode(s.getPhaseCode());
                sado.setPrimarySponsorCode(s.getPrimarySponsorCode());
                rs.add(sado);
            }
        }

        return rs;
    }

    // ASSIGN Study Search
    public List<StudySiteAjaxableDomainObject> getTableForAssignParticipant(Map parameterMap, String type, String text, HttpServletRequest request) {
        int organizationID;
        try {
            organizationID = Integer.parseInt((String) parameterMap.get("organizationID"));
        } catch (Exception e) {
            organizationID = 0;
        }

        List<StudySite> studySites = getStudySites(type, text, organizationID, true);
        List<StudySiteAjaxableDomainObject> rs = new ArrayList<StudySiteAjaxableDomainObject>();

        for (StudySite ss : studySites) {
            StudySiteAjaxableDomainObject ssado = new StudySiteAjaxableDomainObject();
            ssado.setId(ss.getId());
            ssado.setPrimaryId(ss.getStudy().getPrimaryIdentifierValue());
            ssado.setStudyShortTitle(ss.getStudy().getShortTitle());
            ssado.setStatus(ss.getStudy().getStatus());
            ssado.setStudyPhase(ss.getStudy().getPhaseCode());
            ssado.setNciInstituteCode(ss.getStudy().getPrimaryFundingSponsor().getOrganization().getNciInstituteCode());
            ssado.setName(ss.getOrganization().getFullName());  //CAAERS-4565
            rs.add(ssado);
        }

        return rs;
    }

    private List<StudySite> getObjects(String type, String text) {
        return getStudySites(type, text, 0, false);
    }

    public List<StudySite> getStudySites(String type, String text, int organizationID, boolean hideIncomplete) {
        StudySitesQuery studySitesQuery = new StudySitesQuery();

        if (organizationID > 0)
            studySitesQuery.filterByOrganizationId(organizationID);
        studySitesQuery.filterByDataEntryComplete(hideIncomplete);

        StringTokenizer typeToken = new StringTokenizer(type, ",");
        StringTokenizer textToken = new StringTokenizer(text, ",");
        String sType;
        String sText;
        while (typeToken.hasMoreTokens() && textToken.hasMoreTokens()) {
            sType = typeToken.nextToken();
            sText = textToken.nextToken();

            if ("st".equals(sType)) {
                studySitesQuery.filterStudiesWithMatchingShortTitleOnly(sText);
            } else if ("idtf".equals(sType)) {
                studySitesQuery.filterStudiesWithMatchingIdentifierOnly(sText);
            }
        }

        List<StudySite> studySites = studyRepository.search(studySitesQuery, type, text, coppaMode);
        return studySites;
    }


    @Required
    public void setStudySearchableAjaxableDomainObjectRepository(StudySearchableAjaxableDomainObjectRepository studySearchableAjaxableDomainObjectRepository) {
        this.studySearchableAjaxableDomainObjectRepository = studySearchableAjaxableDomainObjectRepository;
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

    public Class<?>[] getCONTROLLERS() {
        return CONTROLLERS;
    }

    public void setCONTROLLERS(Class<?>[] CONTROLLERS) {
        this.CONTROLLERS = CONTROLLERS;
    }

    public StudyRepository getStudyRepository() {
        return studyRepository;
    }

    public void setStudyRepository(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

	public void setCoppaMode(boolean coppaMode) {
		this.coppaMode = coppaMode;
	}*/
}