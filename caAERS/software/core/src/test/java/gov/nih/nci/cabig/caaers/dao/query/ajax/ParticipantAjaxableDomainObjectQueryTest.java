package gov.nih.nci.cabig.caaers.dao.query.ajax;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import gov.nih.nci.cabig.caaers.domain.ajax.ParticipantAjaxableDomainObject;

/**
 * @author Saurabh Agrawal
 */
public class ParticipantAjaxableDomainObjectQueryTest extends TestCase {
    
	public void testTest() throws Exception {
	}
	private String baseQuery = "select participant.id, participant.firstName, participant.lastName, " +
            "participant.gender,participant.race,participant.ethnicity," +
            "identifier.value,identifier.primaryIndicator,spa.studySubjectIdentifier from Participant participant " +
            "left join participant.identifiers as identifier left join participant.assignments as spa " ;
					
    public void testQueryConstructor() throws Exception {
        AbstractAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
       // String qry = "Select participant.id,participant.firstName,participant.lastName, participant.gender,participant.race,participant.ethnicity, identifier.value, identifier.primaryIndicator, study.shortTitle as st, study.id as studyId, sIdentifier.value, sIdentifier.primaryIndicator, studyOrgs.organization.name, studyOrgs.id, studyOrgs.class, studyOrgs.organization.nciInstituteCode, siteResearchStaff.researchStaff.id, ss.organization.id as assignedSiteId, ss.organization.name as assignedSite, ss.organization.nciInstituteCode as assignedSiteCode, spa.studySubjectIdentifier from Participant participant left join participant.identifiers as identifier left join participant.assignments as spa join spa.studySite as ss join ss.study as study join study.identifiers as sIdentifier join study.studyOrganizations as studyOrgs left join studyOrgs.studyPersonnelsInternal as stper left join stper.siteResearchStaff as siteResearchStaff order by participant.firstName";
    	String qry = baseQuery + "order by participant.firstName";
        assertEquals("wrong parsing for constructor",qry.trim(), query.getQueryString().trim());
    }

    public void testFilterByStudyId() throws Exception {
        ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
        query.filterByStudy(1);        
        //String qry = "Select participant.id,participant.firstName,participant.lastName, participant.gender,participant.race,participant.ethnicity, identifier.value, identifier.primaryIndicator, study.shortTitle as st, study.id as studyId, sIdentifier.value, sIdentifier.primaryIndicator, studyOrgs.organization.name, studyOrgs.id, studyOrgs.class, studyOrgs.organization.nciInstituteCode, siteResearchStaff.researchStaff.id, ss.organization.id as assignedSiteId, ss.organization.name as assignedSite, ss.organization.nciInstituteCode as assignedSiteCode, spa.studySubjectIdentifier from Participant participant left join participant.identifiers as identifier left join participant.assignments as spa join spa.studySite as ss join ss.study as study join study.identifiers as sIdentifier join study.studyOrganizations as studyOrgs left join studyOrgs.studyPersonnelsInternal as stper left join stper.siteResearchStaff as siteResearchStaff WHERE study.id =:studyId order by participant.firstName";
       
        String qry = baseQuery + "join spa.studySite as ss join ss.study as study WHERE study.id =:studyId  order by participant.firstName";
        
        assertEquals(qry.trim(), query.getQueryString().trim());
        assertEquals("wrong number of parameters", query.getParameterMap().size(), 1);
        assertTrue("missing paramenter name", query.getParameterMap().containsKey("studyId"));
        assertEquals("wrong parameter value", query.getParameterMap().get("studyId"), Integer.valueOf(1));
    }


