package gov.nih.nci.cabig.caaers.rules.deploy;

import edu.nwu.bioinformatics.commons.DateUtils;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Grade;
import gov.nih.nci.cabig.caaers.domain.Hospitalization;
import gov.nih.nci.cabig.caaers.validation.ValidationErrors;

import java.util.Calendar;

public class AdverseEventBusinessRulesTest extends AbstractBusinessRulesExecutionTestCase {

    @Override
    public String getBindUri() {
        // TODO Auto-generated method stub
        return "gov.nih.nci.cabig.caaers.rules.reporting_basics_section";
    }

    @Override
    public String getRuleFile() {
        // TODO Auto-generated method stub
        return "rules_reporting_adverse_events.xml";
    }

    /**
     * RuleName : AER_BR3_CHK Logic : "'Hospitalization' must be provided if 'Grade' greater than 2"
     * Error Code : AER_BR3_ERR Error Message : YES must be provided if GRADE is greater
     * than or equal to 3
     */
    public void testNoGrade_NoHospitalization() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        for (AdverseEvent ae : aeReport.getAdverseEvents()) {
            ae.setGrade(null);
            ae.setHospitalization(null);
        }

        ValidationErrors errors = fireRules(aeReport);
        assertNoErrors(errors, "When no grade and no hospitalization");
    }

    /**
     * RuleName : AER_BR3_CHK Logic : "'Hospitalization' must be provided if 'Grade' greater than 2"
     * Error Code : AER_BR3_ERR Error Message : YES must be provided if GRADE is greater
     * than or equal to 3
     */
    public void testNoGrade_HospitalizationNONE() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        for (AdverseEvent ae : aeReport.getAdverseEvents()) {
            ae.setGrade(null);
            ae.setHospitalization(Hospitalization.NONE);
        }

        ValidationErrors errors = fireRules(aeReport);
        assertNoErrors(errors, "When no grade and  hospitalization is NONE");
    }

    /**
     * RuleName : AER_BR3_CHK Logic : "'Hospitalization' must be provided if 'Grade' greater than 2"
     * Error Code : AER_BR3_ERR Error Message : YES must be provided if GRADE is greater
     * than or equal to 3
     */
    public void testGradeMODERATE_HospitalizationNONE() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        for (AdverseEvent ae : aeReport.getAdverseEvents()) {
            ae.setGrade(Grade.MILD);
            ae.setHospitalization(Hospitalization.NONE);
        }

        ValidationErrors errors = fireRules(aeReport);
        assertNoErrors(errors, "When  grade is MILDs and  hospitalization is NONE");

    }


    /**
     * RuleName : AER_BR3_CHK Logic : "'Hospitalization' must be provided if 'Grade' greater than 2"
     * Error Code : AER_BR3_ERR Error Message : YES must be provided if GRADE is greater
     * than or equal to 3
     */
    public void testGradeSEVER_Hospitalization_HOSPITALIZATION() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        for (AdverseEvent ae : aeReport.getAdverseEvents()) {
            ae.setGrade(Grade.SEVERE);
            ae.setHospitalization(Hospitalization.YES);
        }

        ValidationErrors errors = fireRules(aeReport);
        assertNoErrors(errors, "When  grade is SEVERE and  hospitalization is YES");

    }

    /**
     * RuleName : AER_BR3_CHK Logic : "'Hospitalization' must be provided if 'Grade' greater than 2"
     * Error Code : AER_BR3_ERR Error Message : YES must be provided if GRADE is greater
     * than or equal to 3
     */
    public void testGradeDEATH_HospitalizationPROLONGED() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        for (AdverseEvent ae : aeReport.getAdverseEvents()) {
            ae.setGrade(Grade.DEATH);
            ae.setHospitalization(Hospitalization.NO);
        }

        ValidationErrors errors = fireRules(aeReport);
        assertNoErrors(errors,
                        "When  grade is DEATH and  hospitalization is NO");

    }

