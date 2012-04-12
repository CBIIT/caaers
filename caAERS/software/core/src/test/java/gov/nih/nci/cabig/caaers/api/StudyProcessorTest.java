package gov.nih.nci.cabig.caaers.api;

import edu.nwu.bioinformatics.commons.ResourceRetriever;
import gov.nih.nci.cabig.caaers.CaaersDbNoSecurityTestCase;
import gov.nih.nci.cabig.caaers.api.impl.StudyProcessorImpl;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.domain.CtepStudyDisease;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyInvestigator;
import gov.nih.nci.cabig.caaers.domain.StudyPersonnel;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.TreatmentAssignment;
import gov.nih.nci.cabig.caaers.integration.schema.common.CaaersServiceResponse;
import gov.nih.nci.cabig.caaers.security.SecurityTestUtils;
import gov.nih.nci.cabig.caaers.service.migrator.StudyMigrator;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 * Test case to test conversion of JAXB study object to domain study object and call to {@link StudyMigrator} with study domain object.
 *
 * @author Monish Dombla
 * @author Biju Joseph - added testcases for identifiers uniqueness.
 */
public class StudyProcessorTest extends CaaersDbNoSecurityTestCase {

    private StudyProcessorImpl studyProcessor = null;
    private JAXBContext jaxbContext = null;
    private Unmarshaller unmarshaller = null;
    private gov.nih.nci.cabig.caaers.integration.schema.study.Studies studies = null;
    private StudyDao studyDao = null;

    Identifier identifier = null;
    Organization organization = null;
    Study updatedStudy = null;
    boolean authorizationOnByDefault;

