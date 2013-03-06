/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.dao.CtcDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.Ctc;
import gov.nih.nci.cabig.caaers.domain.CtcTerm;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldAttributes;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * @author Rhett Sutphin
 */
public class CtcBasicsTab extends BasicsTab {
	 private static final Log log = LogFactory.getLog(CtcBasicsTab.class);
	
    private static final String CTC_TERM_FIELD_GROUP = "ctcTerm";
    private static final String CTC_OTHER_FIELD_GROUP = "ctcOther";

    private CtcDao ctcDao;

    public CtcBasicsTab() {
        super("Enter basic AE information", ExpeditedReportSection.BASICS_SECTION.getDisplayName(), "ae/enterBasic");
    }

    @Override
    public Map<String, Object> referenceData(HttpServletRequest request, ExpeditedAdverseEventInputCommand command) {
        Map<String, Object> refdata = super.referenceData(request, command);
        int ctcVersionId = command.getAssignment().getStudySite().getStudy().getAeTerminology().getCtcVersion().getId();
        Ctc ctc = ctcDao.getCtcWithCategories(ctcVersionId);
        refdata.put("ctcCategories", ctc.getCategories());
        return refdata;
    }

    @Override
    protected void createFieldGroups(AeInputFieldCreator creator, ExpeditedAdverseEventInputCommand command) {
        super.createFieldGroups(creator, command);
        InputField ctcTermField = InputFieldFactory.createAutocompleterField("adverseEventCtcTerm.ctcTerm", "CTC term", false);
        /*
         * InputFieldAttributes.setDetails(ctcTermField, "Type a portion of the CTC term you are
         * looking for. If you select a category, only terms in that category will be shown.");
         */
        creator.createRepeatingFieldGroup(CTC_TERM_FIELD_GROUP, "adverseEvents", ctcTermField);
        /*
         * InputFieldAttributes.setDetails(otherVerbatimField,"The CTC term chosen requires a MedDRA
         * based term or a free text entry ");
         */
        
        if(command.getStudy().getOtherMeddra() != null){
        	InputField otherLowLevelTermField = InputFieldFactory.createAutocompleterField("lowLevelTerm", "Other (MedDRA)", false);
        	creator.createRepeatingFieldGroup(CtcBasicsTab.CTC_OTHER_FIELD_GROUP, "adverseEvents", otherLowLevelTermField);
        } else  {     
			InputField otherSpecifyField =  InputFieldFactory.createTextField("otherSpecify", "Other (specify)", "aeReport.adverseEvents.otherSpecify", false);
	        InputFieldAttributes.setSize(otherSpecifyField, 25);
	        creator.createRepeatingFieldGroup(CtcBasicsTab.CTC_OTHER_FIELD_GROUP, "adverseEvents", otherSpecifyField);
        }
        
        //add the fields for outcomes
        for(InputFieldGroup outcomeFieldGrp : getOutcomeInputFieldGroups(command)){
        	creator.addUnprocessedFieldGroup(outcomeFieldGrp);
        }
    }
    
    
    @Override
    public void postProcess(HttpServletRequest request,	ExpeditedAdverseEventInputCommand command, Errors errors) {
    	super.postProcessOutcomes(command);
    }

    @Override
    protected void validateAdverseEvent(AdverseEvent ae, int index, Map<String, InputFieldGroup> groups, Errors errors) {
        CtcTerm ctcTerm = ae.getAdverseEventCtcTerm().getCtcTerm();
        if (ctcTerm != null && ctcTerm.isOtherRequired() && ae.getDetailsForOther() == null && ae.getLowLevelTerm() == null) {
            InputField field0 = groups.get(CTC_OTHER_FIELD_GROUP + index).getFields().get(0);
            errors.rejectValue(field0.getPropertyName(), "SAE_005", new Object[] {field0.getDisplayName()},"Missing " + field0.getDisplayName());
        }
        super.validateAdverseEvent(ae, index, groups, errors);

        // Inforce business Rule
        if (ctcTerm != null && !ctcTerm.isOtherRequired()) {
            ae.setLowLevelTerm(null);
        }
    }

    // //// CONFIGURATION

    @Required
    public void setCtcDao(CtcDao ctcDao) {
        this.ctcDao = ctcDao;
    }

    // for testing
    CtcDao getCtcDao() {
        return ctcDao;
    }
}
