/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.dao.AdverseEventReportingPeriodDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Grade;
import gov.nih.nci.cabig.caaers.domain.Person;
import gov.nih.nci.cabig.caaers.domain.PostAdverseEventStatus;
import gov.nih.nci.cabig.caaers.domain.User;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.domain.repository.PersonRepository;
import gov.nih.nci.cabig.caaers.domain.repository.UserRepository;
import gov.nih.nci.cabig.caaers.security.SecurityUtils;
import gov.nih.nci.cabig.caaers.web.RenderDecisionManager;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;
import gov.nih.nci.cabig.caaers.web.validation.validator.WebControllerValidator;
import gov.nih.nci.cabig.ctms.web.chrome.Task;
import gov.nih.nci.cabig.ctms.web.tabs.FlowFactory;
import gov.nih.nci.cabig.ctms.web.tabs.Tab;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Webflow for ExpeditedAdverseEventReport modification is maintained by this controller. 
 * @author Rhett Sutphin
 * @author Biju Joseph
 */
public class EditAdverseEventController extends AbstractAdverseEventInputController {

    private Task task;
	//validator needs to be called in onBindAndValidate()
	protected WebControllerValidator webControllerValidator;
	private static final String ACTION_PARAMETER = "action";

    private AdverseEventReportingPeriodDao adverseEventReportingPeriodDao;
    private PersonRepository personRepository;
    private UserRepository userRepository;

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

	public EditAdverseEventController() {
        setCommandClass(EditExpeditedAdverseEventCommand.class);
        setBindOnNewForm(true);
    }

    @Override
    protected FlowFactory<ExpeditedAdverseEventInputCommand> createFlowFactory() {
        return new ExpeditedFlowFactory("Edit expedited report");
    }


    /**
     * The current task that we are on has to be set in the context for hilighting. 
     * @param request - The HttpServletRequest
     * @param oCommand - The ExpeditedAdverseEventInputCommand object
     * @param errors   - The Spring Errors object
     * @param page     - The current page number in the flow
     * @return         - A Map consisting of additional information that should be added to the context
     * @throws Exception
     */
    @Override
    protected Map referenceData(HttpServletRequest request, Object oCommand, Errors errors, int page) throws Exception {
        Map<String, Object> refdata = super.referenceData(request, oCommand, errors, page);
        refdata.put("currentTask", task);
        return refdata;
    }

    
    /**
     * Will generate the command object that we have to work on. 
     * @param request  - HttpServletRequest  object
     * @return   - An EditExpeditedAdverseEventCommand instance. 
     * @throws Exception
     */
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
    	
    	RenderDecisionManager renderDecisionManager = renderDecisionManagerFactoryBean.getRenderDecisionManager();
    	EditExpeditedAdverseEventCommand command = new EditExpeditedAdverseEventCommand(expeditedAdverseEventReportDao,studyDao, reportDefinitionDao,
    				assignmentDao, reportingPeriodDao, expeditedReportTree, renderDecisionManager, reportRepository,
                adverseEventRoutingAndReviewRepository, evaluationService, personRepository, userRepository);
    	command.setWorkflowEnabled(getConfiguration().get(getConfiguration().ENABLE_WORKFLOW));
    	
        if(request.getParameter("aeReport") != null){
           int reportId = WebUtils.getIntParameter(request,"aeReport");
            ExpeditedAdverseEventReport aeReport = expeditedAdverseEventReportDao.getById(reportId);
          //(CAAERS-5865)to perform sync only for ctep-esys studies, 
            //set studyOutOfSync to false, so sync will not run for non-ctep-esys studies
            if(aeReport.getStudy().getCtepEsysIdentifier() == null) {
            	command.setStudyOutOfSync(false);
            }
            command.setAeReport(aeReport);
            //initializing the review comments collection
            for(Report r: command.getAeReport().getActiveReports()){
            	r.getReviewCommentsInternal().size();
            }
        }

