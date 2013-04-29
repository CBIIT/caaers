/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service;

import gov.nih.nci.cabig.caaers.AbstractNoSecurityTestCase;
import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.StudySiteDao;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.OrganizationAssignedIdentifier;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.SystemAssignedIdentifier;
import gov.nih.nci.cabig.caaers.domain.repository.OrganizationRepository;
import gov.nih.nci.cabig.caaers.domain.repository.ParticipantRepository;
import gov.nih.nci.cabig.caaers.domain.repository.StudyRepository;
import gov.nih.nci.cabig.caaers.domain.workflow.WorkflowConfig;
import gov.nih.nci.cabig.caaers.service.migrator.IdentifierMigrator;
import gov.nih.nci.cabig.caaers.service.migrator.Migrator;
import gov.nih.nci.cabig.caaers.service.migrator.ParticipantMigrator;
import gov.nih.nci.cabig.caaers.service.migrator.StudyParticipantAssignmentMigrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.easymock.classextension.EasyMock;

/**
 * @author Biju Joseph
 */
public class ParticipantImportServiceIntegrationTest extends AbstractNoSecurityTestCase {

    private ParticipantImportServiceImpl participantImportService;
    private StudySiteDao studySiteDao;

    private ParticipantRepository participantRepository;
    private Participant xstreamParticipant;

    private SystemAssignedIdentifier systemAssignedIdentifier;
    private OrganizationAssignedIdentifier organizationAssignedIdentifier;

    private Organization organization;
    private OrganizationDao organizationDao;
    private OrganizationRepository organizationRepository;
    private StudyDao studyDao;
    private StudyRepository studyRepository;
    
    private StudyParticipantAssignment studyParticipantAssignment;
    private Study study;

    protected void setUp() throws Exception {
        super.setUp();
        studyRepository = registerMockFor(StudyRepository.class);
        participantRepository = registerMockFor(ParticipantRepository.class);
        studySiteDao = registerMockFor(StudySiteDao.class);
        organizationDao = registerMockFor(OrganizationDao.class);
        studyDao = registerMockFor(StudyDao.class);
        organizationRepository = registerMockFor(OrganizationRepository.class);
        participantImportService = new ParticipantImportServiceImpl();
        IdentifierMigrator<Participant> idMigrator = new IdentifierMigrator<Participant>();
        StudyParticipantAssignmentMigrator spaMigrator = new StudyParticipantAssignmentMigrator();
        List<Migrator<Participant>> migrators = new ArrayList<Migrator<Participant>>();
        migrators.add(idMigrator);
        migrators.add(spaMigrator);
        ParticipantMigrator migrator = new ParticipantMigrator();
        migrator.setChildren(migrators);

        spaMigrator.setStudyRepository(studyRepository);
        spaMigrator.setStudySiteDao(studySiteDao);
        spaMigrator.setOrganizationDao(organizationDao);
        spaMigrator.setStudyDao(studyDao);
        spaMigrator.setOrganizationRepository(organizationRepository);
        participantImportService.setParticipantRepository(participantRepository);
        idMigrator.setOrganizationDao(organizationDao);
        participantImportService.setParticipantMigrator(migrator);

        xstreamParticipant = Fixtures.createParticipant("first", "last");
        xstreamParticipant.getIdentifiersLazy().remove(0);
        systemAssignedIdentifier = Fixtures.createSystemAssignedIdentifier("value");
        organization = Fixtures.createOrganization("org name",null);
        organizationAssignedIdentifier = Fixtures.createOrganizationAssignedIdentifier("org value", organization);
        study = Fixtures.createStudy("short study");
        study.setId(1);


    }

    public void testImportParticipantForBasicProperties() {

        DomainObjectImportOutcome<Participant> participantDomainObjectImportOutcome = participantImportService.importParticipant(xstreamParticipant);

        validate(xstreamParticipant, participantDomainObjectImportOutcome);
        validateImportedObject(participantDomainObjectImportOutcome);


        List<DomainObjectImportOutcome.Message> messages = participantDomainObjectImportOutcome.getMessages();
        assertEquals(2, messages.size());

        assertEquals("Identifiers are either Empty or Not Valid", messages.get(0).getMessage());
        assertEquals("Assignments are either Empty or Not Valid", messages.get(1).getMessage());

    }


