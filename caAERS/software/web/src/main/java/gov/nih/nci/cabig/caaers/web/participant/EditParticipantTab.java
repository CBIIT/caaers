package gov.nih.nci.cabig.caaers.web.participant;

//java imports

import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.StudySiteDao;
import gov.nih.nci.cabig.caaers.dao.query.StudyParticipantAssignmentQuery;
import gov.nih.nci.cabig.caaers.domain.DateValue;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.OrganizationAssignedIdentifier;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.SystemAssignedIdentifier;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.utils.Lov;
import gov.nih.nci.cabig.caaers.web.ListValues;
import gov.nih.nci.cabig.caaers.web.fields.CompositeField;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldAttributes;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.fields.ReadonlyFieldDecorator;
import gov.nih.nci.cabig.caaers.web.fields.SecurityObjectIdFieldDecorator;
import gov.nih.nci.cabig.caaers.web.fields.TabWithFields;
import gov.nih.nci.cabig.caaers.web.fields.validators.FieldValidator;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

/*
* @author Ion C. Olaru
* 
* */
public class EditParticipantTab<T extends ParticipantInputCommand> extends TabWithFields<T> {
	private StudySiteDao studySiteDao;
	
    public EditParticipantTab() {
        super("Enter Subject Information", "Details", "par/par_edit_participant");
        addFieldDecorators(new SecurityObjectIdFieldDecorator(Participant.class), new ReadonlyFieldDecorator());
    }

    private OrganizationDao organizationDao;
    private ParticipantDao participantDao;
    private ListValues listValues;
    private ConfigProperty configurationProperty;
    private StudyDao studyDao;

    public void setStudyDao(StudyDao studyDao) {
		this.studyDao = studyDao;
	}

	private static final String PARTICIPANT_FIELD_GROUP = "participant";
    private static final String SITE_FIELD_GROUP = "site";


    protected Map<Object, Object> collectOptions(final List<ListValues> list) {
        Map<Object, Object> options = new LinkedHashMap<Object, Object>();
        options.putAll(WebUtils.collectOptions(list, "code", "desc"));
        return options;
    }
    @Override
    public Map<String, InputFieldGroup> createFieldGroups(final T command) {

        InputFieldGroup participantFieldGroup;
        Map<Object, Object> options = null;

        participantFieldGroup = new DefaultInputFieldGroup(PARTICIPANT_FIELD_GROUP);

        FieldValidator fv = FieldValidator.ALPHANUMERIC_VALIDATOR;
        if (!command.isUnidentifiedMode()) {
            participantFieldGroup.getFields().add(InputFieldFactory.createTextField("participant.firstName", "First Name", FieldValidator.NOT_NULL_VALIDATOR, fv));
            participantFieldGroup.getFields().add(InputFieldFactory.createTextField("participant.lastName", "Last Name", FieldValidator.NOT_NULL_VALIDATOR, fv));
            participantFieldGroup.getFields().add(InputFieldFactory.createTextField("participant.maidenName", "Maiden Name", fv));
            participantFieldGroup.getFields().add(InputFieldFactory.createTextField("participant.middleName", "Middle Name", fv));
        }

        InputField dobYear = InputFieldFactory.createTextField("yearString", "Year", true);
        InputFieldAttributes.setSize(dobYear, 4);
        InputField dobMonth = InputFieldFactory.createTextField("monthString", "Month",true);
        InputFieldAttributes.setSize(dobMonth, 2);
        InputField dobDay = InputFieldFactory.createTextField("dayString", "Day");
        InputFieldAttributes.setSize(dobDay, 2);

        CompositeField dobField;
        if (command.isUnidentifiedMode())
            dobField = new CompositeField("participant.dateOfBirth", new DefaultInputFieldGroup(null, "Date of birth").addField(dobYear).addField(dobMonth));
        else
            dobField = new CompositeField("participant.dateOfBirth", new DefaultInputFieldGroup(null, "Date of birth").addField(dobYear).addField(dobMonth).addField(dobDay));
        
        dobField.setRequired(true);
        dobField.getAttributes().put(InputField.HELP, "par.par_create_participant.participant.dateOfBirth");

        participantFieldGroup.getFields().add(dobField);
        participantFieldGroup.getFields().add(InputFieldFactory.createSelectField("participant.gender", "Gender", true, collectOptions(listValues.getParticipantGender())));
        participantFieldGroup.getFields().add(InputFieldFactory.createSelectField("participant.ethnicity", "Ethnicity", true,collectOptions(listValues.getParticipantEthnicity())));

        participantFieldGroup.getFields().add(InputFieldFactory.createSelectField("participant.race", "Race", true, collectOptions(listValues.getParticipantRace())));
/*
        repeatingFieldGroupFactory = new RepeatingFieldGroupFactory("main", "participant.identifiers");
        repeatingFieldGroupFactory.addField(InputFieldFactory.createTextField("value", "Identifier", true));
*/

        options = new LinkedHashMap<Object, Object>();
        List<Lov> list = configurationProperty.getMap().get("participantIdentifiersType");
        options.put("", "Please select");
        options.putAll(WebUtils.collectOptions(list, "code", "desc"));

        InputFieldGroupMap map = new InputFieldGroupMap();

        if (!command.isUnidentifiedMode()) {
            byte i = 0;
            byte j = 0;

            InputFieldGroup idtFieldGroupOrg;
            InputFieldGroup idtFieldGroupSys;

            for (Identifier idt : command.participant.getIdentifiers()) {

                if (idt instanceof SystemAssignedIdentifier) {
                    String s = "participant.systemAssignedIdentifiers[" + i + "].";
                    idtFieldGroupSys = new DefaultInputFieldGroup("mainSys" + i);

                    idtFieldGroupSys.getFields().add(InputFieldFactory.createTextField(s + "value", "Identifier", FieldValidator.NOT_NULL_VALIDATOR, FieldValidator.IDENTIFIER_VALIDATOR));
                    idtFieldGroupSys.getFields().add(InputFieldFactory.createSelectField(s + "type", "Identifier Type", true, options));
                    idtFieldGroupSys.getFields().add(InputFieldFactory.createTextField(s + "systemName", "System Name", true));
                    map.addInputFieldGroup(idtFieldGroupSys);

                    i++;

                } else {
                    String s = "participant.organizationIdentifiers[" + j + "].";
                    idtFieldGroupOrg = new DefaultInputFieldGroup("mainOrg" + j);

                    idtFieldGroupOrg.getFields().add(InputFieldFactory.createTextField(s + "value", "Identifier", FieldValidator.NOT_NULL_VALIDATOR, FieldValidator.IDENTIFIER_VALIDATOR));
                    idtFieldGroupOrg.getFields().add(InputFieldFactory.createSelectField(s + "type", "Identifier Type", true, options));
                    idtFieldGroupOrg.getFields().add(InputFieldFactory.createAutocompleterField(s + "organization", "Organization", true));
                    map.addInputFieldGroup(idtFieldGroupOrg);

                    j++;
                }

            }
        }

        map.addInputFieldGroup(participantFieldGroup);
        
        return map;
    }


