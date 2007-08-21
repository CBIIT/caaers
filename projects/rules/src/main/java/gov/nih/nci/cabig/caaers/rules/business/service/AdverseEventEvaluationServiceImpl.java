package gov.nih.nci.cabig.caaers.rules.business.service;

import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyOrganization;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.rules.RuleException;
import gov.nih.nci.cabig.caaers.rules.brxml.RuleSet;
import gov.nih.nci.cabig.caaers.rules.common.CategoryConfiguration;
import gov.nih.nci.cabig.caaers.rules.common.RuleType;
import gov.nih.nci.cabig.caaers.rules.common.RuleUtil;
import gov.nih.nci.cabig.caaers.rules.domain.AdverseEventEvaluationResult;
import gov.nih.nci.cabig.caaers.rules.objectgraph.FactResolver;
import gov.nih.nci.cabig.caaers.rules.runtime.BusinessRulesExecutionService;
import gov.nih.nci.cabig.caaers.rules.runtime.BusinessRulesExecutionServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AdverseEventEvaluationServiceImpl implements AdverseEventEvaluationService {

	//Replace with spring injection
	private BusinessRulesExecutionService businessRulesExecutionService = new BusinessRulesExecutionServiceImpl();
	private RulesEngineService rulesEngineService= new RulesEngineServiceImpl();
	//private ReportDefinitionDao reportDefinitionDao;
	//private ReportServiceImpl reportService;
	
	public static final String CAN_NOT_DETERMINED = "CAN_NOT_DETERMINED";
	public static final String SERIOUS_ADVERSE_EVENT = "SERIOUS_ADVERSE_EVENT";
	


/**
 * This method will asses adverse event and will return one of the
 * following vlue
 * 	1. Routine AE
 *  2. SAE
 *  3. Can't be determined
 *  Calling this method again and again will not affect the rules
 *  firing adversly as nothing gets fires subsequently
 *  
 *  fire the rules at sponsor defined defined study level..
 *  if not rules specified , then fire sponsor level rules.
 *  
 */
public String assesAdverseEvent(AdverseEvent ae, Study study) throws Exception{

	String message = evaluateSponsorTarget(ae,study, null , RuleType.REPORT_SCHEDULING_RULES.getName());
		if (!message.equals(CAN_NOT_DETERMINED)) {
			return SERIOUS_ADVERSE_EVENT;
		}
		
		for(StudyOrganization so : study.getStudyOrganizations() )
		{

		    message = evaluateInstitutionTarget(ae, study, so.getOrganization(), null , RuleType.REPORT_SCHEDULING_RULES.getName());
			if (!message.equals(CAN_NOT_DETERMINED)) {
				return SERIOUS_ADVERSE_EVENT;
			}

		}
		
		System.out.println("message is : " + message );
		
		return CAN_NOT_DETERMINED;

}



public Map<String,List<String>> evaluateSAEReportSchedule(ExpeditedAdverseEventReport aeReport) throws Exception {
	
	Map<String,List<String>> map = new HashMap<String,List<String>>();
	
	
	
	List<AdverseEvent> aes = aeReport.getAdverseEvents();
	List<String> reportDefinitionsForSponsor = new ArrayList<String>();
	for(AdverseEvent ae : aes )
	{
		String message = evaluateSponsorTarget(ae,aeReport.getStudy(), null , RuleType.REPORT_SCHEDULING_RULES.getName());
		if (!message.equals(CAN_NOT_DETERMINED)) {
			
			String[] messages = RuleUtil.charSeparatedStringToStringArray(message,"\\|\\|");
			
			for (int i=0;i<messages.length;i++) {
				System.out.println("adding .... " + messages[i]);
				reportDefinitionsForSponsor.add(messages[i]);
			}
		}
	}
	
	//System.out.println("KEY IN IS :" + aeReport.getStudy().getPrimaryFundingSponsorOrganization().getName());
	map.put(aeReport.getStudy().getPrimaryFundingSponsorOrganization().getName(), reportDefinitionsForSponsor);
	
	Study study = aeReport.getStudy();
	
	//TO-DO get orgs like FDA, CALGB and add to this list
	
	for(StudyOrganization so : study.getStudyOrganizations() )
	{
		List<String> reportDefinitionsForInstitution = new ArrayList<String>();
		
		for(AdverseEvent ae : aes ) {
			String message = evaluateInstitutionTarget(ae, study, so.getOrganization(), null , RuleType.REPORT_SCHEDULING_RULES.getName());
			if (!message.equals(CAN_NOT_DETERMINED)) {
				String[] messages = RuleUtil.charSeparatedStringToStringArray(message,"\\|\\|");
				
				for (int i=0;i<messages.length;i++) {
					reportDefinitionsForInstitution.add(messages[i]);
				}
				//break;
			}
		}
		//System.out.println("KEY-2 IN IS :" + so.getOrganization().getName());
		
		//chek for key 		
		List<String> existingList = map.get(so.getOrganization().getName());
		if (existingList != null ) {
			reportDefinitionsForInstitution.addAll(existingList);
		}
		
		map.put(so.getOrganization().getName(), reportDefinitionsForInstitution);
	}
	
	
	return map;
}


public List<String> mandatorySections(ExpeditedAdverseEventReport aeReport) throws Exception{
	
	List<AdverseEvent> aes = aeReport.getAdverseEvents();
	List<String> mandatorySections = new ArrayList<String>();
	
	for(AdverseEvent ae : aes )
	{
		for(Report report : aeReport.getReports() ) {
			String message = evaluateSponsorTarget(ae,aeReport.getStudy(),report.getReportDefinition(),RuleType.MANDATORY_SECTIONS_RULES.getName());
			
			if (!message.equals(CAN_NOT_DETERMINED)) {
				
				String[] messages = RuleUtil.charSeparatedStringToStringArray(message,"\\|\\|");
				
				for (int i=0;i<messages.length;i++) {
					System.out.println("adding .... " + messages[i]);
					mandatorySections.add(messages[i]);
				}

			}
		}
		
	}

	return mandatorySections;
}

/**
 *  fire the rules at sponsor defined defined study level..
 *  if not rules specified , then fire sponsor level rules.
 *  
  */
private String evaluateSponsorTarget(AdverseEvent ae, Study study, ReportDefinition reportDefinition, String ruleTypeName) throws Exception{
	
	String sponsor_define_study_level_evaluation = null;
	String sponsor_level_evaluation = null;
	String final_result = null;
	
	/**
	 * get and fire study level rules
	 */
	sponsor_define_study_level_evaluation = sponsorDefinedStudyLevelRules(ae, study, reportDefinition, ruleTypeName);

	// if study level rule exist and null message...
	if (sponsor_define_study_level_evaluation == null) {
		return CAN_NOT_DETERMINED;
	
	// if study level rules not found , then get to sponsor rules..
	} else 	if (sponsor_define_study_level_evaluation.equals("no_rules_found")) {
		sponsor_level_evaluation = sponsorLevelRules(ae, study, reportDefinition, ruleTypeName);
		final_result = sponsor_level_evaluation;
		
	// if study level rules exist and returned a message.. 
	} else {
		final_result = sponsor_define_study_level_evaluation;
	}
	
	if (final_result == null || "no_rules_found".endsWith(final_result)) {
		final_result = CAN_NOT_DETERMINED;
	}
	
	return final_result;	

}

private String evaluateInstitutionTarget(AdverseEvent ae, Study study , Organization organization, ReportDefinition reportDefinition, String ruleTypeName) throws Exception {
	String institution_define_study_level_evaluation = null;
	String institution_level_evaluation = null;
	String final_result = null;

	/**
	 * get and fire study level rules
	 */
	institution_define_study_level_evaluation = institutionDefinedStudyLevelRules(ae, study, organization, reportDefinition, ruleTypeName);

	// if study level rule exist and null message...
	if (institution_define_study_level_evaluation == null) {
		return CAN_NOT_DETERMINED;
	
	// if study level rules not found , then get to sponsor rules..
	} else 	if (institution_define_study_level_evaluation.equals("no_rules_found")) {
		institution_level_evaluation = institutionLevelRules(ae, study, organization, reportDefinition, ruleTypeName);
		final_result = institution_level_evaluation;
		
	// if study level rules exist and returned a message.. 
	} else {
		final_result = institution_define_study_level_evaluation;
	}
	
	if (final_result == null || "no_rules_found".endsWith(final_result)) {
		final_result = CAN_NOT_DETERMINED;
	}
	
	return final_result;
	
}



/**
 *  fire the rules at institution defined defined study level..
 *  if not rules specified , then fire institution level rules.
 *  
  */



// RULE METHODS 

private String sponsorLevelRules(AdverseEvent ae, Study study, ReportDefinition reportDefinition, String ruleTypeName) throws Exception{
	String message = null;
	String bindURI = getBindURI(study.getPrimaryFundingSponsorOrganization().getName(), "","SPONSOR",ruleTypeName);
	
	RuleSet ruleSetForSponsor = rulesEngineService.getRuleSetForSponsor(ruleTypeName, study.getPrimaryFundingSponsorOrganization().getName());
	
	if(ruleSetForSponsor==null){
		return "no_rules_found";
		//throw new Exception("There are no rules configured for adverse event scheduling for this sponsor!");
	}
	
	AdverseEventEvaluationResult evaluationForSponsor = new AdverseEventEvaluationResult();
	
	try {
		evaluationForSponsor = this.getEvaluationObject(ae, study, null, reportDefinition, bindURI);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		throw new Exception(e.getMessage(),e);
	}
	
	message = evaluationForSponsor.getMessage();
	
	return message;
	
}

private String sponsorDefinedStudyLevelRules(AdverseEvent ae, Study study, ReportDefinition reportDefinition, String ruleTypeName) throws Exception{
	String message = null;
	String bindURI = getBindURI(study.getPrimaryFundingSponsorOrganization().getName(), study.getShortTitle(),"SPONSOR_DEFINED_STUDY",ruleTypeName);
	
	RuleSet ruleSetForSponsorDefinedStudy = rulesEngineService.getRuleSetForSponsorDefinedStudy(ruleTypeName, study.getShortTitle(), study.getPrimaryFundingSponsorOrganization().getName());
	if(ruleSetForSponsorDefinedStudy==null){
		return "no_rules_found";
		//throw new Exception("There are no rules configured for adverse event assesment for this sponsor defined study!");
	}
	
	AdverseEventEvaluationResult evaluationForSponsorDefinedStudy = new AdverseEventEvaluationResult();
	
	try {
		evaluationForSponsorDefinedStudy = this.getEvaluationObject(ae, study, null, reportDefinition, bindURI);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		throw new Exception(e.getMessage(),e);
	}
	
	message = evaluationForSponsorDefinedStudy.getMessage();
	
	return message;
	
}


private String institutionDefinedStudyLevelRules(AdverseEvent ae, Study study , Organization organization, ReportDefinition reportDefinition, String ruleTypeName) throws Exception{
	String message = null;
	
	String studyShortTitle = study.getShortTitle();
	String organizationName = organization.getName();
	
	String bindURI = getBindURI(organizationName, studyShortTitle,"INSTITUTION_DEFINED_STUDY",ruleTypeName);
	
	RuleSet ruleSetForInstitutionDefinedStudy = rulesEngineService.getRuleSetForInstitutionDefinedStudy(ruleTypeName, studyShortTitle, organizationName);
	if(ruleSetForInstitutionDefinedStudy==null){
		return "no_rules_found";
		//throw new Exception("There are no rules configured for adverse event assesment for this sponsor defined study!");
	}
	
	AdverseEventEvaluationResult evaluationForInstitutionDefinedStudy = new AdverseEventEvaluationResult();
	
	try {
		evaluationForInstitutionDefinedStudy = this.getEvaluationObject(ae, study, organization, reportDefinition, bindURI);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		throw new Exception(e.getMessage(),e);
	}
	
	message = evaluationForInstitutionDefinedStudy.getMessage();
	
	return message;
	
}

private String institutionLevelRules(AdverseEvent ae,  Study study, Organization organization, ReportDefinition reportDefinition, String ruleTypeName) throws Exception{
	String message = null;
	
	String organizationName = organization.getName();
	
	System.out.println("org name : " + organizationName);
	
	String bindURI = getBindURI(organizationName, "","INSTITUTION",ruleTypeName);
	
	System.out.println("url " + bindURI);
	
	RuleSet ruleSetForInstiution = rulesEngineService.getRuleSetForInstitution(ruleTypeName, organizationName);
	
	if(ruleSetForInstiution==null){
		return "no_rules_found";
		//throw new Exception("There are no rules configured for adverse event scheduling for this sponsor!");
	}
	
	AdverseEventEvaluationResult evaluationForInstitution = new AdverseEventEvaluationResult();
	
	try {
		evaluationForInstitution = this.getEvaluationObject(ae, study, organization, reportDefinition, bindURI);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		throw new Exception(e.getMessage(),e);
	}
	
	message = evaluationForInstitution.getMessage();
	
	System.out.println("message " + message);
	
	return message;
}	

private String getBindURI(String sponsorOrInstitutionName, String studyName, String type, String ruleSetName){
		String bindURI = null;
		if (type.equalsIgnoreCase("SPONSOR")){
			bindURI = CategoryConfiguration.SPONSOR_BASE.getPackagePrefix() + "." +RuleUtil.getStringWithoutSpaces(sponsorOrInstitutionName)+"."+RuleUtil.getStringWithoutSpaces(ruleSetName);
		}
		
		if(type.equalsIgnoreCase("INSTITUTION")){
			bindURI = CategoryConfiguration.INSTITUTION_BASE.getPackagePrefix() + "."+RuleUtil.getStringWithoutSpaces(sponsorOrInstitutionName)+"."+RuleUtil.getStringWithoutSpaces(ruleSetName);
		}
		
		if(type.equalsIgnoreCase("SPONSOR_DEFINED_STUDY")){
			//System.out.println("sponsorOrInstitutionName : " + sponsorOrInstitutionName);
			//System.out.println("studyName : " + studyName);
			bindURI = CategoryConfiguration.SPONSOR_DEFINED_STUDY_BASE.getPackagePrefix() + "."+RuleUtil.getStringWithoutSpaces(studyName)+"."+RuleUtil.getStringWithoutSpaces(sponsorOrInstitutionName)+"."+RuleUtil.getStringWithoutSpaces(ruleSetName);
		}
		
		
		if(type.equalsIgnoreCase("INSTITUTION_DEFINED_STUDY")){
			bindURI = CategoryConfiguration.INSTITUTION_DEFINED_STUDY_BASE.getPackagePrefix() + "."+RuleUtil.getStringWithoutSpaces(studyName)+"."+RuleUtil.getStringWithoutSpaces(sponsorOrInstitutionName)+"."+RuleUtil.getStringWithoutSpaces(ruleSetName);
		}
		
		
		return bindURI;
	}
	

	
	private AdverseEventEvaluationResult getEvaluationObject(AdverseEvent ae, Study study, Organization organization, ReportDefinition reportDefinition, String bindURI) throws Exception{
		
		AdverseEventEvaluationResult evaluationForSponsor = new AdverseEventEvaluationResult();
		List<Object> inputObjects = new ArrayList<Object>();
		inputObjects.add(ae);
		FactResolver f = new FactResolver();
		inputObjects.add(f);
		
		if (study != null ) {
			inputObjects.add(study);
		}
		if (organization != null) {
			inputObjects.add(organization);
		}
		if (reportDefinition != null) {
			inputObjects.add(reportDefinition);
		}
		//inputObjects.add(new AdverseEventEvaluationResult());
		
		List<Object> outputObjects = null;
		try{
		
			outputObjects = businessRulesExecutionService.fireRules(bindURI, inputObjects);
		
		}catch(Exception ex){
			/**
			 * Don't do anything, it means there are no rules for this package
			 */
			throw new RuleException("There are no rule configured for this sponsor",ex);
			//return evaluationForSponsor;
		}
		
		
		
		Iterator<Object> it = outputObjects.iterator();
		
		while(it.hasNext()){
			Object obj = it.next();
			
			if(obj instanceof AdverseEventEvaluationResult) {
				evaluationForSponsor = (AdverseEventEvaluationResult)obj;
				break;
			}
			
			
		}
		
		return evaluationForSponsor;
	}
	

	
}