    public void testImportParticipantForMigratingIdentifiers() {
        xstreamParticipant.addIdentifier(organizationAssignedIdentifier);
        xstreamParticipant.addIdentifier(systemAssignedIdentifier);

      //  List<Organization> organizations = new ArrayList<Organization>();
        //organizations.add(organization);

        EasyMock.expect(organizationDao.getByName(organization.getName())).andReturn(organization);
        //EasyMock.expect(participantRepository.checkIfParticipantExistsForGivenIdentifiers(xstreamParticipant.getIdentifiers())).andReturn(false);
        replayMocks();

        DomainObjectImportOutcome<Participant> participantDomainObjectImportOutcome = participantImportService.importParticipant(xstreamParticipant);
        verifyMocks();
        validate(xstreamParticipant, participantDomainObjectImportOutcome);
        validateImportedObject(participantDomainObjectImportOutcome);
        List<DomainObjectImportOutcome.Message> messages = participantDomainObjectImportOutcome.getMessages();
        assertEquals(1, messages.size());

        assertEquals("Assignments are either Empty or Not Valid", messages.get(0).getMessage());


    }

    public void testImportParticipantForMigratingAssignmentsIfStudyHasNoIdentifiers() {

        //first migrate assignments when assignments has not identifiers

        studyParticipantAssignment = Fixtures.assignParticipant(xstreamParticipant, study, organization);
        DomainObjectImportOutcome<Participant> participantDomainObjectImportOutcome = participantImportService.importParticipant(xstreamParticipant);
        validate(xstreamParticipant, participantDomainObjectImportOutcome);
        validateImportedObject(participantDomainObjectImportOutcome);
        List<DomainObjectImportOutcome.Message> messages = participantDomainObjectImportOutcome.getMessages();
        assertEquals(1, messages.size());

        assertEquals("Identifiers are either Empty or Not Valid", messages.get(0).getMessage());

    }

    
    public void testImportParticipantForNoErrors() {
        xstreamParticipant.addIdentifier(organizationAssignedIdentifier);
        xstreamParticipant.addIdentifier(systemAssignedIdentifier);

        // migrate assignments when study has  identifiers
        study.addIdentifier(organizationAssignedIdentifier);
        studyParticipantAssignment = Fixtures.assignParticipant(xstreamParticipant, study, organization);

        StudySite studySite = new StudySite();
        studySite.setId(123);
        EasyMock.expect(organizationDao.getByName(organization.getName())).andReturn(organization).anyTimes();
        EasyMock.expect(studySiteDao.matchByStudyAndOrg(organization.getName(), organizationAssignedIdentifier.getValue(),
                organizationAssignedIdentifier.getType())).andReturn(studySite);
        replayMocks();

        DomainObjectImportOutcome<Participant> participantDomainObjectImportOutcome = participantImportService.importParticipant(xstreamParticipant);
        verifyMocks();
        validate(xstreamParticipant, participantDomainObjectImportOutcome);
        assertFalse(participantDomainObjectImportOutcome.hasErrors());
        assertTrue(participantDomainObjectImportOutcome.isSavable());
        assertTrue(participantDomainObjectImportOutcome.getMessages().isEmpty());

    }