    @Override
    public void postProcess(HttpServletRequest request, T command, Errors errors) {
        if (command.getAssignment() != null) {
            StudySite site = studySiteDao.getById(command.getAssignment().getStudySite().getId());
            command.setStudy(site.getStudy());
            command.refreshStudyDiseases();
        }
    }
    
    protected void validateUniqueStudySubjectIdentifiersInStudy(Study study, Errors errors, String studySubjectIdentifier, Integer excludeStudySubjectId){
			if(studyDao.getNumberOfStudySubjectsInStudyWithGivenAssignmentIdentifier(study, studySubjectIdentifier, excludeStudySubjectId) > 0){
				errors.reject("PT_013",new Object[]{studySubjectIdentifier} ,"The study subject identifier, " + studySubjectIdentifier  + " has been already" +
						" assigned to another subject on the study");
			}
    }

    /**
     * Validate the Participant info
     * The participant identifiers should be unique per subject's site.
     */
    @Override
    protected void validate(T command, BeanWrapper commandBean, Map<String, InputFieldGroup> fieldGroups, Errors errors) {
        DateValue dob = command.getParticipant().getDateOfBirth();

        if (dob.checkIfDateIsInValid()) {
            errors.rejectValue("participant.dateOfBirth", "PT_010", "Incorrect Date Of Birth");
        }

        if (!command.getAdditionalParameters().containsKey("DO_PARTIAL_VALIDATION")) {
            if (command.getAssignment() == null) errors.reject("PT_002", "Select one assignment please.");
        } else {
            command.getAdditionalParameters().remove("DO_PARTIAL_VALIDATION");
        }
        
        // Check uniqueness of Study Subject identifier across study
        for(StudyParticipantAssignment assignment : command.getAssignments()){
        	validateUniqueStudySubjectIdentifiersInStudy(assignment.getStudySite().getStudy(),errors,assignment.getStudySubjectIdentifier(), assignment.getId());
        }
       
        // CHECK Participant Identifiers
        List<Identifier> siteIdentifiers = participantDao.getSiteIdentifiers(command.getOrganization().getId().intValue());
        for (int i=0; i<siteIdentifiers.size(); i++) {
            Identifier sID = siteIdentifiers.get(i);
            if (sID == null || sID.getValue() == null) continue;

            for (int j=0; j<command.getParticipant().getIdentifiers().size(); j++) {
                Identifier pID = command.getParticipant().getIdentifiers().get(j);
                if (pID == null || pID.getValue() == null) return;

                if ( ( sID.getValue().toLowerCase().equals(pID.getValue().toLowerCase()) && StringUtils.equals(sID.getType(), pID.getType()) ) && (sID.getId() == null || sID.getId().intValue() != pID.getId().intValue())) {
                    errors.reject("ERR_DUPLICATE_SITE_IDENTIFIER", new Object[] {command.getOrganization().getName(), pID.getValue()}, "Duplicate identifiers for the same site.");
                }
            }
        }

        // Checking Study-Subject identifiers, uniqueness per StudySite
        Integer pID = command.getParticipant().getId();
        List<StudyParticipantAssignment> assignments = command.getAssignments();

        for (StudyParticipantAssignment a : assignments) {
            StudyParticipantAssignmentQuery query = new StudyParticipantAssignmentQuery();
            query.filterByStudySiteId(a.getStudySite().getId());
            query.filterByStudySubjectIdentifier(a.getStudySubjectIdentifier());
            query.filterByParticipantExcluded(pID);

            List l = studySiteDao.search(query);
            if (l.size() > 0) {
                errors.reject("ERR_DUPLICATE_STUDY_SITE_IDENTIFIER", new Object[] {a.getStudySubjectIdentifier(), a.getStudySite().getStudy().getShortTitle(), a.getStudySite().getOrganization().getName()}, "Duplicate Study Site identifier.");
            }
        }

    }

