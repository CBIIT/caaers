package gov.nih.nci.cabig.caaers.web.ae;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.drools.util.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.validation.Errors;

import gov.nih.nci.cabig.caaers.dao.AdverseEventReportingPeriodDao;
import gov.nih.nci.cabig.caaers.dao.CtcTermDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventCtcTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.Arm;
import gov.nih.nci.cabig.caaers.domain.Attribution;
import gov.nih.nci.cabig.caaers.domain.CtcGrade;
import gov.nih.nci.cabig.caaers.domain.Epoch;
import gov.nih.nci.cabig.caaers.domain.Grade;
import gov.nih.nci.cabig.caaers.domain.Hospitalization;
import gov.nih.nci.cabig.caaers.domain.SolicitedAdverseEvent;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.Term;
import gov.nih.nci.cabig.caaers.domain.TreatmentAssignment;
import gov.nih.nci.cabig.caaers.domain.UserGroupType;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldAttributes;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.fields.MultipleFieldGroupFactory;
import gov.nih.nci.cabig.caaers.web.fields.RepeatingFieldGroupFactory;
import gov.nih.nci.cabig.caaers.web.fields.TabWithFields;
import gov.nih.nci.cabig.caaers.web.participant.AssignParticipantStudyCommand;
import gov.nih.nci.cabig.caaers.web.participant.NewParticipantCommand;

/**
 * 
 * @author Biju Joseph
 *
 */
public class AdverseEventCaptureTab extends AdverseEventTab{
	
	private static final String MAIN_FIELD_GROUP = "main";
	
	public AdverseEventCaptureTab() {
		super("Enter Adverse Events", "Adverse events", "ae/captureAdverseEvents");
		addHelpKeyExclusion("ctcVersion");
	}
	
	
	/**
	 * This method will create the fields to be displayed on the screen.
	 * Notes<br>
	 * 	1. For solicited adverse events, the "Notes/Verbatim", "Other Meddra" will added to the fields.
	 * 	2. If Study is MedDRA, the "Other MedDRA", will not be added in the fields. 	 
	 *  3. We should run the adverse events against the index fixed list, since that list will have null items in it, we should skip if 'AdverseEvent' is null.
	 */
	