//
//    /**
//     * RuleName : AER_BR4_CHK Logic : "'AE Start Date' must be provided if 'Is Primary AE?' is
//     * 'Yes'" Error Code : AER_BR4_ERR Error Message : AE_START_DATE Date must be provided if
//     * IS_PRIMARY_AE is "Yes"
//     */
//    public void testStartDateForAllAEs() throws Exception {
//        ExpeditedAdverseEventReport aeReport = createAEReport();
//        ValidationErrors errors = fireRules(aeReport);
//        assertNoErrors(errors, "When startDate present on all AEs");
//    }
//
//    /**
//     * RuleName : AER_BR4_CHK Logic : "'AE Start Date' must be provided if 'Is Primary AE?' is
//     * 'Yes'" Error Code : AER_BR4_ERR Error Message : AE_START_DATE Date must be provided if
//     * IS_PRIMARY_AE is "Yes"
//     */
//    public void testStartDateForOnlyPrimaryAE() throws Exception {
//        ExpeditedAdverseEventReport aeReport = createAEReport();
//        aeReport.getAdverseEvents().get(1).setStartDate(null);
//        ValidationErrors errors = fireRules(aeReport);
//        assertNoErrors(errors, "When startDate present on primary AE");
//    }
//
//    /**
//     * RuleName : AER_BR4_CHK Logic : "'AE Start Date' must be provided if 'Is Primary AE?' is
//     * 'Yes'" Error Code : AER_BR4_ERR Error Message : AE_START_DATE Date must be provided if
//     * IS_PRIMARY_AE is "Yes"
//     */
//    public void testNoStartDateForAllAEs() throws Exception {
//        ExpeditedAdverseEventReport aeReport = createAEReport();
//        aeReport.getAdverseEvents().get(0).setStartDate(null);
//        aeReport.getAdverseEvents().get(1).setStartDate(null);
//        ValidationErrors errors = fireRules(aeReport);
//        assertCorrectErrorCode(errors, "AER_BR4_ERR");
//        assertSameErrorCount(errors, 1,
//                        "There should be error when Primary AE dont have start date");
//    }

    /**
     * RuleName : AER_BR5_CHK Logic : "'End date' must be greater than or equal to 'Start date' for
     * adverse event" Error Code : AER_BR5_ERR Error Message : AE_END_DATE must be later than OR
     * equal to AE_START_DATE
     */
    public void testAloneStartDateForAllAEs() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        aeReport.getAdverseEvents().get(0).setEndDate(null);
        aeReport.getAdverseEvents().get(1).setEndDate(null);
        ValidationErrors errors = fireRules(aeReport);
        assertNoErrors(errors, "When Only StartDate is present on all AES");
    }

    /**
     * RuleName : AER_BR5_CHK Logic : "'End date' must be greater than or equal to 'Start date' for
     * adverse event" Error Code : AER_BR5_ERR Error Message : AE_END_DATE must be later than OR
     * equal to AE_START_DATE
     */
    public void testEndDateBeforeStartDate() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        Calendar c = Calendar.getInstance();

        c.set(2009, 6, 01);
        aeReport.getAdverseEvents().get(0).setEndDate(c.getTime());
        c.set(2009, 7, 1);
        aeReport.getAdverseEvents().get(0).setStartDate(c.getTime());

        aeReport.getAdverseEvents().get(1).setEndDate(null);
        ValidationErrors errors = fireRules(aeReport);
        assertSameErrorCount(errors, 1);
        
    }

    /**
     * RuleName : AER_BR5_CHK Logic : "'End date' must be greater than or equal to 'Start date' for
     * adverse event" Error Code : AER_BR5_ERR Error Message : AE_END_DATE must be later than OR
     * equal to AE_START_DATE
     */
    public void testEndDateGTStartDateForAllAEs() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        ValidationErrors errors = fireRules(aeReport);
        assertNoErrors(errors, "When EndDate is Greater Than StartDate for all AES");
    }

    /**
     * RuleName : AER_BR5_CHK Logic : "'End date' must be greater than or equal to 'Start date' for
     * adverse event" Error Code : AER_BR5_ERR Error Message : AE_END_DATE must be later than OR
     * equal to AE_START_DATE
     */
    public void testEndDateLTStartDateForAllAEs() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        aeReport.getAdverseEvents().get(0).setEndDate(DateUtils.createDate(2006, 3, 3));
        aeReport.getAdverseEvents().get(1).setEndDate(DateUtils.createDate(2007, 3, 1));
        ValidationErrors errors = fireRules(aeReport);
        assertSameErrorCount(errors, 2, "When 2 of the AEs has wrong end dates");
        assertCorrectErrorCode(errors, "AER_BR5_ERR");
    }

    /**
     * RuleName : AER_BR5_CHK Logic : "'End date' must be greater than or equal to 'Start date' for
     * adverse event" Error Code : AER_BR5_ERR Error Message : AE_END_DATE must be later than OR
     * equal to AE_START_DATE
     */
    public void testWrongEndDateInOneOutOfTwoAEs() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        aeReport.getAdverseEvents().get(1).setEndDate(DateUtils.createDate(2007, 3, 1));
        ValidationErrors errors = fireRules(aeReport);
        assertSameErrorCount(errors, 1, "When 1 of the AEs has wrong end dates");
        assertCorrectErrorCode(errors, "AER_BR5_ERR");
        assertEquals("Correct replacement in error", 1, errors.getErrorAt(0).getReplacementVariables()[0]);

        assertNotNull(errors.getErrorAt(0).getFieldNames());
        Object i = errors.getErrorAt(0).getReplacementVariables()[0];
        assertCorrectFieldNames(errors.getErrorAt(0), "aeReport.adverseEvents[" + i + "].endDate","aeReport.adverseEvents[" + i + "].startDate");

    }

    /**
     * RuleName : "AER_UK_CHK" Logic : Adverse Events CTC must be unique Error Code : AER_UK_ERR
     * Error Message : ADVERSE_EVENT_CTC must be unique
     */
    public void testUniqueCTC() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        ValidationErrors errors = fireRules(aeReport);
        assertNoErrors(errors, "Adverse events CTC when unique");
    }


    public void testSingleAeTerm() throws Exception {
    	ExpeditedAdverseEventReport aeReport = createAEReport();
    	aeReport.getAdverseEvents().remove(1);
    	
    	ValidationErrors errors = fireRules(aeReport);
    	assertNoErrors(errors,"AeReport has one adverse event");
    }
    
    public void testMultipleAeTerms() throws Exception {
    	ExpeditedAdverseEventReport aeReport = createAEReport();
    	
    	ValidationErrors errors = fireRules(aeReport);
    	assertNoErrors(errors, "AeReport has multiple adverse events");
    	
    }
    
    public void testNoAeTerms() throws Exception {
    	ExpeditedAdverseEventReport aeReport = createAEReport();
    	aeReport.getAdverseEvents().clear();
    	
    	ValidationErrors errors = fireRules(aeReport);
    	assertSameErrorCount(errors, 1);
    	assertCorrectErrorCode(errors, "AER_PRESENT_ERR");
    }

    public void testFieldNames() throws Exception {
    }

    /**
     *
     * TEst the fix for NPE for ae.getAdverseEventCtcTerm().getCtcTerm() in AER_UK_CHK when Term is null
     *
     * */
    public void testDuplicateCTCWithANullTerm() throws Exception {
        ExpeditedAdverseEventReport aeReport = createAEReport();
        aeReport.getAdverseEvents().get(0).getAdverseEventCtcTerm().getCtcTerm().setId(3002);
        aeReport.getAdverseEvents().get(1).getAdverseEventCtcTerm().getCtcTerm().setId(3002);
        aeReport.getAdverseEvents().get(1).getAdverseEventCtcTerm().setCtcTerm(null);
        ValidationErrors errors = fireRules(aeReport);
        assertSameErrorCount(errors, 0);
    }

}