    public void setOrganizationDao(final OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    public void setListValues(final ListValues listValues) {
        this.listValues = listValues;
    }

    public ConfigProperty getConfigurationProperty() {
        return configurationProperty;
    }

    public void setConfigurationProperty(ConfigProperty configurationProperty) {
        this.configurationProperty = configurationProperty;
    }

    public ModelAndView addOrganizationIdentifier(HttpServletRequest request, Object cmd, Errors error) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        ModelAndView modelAndView = new ModelAndView(getAjaxViewName(request), map);

        ParticipantInputCommand command =(ParticipantInputCommand)cmd;
        List<OrganizationAssignedIdentifier> list = command.getParticipant().getOrganizationIdentifiers();

        // store the new index for the new Identifier
        int size = list.size();
        Integer[] indexes = new Integer[]{size};
        modelAndView.getModel().put("indexes", indexes);

        // store the new Identifier object into the command.participant
        OrganizationAssignedIdentifier newIdentifier = new OrganizationAssignedIdentifier();
        newIdentifier.setOrganization(organizationDao.getById(1));
        command.getParticipant().addIdentifier(newIdentifier);
        
        return modelAndView;
    }

    public ModelAndView removeOrganizationIdentifier(HttpServletRequest request, Object cmd, Errors error) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        ModelAndView modelAndView = new ModelAndView(getAjaxViewName(request), map);

        ParticipantInputCommand command =(ParticipantInputCommand)cmd;
        List<OrganizationAssignedIdentifier> list = command.getParticipant().getOrganizationIdentifiers();
        list.remove(list.get(command.getIndex()));

        // update the array of remainning indexes after deleting
        int size = list.size();
        Integer[] indexes = new Integer[size];
        for(int i = 0 ; i < size ; i++){
            indexes[i] = i;
        }

        modelAndView.getModel().put("indexes", indexes);
        modelAndView.getModel().put("remove", "remove");
        return modelAndView;
    }

    public ModelAndView addSystemIdentifier(HttpServletRequest request, Object cmd, Errors error) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        ModelAndView modelAndView = new ModelAndView(getAjaxViewName(request), map);

        ParticipantInputCommand command =(ParticipantInputCommand)cmd;
        List<SystemAssignedIdentifier> list = command.getParticipant().getSystemAssignedIdentifiers();

        // store the new index for the new Identifier
        int size = list.size();
        Integer[] indexes = new Integer[]{size};
        modelAndView.getModel().put("indexes", indexes);

        // store the new Identifier object into the command.participant
        SystemAssignedIdentifier newIdentifier = new SystemAssignedIdentifier();
        command.getParticipant().addIdentifier(newIdentifier);

        return modelAndView;
    }

    public ModelAndView removeSystemIdentifier(HttpServletRequest request, Object cmd, Errors error) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        ModelAndView modelAndView = new ModelAndView(getAjaxViewName(request), map);

        ParticipantInputCommand command =(ParticipantInputCommand)cmd;
        List<SystemAssignedIdentifier> list = command.getParticipant().getSystemAssignedIdentifiers();
        list.remove(list.get(command.getIndex()));

        // update the array of remainning indexes after deleting
        int size = list.size();
        Integer[] indexes = new Integer[size];
        for(int i = 0 ; i < size ; i++){
            indexes[i] = i;
        }

        modelAndView.getModel().put("indexes", indexes);
        return modelAndView;
    }
    
    public void setStudySiteDao(StudySiteDao studySiteDao) {
		this.studySiteDao = studySiteDao;
	}

    public StudySiteDao getStudySiteDao() {
		return studySiteDao;
	}

    public ParticipantDao getParticipantDao() {
        return participantDao;
    }

    public void setParticipantDao(ParticipantDao participantDao) {
        this.participantDao = participantDao;
    }
}