    public void testParticipantsWithMatchingText() throws Exception {
        ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
        query.filterParticipantsWithMatchingText("a");

        //String qry = "Select participant.id,participant.firstName,participant.lastName, participant.gender,participant.race,participant.ethnicity, identifier.value, identifier.primaryIndicator, study.shortTitle as st, study.id as studyId, sIdentifier.value, sIdentifier.primaryIndicator, studyOrgs.organization.name, studyOrgs.id, studyOrgs.class, studyOrgs.organization.nciInstituteCode, siteResearchStaff.researchStaff.id, ss.organization.id as assignedSiteId, ss.organization.name as assignedSite, ss.organization.nciInstituteCode as assignedSiteCode, spa.studySubjectIdentifier from Participant participant left join participant.identifiers as identifier left join participant.assignments as spa join spa.studySite as ss join ss.study as study join study.identifiers as sIdentifier join study.studyOrganizations as studyOrgs left join studyOrgs.studyPersonnelsInternal as stper left join stper.siteResearchStaff as siteResearchStaff WHERE (lower(participant.firstName) LIKE :firstName or lower(participant.lastName) LIKE :lastName or lower(identifier.value) LIKE :identifierValue or lower(spa.studySubjectIdentifier) LIKE :studySubjectIdentifier) order by participant.firstName";
        
        String qry = baseQuery + "WHERE (lower(participant.firstName) LIKE :firstName or lower(participant.lastName) LIKE :lastName or lower(identifier.value) LIKE :identifierValue or lower(spa.studySubjectIdentifier) LIKE :studySubjectIdentifier)  order by participant.firstName";
        
        
        
        
        assertEquals(qry.trim(), query.getQueryString());
        assertEquals("wrong number of parameters", 4, query.getParameterMap().size());

        assertTrue("missing paramenter name", query.getParameterMap().containsKey("lastName"));
        assertEquals("wrong parameter value", query.getParameterMap().get("lastName"),"%a%");
        assertTrue("missing paramenter name", query.getParameterMap().containsKey("identifierValue"));
        assertEquals("wrong parameter value", query.getParameterMap().get("identifierValue"),"%a%");
        assertTrue("missing paramenter name", query.getParameterMap().containsKey("firstName"));
        assertEquals("wrong parameter value", query.getParameterMap().get("firstName"),"%a%");
    }

    public void testParticipantsWithMatchingTextTwoTexts() throws Exception {
        ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
        query.filterParticipantsWithMatchingText("John Doe");
        String qry = baseQuery + "WHERE ((lower(participant.firstName) LIKE :firstName AND lower(participant.lastName) LIKE :lastName) OR (lower(participant.lastName) LIKE :firstName AND lower(participant.firstName) LIKE :lastName))  order by participant.firstName";

        assertEquals(qry.trim(), query.getQueryString());
        assertEquals("wrong number of parameters", 2, query.getParameterMap().size());

        assertTrue("missing paramenter name", query.getParameterMap().containsKey("lastName"));
        assertEquals("wrong parameter value", query.getParameterMap().get("lastName"),"%doe%");
        assertTrue("missing paramenter name", query.getParameterMap().containsKey("firstName"));
        assertEquals("wrong parameter value", query.getParameterMap().get("firstName"),"%john%");
    }