    @Override
    protected void setUp() throws Exception {
        super.setUp();


        jaxbContext = JAXBContext.newInstance("gov.nih.nci.cabig.caaers.integration.schema.study");
        unmarshaller = jaxbContext.createUnmarshaller();
        studyProcessor = (StudyProcessorImpl)getDeployedApplicationContext().getBean("studyProcessorImpl");
        studyDao = (StudyDao) getDeployedApplicationContext().getBean("studyDao");

        updatedStudy = studyDao.getByShortTitle("Study PCS");
        if (updatedStudy != null) {
            updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());
            studyDao.delete(updatedStudy);
        }
    }
    
    /**
     * Tests the update of attributes shortTitle,longTitle,percis,description,phaseCode,
     * status,design,multiInsitutionIndicator,adeersReporting,
     * Also tests the update of Study Therapies.
     * DrugAdministrationTherapyType,RadiationTherapyType,DeviceTherapyType,SurgeryTherapyType,
     * BehavioralTherapyType
     */
    public void testStudyUpdateOfInstanceAtt() throws Exception {
        createStudy("studydata/CreateStudyTest.xml");
        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfInstanceAtt.xml"));
        studyProcessor.updateStudy(studies);
        SecurityTestUtils.switchToSuperuser();
        updatedStudy = studyDao.getByShortTitle("Study_PCS_Updated");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());
        assertNotNull(updatedStudy);
        assertEquals("Phase III Trial", updatedStudy.getPhaseCode());
    }


    /**
     * theCode1 and theCode3 are 2 TreatmentAssignments for the study with shorttitle Study PSC.
     * Tests Treatmentassignment updates. The attributes doseLevelOrder,description and comments
     * are updated for 2 exisitng treatmentassignments.
     */
    public void testStudyUpdateOfTreatmentAssignmentAttr() throws Exception {

        createStudy("studydata/CreateStudyTest.xml");

        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfTreatmentAssignmentAttr.xml"));

        studyProcessor.updateStudy(studies);

        SecurityTestUtils.switchToSuperuser();

        updatedStudy = studyDao.getByShortTitle("Study PCS");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());

        assertNotNull(updatedStudy);

        assertEquals(2, updatedStudy.getTreatmentAssignments().size());

        for (TreatmentAssignment treatmentAssignment : updatedStudy.getTreatmentAssignments()) {
            if (treatmentAssignment.getCode().equals("theCode1")) {
                assertEquals(2, treatmentAssignment.getDoseLevelOrder().intValue());
                assertEquals("Description_Updated", treatmentAssignment.getDescription());
                assertEquals("Comments_Updated", treatmentAssignment.getComments());
            }
            if (treatmentAssignment.getCode().equals("theCode3")) {
                assertEquals(3, treatmentAssignment.getDoseLevelOrder().intValue());
                assertEquals("Description3_Updated", treatmentAssignment.getDescription());
                assertEquals("Comments3_Updated", treatmentAssignment.getComments());
            }
        }
    }


    /**
     * Study with short title Study PSC has 2 TreatmentAssignments.
     * This testcase tests the addition of a TreatmentAssignment to an existing study.
     * theCode1 and theCode3 are the existing treatment assignments.
     * theCode4 is the new treamentAssignment.
     */
    public void testStudyUpdateOfTreatmentAssignmentAdd() throws Exception {

        createStudy("studydata/CreateStudyTest.xml");

        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfTreatmentAssignmentAdd.xml"));

        studyProcessor.updateStudy(studies);

        SecurityTestUtils.switchToSuperuser();

        updatedStudy = studyDao.getByShortTitle("Study PCS");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());

        assertNotNull(updatedStudy);

        assertEquals(3, updatedStudy.getTreatmentAssignments().size());

        for (TreatmentAssignment treatmentAssignment : updatedStudy.getTreatmentAssignments()) {
            if (treatmentAssignment.getCode().equals("theCode1")) {
                assertEquals(1, treatmentAssignment.getDoseLevelOrder().intValue());
                assertEquals("description1", treatmentAssignment.getDescription());
                assertEquals("Comments1", treatmentAssignment.getComments());
            }
            if (treatmentAssignment.getCode().equals("theCode3")) {
                assertEquals(2, treatmentAssignment.getDoseLevelOrder().intValue());
                assertEquals("description3", treatmentAssignment.getDescription());
                assertEquals("Comments3", treatmentAssignment.getComments());
            }
            if (treatmentAssignment.getCode().equals("theCode4")) {
                assertEquals(4, treatmentAssignment.getDoseLevelOrder().intValue());
                assertEquals("description4", treatmentAssignment.getDescription());
                assertEquals("Comments4", treatmentAssignment.getComments());
            }
        }
    }


    /**
     * 2 ctepStudyDiseases with term "Chondrosarcoma" and "Medulloblastoma" exists.
     * Tests the addition of an other ctepStudyDisease with term "Osteosarcoma"
     */
    public void testStudyUpdateOfCtepStudyDiseasesAdd() throws Exception {

        createStudy("studydata/CreateStudyTest.xml");

        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfCtepStudyDiseasesAdd.xml"));

        studyProcessor.updateStudy(studies);

        SecurityTestUtils.switchToSuperuser();

        updatedStudy = studyDao.getByShortTitle("Study PCS");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());

        assertNotNull(updatedStudy);

        assertEquals(3, updatedStudy.getCtepStudyDiseases().size());

        for (CtepStudyDisease ctepStudyDisease : updatedStudy.getCtepStudyDiseases()) {
            if (ctepStudyDisease.getDiseaseTerm().getCtepTerm().equals("Chondrosarcoma")) {
                assertFalse(ctepStudyDisease.getLeadDisease());
            }
            if (ctepStudyDisease.getDiseaseTerm().getCtepTerm().equals("Osteosarcoma")) {
                assertFalse(ctepStudyDisease.getLeadDisease());
            }
            if (ctepStudyDisease.getDiseaseTerm().getCtepTerm().equals("Medulloblastoma")) {
                assertTrue(ctepStudyDisease.getLeadDisease());
            }
        }
    }

    /**
     * 2 ctepStudyDiseases with terms "Chondrosarcoma" and "Medulloblastoma" exists.
     * leadDisease = false for Chondrosarcoma
     * leadDisease = true for  Medulloblastoma
     * <p/>
     * Tests the leadDisease change.
     * leadDisease = true for Chondrosarcoma
     * leadDisease = false for  Medulloblastoma
     */
    public void testStudyUpdateOfCtepStudyDiseasesUpdate() throws Exception {

        createStudy("studydata/CreateStudyTest.xml");

        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfCtepStudyDiseasesUpdate.xml"));

        studyProcessor.updateStudy(studies);

        SecurityTestUtils.switchToSuperuser();

        updatedStudy = studyDao.getByShortTitle("Study PCS");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());

        assertNotNull(updatedStudy);

        assertEquals(2, updatedStudy.getCtepStudyDiseases().size());

        for (CtepStudyDisease ctepStudyDisease : updatedStudy.getCtepStudyDiseases()) {
            //System.out.println("Term  " + ctepStudyDisease.getDiseaseTerm().getCtepTerm());
            if (ctepStudyDisease.getDiseaseTerm().getCtepTerm().equals("Chondrosarcoma")) {
                assertTrue(ctepStudyDisease.getLeadDisease());
            }
            if (ctepStudyDisease.getDiseaseTerm().getCtepTerm().equals("Medulloblastoma")) {
                assertFalse(ctepStudyDisease.getLeadDisease());
            }
        }
    }


    /**
     * 2 meddraStudyDiseases with code "10000002" and "10000003" exists.
     * Tests the addition of an other meddraStudyDisease with code "10000004"
     */
    public void testStudyUpdateOfMeddraStudyDiseasesAdd() throws Exception {

        createStudy("studydata/CreateStudyTest.xml");

        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfMeddraStudyDiseasesAdd.xml"));

        studyProcessor.updateStudy(studies);

        SecurityTestUtils.switchToSuperuser();

        updatedStudy = studyDao.getByShortTitle("Study PCS");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());

        assertNotNull(updatedStudy);
        assertEquals(3, updatedStudy.getMeddraStudyDiseases().size());
    }


    /**
     * 2 StudySites Cancer Therapy Evaluation Program and University of Jonathan Dean already exists
     * Tests the addition of a third StudySite Sydney Hospital with one ResearchStaff
     */
    public void testStudyUpdateOfStudySiteAdd() throws Exception {

        createStudy("studydata/CreateStudyTest.xml");

        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfStudySiteAdd.xml"));

        studyProcessor.updateStudy(studies);

        SecurityTestUtils.switchToSuperuser();

        updatedStudy = studyDao.getByShortTitle("Study PCS");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());

        assertNotNull(updatedStudy);

        assertEquals(3, updatedStudy.getStudySites().size());
    }


    /**
     * 2 StudyInvestigators are on for Cancer Therapy Evaluation Program
     * Tests the addition of a StudyInvestigator to Cancer Therapy Evaluation Program (David Algor added)
     */
    public void testStudyUpdateOfStudySite_StudyInvestigatorAdd() throws Exception {

        createStudy("studydata/CreateStudyTest.xml");

        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfStudySite_StudyInvestigatorAdd.xml"));

        studyProcessor.updateStudy(studies);

        SecurityTestUtils.switchToSuperuser();

        updatedStudy = studyDao.getByShortTitle("Study PCS");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());

        assertNotNull(updatedStudy);

        assertEquals(2, updatedStudy.getStudySites().size());

        for (StudySite studySite : updatedStudy.getStudySites()) {
            if (studySite.getOrganization() != null) {
                if ("Cancer Therapy Evaluation Program".equals(studySite.getOrganization().getName())) {
                    assertEquals(3, studySite.getStudyInvestigators().size());
                }
            }
        }
    }


    /**
     * 2 StudyInvestigators are on for Cancer Therapy Evaluation Program
     * Tests the status update of a StudyInvestigator from "Active" to "Inactive"
     * RoleCode updated from "Site Investigator" to "Site Principal Investigator" (George Clinton Status updated)
     */
    public void testStudyUpdateOfStudySite_StudyInvestigatorUpdate() throws Exception {

        createStudy("studydata/CreateStudyTest.xml");

        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfStudySite_StudyInvestigatorUpdate.xml"));

        studyProcessor.updateStudy(studies);

        SecurityTestUtils.switchToSuperuser();

        updatedStudy = studyDao.getByShortTitle("Study PCS");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());

        assertNotNull(updatedStudy);

        assertEquals(2, updatedStudy.getStudySites().size());

        for (StudySite studySite : updatedStudy.getStudySites()) {
            if (studySite.getOrganization() != null) {
                if ("Cancer Therapy Evaluation Program".equals(studySite.getOrganization().getName())) {
                    assertEquals(2, studySite.getStudyInvestigators().size());
                    for (StudyInvestigator studyInvestigator : studySite.getStudyInvestigators()) {
                        if ("George".equals(studyInvestigator.getSiteInvestigator().getInvestigator().getFirstName()) &&
                                "Clinton".equals(studyInvestigator.getSiteInvestigator().getInvestigator().getLastName())) {
                            assertEquals("SI", studyInvestigator.getRoleCode());
                        }
                        if ("Gerry".equals(studyInvestigator.getSiteInvestigator().getInvestigator().getFirstName()) &&
                                "Elbridge".equals(studyInvestigator.getSiteInvestigator().getInvestigator().getLastName())) {
                            assertEquals("SPI", studyInvestigator.getRoleCode());
                        }
                    }
                }
            }
        }
    }


    /**
     * 1 StudyPersonnel exists on for University of Jonathan Dean
     * Tests the addition of a StudyPersonnel to University of Jonathan Dean (Roger Keith added)
     */
    public void testStudyUpdateOfStudySite_StudyPersonnelAdd() throws Exception {

        createStudy("studydata/CreateStudyTest.xml");

        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfStudySite_StudyPersonnelAdd.xml"));

        studyProcessor.updateStudy(studies);

        SecurityTestUtils.switchToSuperuser();

        updatedStudy = studyDao.getByShortTitle("Study PCS");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());

        assertNotNull(updatedStudy);

        assertEquals(2, updatedStudy.getStudySites().size());

        for (StudySite studySite : updatedStudy.getStudySites()) {
            if (studySite.getOrganization() != null) {
                if ("University of Jonathan Dean".equals(studySite.getOrganization().getName())) {
                    assertEquals(2, studySite.getStudyPersonnels().size());
                }
            }
        }
    }


    /**
     * 1 StudyPersonnel exists on for University of Jonathan Dean
     * Tests the updation of status and roleCode
     */
    public void testStudyUpdateOfStudySite_StudyPersonnelUpdate() throws Exception {

        createStudy("studydata/CreateStudyTest.xml");


        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfStudySite_StudyPersonnelUpdate.xml"));


        studyProcessor.updateStudy(studies);

        SecurityTestUtils.switchToSuperuser();


        updatedStudy = studyDao.getByShortTitle("Study PCS");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());

        assertNotNull(updatedStudy);

        assertEquals(2, updatedStudy.getStudySites().size());

        for (StudySite studySite : updatedStudy.getStudySites()) {
            if (studySite.getOrganization() != null) {
                if ("University of Jonathan Dean".equals(studySite.getOrganization().getName())) {
                    assertEquals(1, studySite.getStudyPersonnels().size());
                    for (StudyPersonnel studyPersonnel : studySite.getStudyPersonnels()) {
                        if ("Allan".equals(studyPersonnel.getSiteResearchStaff().getResearchStaff().getFirstName()) &&
                                "Border".equals(studyPersonnel.getSiteResearchStaff().getResearchStaff().getLastName())) {
                            assertEquals("Study Coordinator", studyPersonnel.getRoleCode());
                        }
                    }
                }
            }
        }
    }


    /**
     * 1 StudyAgent "1-Aminocyclopentane" exists for Study "Study PSC"
     * Tests the addition of an other StudyAgent "17-Methyltestosterone"
     */
    public void testStudyUpdateOfStudyAgentAdd() throws Exception {

        createStudy("studydata/CreateStudyTest.xml");

        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdateOfStudyAgentAdd.xml"));

        studyProcessor.updateStudy(studies);

        SecurityTestUtils.switchToSuperuser();

        updatedStudy = studyDao.getByShortTitle("Study PCS");
        assertNotNull(updatedStudy);
        updatedStudy = studyDao.getStudyDesignById(updatedStudy.getId());

        assertNotNull(updatedStudy);
        assertEquals(2, updatedStudy.getStudyAgents().size());

    }
    
    /**
     * Tests : Created a Study, then trying to create another study with same identifier. 
     * @throws Exception
     */
    public void testStudyCreate_DuplicateIdentifiers() throws Exception{
    	 createStudy("studydata/CreateStudyTest.xml");
    	 
    	 //make sure it got created
    	 Study study = studyDao.getByShortTitle("Study PCS");
    	 assertNotNull(study);
    	 
    	 CaaersServiceResponse response = createStudy("studydata/CreateStudyTest_3.xml");
    	 assertEquals("1", response.getServiceResponse().getResponsecode());
    }

    /**
     * Tests : Created a Study, then trying to update the same study, no changes made to identifiers, but added agents. 
     * @throws Exception
     */
    public void testStudyUpdate_NoDuplicateIdentifiers() throws Exception{
    	 createStudy("studydata/CreateStudyTest.xml");
    	 
    	 //make sure it got created
    	 Study study = studyDao.getByShortTitle("Study PCS");
    	 assertNotNull(study);

         studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdate_SameIdentifiers.xml"));
         CaaersServiceResponse response = studyProcessor.updateStudy(studies);
         assertEquals("0", response.getServiceResponse().getResponsecode());
    }
    
    /**
     * Created two studies (CreateStudyTest and CreateStudyTest_4), now trying to update first study by adding an identifier present in second study. 
     *  - Not update should fail. 
     * @throws Exception
     */
    public void testStudyUpdate_AddingDuplicateIdentifiers() throws Exception{
    	 createStudy("studydata/CreateStudyTest.xml");
    	 
    	 //make sure it got created
    	 Study study = studyDao.getByShortTitle("Study PCS");
    	 assertNotNull(study);

    	 CaaersServiceResponse response =  createStudy("studydata/CreateStudyTest_4.xml");
    	 assertEquals("0", response.getServiceResponse().getResponsecode());
    	 
    	//make sure it got created
    	 study = studyDao.getByShortTitle("A Strange Study");
    	 assertNotNull(study);
    	 
         studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream("studydata/StudyUpdate_SameIdentifiers.xml"));

         response = studyProcessor.updateStudy(studies);
         assertEquals("1", response.getServiceResponse().getResponsecode());
    }

    private CaaersServiceResponse createStudy(String studyXmlLocation) throws Exception {

        studies = (gov.nih.nci.cabig.caaers.integration.schema.study.Studies) unmarshaller.unmarshal(createInputStream(studyXmlLocation));
        return studyProcessor.createStudy(studies);
    }

    private InputStream createInputStream(String testDataFileName) throws FileNotFoundException {
        InputStream testDataStream = ResourceRetriever.getResource(getClass().getPackage(), testDataFileName);
        if (testDataStream == null) {
            testDataStream = handleTestDataFileNotFound();
            // if it is still null, fail gracefully
            if (testDataStream == null) {
                throw new NullPointerException(
                        "Test data resource " + ResourceRetriever.getResourceName(getClass().getPackage(), testDataFileName)
                                + " not found and fallback call to handleTestDataFileNotFound() did not provide a substitute.");
            }
        }
        return testDataStream;
    }

}
