package gov.nih.nci.cabig.caaers.web.participant;

import gov.nih.nci.cabig.caaers.dao.query.ParticipantQuery;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.repository.ParticipantRepository;
import gov.nih.nci.cabig.caaers.web.fields.TabWithFields;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.ListValues;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.BeanWrapper;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AssignParticipantTab extends TabWithFields<AssignParticipantStudyCommand> {

    private static final Log log = LogFactory.getLog(AssignParticipantTab.class);
    private ParticipantRepository participantRepository;
    protected ListValues listValues;

    public AssignParticipantTab() {
        super("Search for subject", "Search Subject", "par/reg_participant_search");
    }

    @Override
    public void postProcess(HttpServletRequest request, AssignParticipantStudyCommand command, Errors errors) {
        super.postProcess(request, command, errors);
    }

    protected void validate(AssignParticipantStudyCommand command, BeanWrapper commandBean, Map<String, InputFieldGroup> fieldGroups, Errors errors) {
        super.validate(command, commandBean, fieldGroups, errors);
        if (command.getParticipant() == null || command.getParticipant().getId() == null) errors.rejectValue("participant", "REQUIRED", "Subject not selected");
    }

    public Map<String, InputFieldGroup> createFieldGroups(AssignParticipantStudyCommand command) {
        InputFieldGroupMap map = new InputFieldGroupMap();
        return map;
    }

    @Required
    public void setParticipantRepository(final ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public ModelAndView searchSubjects(HttpServletRequest request, Object cmd, Errors error) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        return new ModelAndView(getAjaxViewName(request), map);
    }

    public ListValues getListValues() {
        return listValues;
    }

    public void setListValues(ListValues listValues) {
        this.listValues = listValues;
    }

    public Map<String, Object> referenceData(HttpServletRequest request, AssignParticipantStudyCommand command) {

        Map<String, Object> refdata = super.referenceData(command);

        String searchText = command.getSearchText();
        String searchType = command.getSearchType();

        if (searchText != null && searchType != null && !searchText.trim().equals("") && searchText.trim().length() >=2) {

                ParticipantQuery participantQuery = new ParticipantQuery();
                if ("fn".equals(searchType)) {
                    participantQuery.filterByFirstName(searchText);
                } else if ("ln".equals(searchType)) {
                    participantQuery.filterByLastName(searchText);
                } else if ("idtf".equals(searchType)) {
                    participantQuery.leftJoinFetchOnIdentifiers();
                    participantQuery.filterByIdentifierValue(searchText);
                }

                try {
                    command.setParticipantSearchResults(participantRepository.searchParticipant(participantQuery));
                } catch (Exception e) {
                    log.error("Error while searching participants", e);
                }

                command.setSearchText("");
        }
        
        return refdata;
        
    }
}