    public void testFilterByBothParticipantAndText() throws Exception {
        ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
        query.filterParticipantsWithMatchingText("a");
        query.filterByStudy(1);
       // String qry = "Select participant.id,participant.firstName,participant.lastName, participant.gender,participant.race,participant.ethnicity, identifier.value, identifier.primaryIndicator, study.shortTitle as st, study.id as studyId, sIdentifier.value, sIdentifier.primaryIndicator, studyOrgs.organization.name, studyOrgs.id, studyOrgs.class, studyOrgs.organization.nciInstituteCode, siteResearchStaff.researchStaff.id, ss.organization.id as assignedSiteId, ss.organization.name as assignedSite, ss.organization.nciInstituteCode as assignedSiteCode, spa.studySubjectIdentifier from Participant participant left join participant.identifiers as identifier left join participant.assignments as spa join spa.studySite as ss join ss.study as study join study.identifiers as sIdentifier join study.studyOrganizations as studyOrgs left join studyOrgs.studyPersonnelsInternal as stper left join stper.siteResearchStaff as siteResearchStaff WHERE (lower(participant.firstName) LIKE :firstName or lower(participant.lastName) LIKE :lastName or lower(identifier.value) LIKE :identifierValue or lower(spa.studySubjectIdentifier) LIKE :studySubjectIdentifier) AND study.id =:studyId order by participant.firstName";

        String qry = baseQuery + "join spa.studySite as ss join ss.study as study WHERE (lower(participant.firstName) LIKE :firstName or lower(participant.lastName) LIKE :lastName or lower(identifier.value) LIKE :identifierValue or lower(spa.studySubjectIdentifier) LIKE :studySubjectIdentifier) AND study.id =:studyId  order by participant.firstName";

        assertEquals(qry.trim(),query.getQueryString());
        assertEquals("wrong number of parameters", 5, query.getParameterMap().size());
        assertTrue("missing paramenter name", query.getParameterMap().containsKey("lastName"));
        assertEquals("wrong parameter value", query.getParameterMap().get("lastName"),"%a%");
        assertTrue("missing paramenter name", query.getParameterMap().containsKey("identifierValue"));
        assertEquals("wrong parameter value", query.getParameterMap().get("identifierValue"),"%a%");
        assertTrue("missing paramenter name", query.getParameterMap().containsKey("firstName"));
        assertEquals("wrong parameter value", query.getParameterMap().get("firstName"),"%a%");
        assertTrue("missing paramenter name", query.getParameterMap().containsKey("studyId"));
        assertEquals("wrong parameter value", query.getParameterMap().get("studyId"),Integer.valueOf(1));
    }
    
