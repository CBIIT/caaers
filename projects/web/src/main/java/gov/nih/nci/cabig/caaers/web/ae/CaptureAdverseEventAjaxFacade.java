package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventCtcTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventMeddraLowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.CtcTerm;
import gov.nih.nci.cabig.caaers.domain.Term;
import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;
import gov.nih.nci.cabig.caaers.tools.ObjectTools;
import gov.nih.nci.cabig.caaers.web.dwr.AjaxOutput;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CaptureAdverseEventAjaxFacade  extends CreateAdverseEventAjaxFacade{
	
	 private static Class<?>[] CONTROLLERS = { 	CaptureAdverseEventController.class   };
	 
	 @Override
	public Class<?>[] controllers() {
		return CONTROLLERS;
	}
	 
    /**
     * This function is called to fetch the content associated to a reporting period
     *   -  after we create a new reporting period
     *   -  after we select a reporting period from the combo box.
     *   
     *   A little bit on the working, 
     *     - Will refresh the assignment object, (to support newly added Reporting period ordering)
     *     - Will fetch the content associated to the reporting period by calling captureAdverseEventDetailSection.jsp
     * @param reportingPeriodId
     * @return
     */
    
    public AjaxOutput refreshReportingPeriodAndGetDetails(int reportingPeriodId, boolean fetchOnlyDetails){
    	CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand)extractCommand();
    	command.refreshAssignment(reportingPeriodId);
    	
    	List<AdverseEventReportingPeriod> rpList = ObjectTools.reduceAll(command.getAssignment().getReportingPeriods(), "id", "startDate" , "endDate", "name");
    	AjaxOutput output = new AjaxOutput();
    	output.setObjectContent(rpList);
    	
    	//get the content for the below html section. 
    	
    	Map<String, String> params = new LinkedHashMap<String, String>(); // preserve order for testing
    	params.put("adverseEventReportingPeriod", "" + reportingPeriodId);
    	String html = renderAjaxView("captureAdverseEventDetailSection", 0, params);
    	output.setHtmlContent(html);
    	return output;
    }
    /**
     * Create AdverseEvent objects corresponding to the terms(listOfTermIDs).
     *  Add the following parameters to request :- 
     *     1. "index" - corresponds to begin (of AE).
     *     2. "ajaxView" - 'observedAdverseEventSection'
     *  
     * @param listOfTermIDs
     * @return
     */
    public String addObservedAE(int[] listOfTermIDs) {
        
        CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
        int index = command.getAdverseEvents().size();
        
        List<Integer> filteredTermIDs = new ArrayList<Integer>();
        //filter off the terms that are already present
        for(int id : listOfTermIDs){
        	filteredTermIDs.add(id);
        }
        //remove from filteredTermIds, the ones that are avaliable in AE
        for(AdverseEvent ae : command.getAdverseEventReportingPeriod().getAdverseEvents()){
        	filteredTermIDs.remove(ae.getAdverseEventTerm().getTerm().getId());
        }
        
        if(filteredTermIDs.isEmpty()) return "";
        
        boolean isMeddra = command.getStudy().getAeTerminology().getTerm() == Term.MEDDRA;
        for(int id: filteredTermIDs){
        	AdverseEvent ae = new AdverseEvent();
        	ae.setSolicited(false);
        	ae.setRequiresReporting(false);
        	
        	if(isMeddra){
        		//populate MedDRA term
        		LowLevelTerm llt = lowLevelTermDao.getById(id);
        		AdverseEventMeddraLowLevelTerm aellt = new AdverseEventMeddraLowLevelTerm();
        		aellt.setLowLevelTerm(llt);
        		ae.setAdverseEventMeddraLowLevelTerm(aellt);
        		aellt.setAdverseEvent(ae);
        	}else{
        		//properly set CTCterm
        		CtcTerm ctc =ctcTermDao.getById(id);
        		AdverseEventCtcTerm aeCtc = new AdverseEventCtcTerm();
        		aeCtc.setCtcTerm(ctc);
        		ae.setAdverseEventCtcTerm(aeCtc);
        		aeCtc.setAdverseEvent(ae);
        	}
        	
        	ae.setReportingPeriod(command.getAdverseEventReportingPeriod());
        	command.getAdverseEvents().add(ae);
        }
        Map<String, String> params = new LinkedHashMap<String, String>(); // preserve order for testing
    	params.put("adverseEventReportingPeriod", "" + command.getAdverseEventReportingPeriod());
    	 params.put("index", Integer.toString(index));
        return renderAjaxView("observedAdverseEventSection", 0, params);
    }
    
    public AjaxOutput deleteAdverseEvent(int index){
    	CaptureAdverseEventInputCommand command = (CaptureAdverseEventInputCommand) extractCommand();
    	command.getAdverseEvents().remove(index);
    	return new AjaxOutput();
    }
}
