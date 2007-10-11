package gov.nih.nci.cabig.caaers.web.study;

import gov.nih.nci.cabig.caaers.domain.Agent;
import gov.nih.nci.cabig.caaers.domain.InvestigationalNewDrug;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyAgent;
import gov.nih.nci.cabig.caaers.domain.StudyAgentINDAssociation;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldAttributes;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanWrapper;
import org.springframework.validation.Errors;

/**
 * @author Rhett Sutphin
 */
public class AgentsTab extends StudyTab {
	public static int IND_TYPE_NOT_USED = 0;

	public static int IND_TYPE_CTEP = 1;

	public static int IND_TYPE_OTHER = 2;

	public static int CTEP_IND = -111;

	private LinkedHashMap<Object, Object> indTypeMap = new LinkedHashMap<Object, Object>();

	public AgentsTab() {
		super("Study Agents", "Agents", "study/study_agents");
		// setAutoPopulateHelpKey(true);
		indTypeMap.put(AgentsTab.IND_TYPE_NOT_USED, "Not Used");
		indTypeMap.put(AgentsTab.IND_TYPE_CTEP, "CTEP IND");
		indTypeMap.put(AgentsTab.IND_TYPE_OTHER, "Other");
	}

	@Override
	public void postProcess(final HttpServletRequest request, final Study command, final Errors errors) {
		handleStudyAgentAction(command, request.getParameter("_action"), request.getParameter("_selected"), request
				.getParameter("_selectedInd"));
	}

	@Override
	protected void validate(final Study command, final BeanWrapper commandBean,
			final Map<String, InputFieldGroup> fieldGroups, final Errors errors) {

		boolean isAgentEmpty = false;
		StudyAgent studyAgent = null;
		List<StudyAgent> studyAgents = command.getStudyAgents();
		for (int i = 0; i < command.getStudyAgents().size(); i++) {
			studyAgent = studyAgents.get(i);
			if (studyAgent.getAgent() == null && studyAgent.getOtherAgent() == null) {
				isAgentEmpty = true;
				errors.rejectValue("studyAgents[" + i + "].otherAgent", "REQUIRED", "Select either Agent or Other ");
			}
			if (studyAgent.getAgent() != null) {
				studyAgent.setOtherAgent(null);
			}

		}

		if (isAgentEmpty) {
			errors.rejectValue("studyAgents", "REQUIRED", "One or more Agents are missing or not in list");
		}

	}

	@Override
	public Map<String, InputFieldGroup> createFieldGroups(final Study command) {
		InputFieldGroupMap map = new InputFieldGroupMap();
		String baseName = "studyAgents";
		int i = -1;
		for (StudyAgent sa : command.getStudyAgents()) {
			i++;
			InputFieldGroup fieldGrp = new DefaultInputFieldGroup("main" + i);
			List<InputField> fields = fieldGrp.getFields();
			InputField agentField = InputFieldFactory.createAutocompleterField(baseName + "[" + i + "].agent", "Agent",
					false);
			InputFieldAttributes.setSize(agentField, 70);
			agentField.getAttributes().put(InputField.ENABLE_CLEAR, false);
			fields.add(agentField);

			InputField otherAgentField = InputFieldFactory.createTextField(baseName + "[" + i + "].otherAgent",
					"Other", false);
			InputFieldAttributes.setSize(otherAgentField, 70);
			fields.add(otherAgentField);

			InputField indTypeField = InputFieldFactory.createSelectField(baseName + "[" + i + "].indType",
					"Enter IND information", indTypeMap);
			fields.add(indTypeField);
			if (sa.getStudyAgentINDAssociations() != null) {
				int j = -1;
				for (StudyAgentINDAssociation saInd : sa.getStudyAgentINDAssociations()) {
					// dont show IND field for CTEP unspecified IND
					InvestigationalNewDrug ind = saInd.getInvestigationalNewDrug();
					if (ind != null && ind.getIndNumber() != null && ind.getIndNumber() == CTEP_IND) {
						continue;
					}
					j++;
					InputField indField = InputFieldFactory.createAutocompleterField(baseName + "[" + i
							+ "].studyAgentINDAssociations[" + j + "].investigationalNewDrug", "IND #", true);
					indField.getAttributes().put(InputField.ENABLE_CLEAR, true);
					InputFieldAttributes.setSize(indField, 11);
					fields.add(indField);
				}
			}// ~if
			map.addInputFieldGroup(fieldGrp);
		}
		return map;
	}

	private void handleStudyAgentAction(final Study study, final String action, final String selected,
			final String selectedInd) {
		if ("addStudyAgent".equals(action)) {
			StudyAgent studyAgent = new StudyAgent();
			studyAgent.setAgent(new Agent());
			study.addStudyAgent(studyAgent);
		}
		else if ("removeStudyAgent".equals(action)) {
			study.getStudyAgents().remove(Integer.parseInt(selected));
		}
		else if ("removeInd".equals(action)) {
			StudyAgent sa = study.getStudyAgents().get(Integer.parseInt(selected));
			List<StudyAgentINDAssociation> sas = sa.getStudyAgentINDAssociations();
			if (sas.size() > 0) {
				sas.remove(Integer.parseInt(selectedInd));
			}
		}
	}
}