    public void testFilterByPrimaryIdentifiers(){
    	ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
    	query.filterByPrimaryIdentifiers();
    	//String qry = "Select participant.id,participant.firstName,participant.lastName, participant.gender,participant.race,participant.ethnicity, identifier.value, identifier.primaryIndicator, study.shortTitle as st, study.id as studyId, sIdentifier.value, sIdentifier.primaryIndicator, studyOrgs.organization.name, studyOrgs.id, studyOrgs.class, studyOrgs.organization.nciInstituteCode, siteResearchStaff.researchStaff.id, ss.organization.id as assignedSiteId, ss.organization.name as assignedSite, ss.organization.nciInstituteCode as assignedSiteCode, spa.studySubjectIdentifier from Participant participant left join participant.identifiers as identifier left join participant.assignments as spa join spa.studySite as ss join ss.study as study join study.identifiers as sIdentifier join study.studyOrganizations as studyOrgs left join studyOrgs.studyPersonnelsInternal as stper left join stper.siteResearchStaff as siteResearchStaff WHERE identifier.primaryIndicator is true and sIdentifier.primaryIndicator is true order by participant.firstName";
    	String qry = baseQuery + "WHERE identifier.primaryIndicator is true and sIdentifier.primaryIndicator is true  order by participant.firstName";
    	assertEquals(qry.trim(),query.getQueryString());
    }
 /*   
    public void testFilterByStudyIdentifierValue(){
    	ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
    	query.filterByStudyIdentifierValue("idvalue");
    	
    	String qry = "Select participant.id,participant.firstName,participant.lastName, participant.gender,participant.race,participant.ethnicity, identifier.value, identifier.primaryIndicator, study.shortTitle as st, study.id as studyId, sIdentifier.value, sIdentifier.primaryIndicator, studyOrgs.organization.name, studyOrgs.id, studyOrgs.class, studyOrgs.organization.nciInstituteCode, siteResearchStaff.researchStaff.id, ss.organization.id as assignedSiteId, ss.organization.name as assignedSite, ss.organization.nciInstituteCode as assignedSiteCode, spa.studySubjectIdentifier from Participant participant left join participant.identifiers as identifier left join participant.assignments as spa join spa.studySite as ss join ss.study as study join study.identifiers as sIdentifier join study.studyOrganizations as studyOrgs left join studyOrgs.studyPersonnelsInternal as stper left join stper.siteResearchStaff as siteResearchStaff WHERE lower(sIdentifier.value) LIKE :studyIdentifierValue order by participant.firstName";
    	
    	assertEquals(qry.trim(), query.getQueryString());
    	assertTrue("missing paramenter name", query.getParameterMap().containsKey("studyIdentifierValue"));
    	assertEquals("wrong parameter value", query.getParameterMap().get("studyIdentifierValue"),"%idvalue%");
    }
    
    public void testFilterByStudyShortTitle(){
    	ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
    	query.filterByStudyShortTitle("shorttitle");
    	
    	String qry = "Select participant.id,participant.firstName,participant.lastName, participant.gender,participant.race,participant.ethnicity, identifier.value, identifier.primaryIndicator, study.shortTitle as st, study.id as studyId, sIdentifier.value, sIdentifier.primaryIndicator, studyOrgs.organization.name, studyOrgs.id, studyOrgs.class, studyOrgs.organization.nciInstituteCode, siteResearchStaff.researchStaff.id, ss.organization.id as assignedSiteId, ss.organization.name as assignedSite, ss.organization.nciInstituteCode as assignedSiteCode, spa.studySubjectIdentifier from Participant participant left join participant.identifiers as identifier left join participant.assignments as spa join spa.studySite as ss join ss.study as study join study.identifiers as sIdentifier join study.studyOrganizations as studyOrgs left join studyOrgs.studyPersonnelsInternal as stper left join stper.siteResearchStaff as siteResearchStaff WHERE lower(study.shortTitle) LIKE :shortTitle order by participant.firstName";
    	
    	assertEquals(qry.trim(), query.getQueryString());
    	assertTrue("missing paramenter name", query.getParameterMap().containsKey("shortTitle"));
    	assertEquals("wrong parameter value", query.getParameterMap().get("shortTitle"), "%shorttitle%");
    }
    
    public void testFilterStudiesWithShortTitle(){
    	
    	ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
    	query.filterStudiesWithShortTitle("shorttitle");
    	
    	String qry = "Select participant.id,participant.firstName,participant.lastName, participant.gender,participant.race,participant.ethnicity, identifier.value, identifier.primaryIndicator, study.shortTitle as st, study.id as studyId, sIdentifier.value, sIdentifier.primaryIndicator, studyOrgs.organization.name, studyOrgs.id, studyOrgs.class, studyOrgs.organization.nciInstituteCode, siteResearchStaff.researchStaff.id, ss.organization.id as assignedSiteId, ss.organization.name as assignedSite, ss.organization.nciInstituteCode as assignedSiteCode, spa.studySubjectIdentifier from Participant participant left join participant.identifiers as identifier left join participant.assignments as spa join spa.studySite as ss join ss.study as study join study.identifiers as sIdentifier join study.studyOrganizations as studyOrgs left join studyOrgs.studyPersonnelsInternal as stper left join stper.siteResearchStaff as siteResearchStaff WHERE (lower(study.shortTitle) LIKE :shortTitle ) order by participant.firstName";
    	
    	assertEquals(qry.trim(), query.getQueryString());
    	assertTrue("missing paramenter name", query.getParameterMap().containsKey("shortTitle"));
    	assertEquals("wrong parameter value", query.getParameterMap().get("shortTitle"),"%shorttitle%");
    }*/
    
