/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.study;

import gov.nih.nci.cabig.caaers.domain.StudyOrganization;
import gov.nih.nci.cabig.caaers.domain.StudyPersonnel;
import gov.nih.nci.cabig.caaers.domain.StudyInvestigator;
import gov.nih.nci.cabig.caaers.utils.DateUtils;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.fields.RepeatingFieldGroupFactory;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanWrapper;
import org.springframework.validation.Errors;

/**
 * @author Ion C. Olaru
 * @author Biju Joseph
 */
class PersonnelTab extends StudyTab {
    private List<InputField> fields;

    public PersonnelTab() {
        super("Personnel", "Personnel", "study/study_personnel");
        // set Decorators
        
    }

    @Override
    public Map<String, Object> referenceData(HttpServletRequest request, StudyCommand command) {
        Map<String, Object> refdata = super.referenceData(request, command);
        return refdata;
    }

    @Override
    public void postProcess(HttpServletRequest request, StudyCommand command, Errors errors) {
        String action = request.getParameter("_action");
        String selectedPersonnel = request.getParameter("_selectedPersonnel");
        String prevSiteIndex = request.getParameter("_prevSite");
        int selectedIndex = command.getStudySiteIndex();

        int selectedPersonnelIndex;
        if (request.getParameter("_selectedPersonnel") != null && !request.getParameter("_selectedPersonnel").equals(""))
            selectedPersonnelIndex = Integer.parseInt(selectedPersonnel);
        else
            selectedPersonnelIndex = -1;

        // START particular operations
        if ("removeStudyPersonnel".equals(action) && selectedIndex >= 0) {
            command.deleteStudyPersonAtIndex(selectedIndex, Integer.parseInt(selectedPersonnel));
        } else if ("changeSite".equals(action) && errors.hasErrors()) {
            int siteIndex = Integer.parseInt(prevSiteIndex);
            command.setStudySiteIndex(siteIndex);
            if (siteIndex >= 0) {
                command.getStudy().getActiveStudyOrganizations().get(siteIndex).getStudyPersonnels().get(0);
            }
        } else if ("activate".equals(action)) {
            command.getStudy().getActiveStudyOrganizations().get(selectedIndex).getStudyPersonnels().get(selectedPersonnelIndex).activate();
        } else if ("deactivate".equals(action)) {
            command.getStudy().getActiveStudyOrganizations().get(selectedIndex).getStudyPersonnels().get(selectedPersonnelIndex).deactivate();
        }
        // END particular operations

        if (command.getStudySiteIndex() >= 0) {
            StudyOrganization so = command.getStudy().getActiveStudyOrganizations().get(command.getStudySiteIndex());
            for (StudyPersonnel sp : so.getStudyPersonnels()) {
                // https://tracker.nci.nih.gov/browse/CAAERS-4739
                // Activate the newly added RSs
                if (sp.getId() == null) {
                    if (sp.getSiteResearchStaff() != null && sp.getSiteResearchStaff().getActiveDate() != null) {
                        sp.setStartDate(sp.getSiteResearchStaff().getActiveDate());
                    } else {
                        sp.setStartDate(DateUtils.today());
                    }
                    sp.setEndDate(null);
                }
            }
        }

        command.setMustFireEvent(true);
        request.setAttribute("tabFlashMessage", messageSource.getMessage(String.format("MSG_study.%s.flash_message", this.getClass().getSimpleName()), null, Locale.getDefault()));
    }

    @Override
    public Map<String, InputFieldGroup> createFieldGroups(StudyCommand command) {
        InputFieldGroupMap map = new InputFieldGroupMap();
        InputFieldGroup siteFieldGroup = new DefaultInputFieldGroup("site");
        siteFieldGroup.getFields().add(InputFieldFactory.createSelectField("studySiteIndex", "Site", true, WebUtils.collectOptions(collectStudyOrganizations(command.getStudy()), "code", "desc")));
        map.addInputFieldGroup(siteFieldGroup);

        if (fields == null) {
            fields = new ArrayList<InputField>();
            InputField investigatorField = InputFieldFactory.createAutocompleterField("siteResearchStaff", "Research Staff", true);
            fields.add(investigatorField);
            fields.add(InputFieldFactory.createSelectField("roleCode", "Role", true, WebUtils.collectOptions(command.getAllPersonnelRoles(), "code", "name", "")));
            //TODO: BJ need to show startDate and endDate and deactivate/activate
            fields.add(InputFieldFactory.createLabelField("active", "Status", false));
        }

        int ssIndex = command.getStudySiteIndex();
        if (ssIndex >= 0) {
            RepeatingFieldGroupFactory rfgFactory = new RepeatingFieldGroupFactory("main", "study.activeStudyOrganizations[" + ssIndex + "].studyPersonnels");

            for (InputField f : fields) {
                rfgFactory.addField(f);
            }
            map.addRepeatingFieldGroupFactory(rfgFactory, command.getStudy().getActiveStudyOrganizations().get(ssIndex).getStudyPersonnels().size());
        }
        return map;
    }

    @Override
    protected void validate(StudyCommand command, BeanWrapper commandBean, Map<String, InputFieldGroup> fieldGroups, Errors errors) {
        super.validate(command, commandBean, fieldGroups, errors);
        if (command.getStudySiteIndex() >= 0) {
            StudyOrganization so = command.getStudy().getActiveStudyOrganizations().get(command.getStudySiteIndex());
            HashSet<String> hSet = new HashSet<String>();
            for (StudyPersonnel sp : so.getStudyPersonnels()) {
//                if (sp.isActive() && sp.getSiteResearchStaff() != null)
                if (sp.getSiteResearchStaff() != null)
                    if (!hSet.add(sp.getRoleCode() + sp.getSiteResearchStaff().getResearchStaff().getId().toString())) {
                        errors.reject("STU_012", new Object[] {sp.getSiteResearchStaff().getResearchStaff().getFullName()}, "Duplicate entry");
                    }
            }
        }
    }
}
