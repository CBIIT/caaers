package gov.nih.nci.cabig.caaers.web.participant;

//java imports
import gov.nih.nci.cabig.caaers.dao.StudySiteDao;
import gov.nih.nci.cabig.caaers.dao.query.StudyHavingStudySiteQuery;
import gov.nih.nci.cabig.caaers.dao.query.StudyParticipantAssignmentQuery;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.repository.StudyRepository;
import gov.nih.nci.cabig.caaers.web.ListValues;
import gov.nih.nci.cabig.caaers.web.fields.*;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Ion C. Olaru
 */

public class SelectStudyForParticipantTab <T extends ParticipantInputCommand> extends TabWithFields<T> {

    private final String STUDY_SUBJECT_IDENTIFIER_FIELD_GROUP = "studySubjectIdentifier";
    private final String STUDY_SUBJECT_IDENTIFIER_FIELD = "assignment.studySubjectIdentifier";
    
    StudyRepository studyRepository;
    private StudySiteDao studySiteDao;

    public SelectStudyForParticipantTab() {
        super("Choose Study", "Choose Study", "par/par_create_choose_study");
        addFieldDecorators(new SecurityObjectIdFieldDecorator(Participant.class), new ReadonlyFieldDecorator());
    }

    private ListValues listValues;

    @Override
    public Map<String, Object> referenceData(T command) {
        Map<String, Object> refdata = super.referenceData(command);
        refdata.put("searchType", listValues.getStudySearchType());


        // get the StudySites to filterby 

/*
        // Search START
        // Search END
*/

        return refdata;
    }

    public void onBind(HttpServletRequest request, T command, Errors errors) {
        super.onBind(request, command, errors);
        if (command.getStudy() != null && command.getStudy().getId() != null) {
            ParticipantInputCommand participantCommand = (ParticipantInputCommand) command;
            StudySite studySite = studySiteDao.findByStudyAndOrganization(command.getStudy().getId(), command.getOrganization().getId());
            participantCommand.getAssignment().setStudySite(studySite);
            participantCommand.getAssignment().setParticipant(participantCommand.getParticipant());
            studySite.getStudy().getPrimaryIdentifier();
        }
    }

    public void postProcess(HttpServletRequest request, T command, Errors errors) {
        super.postProcess(request, command, errors);
        command.getParticipant().addAssignment(command.getAssignment());
    }

    @Override
    protected void validate(T command, BeanWrapper commandBean, Map<String, InputFieldGroup> fieldGroups, Errors errors) {
        super.validate(command, commandBean, fieldGroups, errors);
        boolean studySiteArray = (command.getStudy() == null || command.getStudy().getId() == null);

        if (studySiteArray) {
            errors.rejectValue("studySiteArray", "PT_012", "Please select a Study to Continue");
        }

        if (StringUtils.isEmpty(command.assignment.getStudySubjectIdentifier())) {
            errors.rejectValue("assignment.studySubjectIdentifier", "PT_003", "Specify the Study Subject Identifier");

            // this is because the previewsly selected study is not selected anymore in the radiobuttons, causing the error on continue
            command.setStudy(null);
        }

        Integer pID = command.getAssignment().getParticipant().getId();

        StudyParticipantAssignmentQuery query = new StudyParticipantAssignmentQuery();
        query.filterByStudySiteId(command.getAssignment().getStudySite().getId());
        query.filterByStudySubjectIdentifier(command.getAssignment().getStudySubjectIdentifier());

        if (pID != null) {
            query.filterByParticipantExcluded(pID);
        }

        List l = studySiteDao.search(query);
        if (l.size() > 0) {
            errors.reject("ERR_DUPLICATE_STUDY_SITE_IDENTIFIER", new Object[] {command.getAssignment().getStudySubjectIdentifier(), command.getAssignment().getStudySite().getStudy().getShortTitle(), command.getAssignment().getStudySite().getOrganization().getName()}, "Duplicate Study Site identifier.");
        }

    }

    @Override
    public Map<String, InputFieldGroup> createFieldGroups(T command) {
        InputFieldGroupMap map = new InputFieldGroupMap();
        InputFieldGroup studySubjectIdentifierFieldGroup = new DefaultInputFieldGroup(STUDY_SUBJECT_IDENTIFIER_FIELD_GROUP);
        studySubjectIdentifierFieldGroup.getFields().add(InputFieldFactory.createTextField(STUDY_SUBJECT_IDENTIFIER_FIELD, "Study subject identifier", true));
        map.addInputFieldGroup(studySubjectIdentifierFieldGroup);
        return map;
    }

    public void setListValues(final ListValues listValues) {
        this.listValues = listValues;
    }

    /**
     *  this is an Ajaxable method called using <tags:tabMethod>
     */
    public ModelAndView searchStudies(HttpServletRequest request, Object cmd, Errors error) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        return new ModelAndView(getAjaxViewName(request), map);
    }

    public StudyRepository getStudyRepository() {
        return studyRepository;
    }

    public void setStudyRepository(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    public StudySiteDao getStudySiteDao() {
        return studySiteDao;
    }

    public void setStudySiteDao(StudySiteDao studySiteDao) {
        this.studySiteDao = studySiteDao;
    }
}

/*
        if (participantCommand.getStudySiteArray() != null) {
            Set<String> studySiteIdSet = new java.util.HashSet<String>(java.util.Arrays.asList(participantCommand.getStudySiteArray()));
            for (String studyId : studySiteIdSet) {
                StudySite studySite = studySiteDao.findByStudyAndOrganization(Integer.parseInt(studyId),participantCommand.getOrganization().getId());
                studySites.add(studySite);

            }
            Participant participant = participantCommand.getParticipant();
            List<StudyParticipantAssignment> assignments = new ArrayList<StudyParticipantAssignment>();
            for (int i = 0; i < studySites.size(); i++) {
                final StudyParticipantAssignment studyParticipantAssignment = new StudyParticipantAssignment(participant, studySites.get(i));
                studyParticipantAssignment.setStudySubjectIdentifier(participantCommand.getStudySubjectIdentifier());
                assignments.add(studyParticipantAssignment);
            }
            participant.setAssignments(assignments);
            participantCommand.setStudySites(studySites);
        }
*/

