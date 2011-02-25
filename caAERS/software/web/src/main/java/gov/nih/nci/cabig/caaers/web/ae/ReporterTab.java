package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.domain.repository.*;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection;
import gov.nih.nci.cabig.caaers.security.SecurityUtils;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldAttributes;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;

/**
 * @author Kulasekaran
 * @author Rhett Sutphin
 * @author Biju Joseph
 */
public class ReporterTab extends AeTab {
    private static final Log log = LogFactory.getLog(ReporterTab.class);
    private PersonRepository personRepository;
    private UserRepository userRepository;


    public ReporterTab() {
        super(ExpeditedReportSection.REPORTER_INFO_SECTION.getDisplayName(), "Reporter", "ae/reporter");
    }

    @Override
    public ExpeditedReportSection[] section() {
        return new ExpeditedReportSection[] {ExpeditedReportSection.REPORTER_INFO_SECTION};
    }

    @Override
    protected void createFieldGroups(AeInputFieldCreator creator, ExpeditedAdverseEventInputCommand command) {
        createPersonGroup(creator, "reporter");
        createPersonGroup(creator, "physician");
    }
    
    @Override
    public Map<String, Object> referenceData(HttpServletRequest request,ExpeditedAdverseEventInputCommand command) {
    	Map<String, Object> refData =  super.referenceData(request, command);
    
    	List<ResearchStaff> researchStaffList = new ArrayList<ResearchStaff>();
    	List<Investigator> investigatorList = new ArrayList<Investigator>();
    	
    	Set<ResearchStaff> researchStaffSet = new HashSet<ResearchStaff>();
    	Set<Investigator> investigatorSet = new HashSet<Investigator>();

        HashSet<ResearchStaff> temporaryRsSet = new HashSet<ResearchStaff>();
        for(SiteResearchStaff srs : command.getAssignment().getStudySite().getOrganization().getSiteResearchStaffs()){
           ResearchStaff rs = srs.getResearchStaff();
           if(!temporaryRsSet.contains(rs)){
              if(rs.isUser()){
                 try{
                    User user = userRepository.getUserByLoginName(rs.getCaaersUser().getLoginName());
                    if(user.hasRole(UserGroupType.ae_reporter)) researchStaffSet.add(rs);
                 }catch(CaaersSystemException ignore){
                     //just continue the loop.. 
                 }
              }

           }
           temporaryRsSet.add(rs);
        }

    	for (StudyInvestigator sInvestigator: command.getAssignment().getStudySite().getActiveStudyInvestigators()){
    		//investigatorList.add(sInvestigator.getSiteInvestigator().getInvestigator());
    		investigatorSet.add(sInvestigator.getSiteInvestigator().getInvestigator());
    	}

    	Iterator<ResearchStaff> rIterator = researchStaffSet.iterator();
    	while(rIterator.hasNext()){
    		researchStaffList.add(rIterator.next());
    	}
    	
    	Iterator<Investigator> invIterator = investigatorSet.iterator();
    	while(invIterator.hasNext()){
    		investigatorList.add(invIterator.next());
    	}
    	
    	//Sort the researchStaff and investigators list
    	Collections.sort(researchStaffList);
    	Collections.sort(investigatorList);
    	
    	refData.put("researchStaffList", researchStaffList);
    	refData.put("investigatorList", investigatorList);
    	
    	//set the reporter, as the login person
        String loginId = SecurityUtils.getUserLoginName();
        Person loggedInPerson = null;
        if(loginId != null){
           loggedInPerson =  personRepository.getByLoginId(loginId);
     	   refData.put("validPersonnel", loggedInPerson != null);
           refData.put("loggedInPersonId", loggedInPerson != null ? loggedInPerson.getId() : 0);

        }

        Person reporter = command.getAeReport().getReporter().getPerson();
        Person physician = command.getAeReport().getPhysician().getPerson();

        int reporterPersonId = (reporter != null) ? reporter.getId(): loggedInPerson.getId();
        refData.put("reporterPersonId", reporterPersonId);
        
        refData.put("reporterIsResearchStaff", reporter != null && reporter instanceof ResearchStaff);
        refData.put("reporterIsInvestigator", reporter != null && reporter instanceof Investigator);

        int physicianPersonId = (physician != null) ? physician.getId() : 0;
        refData.put("physicianPersonId", physicianPersonId);
        
        refData.put("physicianAPerson", physician != null);
        refData.put("editFlow", command.getAeReport().getId() != null);

    	return refData;
    }

