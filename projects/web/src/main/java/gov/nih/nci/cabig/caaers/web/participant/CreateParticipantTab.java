package gov.nih.nci.cabig.caaers.web.participant;

import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.repository.OrganizationRepository;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.utils.Lov;
import gov.nih.nci.cabig.caaers.web.ListValues;
import gov.nih.nci.cabig.caaers.web.fields.*;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.validation.Errors;
import org.springframework.beans.BeanWrapper;
import org.springframework.web.servlet.ModelAndView;

public class CreateParticipantTab<T extends ParticipantInputCommand> extends TabWithFields<T> {

    public CreateParticipantTab() {
        super("Enter Subject Information", "Details", "par/par_create_participant");
    }
    
    OrganizationRepository organizationRepository;
    private ListValues listValues;
    private ConfigProperty configurationProperty;

    private static final String PARTICIPANT_FIELD_GROUP = "participant";
    private static final String SITE_FIELD_GROUP = "site";

    public Map<String, InputFieldGroup> createFieldGroups(ParticipantInputCommand command) {
        InputFieldGroup participantFieldGroup;
        InputFieldGroup siteFieldGroup;
        RepeatingFieldGroupFactory repeatingFieldGroupFactoryOrg;
        RepeatingFieldGroupFactory repeatingFieldGroupFactorySys;

        siteFieldGroup = new DefaultInputFieldGroup(SITE_FIELD_GROUP);

        Map<Object, Object> options = new LinkedHashMap<Object, Object>();
        options.put("", "Please select");
        List<Organization> organizations = organizationRepository.getOrganizationsHavingStudySites();
        if (organizations != null) {
            options.putAll(WebUtils.collectOptions(organizations, "id", "fullName"));
        }
        siteFieldGroup.getFields().add(InputFieldFactory.createSelectField("organization", "Site", true, options));

        participantFieldGroup = new DefaultInputFieldGroup(PARTICIPANT_FIELD_GROUP);
        participantFieldGroup.getFields().add(InputFieldFactory.createTextField("participant.firstName", "First Name",true));
        participantFieldGroup.getFields().add(InputFieldFactory.createTextField("participant.lastName", "Last Name", true));
        participantFieldGroup.getFields().add(InputFieldFactory.createTextField("participant.maidenName", "Maiden Name", false));
        participantFieldGroup.getFields().add(InputFieldFactory.createTextField("participant.middleName", "Middle Name", false));

        InputField dobYear = InputFieldFactory.createTextField("year", "Year", true);
        InputFieldAttributes.setSize(dobYear, 4);
        InputField dobMonth = InputFieldFactory.createTextField("month", "Month");
        InputFieldAttributes.setSize(dobMonth, 2);
        InputField dobDay = InputFieldFactory.createTextField("day", "Day");
        InputFieldAttributes.setSize(dobDay, 2);

        CompositeField dobField = new CompositeField("participant.dateOfBirth", new DefaultInputFieldGroup(null, "Date of birth").addField(dobYear).addField(dobMonth).addField(dobDay));

        dobField.setRequired(true);
        dobField.getAttributes().put(InputField.HELP, "par.par_create_participant.participant.dateOfBirth");

        participantFieldGroup.getFields().add(dobField);

        participantFieldGroup.getFields().add(InputFieldFactory.createSelectField("participant.gender", "Gender", true, collectOptions(listValues.getParticipantGender())));
        participantFieldGroup.getFields().add(InputFieldFactory.createSelectField("participant.ethnicity", "Ethnicity", true, collectOptions(listValues.getParticipantEthnicity())));
        participantFieldGroup.getFields().add(InputFieldFactory.createSelectField("participant.race", "Race", true, collectOptions(listValues.getParticipantRace())));

        repeatingFieldGroupFactoryOrg = new RepeatingFieldGroupFactory("mainOrg", "participant.organizationIdentifiers");
        repeatingFieldGroupFactorySys = new RepeatingFieldGroupFactory("mainSys", "participant.systemAssignedIdentifiers");

        repeatingFieldGroupFactoryOrg.addField(InputFieldFactory.createTextField("value", "Identifier", true));
        repeatingFieldGroupFactorySys.addField(InputFieldFactory.createTextField("value", "Identifier", true));

        options = new LinkedHashMap<Object, Object>();
        List<Lov> list = configurationProperty.getMap().get("participantIdentifiersType");
        options.put("", "Please select");
        options.putAll(WebUtils.collectOptions(list, "code", "desc"));

        repeatingFieldGroupFactoryOrg.addField(InputFieldFactory.createSelectField("type", "Identifier Type", true, options));
        repeatingFieldGroupFactorySys.addField(InputFieldFactory.createSelectField("type", "Identifier Type", true, options));


        repeatingFieldGroupFactoryOrg.addField(InputFieldFactory.createAutocompleterField("organization", "Organization Identifier", true));
        repeatingFieldGroupFactorySys.addField(InputFieldFactory.createTextField("systemName", "System Name", true));

        repeatingFieldGroupFactoryOrg.addField(InputFieldFactory.createCheckboxField("primaryIndicator", "Primary Indicator"));
        repeatingFieldGroupFactorySys.addField(InputFieldFactory.createCheckboxField("primaryIndicator", "Primary Indicator"));

        InputFieldGroupMap map = new InputFieldGroupMap();
        if (command.getParticipant() != null) {
            map.addRepeatingFieldGroupFactory(repeatingFieldGroupFactoryOrg, command.getParticipant().getOrganizationIdentifiers().size());
            map.addRepeatingFieldGroupFactory(repeatingFieldGroupFactorySys, command.getParticipant().getSystemAssignedIdentifiers().size());
        }

        map.addInputFieldGroup(participantFieldGroup);
        map.addInputFieldGroup(siteFieldGroup);
        return map;
    }

