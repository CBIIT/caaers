/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao.query;

import gov.nih.nci.cabig.caaers.domain.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class StudyQuery extends AbstractQuery {
	
	private static final String STUDY_ID = "studyId";

    
    private static final String STUDY_IDENTIFIER_SYSTEM = "idSysName";
    
    public static final String STUDY_PARTICIPANT_ALIAS = "spa";
    
    public static final String TERMINOLOGY_ALIAS = "terminology";
    
    public static final String TREATMENT_ASSIGNMENT_ALIAS = "ta";
    
    public static final String ORGANIZATION_ALIAS = "org";
    
    public static final String OTHER_INT_ALIAS = "i";
    
    public static final String DEVICE_INT_ALIAS = "d";
    
   	public static final String AGENT_INT_ALIAS = "sai";
    
    public static final String AGENT_ALIAS = "agt";
    
    

    private SimpleDateFormat dateFormat;

    public StudyQuery() {
        super("select  distinct "+STUDY_ALIAS+" from Study "+STUDY_ALIAS);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    }

    public void filterByRetiredStatus(Boolean status) {
        super.filterByRetiredStatus(STUDY_ALIAS, status);
    }

    public void joinIdentifier() {
        join(STUDY_ALIAS+".identifiers as identifier");
    }

    public void joinStudyOrganization() {
        join("s.studyOrganizations as ss");
    }

    public void outerjoinStudyOrganization() {
    	leftJoin("s.studyOrganizations as ss");
    }
    
    public void joinOrganization() {
    	joinStudyOrganization();
        join("ss.organization as org");
    }

    public void outerjoinOrganization() {
    	outerjoinStudyOrganization();
    	leftJoin("ss.organization as org");
    }
    
    public void joinStudyParticipantAssignment() {
        joinStudyOrganization();
        join("ss.studyParticipantAssignments as spa");
    }
    
    public void joinParticipant() {
        joinStudyParticipantAssignment();
        join("spa.participant as p");
    }
    
    // dont change naming convention .. (outer key word..)
    public void outerjoinStudyParticipantAssignment() {
        joinStudyOrganization();
        leftJoin("ss.studyParticipantAssignments as spa");
    }
    
    public void outerjoinParticipant() {
    	outerjoinStudyParticipantAssignment();
    	leftJoin("spa.participant as p");
    }
    
    
    public void joinAeTerminology() {
        join("s.aeTerminology as terminology");
    }
    public void outerjoinAeTerminology() {
        leftJoin("s.aeTerminology as terminology");
    }
    
    public void joinTreatmentAssignment() {
        join("s.treatmentAssignmentsInternal as ta");
    }

    public void outerjoinTreatmentAssignment() {
        leftJoin("s.treatmentAssignmentsInternal as ta");
    }
    
    public void joinOtherIntervention() {
        leftJoin("s.otherInterventionsInternal as i");
    }
    
    public void joinDeviceIntervention() {
        leftJoin("s.studyDevicesInternal as d");

    }
    
    public void joinAgentIntervention() {

        leftJoin("s.studyAgentsInternal as sai");
    }
    
    public void joinStudyIntervention() {
        leftJoin("s.studyDevicesInternal as d");
        leftJoin("s.otherInterventionsInternal as i");
        leftJoin("s.studyAgentsInternal as sai");
    }
    

    public void joinParticipantIdentifier() {
        joinParticipant();
        join("p.identifiers as pIdentifier");
    }
    public void joinStudyAgents(){
    	join("s.studyAgentsInternal as sagents");
    }
    public void joinAgent() {
    	joinStudyAgents();
        join("sagents.agent as agt");
    }
    
    public void outerjoinStudyAgents(){
    	leftJoin("s.studyAgentsInternal as sagents");
    }
    public void outerjoinAgent() {
    	outerjoinStudyAgents();
    	leftJoin("sagents.agent as agt");
    }    
    
    /**
     * Add a NOT condition on Study.id
     * @param id
     */
    public void ignoreStudyById(Integer id){
    	andWhere("s.id <> :" + STUDY_ID);
    	setParameter(STUDY_ID, id);
    }
    
    public void filterByTerminology(Integer code, String operator) {
    	andWhere("terminology.term " + parseOperator(operator) + " :term");
        setParameter("term", Term.getByCode(code));
    }
    
    public void filterByTreatmentCode(String code, String operator) {
    	
    	andWhere("lower(ta.code) " + parseOperator(operator) + " :CODE");
    	if (operator.equals("like")) {
    		setParameter("CODE", getLikeValue(code.toLowerCase()));
    	} else {
    		setParameter("CODE", code.toLowerCase());
    	}
    }

	public void filterByTreatmentDescription(String description,String operator) {
    	andWhere("lower(ta.description) " + parseOperator(operator) + " :DESC");
    	if (operator.equals("like")) {
    		setParameter("DESC", getLikeValue(description.toLowerCase()));
    	} else {
    		setParameter("DESC", description.toLowerCase());
    	}
        
    }
 
    public void filterByOtherIntervention(Integer code, String operator) {
    	orWhere("i.studyTherapyType " + parseOperator(operator) + " :OTHERINT" );
    	setParameter("OTHERINT", code);
    }
    
    public void filterByDeviceIntervention(Integer code, String operator) {
    	orWhere("d.studyTherapyType " + parseOperator(operator) + " :DEVICEINT" );
    	setParameter("DEVICEINT", code);
    }
    
    public void filterByAgentIntervention(Integer code, String operator) {
    	orWhere("sai.studyTherapyType " + parseOperator(operator) + " :AGENTINT" );
    	setParameter("AGENTINT", code);
    }
    
    public void filterByStudyIntervention(Integer code, String operator) {
    	orWhere("i.studyTherapyType " + parseOperator(operator) + " :STUDYINT" );
    	orWhere("d.studyTherapyType " + parseOperator(operator) + " :STUDYINT" );
    	orWhere("sai.studyTherapyType " + parseOperator(operator) + " :STUDYINT" );
    	setParameter("STUDYINT", code);
    }
    
    public void filterByStudySubjectIdentifier(String studySubjectIdentifier,String operator) {
    	final String param = generateParam();
    	andWhere("lower(spa.studySubjectIdentifier) " + parseOperator(operator) + " :" + param);
    	if (operator.equals("like")) {
    		setParameter(param, getLikeValue(studySubjectIdentifier.toLowerCase()));
    	} else {
    		setParameter(param, studySubjectIdentifier.toLowerCase());
    	}
        
    }

    public void filterByAgent(Integer id ,String operator) {
    	andWhere("agt.id " + parseOperator(operator) + " :" + generateParam(id));
    }
    
    // identifier
    public void filterByIdentifierValue(final String Identifiervalue) {
    	joinIdentifier();
        String searchString = "%" + Identifiervalue.toLowerCase() + "%";
        andWhere("lower(identifier.value) LIKE :" + generateParam(searchString));
    }

    public void filterByIdentifierValueExactMatch(final String identifiervalue) {
		joinIdentifier();
        String searchString = identifiervalue.toLowerCase();
        andWhere("lower(identifier.value) LIKE :" + generateParam(searchString));
    }

    public void filterByIdentifierType(final String type) {
    	joinIdentifier();
        andWhere("identifier.type LIKE :" + generateParam(type));
    }
    
    public void filterByIdentifier(Identifier identifier){
    	joinIdentifier();
    	//type
    	String type = identifier.getType();
    	if(type != null){
    		andWhere("lower(identifier.type) = :" + generateParam(type.toLowerCase()));
    	}
    	
    	//value
    	String value = identifier.getValue();
    	if(value != null){
    		andWhere("lower(identifier.value) = :" + generateParam(value.toLowerCase()));
    	}
    	
    	if(identifier instanceof OrganizationAssignedIdentifier){
    		//organization
            Organization org =  ((OrganizationAssignedIdentifier) identifier).getOrganization();
            if (org != null) {
				if (org.getNciInstituteCode() != null) {
					andWhere("identifier.organization.nciInstituteCode = :" + generateParam(org.getNciInstituteCode()));
				} else {
					andWhere("identifier.organization.id = :" + generateParam(org.getId()));
				}
			}

    	}else {
    		//system
    		andWhere("lower(identifier.systemName) = :" + STUDY_IDENTIFIER_SYSTEM);
    		setParameter(STUDY_IDENTIFIER_SYSTEM, ((SystemAssignedIdentifier)identifier).getSystemName());
    	}
    }

    // shortTitle
    public void filterByShortTitle(final String shortTitleText) {
        andWhere("lower(s.shortTitle) LIKE :" + generateParam("%" + shortTitleText.toLowerCase() + "%"));
    }

    public void filterByShortTitleOrIdentifiers(String text) {
        orWhere("lower(s.shortTitle) LIKE :" + generateParam("%" + text.toLowerCase() + "%"));
        leftJoin(STUDY_ALIAS + ".identifiers as identifier");
        orWhere("lower(identifier.value) LIKE :"  + generateParam("%" + text.toLowerCase() + "%"));
    }

    // longTitle
    public void filterByLongTitle(final String longTitleText) {
        andWhere("lower(s.shortTitle) LIKE :" + generateParam("%" + longTitleText.toLowerCase() + "%"));
    }
    
    // id
    public void filterById(final Integer id) {
        andWhere("s.id = :ID");
        setParameter("ID", id);
    }

    // participant-id
    public void filterByParticipantId(final Integer id) {
        andWhere("p.id = :id");
        setParameter("id", id);
    }

    // participant DOB
    public void filterByParticipantDateOfBirth(String strDob) {
        andWhere("p.dateOfBirth = :pDOB");
        Date dob = null;
        try {
            dob = dateFormat.parse(strDob);
        } catch (Exception e) {
        }
        setParameter("pDOB", dob);
    }
    
    public void filterStudiesWithMatchingText(String text) {
    	final String param = generateParam();
    	joinIdentifier();
        String searchString = text != null ? "%" + text.toLowerCase() + "%" : null;
        andWhere(String.format("(lower(s.shortTitle) LIKE :%s or lower(s.longTitle) LIKE :%s " 
        + "or lower(identifier.value) LIKE :%s)", param, param, param));
        setParameter(param, searchString);
    }
    
    /**
     * Introduced to have left join fetch on identifiers
     * @param text
     */
    public void filterStudiesMatchingText(String text) {
    	final String param = generateParam();
    	leftJoinFetch(STUDY_ALIAS+".identifiers as identifier");
        final String searchString = text != null ? "%" + text.toLowerCase() + "%" : null;
        andWhere(String.format("(lower(s.shortTitle) LIKE :%s or lower(s.longTitle) LIKE :%s or lower(identifier.value) LIKE :%s)", param, param, param));
        setParameter(param, searchString);
    }


    // participantIdentifier
    public void filterByParticipantIdentifierValue(final String participantIdentifierValue) {
        andWhere("lower(pIdentifier.value) LIKE :" + generateParam("%" + participantIdentifierValue.toLowerCase() + "%"));
    }

    /**
     * filter ther result by sponsor organizationId
     * @param id
     */
    public void filterBySponsorOrganizationId(Integer id){
    	andWhere("ss.class = 'SFS'");
    	andWhere("ss.organization.id = :" + generateParam(id));
    }
    
    public void filterByStudyOrganizationNameExactMatch(String studyOrgName){
    	andWhere("ss.organization.name = :" + generateParam(studyOrgName));
    }
    
    public void filterByOrganizationId(Integer id){
    	andWhere("ss.organization.id = :" + generateParam(id));
    }
    
    /**
     * If true, will return only DATA ENTRY completed studies.
     * @param ignoreNonQCedStudy
     */
    public void filterByDataEntryStatus(boolean ignoreNonQCedStudy) {
        if (ignoreNonQCedStudy) {
            andWhere("s.dataEntryStatus = :" + generateParam(true));
        }
    }
    
    public void filterStudiesByOrganizations(String[] organizationCodes) {
        if (organizationCodes != null && organizationCodes.length > 0) {
        	joinStudyOrganization();
        	String orgCodes = generateParam();
            andWhere(String.format("ss.organization.nciInstituteCode in (:%s)", orgCodes));
            setParameter(orgCodes, Arrays.asList(organizationCodes));
        }
    }

    public void orderBy(String orderField){
        super.orderBy(STUDY_ALIAS + "." + orderField);
    }

}