    public void testImportParticipantForMigratingAssignmentsIfStudySiteIsNotNull() {

        // migrate assignments when study has  identifiers
        study.addIdentifier(organizationAssignedIdentifier);
        studyParticipantAssignment = Fixtures.assignParticipant(xstreamParticipant, study, organization);

        StudySite studySite = new StudySite();
        studySite.setId(123);
        EasyMock.expect(studySiteDao.matchByStudyAndOrg(organization.getName(), organizationAssignedIdentifier.getValue(),
                organizationAssignedIdentifier.getType())).andReturn(studySite);
        EasyMock.expect(organizationDao.getByName(organization.getName())).andReturn(organization);
        replayMocks();

        DomainObjectImportOutcome<Participant> participantDomainObjectImportOutcome = participantImportService.importParticipant(xstreamParticipant);
        verifyMocks();
        validate(xstreamParticipant, participantDomainObjectImportOutcome);
        validateImportedObject(participantDomainObjectImportOutcome);
        List<DomainObjectImportOutcome.Message> messages = participantDomainObjectImportOutcome.getMessages();
        assertEquals(1, messages.size());

        assertEquals("Identifiers are either Empty or Not Valid", messages.get(0).getMessage());

    }

    public void testImportParticipantForMigratingAssignmentsIfStudySiteIsNull() {

        // migrate assignments when study has  identifiers
        study.addIdentifier(organizationAssignedIdentifier);
        studyParticipantAssignment = Fixtures.assignParticipant(xstreamParticipant, study, organization);
        StudySite studySite = new StudySite();
        EasyMock.expect(studySiteDao.matchByStudyAndOrg(organization.getName(), organizationAssignedIdentifier.getValue(),
                organizationAssignedIdentifier.getType())).andReturn(null);
        EasyMock.expect(organizationDao.getByName(organization.getName())).andReturn(organization);
        EasyMock.expect(studyDao.getByIdentifier(organizationAssignedIdentifier)).andReturn(study);
        studySiteDao.save((StudySite)EasyMock.anyObject());
        List<StudySite> sites = new ArrayList<StudySite>();
        sites.add((StudySite)EasyMock.anyObject());
        sites.add(studySite);
        studyRepository.associateSiteToWorkflowConfig(sites);
        
        
        
        
        replayMocks();
        DomainObjectImportOutcome<Participant> participantDomainObjectImportOutcome = participantImportService.importParticipant(xstreamParticipant);
        verifyMocks();
        validate(xstreamParticipant, participantDomainObjectImportOutcome);
        validateImportedObject(participantDomainObjectImportOutcome);
        List<DomainObjectImportOutcome.Message> messages = participantDomainObjectImportOutcome.getMessages();
        assertEquals(1, messages.size());

        assertEquals("Identifiers are either Empty or Not Valid", messages.get(0).getMessage());

    }

    private void validateImportedObject(final DomainObjectImportOutcome<Participant> participantDomainObjectImportOutcome) {
        assertTrue(participantDomainObjectImportOutcome.hasErrors());
        assertFalse(participantDomainObjectImportOutcome.isSavable());
    }

    private void validate(final Participant xstreamParticipant, final DomainObjectImportOutcome<Participant> participantDomainObjectImportOutcome) {
        assertNotNull(participantDomainObjectImportOutcome);
        assertNotNull(xstreamParticipant);
        final Participant participant = participantDomainObjectImportOutcome.getImportedDomainObject();

        assertEquals(xstreamParticipant.getFirstName(), participant.getFirstName());
        assertEquals(xstreamParticipant.getLastName(), participant.getLastName());
        assertEquals(xstreamParticipant.getDateOfBirth(), participant.getDateOfBirth());
        assertEquals(xstreamParticipant.getId(), participant.getId());

        assertEquals(xstreamParticipant.getGender(), participant.getGender());

        assertEquals(xstreamParticipant.getMaidenName(), participant.getMiddleName());
        assertEquals(xstreamParticipant.getMaidenName(), participant.getMaidenName());
        assertEquals(xstreamParticipant.getRace(), participant.getRace());
        assertEquals(xstreamParticipant.getEthnicity(), participant.getEthnicity());

        assertEquals(xstreamParticipant.getIdentifiers().size(), participant.getIdentifiers().size());
        for (Identifier identifier : xstreamParticipant.getIdentifiers()) {
            assertTrue(participant.getIdentifiers().contains(identifier));
        }


    }
}
