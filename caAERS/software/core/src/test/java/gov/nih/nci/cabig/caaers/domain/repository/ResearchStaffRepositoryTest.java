package gov.nih.nci.cabig.caaers.domain.repository;

import static org.easymock.EasyMock.expect;
import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.dao.ResearchStaffDao;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.domain.SiteResearchStaff;
import gov.nih.nci.cabig.caaers.domain.SiteResearchStaffRole;
import gov.nih.nci.cabig.caaers.domain.UserGroupType;
import gov.nih.nci.cabig.caaers.security.CaaersSecurityFacadeImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResearchStaffRepositoryTest extends AbstractTestCase {
	
	ResearchStaffRepository repository;
	CaaersSecurityFacadeImpl caaersSecurityFacadeImpl;
	ResearchStaffDao researchStaffDao;
	StudyRepository studyRepository;
	
	protected void setUp() throws Exception {
		super.setUp();
		repository = new ResearchStaffRepository();
		caaersSecurityFacadeImpl = registerMockFor(CaaersSecurityFacadeImpl.class);
		studyRepository = registerMockFor(StudyRepository.class);
		repository.setCaaersSecurityFacade(caaersSecurityFacadeImpl);
		researchStaffDao = registerDaoMockFor(ResearchStaffDao.class);
		repository.setResearchStaffDao(researchStaffDao);
		repository.setAuthenticationMode("local");
		repository.setStudyRepository(studyRepository);
	}

	public void testSave() throws Exception {
		Organization org = Fixtures.createOrganization("NCI");
		List<UserGroupType> groupList = new ArrayList<UserGroupType>();
		groupList.add(UserGroupType.ae_reporter);
		ResearchStaff staff = Fixtures.createResearchStaff(org, groupList, "Joel");
		SiteResearchStaff siteResearchStaff = new SiteResearchStaff();
		siteResearchStaff.setEmailAddress("Joel@def.com");
		siteResearchStaff.setOrganization(org);
		SiteResearchStaffRole siteResearchStaffRole = new SiteResearchStaffRole();
		siteResearchStaffRole.setRoleCode("ae_reporter");
		siteResearchStaffRole.setStartDate(new Date());
		siteResearchStaff.addSiteResearchStaffRole(siteResearchStaffRole);
		siteResearchStaff.setAssociateAllStudies(Boolean.TRUE);
		staff.addSiteResearchStaff(siteResearchStaff);
		staff.setLoginId("Joel@def.com");
		String changeUrl = "/pages/url";
		expect(researchStaffDao.merge(staff)).andReturn(staff).anyTimes();
		caaersSecurityFacadeImpl.createOrUpdateCSMUser(staff, changeUrl);
		studyRepository.associateStudyPersonnel(staff);
		replayMocks();
		repository.save(staff, changeUrl);
		verifyMocks();
		assertEquals("Joel@def.com", staff.getLoginId());
		
	}
	
	public void testSaveWebSso() throws Exception {
		repository.setAuthenticationMode("webSSO");
		Organization org = Fixtures.createOrganization("NCI");
		List<UserGroupType> groupList = new ArrayList<UserGroupType>();
		groupList.add(UserGroupType.ae_reporter);
		ResearchStaff staff = Fixtures.createResearchStaff(org, groupList, "Joel");
		SiteResearchStaff siteResearchStaff = new SiteResearchStaff();
		siteResearchStaff.setEmailAddress("Joel@def.com");
		siteResearchStaff.setOrganization(org);
		SiteResearchStaffRole siteResearchStaffRole = new SiteResearchStaffRole();
		siteResearchStaffRole.setRoleCode("ae_reporter");
		siteResearchStaffRole.setStartDate(new Date());
		siteResearchStaff.addSiteResearchStaffRole(siteResearchStaffRole);
		staff.addSiteResearchStaff(siteResearchStaff);
		staff.setLoginId("Joel2@def.com");
		String changeUrl = "/pages/url";
		expect(researchStaffDao.merge(staff)).andReturn(staff).anyTimes();
		caaersSecurityFacadeImpl.createOrUpdateCSMUser(staff, changeUrl);
		studyRepository.associateStudyPersonnel(staff);
		replayMocks();
		repository.save(staff, changeUrl);
		verifyMocks();
		assertEquals("Joel2@def.com", staff.getLoginId());
	}
	
	public void testSave_NoLoginId() throws Exception{
		repository.setAuthenticationMode("webSSO");
		Organization org = Fixtures.createOrganization("NCI");
		List<UserGroupType> groupList = new ArrayList<UserGroupType>();
		groupList.add(UserGroupType.ae_reporter);
		ResearchStaff staff = Fixtures.createResearchStaff(org, groupList, "Joel");
		SiteResearchStaff siteResearchStaff = new SiteResearchStaff();
		siteResearchStaff.setEmailAddress("Joel@def.com");
		siteResearchStaff.setOrganization(org);
		SiteResearchStaffRole siteResearchStaffRole = new SiteResearchStaffRole();
		siteResearchStaffRole.setRoleCode("ae_reporter");
		siteResearchStaffRole.setStartDate(new Date());
		siteResearchStaff.addSiteResearchStaffRole(siteResearchStaffRole);
		staff.addSiteResearchStaff(siteResearchStaff);
		String changeUrl = "/pages/url";
		expect(researchStaffDao.merge(staff)).andReturn(staff).anyTimes();
		studyRepository.associateStudyPersonnel(staff);
		replayMocks();
		repository.save(staff, changeUrl);
		verifyMocks();
		assertNull(staff.getLoginId());
	}
	
	public void testUnlockUser() {
		Organization org = Fixtures.createOrganization("NCI");
		List<UserGroupType> groupList = new ArrayList<UserGroupType>();
		groupList.add(UserGroupType.ae_reporter);
		ResearchStaff staff = Fixtures.createResearchStaff(org, groupList, "Joel");
		SiteResearchStaff siteResearchStaff = new SiteResearchStaff();
		siteResearchStaff.setEmailAddress("Joel@def.com");
		siteResearchStaff.setOrganization(org);
		SiteResearchStaffRole siteResearchStaffRole = new SiteResearchStaffRole();
		siteResearchStaffRole.setRoleCode("ae_reporter");
		siteResearchStaffRole.setStartDate(new Date());
		siteResearchStaff.addSiteResearchStaffRole(siteResearchStaffRole);
		siteResearchStaff.setAssociateAllStudies(Boolean.TRUE);
		staff.addSiteResearchStaff(siteResearchStaff);
		staff.setLoginId("Joel@def.com");
		staff.setFailedLoginAttempts(-1);
		Timestamp now = new Timestamp(new Date().getTime());
		staff.setLastFailedLoginAttemptTime(now);
		
		researchStaffDao.save(staff);
		replayMocks();
		repository.unlockResearchStaff(staff);
		assertTrue(staff.getFailedLoginAttempts()==0);
		assertTrue(staff.getLastFailedLoginAttemptTime()==null);
		verifyMocks();
	}

}