        return command;
    }
    
    /**
     * This method will do the following, make the command to be in a consistent state. 
     * 
     * If from Review Report page?
     *  1.- If this is a new data collection:
     *    1.1. Create Expedited Report, associate it with Reporting period.
     *    1.2. Initialize the Treatment information
     *    1.3. Initialize the reporter.
     *  2. Add/Remove adverse events. 
     *  3. Find the mandatory sections.  
     *  4. Pre-initialize the mandatory section fields.    
     *   
     * If from Manage reports page?
     *  1. Find the mandatory sections
     *  2. Refresh/Initialize the Not applicable and mandatory fields. 
     *  
     */
    @Override
    protected void onBindOnNewForm(HttpServletRequest request, Object cmd) throws Exception {
        super.onBindOnNewForm(request, cmd);
        
        HttpSession session = request.getSession();
        
        String screenFlowSource = request.getParameter("from");
        
        EditExpeditedAdverseEventCommand command = (EditExpeditedAdverseEventCommand) cmd;
        command.setScreenFlowSource(screenFlowSource);
        
        
    	command.getNewlySelectedReportDefinitions().clear();
    	command.getSelectedReportDefinitions().clear();
    	command.getApplicableReportDefinitions().clear();
    	
    	
    	ReviewAndReportResult reviewResult = (ReviewAndReportResult)session.getAttribute("reviewResult"); 
        ExpeditedAdverseEventReport aeReport = command.getAeReport();
        if( (reviewResult != null) && StringUtils.equals("captureAE", screenFlowSource)){
        	
        	//If a new data collection?
        	if(reviewResult.getAeReportId() == 0){
        		//create expedited report.
        		aeReport = new ExpeditedAdverseEventReport();
        		aeReport.setCreatedAt(nowFactory.getNowTimestamp());
        		command.setAeReport(aeReport);
        		
        		//populate the reporting period.
        		AdverseEventReportingPeriod adverseEventReportingPeriod = adverseEventReportingPeriodDao.getById(reviewResult.getReportingPeriodId());
            	command.getAeReport().setReportingPeriod(adverseEventReportingPeriod);
        		command.getAeReport().synchronizeMedicalHistoryFromAssignmentToReport();
        		
        		//initialize treatment information
        		command.initializeTreatmentInformation();
        		
        		//set the default reporter as the logged-in person
        		String loginId = SecurityUtils.getUserLoginName();
                if(loginId != null){
                   Person loggedInPerson = getPersonRepository().getByLoginId(loginId);
                   command.getAeReport().getReporter().copy(loggedInPerson);
                   if(loggedInPerson == null){
                       User loggedInUser = getUserRepository().getUserByLoginName(loginId);
                       command.getAeReport().getReporter().copy(loggedInUser);
                   }
                }

               	
        	}
        	
        	//Add all the aes to be added 
        	for(Integer aeId : reviewResult.getAeList()){
        		AdverseEvent ae = command.getAdverseEventReportingPeriod().findAdverseEventById(aeId);
        		if(aeReport.findAdverseEventById(aeId) == null){
        			aeReport.addAdverseEvent(ae);
        		}
        	}
        	
        	//remove all the aes to be removed
        	for(Integer aeId : reviewResult.getUnwantedAEList()){
        		AdverseEvent ae = aeReport.findAdverseEventById(aeId);
        		if(ae != null && aeReport.getAdverseEvents().remove(ae)){
        			ae.clearAttributions();
        			ae.setReport(null);
        		}
        	}
        	
        	//modify the primary ae if necessary
            command.makeAdverseEventPrimary(reviewResult.getPrimaryAdverseEventId());
          
        	//reload- the report definitions (from create & edit list)
            for(ReportDefinition rd : reviewResult.getCreateList()){
            	ReportDefinition loaded = reportDefinitionDao.getById(rd.getId());
            	reportDefinitionDao.initialize(loaded);
            	command.getSelectedReportDefinitions().add(loaded);
            	command.getNewlySelectedReportDefinitions().add(loaded);
            }
            for(ReportDefinition rd : reviewResult.getEditList()){
            	ReportDefinition loaded = reportDefinitionDao.getById(rd.getId());
            	command.getSelectedReportDefinitions().add(loaded);
            }
            
            //update the applicable report definitions.
            command.getApplicableReportDefinitions().addAll(command.getSelectedReportDefinitions());
           
            
        }else{
        	//from manage reports / review and reports, so do cleanup of session attributes explicitly
        	session.removeAttribute("reviewResult");
        	
        	String action = request.getParameter(ACTION_PARAMETER);
        	String strReportId = request.getParameter("report");
        	int reportId = -999;
        	if(StringUtils.isNumeric(strReportId)){
        		reportId = Integer.parseInt(strReportId);
        	}
        	
        	//find the report. 
        	Report selectedReport = aeReport.findReportById(reportId);
        	
        	if(selectedReport  != null){
        		command.getSelectedReportDefinitions().add(selectedReport.getReportDefinition());
        		if(!selectedReport.isActive()){
        			//if selected report is not active, add it into applicable reports
        			command.getApplicableReportDefinitions().add(selectedReport.getReportDefinition());
        		}
        	
        		for(Report report : aeReport.getActiveReports()){
        			if(!command.getApplicableReportDefinitions().contains(report.getReportDefinition()))
        				command.getApplicableReportDefinitions().add(report.getReportDefinition());
        		}
        		
        		//pre initialize all the report mandatory fields.
        		for(ReportDefinition reportDefinition : command.getApplicableReportDefinitions()){
        			reportDefinition.getMandatoryFields().size();;
        		}
        		

            	//if action is amend, keep a mock reviewResult in session. 
            	if(StringUtils.equals(action, "amendReport")&& selectedReport.isSubmitted()){
            		command.getNewlySelectedReportDefinitions().add(selectedReport.getReportDefinition());
            		reviewResult = new ReviewAndReportResult();
            		reviewResult.getAmendList().add(selectedReport.getReportDefinition());
            		reviewResult.getReportsToAmmendList().add(selectedReport);
            		reviewResult.getCreateList().add(selectedReport.getReportDefinition());
            		session.setAttribute("reviewResult", reviewResult);
            	}
            	
        	}
        
        	
        }
       
        //synchronize the outcomes
        command.updateOutcomes();
        
        //find the mandatory sections.
        command.refreshMandatorySections();
        
    	
        if(aeReport.getId() == null){
        	//present status. 
        	for(AdverseEvent ae : aeReport.getAdverseEvents()){
                if (ae.getGrade() == null) continue;
        		if(ae.getGrade().equals(Grade.DEATH)){
        			aeReport.getResponseDescription().setPresentStatus(PostAdverseEventStatus.DEAD);
        			break;
        		}
        	}
        }
        
        // pre-init the mandatory section fields & set present status
        if(!command.getNewlySelectedReportDefinitions().isEmpty() || command.getAeReport().isActive()){
        	 command.initializeMandatorySectionFields();
        }
        
        //will pre determine the display/render-ability of fields 
        command.updateFieldMandatoryness();
        
        //CAAERS-5865, if study is not CTEP-ESYS study, set outOfSync flag as false,
        //so syncing process will not be triggered
        if(command.getAeReport().getStudy().getCtepEsysIdentifier() == null) {
        	command.setStudyOutOfSync(false);
        }

    }

    /**
     * Overriden to handle switching of reporting context.
     * Note:- Will update the mandatory sections and fields 
     * 
     */
    @Override
    protected void onBind(HttpServletRequest request, Object command, BindException errors) throws Exception {
        super.onBind(request, command, errors);
        //bind the context report definitions
        int[] rdIds = {-9999};
        try {
			rdIds = ServletRequestUtils.getIntParameters(request, "reportingContextRdId");
		} catch (Exception e) {
		}
		
		EditExpeditedAdverseEventCommand editCommand = (EditExpeditedAdverseEventCommand) command;
		//AJAX requests will not have reporting context information
		if(!(isAjaxRequest(request) || request.getParameter(AJAX_SUBVIEW_PARAMETER) != null) ){
			editCommand.getSelectedReportDefinitions().clear();
			for(ReportDefinition rd : editCommand.getApplicableReportDefinitions()){
				if(ArrayUtils.contains(rdIds, rd.getId().intValue())){
					editCommand.getSelectedReportDefinitions().add(rd);
				}
			}
		}
		
		
		//now refresh the not applicable/mandatory fields.
		editCommand.refreshMandatorySections();
		editCommand.updateFieldMandatoryness();
    }

    /**
     * Will be invoked when the web flow ends, will take the user to List AdvervesEvents page. 
     * @param request - HttpServletRequest - the request object
     * @param response - HttpServletResponse  - the response object
     * @param oCommand - The command object. 
     * @param errors   - The Spring errors object
     * @return         - A ModelAndView pointing to List Adverse Events page. 
     * @throws Exception
     */
    @Override
    @SuppressWarnings("unchecked")
    protected ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response, Object oCommand, BindException errors) throws Exception {
        EditExpeditedAdverseEventCommand command = (EditExpeditedAdverseEventCommand) oCommand;

        // everything is saved as you move from page to page, so no action required here
        Map<String, Object> model = new ModelMap("participant", command.getParticipant().getId());
        model.put("study", command.getStudy().getId());
        return new ModelAndView("redirectToAeList", model);
    }

    /**
     * Will call the validate method on web controller.
     */
    @Override
	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors, int page) throws Exception {
		super.onBindAndValidate(request, command, errors, page);
		webControllerValidator.validate(request, command, errors);
	}

    /**
     * Supress the validation while navigating back in the flow. `
     * @param request                                           \
     * @param command
     * @return
     */
    @Override
    protected boolean suppressValidation(HttpServletRequest request,Object command) {
    	 if (super.suppressValidation(request, command)) return true;
    	 EditExpeditedAdverseEventCommand aeCommand = (EditExpeditedAdverseEventCommand) command;
    	 
    	  //special case, if it is attribution page allow go back and forward
    	 String shortTitle = getFlow(aeCommand).getTab(getCurrentPage(request)).getShortTitle();
    	 if(shortTitle.equals(ExpeditedReportSection.ATTRIBUTION_SECTION.getDisplayName())){
    		 return true;
    	 }
    	 
    	//any page allow going backward
    	 if(super.getCurrentPage(request) > WebUtils.getTargetPage(request)){
    		 return true;
    	 }
         
         return false;
    }

    @Override
    protected boolean shouldSave(HttpServletRequest request, ExpeditedAdverseEventInputCommand command, Tab<ExpeditedAdverseEventInputCommand> tab) {
        String task = request.getParameter("task");
        if(StringUtils.equals("remove", task)) return true;   //for delete requests save the object.
        return super.shouldSave(request, command, tab);
    }

    @Override
    protected ExpeditedAdverseEventInputCommand save(ExpeditedAdverseEventInputCommand command, Errors errors) {
    	command.save();
    	return null;
    }
    
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
	
	@Required
	public void setWebControllerValidator(WebControllerValidator webControllerValidator) {
	    this.webControllerValidator = webControllerValidator;
	}
	
	/**
     * Returns the value associated with the <code>attributeName</code>, if present in
     * HttpRequest parameter, if not available, will check in HttpRequest attribute map.
     */
    protected Object findInRequest(final ServletRequest request, final String attributName) {

        Object attr = request.getParameter(attributName);
        if (attr == null) {
            attr = request.getAttribute(attributName);
        }
        return attr;
    }
    
    public void setAdverseEventReportingPeriodDao(AdverseEventReportingPeriodDao adverseEventReportingPeriodDao){
    	this.adverseEventReportingPeriodDao = adverseEventReportingPeriodDao;
    }
    
    public AdverseEventReportingPeriodDao getAdverseEventReportingPeriodDao(){
    	return adverseEventReportingPeriodDao;
    }

}