    private void createPersonGroup(AeInputFieldCreator creator, String person) {
        String base = person + '.';
        InputField title = InputFieldFactory.createTextField(base + "title", "Position Title", false);
        InputFieldAttributes.setSize(title, 50);
        InputField firstNameField = InputFieldFactory.createTextField(base + "firstName", "First name", true);
        InputField middleNameField = InputFieldFactory.createTextField(base + "middleName", "Middle name", false);
        InputField lastNameField = InputFieldFactory.createTextField(base + "lastName", "Last name", true);
        InputField emailField = createContactField(base, ReportPerson.EMAIL, "E-mail address", true);
        InputFieldAttributes.setSize(emailField, 50);

        InputField phoneField = createContactField(base, ReportPerson.PHONE, false);
        phoneField.getAttributes().put(InputField.EXTRA_VALUE_PARAMS, "phone-number");

        InputField faxField = createContactField(base, ReportPerson.FAX, false);
        faxField.getAttributes().put(InputField.EXTRA_VALUE_PARAMS, "phone-number");

        InputField streetField = InputFieldFactory.createTextField(base + "address.street", "Street");
        InputFieldAttributes.setColumns(streetField, 50);

        InputField cityField = InputFieldFactory.createTextField(base + "address.city", "City");
        InputFieldAttributes.setColumns(cityField, 50);

        InputField stateField = InputFieldFactory.createTextField(base + "address.state", "State");
        InputFieldAttributes.setColumns(stateField, 50);

        InputField zipField = InputFieldFactory.createZipCodeField(base + "address.zip", "Zip", false);
        InputFieldAttributes.setColumns(zipField, 5);

        creator.createFieldGroup(person, StringUtils.capitalize(person) + " details", title, firstNameField, middleNameField, lastNameField, emailField, phoneField, faxField, streetField, cityField, stateField, zipField);
    }

    private InputField createContactField(String base, String contactType, boolean required) {
        return createContactField(base, contactType, StringUtils.capitalize(contactType), required);
    }

    private InputField createContactField(String base, String contactType, String displayName, boolean required) {
        if (contactType.equals(ReportPerson.PHONE) || contactType.equals(ReportPerson.FAX)) {
            return InputFieldFactory.createPhoneField(base + "contactMechanisms[" + contactType + ']', displayName, required);
        } else {
            return InputFieldFactory.createEmailField(base + "contactMechanisms[" + contactType + ']', displayName, required);
        }
    }
    
    
    public void processReports(HttpServletRequest request,EditExpeditedAdverseEventCommand command){
    	ReviewAndReportResult reviewResult = (ReviewAndReportResult)request.getSession().getAttribute("reviewResult"); 
    	if(reviewResult != null){
    		//- remove review result from session.
			request.getSession().removeAttribute("reviewResult"); 
			
    		//modify the signatures of the adverse events in this report.
	    	command.getAeReport().updateSignatureOfAdverseEvents();
	    

	    	//save the data collection. 
	    	command.save();
	    	
	    	//process the reports
	    	reviewResult.updateBaseDateOnCreateList(command.getNewlySelectedReportDefinitions());
	    	reportRepository.processReports(command.getAeReport(), reviewResult.getReportsToAmmendList(), reviewResult.getReportsToUnAmendList(), 
	    			reviewResult.getReportsToWithdraw(), command.getNewlySelectedReportDefinitions());
    	}
    	
    }
    
    @Override
    public void onDisplay(HttpServletRequest request,ExpeditedAdverseEventInputCommand command) {
    	
    	if(!(command instanceof EditExpeditedAdverseEventCommand)) return;
    	
    	ExpeditedAdverseEventReport aeReport = command.getAeReport();
    	if(aeReport.getId() != null && aeReport.getReporter().isSavable()){
    		processReports(request, (EditExpeditedAdverseEventCommand)command);
    	}
    }

    /**
     * There is a chance of modification in reporter page, so suppress the warning
     * if it is from capture adverse events. 
     */
    @Override
    public String generateWarningMessage(ExpeditedAdverseEventInputCommand command) {
    	if(command instanceof EditExpeditedAdverseEventCommand){
    		String screenFlowSource = ((EditExpeditedAdverseEventCommand)command).getScreenFlowSource();
    		if(StringUtils.equals("captureAE", screenFlowSource)){
    			return null;
    		}
    	}
    	return super.generateWarningMessage(command);
    }
    
    /**
     * 
     */
    @Override
    public void postProcess(HttpServletRequest request, ExpeditedAdverseEventInputCommand cmd, Errors errors) {
    	//only process if there are no validation errors.
    	if(errors.hasErrors()) return;
    	if(!(cmd instanceof EditExpeditedAdverseEventCommand)) return;
    	
    	EditExpeditedAdverseEventCommand command = (EditExpeditedAdverseEventCommand) cmd;
    	processReports(request, (EditExpeditedAdverseEventCommand)command);
    }


    public PersonRepository getPersonRepository() {
        return personRepository;
    }

    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