	@Override
	public Map<String, InputFieldGroup> createFieldGroups(CaptureAdverseEventInputCommand cmd) {
		
		InputFieldGroupMap map = new InputFieldGroupMap();
		MultipleFieldGroupFactory mainFieldFactory;
		List<SolicitedAdverseEvent> saeList;

		// Creating the field groups for the first section of the page
		// which collects the general information from the user (eg., TAC, TAC Description, Start date of first course etc.
		InputFieldGroup reportingPeriodFieldGroup = new DefaultInputFieldGroup("reportingPeriodFG");
		List<InputField> fields = reportingPeriodFieldGroup.getFields();
		InputField reportingPeriodsField = InputFieldFactory.createSelectField("adverseEventReportingPeriod", "Reporting period", true, fetchReportingPeriodsOptions(cmd));
		fields.add(reportingPeriodsField);
		map.addInputFieldGroup(reportingPeriodFieldGroup);
		
		//create the fields, consisting of reporting period details.
		if(cmd.getAdverseEventReportingPeriod() != null){
			
			InputFieldGroup treatmentAssignmentFieldGroup = new DefaultInputFieldGroup("treatmentAssignmentFG"); 
			InputFieldGroup reportingPeriodDetailsFieldGroup = new DefaultInputFieldGroup("reportingPeriodDetailsFG");
			
			//TAC fields groups
			InputField treatmentAssignmentField = InputFieldFactory.createLabelField("adverseEventReportingPeriod.treatmentAssignment.code", "Treatment assignment");
			InputField treatmentAssignmentDescField = InputFieldFactory.createLabelField("adverseEventReportingPeriod.treatmentAssignment.description", "Treatment description");
			
			//startDateOfFirstCourse - TextField, if it is empty in assignment
			InputField firstCourseDateField = null;
			if(cmd.getAssignment().getStartDateOfFirstCourse() == null){
				firstCourseDateField = InputFieldFactory.createDateField("assignment.startDateOfFirstCourse", "Start date of first course", false);
			}else {
				firstCourseDateField = InputFieldFactory.createLabelField("assignment.startDateOfFirstCourse", "Start date of first course");
			}

			treatmentAssignmentFieldGroup.getFields().add(treatmentAssignmentField);
			treatmentAssignmentFieldGroup.getFields().add(treatmentAssignmentDescField);
			treatmentAssignmentFieldGroup.getFields().add(firstCourseDateField);
		
			// add reportingPeriod details group
			reportingPeriodDetailsFieldGroup.getFields().add(InputFieldFactory.createLabelField("adverseEventReportingPeriod.startDate", "Start date"));
			reportingPeriodDetailsFieldGroup.getFields().add(InputFieldFactory.createLabelField("adverseEventReportingPeriod.endDate", "End date"));
			reportingPeriodDetailsFieldGroup.getFields().add(InputFieldFactory.createLabelField("adverseEventReportingPeriod.epoch.name", "Type"));
			reportingPeriodDetailsFieldGroup.getFields().add(InputFieldFactory.createLabelField("adverseEventReportingPeriod.cycleNumber", "Cycle number"));
			reportingPeriodDetailsFieldGroup.getFields().add(InputFieldFactory.createLabelField("adverseEventReportingPeriod.description", "Description"));
			
			// add the reportingPeriodFieldGroup to the map.
			map.addInputFieldGroup(treatmentAssignmentFieldGroup);
			map.addInputFieldGroup(reportingPeriodDetailsFieldGroup);
		
			
			/*
			 * AdversEvent related field groups,
			 *  the fields are different for Meddra study, Ctc study and Observed AEs
			 */
			mainFieldFactory = new MultipleFieldGroupFactory(MAIN_FIELD_GROUP, "adverseEvents");
			boolean isMeddraStudy = cmd.getStudy().getAeTerminology().getTerm() == Term.MEDDRA;

			int size = cmd.getAdverseEvents().size();
			for(int i = 0; i < size; i++){
				AdverseEvent ae = cmd.getAdverseEvents().get(i);
				if(ae == null) continue;
				
				mainFieldFactory.addField(InputFieldFactory.createLabelField("adverseEventTerm.universalTerm", "Term", true)); //Term
				if(!ae.getSolicited()){
					if(!isMeddraStudy && ae.getAdverseEventTerm().isOtherRequired()){ //only if other is requrired
						mainFieldFactory.addField(InputFieldFactory.createAutocompleterField("lowLevelTerm", "Other(MedDRA)", false));
					}
					InputField notesField = InputFieldFactory.createTextField("detailsForOther", "Notes/Verbatim");
					InputFieldAttributes.setSize(notesField, 50);
					mainFieldFactory.addField(notesField); //Notes
				}
				//grade	
				if(isMeddraStudy){
					mainFieldFactory.addField(InputFieldFactory.createSelectField("grade", "Grade", false,
						createGradeOptions(ae, "Meddra")));
				}else{
					mainFieldFactory.addField(InputFieldFactory.createSelectField("grade", "Grade", false,
							createGradeOptions(ae, "Ctc")));
				}
				
				mainFieldFactory.addField(InputFieldFactory.createSelectField("attributionSummary",
						"Attribution to study", false, createAttributionOptions()));
				mainFieldFactory.addField(InputFieldFactory.createSelectField("hospitalization",
						"Hospitalization", false, createHospitalizationOptions()));
				mainFieldFactory.addField(InputFieldFactory.createSelectField("expected", "Expected", false,
						createExpectedOptions()));
				InputFieldGroup fieldGroup = mainFieldFactory.createGroup(i);
				mainFieldFactory.addFieldGroup(fieldGroup);
				mainFieldFactory.clearFields();
				
			}
			map.addMultipleFieldGroupFactory(mainFieldFactory);
		}
		
		return map;
	}
	


	
	@Override
    public Map<String, Object> referenceData(HttpServletRequest request, CaptureAdverseEventInputCommand command) {
		if(command.getAdverseEventReportingPeriod() != null && command.getAdverseEventReportingPeriod().getAdverseEvents().size() == 0){
			for(SolicitedAdverseEvent sae: command.getAdverseEventReportingPeriod().getEpoch().getArms().get(0).getSolicitedAdverseEvents()){
				AdverseEvent adverseEvent = new AdverseEvent();
				adverseEvent.setReportingPeriod(command.getAdverseEventReportingPeriod());
				if(command.getStudy().getAeTerminology().getTerm() == Term.MEDDRA)
					adverseEvent.setLowLevelTerm(sae.getLowLevelTerm());
				else{
					AdverseEventCtcTerm aeCtcTerm = new AdverseEventCtcTerm();
					aeCtcTerm.setCtcTerm(sae.getCtcterm());
					adverseEvent.setAdverseEventTerm(aeCtcTerm);
					aeCtcTerm.setAdverseEvent(adverseEvent);
					adverseEvent.setSolicited(true);
					adverseEvent.setReportingPeriod(command.getAdverseEventReportingPeriod());
				}
				command.getAdverseEventReportingPeriod().addAdverseEvent(adverseEvent);	
			}
			// Save the reportingPeriod here to persist the solicitedAdverseEvents.
			adverseEventReportingPeriodDao.save(command.getAdverseEventReportingPeriod());
			
			// Setup the categories list for aeTermQuery tag.
			if(command.getCtcCategories().size() == 0)
				command.setCtcCategories(command.getStudy().getAeTerminology().getCtcVersion().getCategories());
		}
		
		Map<String, Object> refdata = super.referenceData(request, command);
		
		if(command.getAdverseEventReportingPeriod() != null && command.getAdverseEventReportingPeriod().getAdverseEvents().size() > 0){
			// Put a flag in the map "hasObservedEvents". This will be used to determine whether the table headers should be displayed
			// for observed events and displaying the existing observed events.
			
			boolean hasObservedEvents = false;
			for(AdverseEvent ae: command.getAdverseEventReportingPeriod().getAdverseEvents()){
				if(!ae.getSolicited())
					hasObservedEvents = true;
			}
			refdata.put("hasObservedEvent", hasObservedEvents);
		}
		
		
		
		return refdata;
	}
	
