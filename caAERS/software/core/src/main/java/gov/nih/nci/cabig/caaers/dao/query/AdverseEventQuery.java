/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao.query;

import gov.nih.nci.cabig.caaers.domain.Attribution;
import gov.nih.nci.cabig.caaers.domain.Grade;
import gov.nih.nci.cabig.caaers.domain.Hospitalization;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.Term;

import org.apache.commons.lang.StringUtils;

public class AdverseEventQuery extends AbstractQuery {
	
	public static final String AE_ALIAS = "ae";
	
	public static final String AE_TERM_ALIAS = "aeTerm";
	
	public static final String AE_REPORTING_PERIOD_ALIAS = "aeRp";
	
	public static final String TERMINOLOGY_ALIAS = "terminology";
	
	public static final String STUDY_PARTICIPANT_ALIAS = "spa";
	
	public static final String OUTCOMES_ALIAS = "outcomes";
	
	public static final String TAC = "tac";
	
	public static final String TAC_EXPECTED_AE_PROFILE = "exp";
	
	public static final String LL_TERM_ALIAS = "llt";
	
	public AdverseEventQuery() {
		super("select distinct "+AE_ALIAS+" from AdverseEvent "+ AE_ALIAS);
	}
	
	public AdverseEventQuery(String...selections) {
		super("select distinct "+AE_ALIAS+", "+StringUtils.join(selections, ", ")+" from AdverseEvent "+ AE_ALIAS);
	}
	
	public void joinAdverseEventTerm(){
		join (AE_ALIAS +".adverseEventTerm "+AE_TERM_ALIAS);
	}

	public void joinLowLevelTerm(){
		join (AE_ALIAS +".lowLevelTerm "+LL_TERM_ALIAS);
	}

	public void outerjoinLowLevelTerm(){
		leftJoin (AE_ALIAS +".lowLevelTerm "+LL_TERM_ALIAS);
	}
	
	public void joinReportingPeriod() {
		join (AE_ALIAS +".reportingPeriod "+AE_REPORTING_PERIOD_ALIAS);
	}
	
	public void joinStudy() {
		joinReportingPeriod();
		join (AE_REPORTING_PERIOD_ALIAS +".assignment.studySite.study "+STUDY_ALIAS);
	}
	
	public void joinStudyIdentifiers() {
		join (STUDY_ALIAS +".identifiers sids");	
	}
	
	public void joinParticipantIdentifiers() {
		join (PARTICIPANT_ALIAS +".identifiers pids");	
	}

	public void joinParticipant() {
		joinReportingPeriod();
		join (AE_REPORTING_PERIOD_ALIAS +".assignment.participant "+PARTICIPANT_ALIAS);
	}
	
    public void joinStudyParticipantAssignment() {
    	joinReportingPeriod();
    	join (AE_REPORTING_PERIOD_ALIAS +".assignment "+STUDY_PARTICIPANT_ALIAS);
    }
    
    public void joinAeTerminology() {
    	joinStudy();
        join(STUDY_ALIAS+".aeTerminology "+TERMINOLOGY_ALIAS);
    }
    
    public void joinOutcomes() {
    	join (AE_ALIAS +".outcomes "+OUTCOMES_ALIAS);
    }
    
    public void outerjoinOutcomes() {
    	leftJoin (AE_ALIAS +".outcomes "+OUTCOMES_ALIAS);
    }   
    
	public void joinTreatmentAssignment() {
		joinReportingPeriod();
		join (AE_REPORTING_PERIOD_ALIAS +".treatmentAssignment "+TAC);
	}
	
	public void joinTreatmentAssignmentExpectedAEProfile() {
		joinTreatmentAssignment();
		join (TAC +".abstractStudyInterventionExpectedAEs "+TAC_EXPECTED_AE_PROFILE);
	}

	public void outerjoinTreatmentAssignment() {
		joinReportingPeriod();
		leftJoin (AE_REPORTING_PERIOD_ALIAS +".treatmentAssignment "+TAC);
	}

    public void filterByAeReportId(Integer id){
        andWhere("report.id=:aeReportId");
        setParameter("aeReportId", id);
    }

