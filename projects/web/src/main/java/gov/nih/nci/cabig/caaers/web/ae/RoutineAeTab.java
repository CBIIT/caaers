package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.dao.CtcDao;
import gov.nih.nci.cabig.caaers.dao.CtcTermDao;
import gov.nih.nci.cabig.caaers.domain.Grade;
import gov.nih.nci.cabig.caaers.domain.CtcTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.Hospitalization;
import gov.nih.nci.cabig.caaers.domain.Attribution;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.fields.RepeatingFieldGroupFactory;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * @author Krikor Krumlian
 */
public class RoutineAeTab extends AeRoutTab {
    private static final String REPORT_FIELD_GROUP = "report";
    private static final String MAIN_FIELD_GROUP = "main";
    private static final String CTC_TERM_FIELD_GROUP = "ctcTerm";
    private static final String CTC_OTHER_FIELD_GROUP = "ctcOther";

    private CtcDao ctcDao;
    private CtcTermDao ctcTermDao;
    private InputFieldGroup reportFieldGroup;
    private RepeatingFieldGroupFactory mainFieldFactory, ctcTermFieldFactory, ctcOtherFieldFactory;

    public RoutineAeTab() {
        super("Enter basic AE information", "Adverse Events", "ae/terms");

    }
    
    @Override
    public void postProcess(HttpServletRequest request, RoutineAdverseEventInputCommand command, Errors errors) {
    	handleTermAction(command, request.getParameter("_action"),
            request.getParameter("_selected"));
    }
    
    private void handleTermAction(RoutineAdverseEventInputCommand c, String action, String selected){
    	
    	  if ("addTerm".equals(action)) {
    		  for (String ctcTermId : c.getCtcTermIds()) {
    		  CtcTerm ctcTerm = ctcTermDao.getById(Integer.parseInt(ctcTermId));
    		  AdverseEvent ae = new AdverseEvent();
    		  ae.setCtcTerm(ctcTerm);
    		  c.getAeRoutineReport().addAdverseEvent(ae);
    		  }
    	  }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, InputFieldGroup> createFieldGroups(RoutineAdverseEventInputCommand command) {
        InputFieldGroupMap map = new InputFieldGroupMap();
        //map.addInputFieldGroup(reportFieldGroup);
        //int aeCount = command.getAeReport().getAdverseEvents().size();
        //map.addRepeatingFieldGroupFactory(mainFieldFactory, aeCount);
        //map.addRepeatingFieldGroupFactory(ctcTermFieldFactory, aeCount);
        //map.addRepeatingFieldGroupFactory(ctcOtherFieldFactory, aeCount);
        return map;
    }

    @Override
    public Map<String, Object> referenceData() {
        Map<String, Object> refdata = super.referenceData();
        refdata.put("ctcVersions", ctcDao.getAll());
        refdata.put("hospitalization", Hospitalization.values());
        refdata.put("attribution", Attribution.values());
        refdata.put("grade", Grade.values());
        return refdata;
    }

    /*
    @Override
    protected void validate(
        RoutineAdverseEventInputCommand command, BeanWrapper commandBean,
        Map<String, InputFieldGroup> fieldGroups, Errors errors
    ) {
        // TODO: validate that there is at least one AE
        for (ListIterator<AdverseEvent> lit = command.getAeReport().getAdverseEvents().listIterator(); lit.hasNext();) {
            AdverseEvent ae =  lit.next();
            validateAdverseEvent(ae, lit.previousIndex(), fieldGroups, errors);
        }
    }

    private void validateAdverseEvent(AdverseEvent ae, int index, Map<String, InputFieldGroup> groups, Errors errors) {
        CtcTerm ctcTerm = ae.getCtcTerm();
        if (ctcTerm != null && ctcTerm.isOtherRequired() && ae.getDetailsForOther() == null) {
            InputField field = groups.get(CTC_OTHER_FIELD_GROUP + index).getFields().get(0);
            errors.rejectValue(field.getPropertyName(), "REQUIRED", "Missing " + field.getDisplayName());
        }
    }
	*/
    ////// CONFIGURATION

    @Required
    public void setCtcDao(CtcDao ctcDao) {
        this.ctcDao = ctcDao;
    }

    // for testing
    CtcDao getCtcDao() {
        return ctcDao;
    }

	public CtcTermDao getCtcTermDao() {
		return ctcTermDao;
	}

	@Required
	public void setCtcTermDao(CtcTermDao ctcTermDao) {
		this.ctcTermDao = ctcTermDao;
	}
    
    
}