    public void testFilterParticipants(){
    	ParticipantAjaxableDomainObjectQuery query = new ParticipantAjaxableDomainObjectQuery();
    	
    	Map<String,String> searchMap = new HashMap<String,String>();
    	searchMap.put("participantIdentifier", "participantIdentifier");
    	searchMap.put("participantFirstName", "FirstName");
    	searchMap.put("participantLastName", "LastName");
    	//searchMap.put("participantEthnicity", "Ethnicity");
    	//searchMap.put("participantGender", "Gender");
    	//searchMap.put("participantDateOfBirth", "01/01/1980");
    	
    	try {
			query.filterParticipants(searchMap);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//String qry = "Select participant.id,participant.firstName,participant.lastName, participant.gender,participant.race,participant.ethnicity, identifier.value, identifier.primaryIndicator, study.shortTitle as st, study.id as studyId, sIdentifier.value, sIdentifier.primaryIndicator, studyOrgs.organization.name, studyOrgs.id, studyOrgs.class, studyOrgs.organization.nciInstituteCode, siteResearchStaff.researchStaff.id, ss.organization.id as assignedSiteId, ss.organization.name as assignedSite, ss.organization.nciInstituteCode as assignedSiteCode, spa.studySubjectIdentifier from Participant participant left join participant.identifiers as identifier left join participant.assignments as spa join spa.studySite as ss join ss.study as study join study.identifiers as sIdentifier join study.studyOrganizations as studyOrgs left join studyOrgs.studyPersonnelsInternal as stper left join stper.siteResearchStaff as siteResearchStaff WHERE lower(identifier.value) LIKE :identifierValue or lower(spa.studySubjectIdentifier) LIKE :identifierValue AND lower(participant.firstName) LIKE :firstName AND lower(participant.lastName) LIKE :lastName AND participant.ethnicity = :ethnicity AND participant.gender = :race AND  participant.dateOfBirth.year = :year AND  participant.dateOfBirth.month = :month AND  participant.dateOfBirth.day = :day order by participant.firstName";
    	String qry = baseQuery + "WHERE (lower(identifier.value) LIKE :identifierValue or lower(spa.studySubjectIdentifier) LIKE :identifierValue) AND lower(participant.firstName) LIKE :firstName AND lower(participant.lastName) LIKE :lastName  order by participant.firstName";

    	assertEquals(qry.trim(),query.getQueryString());
    	
    	assertTrue("missing paramenter name", query.getParameterMap().containsKey("identifierValue"));
    	assertEquals("wrong parameter value", query.getParameterMap().get("identifierValue"),"%participantidentifier%");
    	
    	assertTrue("missing paramenter name", query.getParameterMap().containsKey("firstName"));
    	assertEquals("wrong parameter value", query.getParameterMap().get("firstName"),"%firstname%");
    	
    	assertTrue("missing paramenter name", query.getParameterMap().containsKey("lastName"));
    	assertEquals("wrong parameter value", query.getParameterMap().get("lastName"),"%lastname%");
    	/*
    	assertTrue("missing paramenter name", query.getParameterMap().containsKey("ethnicity"));
    	assertEquals("wrong parameter value", query.getParameterMap().get("ethnicity"),"Ethnicity");
    	
    	assertTrue("missing paramenter name", query.getParameterMap().containsKey("race"));
    	assertEquals("wrong parameter value", query.getParameterMap().get("race"),"Gender");
    	
    	assertTrue("missing paramenter name", query.getParameterMap().containsKey("year"));
    	assertEquals("wrong parameter value", query.getParameterMap().get("year"),1980);
    	
    	assertTrue("missing paramenter name", query.getParameterMap().containsKey("month"));
    	assertEquals("wrong parameter value", query.getParameterMap().get("month"),1);
    	
    	assertTrue("missing paramenter name", query.getParameterMap().containsKey("day"));
    	assertEquals("wrong parameter value", query.getParameterMap().get("day"),1);
    	*/
    }

    public void testAssignmentIdentifiers() {
        ParticipantAjaxableDomainObject o = new ParticipantAjaxableDomainObject();
        o.collectStudySubjectIdentifier("ID-One");
        o.collectStudySubjectIdentifier("ID-Two");
        o.collectStudySubjectIdentifier("ID-Three");
        assertEquals(24, o.getStudySubjectIdentifiersCSV().length());
    }
    
    public void testAssignmentIdentifiersNoIdentifiers() {
        ParticipantAjaxableDomainObject o = new ParticipantAjaxableDomainObject();
        assertEquals(0, o.getStudySubjectIdentifiersCSV().length());
    }
}