	public void filterByCtcTerm(String term , String operator) {
		final String pTerm = generateParam();
		andWhere ( AE_TERM_ALIAS + ".id in (select ctcTerm.id from gov.nih.nci.cabig.caaers.domain.AdverseEventCtcTerm ctcTerm where ctcTerm.term.term " + parseOperator(operator) + " :" + pTerm + ")");
		if (operator.equals("like")) {
			setParameter(pTerm , getLikeValue(term));
		} else {
			setParameter(pTerm , term);
		}		
	}
	public void filterBySolicited(boolean flag , String operator) {
		andWhere (AE_ALIAS+".solicited " + parseOperator(operator) + " :" + generateParam(flag));
	}

	public void filterByGrade(Integer code , String operator) {
		andWhere (AE_ALIAS+".grade " + parseOperator(operator) + " :" + generateParam(Grade.getByCode(code)));
	}

	public void filterByHospitalization(Integer code , String operator) {
		andWhere (AE_ALIAS+".hospitalization " + parseOperator(operator) + " :" + generateParam( Hospitalization.getByCode(code)));
	}
	
	public void filterByExpected(boolean flag , String operator) {
		andWhere (AE_ALIAS+".expected " + parseOperator(operator) + " :" + generateParam(flag));
	}
	
	public void filterByAttribution(Integer code , String operator) {
		andWhere (AE_ALIAS+".attributionSummary " + parseOperator(operator) + " :" + generateParam(Attribution.getByCode(code)));
	}
	
	public void filterByVerbatim(String verbatim , String operator) {
		final String param = generateParam();
		andWhere (AE_ALIAS+".detailsForOther " + parseOperator(operator) + " :" + param);
		if (operator.equals("like")) {
			setParameter(param, getLikeValue(verbatim));
		} else {
			setParameter(param, verbatim);
		}
	}

    public void filterByTerminology(Integer code, String operator) {
    	andWhere(TERMINOLOGY_ALIAS+".term " + parseOperator(operator) + " :" + generateParam(Term.getByCode(code)));
    }
    
    public void filterByAEStartDate(String dateString , String operator) throws Exception {
    	andWhere(createDateQuery(AE_ALIAS+".startDate", dateString, operator));
    }

    public void filterByAEEndDate(String dateString , String operator) throws Exception {
    	andWhere(createDateQuery(AE_ALIAS+".endDate", dateString, operator));
    }

    public void filterByCourseStartDate(String dateString , String operator) throws Exception {
    	andWhere(createDateQuery(AE_REPORTING_PERIOD_ALIAS+".startDate", dateString, operator));
    }

    public void filterByCourseEndDate(String dateString , String operator) throws Exception {
    	andWhere(createDateQuery(AE_REPORTING_PERIOD_ALIAS+".endDate", dateString, operator));
    }

    public void filterByCourseNumber(Integer num , String operator) throws Exception {
    	andWhere(AE_REPORTING_PERIOD_ALIAS+".cycleNumber " + parseOperator(operator) + " :" + generateParam(num));
    }
    
    public void filterByStudy(Study study){
    	andWhere(STUDY_ALIAS+" = :" + generateParam(study));
    }
    
    public void filterByMatchingTermsOnExpectedAEProfileAndReportedAE(){
    	andWhere(TAC_EXPECTED_AE_PROFILE+".term = "+AE_TERM_ALIAS+".term");
    }

    /**
     * To filter adverseEents by gradeDate in advance search page
     * @param dateString
     * @param operator
     * @throws Exception
     */
    public void filterByAEAwarenessDate(String dateString , String operator) throws Exception {
        andWhere(createDateQuery(AE_ALIAS+".gradedDate", dateString, operator));
    }

    /**
     * To filter adverseEvents by requireReporting in advance search page
     * @param flag
     * @param operator
     */
    public void filterByRequiresReporting(String flag , String operator) {
        //Requires reporting accepts null values also
        //So if user select Yes/No then we will execute below query
        if(flag.equals("true") || flag.equals("false")) {
            andWhere(AE_ALIAS+".requiresReporting " + parseOperator(operator) + " :" + generateParam(Boolean.parseBoolean(flag)));
        }
        //If user try to find Empty/Non-Empty Requires reporting records then below code will be executed
        else {
             operator = operator.equals("=") ? "IS NULL" : "IS NOT NULL";
             andWhere(AE_ALIAS+".requiresReporting " + operator);
        }
    }

    /**
     * To filter adverseEvents by created date in advance search page
     * @param dateString
     * @param operator
     * @throws Exception
     */
    public void filterByAECreatedDate(String dateString , String operator) throws Exception {
        andWhere(createDateQuery(AE_ALIAS+".createdDate", dateString, operator));
    }
    
}