	@Override
	public void onBind(HttpServletRequest request,CaptureAdverseEventInputCommand command, Errors errors) {
		String rpId = request.getParameter("adverseEventReportingPeriod");
		if(StringUtils.isEmpty(rpId)) {
			command.setAdverseEventReportingPeriod(null);
		}
	}
	
	@Override
    protected void validate(CaptureAdverseEventInputCommand command, BeanWrapper commandBean,
                    Map<String, InputFieldGroup> fieldGroups, Errors errors) {

//		// firstStartDateField should be present for non-baseline reporting periods.
//		if((command.getAdverseEventReportingPeriod() != null) && (command.getAdverseEventReportingPeriod().getEpoch().getName().equals("Baseline"))){
//			InputField firstStartDateField = fieldGroups.get("main0").getFields().get(1);
//			errors.rejectValue(firstStartDateField.getPropertyName(), "REQUIRED",
//                            firstStartDateField.getDisplayName() + " required for primary AE");
//		}
//		
//		// test: if(grade == not evaluated), other fields shouldnt be entered.
//		if(command.getAdverseEventReportingPeriod().getAdverseEvents() != null && command.getAdverseEventReportingPeriod().getAdverseEvents().size() > 0){
//			for(AdverseEvent ae: command.getAdverseEventReportingPeriod().getAdverseEvents()){
//				if(ae.getGrade() != null && ae.getGrade().getName().equals("Not Evaluated")){
//					// Check if other field values are entered. Incase they are, an error should be displayed.
//				}
//			}
//		}
	}
}