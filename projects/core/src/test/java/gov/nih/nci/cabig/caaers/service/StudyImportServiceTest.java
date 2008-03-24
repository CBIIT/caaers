package gov.nih.nci.cabig.caaers.service;

import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.dao.*;
import gov.nih.nci.cabig.caaers.dao.meddra.LowLevelTermDao;
import gov.nih.nci.cabig.caaers.domain.*;
import org.easymock.classextension.EasyMock;

import java.util.List;

/**
 * @author Biju Joseph
 */
public class StudyImportServiceTest extends AbstractTestCase {

    private StudyImportServiceImpl studyImportService;

    private OrganizationDao organizationDao;
    private AgentDao agentDao;
    private MedDRADao meddraDao;
    private CtcDao ctcDao;
    private MeddraVersionDao meddraVersionDao;
    private SiteInvestigatorDao siteInvestigatorDao;
    private ResearchStaffDao researchStaffDao;
    private DiseaseTermDao diseaseTermDao;
    private LowLevelTermDao lowLevelTermDao;
    private InvestigationalNewDrugDao investigationalNewDrugDao;

    private Study xstreamStudy;
    private Organization organization;

    protected void setUp() throws Exception {
        super.setUp();
        organizationDao = registerMockFor(OrganizationDao.class);

        studyImportService = new StudyImportServiceImpl();
        studyImportService.setOrganizationDao(organizationDao);

        xstreamStudy = Fixtures.createStudy("short title");

        organization = Fixtures.createOrganization("org ");
    }


    public void testImportStudyForBasicProperties() {

        DomainObjectImportOutcome<Study> studyDomainObjectImportOutcome = studyImportService.importStudy(xstreamStudy);

        validate(xstreamStudy, studyDomainObjectImportOutcome);
        validateImportedObject(studyDomainObjectImportOutcome);


        List<DomainObjectImportOutcome.Message> messages = studyDomainObjectImportOutcome.getMessages();

        assertEquals(2, messages.size());
        assertEquals("Disease Code Term is either Empty or Not Valid", messages.get(0).getMessage());

        assertEquals("Identifiers are either Empty or Not Valid", messages.get(1).getMessage());

    }

    public void testImportStudyForMigratingTherapy() {

        xstreamStudy.setDrugAdministrationTherapyType(Boolean.TRUE);
        xstreamStudy.setRadiationTherapyType(Boolean.TRUE);


        DomainObjectImportOutcome<Study> studyDomainObjectImportOutcome = studyImportService.importStudy(xstreamStudy);
        assertEquals(2, studyDomainObjectImportOutcome.getImportedDomainObject().getStudyTherapies().size());

        validate(xstreamStudy, studyDomainObjectImportOutcome);
        validateImportedObject(studyDomainObjectImportOutcome);


        List<DomainObjectImportOutcome.Message> messages = studyDomainObjectImportOutcome.getMessages();

        assertEquals(2, messages.size());
        assertEquals("Disease Code Term is either Empty or Not Valid", messages.get(0).getMessage());

        assertEquals("Identifiers are either Empty or Not Valid", messages.get(1).getMessage());

    }

    public void testImportStudyForMigratingStudyOrganizations() {

        StudySite studySite = new StudySite();
        studySite.setStudy(xstreamStudy);
        studySite.setOrganization(organization);
        xstreamStudy.addStudyOrganization(studySite);

        EasyMock.expect(organizationDao.getByName(organization.getName())).andReturn(organization);
        replayMocks();
        DomainObjectImportOutcome<Study> studyDomainObjectImportOutcome = studyImportService.importStudy(xstreamStudy);
        verifyMocks();
        validate(xstreamStudy, studyDomainObjectImportOutcome);
        validateImportedObject(studyDomainObjectImportOutcome);


        List<DomainObjectImportOutcome.Message> messages = studyDomainObjectImportOutcome.getMessages();

        assertEquals(2, messages.size());
        assertEquals("Disease Code Term is either Empty or Not Valid", messages.get(0).getMessage());

        assertEquals("Identifiers are either Empty or Not Valid", messages.get(1).getMessage());

    }