    protected Map<Object, Object> collectOptions(final List<ListValues> list) {
        Map<Object, Object> options = new LinkedHashMap<Object, Object>();
        options.putAll(WebUtils.collectOptions(list, "code", "desc"));
        return options;
    }

    @Override
    public Map<String, Object> referenceData(HttpServletRequest request, T command) {
        Map<String, Object> refdata = super.referenceData(request, command);
        refdata.put("action", "New");
        return refdata;
    }

/*    @Override
    public void postProcess(final HttpServletRequest request, final NewParticipantCommand command, final Errors errors) {
        String action = request.getParameter("_action");
        String selected = request.getParameter("_selected");
        if ("removeIdentifier".equals(action)) {
            NewParticipantCommand newParticipantCommand = command;
            newParticipantCommand.getParticipant().getIdentifiers().remove(
                    Integer.parseInt(selected));
        }
    }*/

    @Override
    protected void validate(T command, BeanWrapper commandBean, Map<String, InputFieldGroup> fieldGroups, Errors errors) {
        boolean hasPrimaryID = false;
        DateValue dob = command.getParticipant().getDateOfBirth();
        if (dob.checkIfDateIsInValid()) {
            errors.rejectValue("participant.dateOfBirth", "REQUIRED", "Incorrect Date Of Birth");
        }

        for (Identifier identifier : command.getParticipant().getIdentifiersLazy()) {
            hasPrimaryID |= identifier.isPrimary();
            if (hasPrimaryID) break;
        }
        if (!hasPrimaryID) errors.rejectValue("participant.identifiers", "REQUIRED", "Please Include at least a single primary Identifier");

    }

    public OrganizationRepository getOrganizationRepository() {
        return organizationRepository;
    }

    public void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
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
        System.out.println("addOrganizationIdentifier");
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
        command.getParticipant().addIdentifier(newIdentifier);
        
        System.out.println("org size after add: " + command.getParticipant().getOrganizationIdentifiers().size());

        return modelAndView;
    }

    public ModelAndView removeOrganizationIdentifier(HttpServletRequest request, Object cmd, Errors error) {
        System.out.println("removeOrganizationIdentifier");
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
        System.out.println("org size after delete: " + command.getParticipant().getOrganizationIdentifiers().size());

        modelAndView.getModel().put("indexes", indexes);
        return modelAndView;
    }

    public ModelAndView addSystemIdentifier(HttpServletRequest request, Object cmd, Errors error) {
        System.out.println("addSystemIdentifier");
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

        System.out.println("sys size after add: " + command.getParticipant().getSystemAssignedIdentifiers().size());

        return modelAndView;
    }

    public ModelAndView removeSystemIdentifier(HttpServletRequest request, Object cmd, Errors error) {
        System.out.println("removeSystemIdentifier");
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        ModelAndView modelAndView = new ModelAndView(getAjaxViewName(request), map);

        ParticipantInputCommand command =(ParticipantInputCommand)cmd;
        List<SystemAssignedIdentifier> list = command.getParticipant().getSystemAssignedIdentifiers();

        System.out.println("before delete: "+list.size());
        list.remove(list.get(command.getIndex()));
        System.out.println("after delete:"+list.size());

        // update the array of remainning indexes after deleting
        int size = list.size();
        Integer[] indexes = new Integer[size];
        for(int i = 0 ; i < size ; i++){
            indexes[i] = i;
        }
        System.out.println("sys size after delete: " + command.getParticipant().getSystemAssignedIdentifiers().size());

        modelAndView.getModel().put("indexes", indexes);
        return modelAndView;
    }

    
}