    public void testImportStudyForMigratingTreatmentAssignments() {
        TreatmentAssignment treatmentAssignment = Fixtures.createTreatmentAssignment();
        xstreamStudy.addTreatmentAssignment(treatmentAssignment);

        DomainObjectImportOutcome<Study> studyDomainObjectImportOutcome = studyImportService.importStudy(xstreamStudy);

        validate(xstreamStudy, studyDomainObjectImportOutcome);
        validateImportedObject(studyDomainObjectImportOutcome);


        List<DomainObjectImportOutcome.Message> messages = studyDomainObjectImportOutcome.getMessages();

        assertEquals(2, messages.size());
        assertEquals("Disease Code Term is either Empty or Not Valid", messages.get(0).getMessage());

        assertEquals("Identifiers are either Empty or Not Valid", messages.get(1).getMessage());

    }

    private void validateImportedObject(final DomainObjectImportOutcome<Study> studyDomainObjectImportOutcome) {
        assertTrue(studyDomainObjectImportOutcome.hasErrors());
        assertFalse(studyDomainObjectImportOutcome.isSavable());

    }

    private void validate(final Study xstreamStudy, final DomainObjectImportOutcome<Study> studyDomainObjectImportOutcome) {

        Study study = studyDomainObjectImportOutcome.getImportedDomainObject();

        assertEquals(xstreamStudy.getLongTitle(), study.getLongTitle());
        assertEquals(xstreamStudy.getDescription(), study.getDescription());
        assertEquals(xstreamStudy.getPrecis(), study.getPrecis());
        assertEquals(xstreamStudy.getPhaseCode(), study.getPhaseCode());
        assertEquals(xstreamStudy.getStatus(), study.getStatus());
        assertEquals(xstreamStudy.getMultiInstitutionIndicator(), study.getMultiInstitutionIndicator());
        assertEquals(xstreamStudy.getAdeersReporting(), study.getAdeersReporting());
        assertEquals(xstreamStudy.getDesign(), study.getDesign());
        assertEquals(xstreamStudy.getDrugAdministrationTherapyType(), study.getDrugAdministrationTherapyType());
        assertEquals(xstreamStudy.getRadiationTherapyType(), study.getRadiationTherapyType());
        assertEquals(xstreamStudy.getDeviceTherapyType(), study.getDeviceTherapyType());
        assertEquals(xstreamStudy.getSurgeryTherapyType(), study.getSurgeryTherapyType());
        assertEquals(xstreamStudy.getBehavioralTherapyType(), study.getBehavioralTherapyType());

        if (!study.getStudyTherapies().isEmpty()) {


            StudyTherapy actualTherapy = study.getStudyTherapies().get(0);
            assertEquals(StudyTherapyType.DRUG_ADMINISTRATION, actualTherapy.getStudyTherapyType());
            assertEquals(study, actualTherapy.getStudy());

            actualTherapy = study.getStudyTherapies().get(1);

            assertEquals(StudyTherapyType.RADIATION, actualTherapy.getStudyTherapyType());
            assertEquals(study, actualTherapy.getStudy());


        }
        assertEquals(study.getTreatmentAssignments().size(), xstreamStudy.getTreatmentAssignments().size());
        if (!xstreamStudy.getTreatmentAssignments().isEmpty()) {

            assertEquals(1, study.getTreatmentAssignments().size());

            final TreatmentAssignment actualTreatmentAssignment = study.getTreatmentAssignments().get(0);
            final TreatmentAssignment expectedTreatmentAssignment = xstreamStudy.getTreatmentAssignments().get(0);

            assertEquals(actualTreatmentAssignment.getCode(), expectedTreatmentAssignment.getCode());
            assertEquals(actualTreatmentAssignment.getComments(), expectedTreatmentAssignment.getComments());
            assertEquals(actualTreatmentAssignment.getDescription(), expectedTreatmentAssignment.getDescription());
            assertEquals(actualTreatmentAssignment.getDoseLevelOrder(), expectedTreatmentAssignment.getDoseLevelOrder());


            assertEquals(xstreamStudy, expectedTreatmentAssignment.getStudy());

            assertEquals(study, actualTreatmentAssignment.getStudy());

        }
        assertEquals(study.getStudyOrganizations().size(), xstreamStudy.getStudyOrganizations().size());
        if (!xstreamStudy.getStudyOrganizations().isEmpty()) {

            assertEquals(1, study.getStudyOrganizations().size());

            final StudyOrganization actualStudyOrganization = study.getStudyOrganizations().get(0);
            final StudyOrganization expectedStudyOrganization = xstreamStudy.getStudyOrganizations().get(0);

            assertEquals(actualStudyOrganization.getOrganization(), expectedStudyOrganization.getOrganization());

            assertEquals(study, actualStudyOrganization.getStudy());

        }

    }

}
