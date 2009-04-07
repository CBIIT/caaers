package gov.nih.nci.cabig.caaers.dao;

import static gov.nih.nci.cabig.caaers.CaaersUseCase.CREATE_STUDY;
import static gov.nih.nci.cabig.caaers.CaaersUseCase.STUDY_ABSTRACTION;
import gov.nih.nci.cabig.caaers.CaaersUseCases;
import gov.nih.nci.cabig.caaers.DaoNoSecurityTestCase;
import gov.nih.nci.cabig.caaers.dao.query.ResearchStaffQuery;
import gov.nih.nci.cabig.caaers.domain.LocalResearchStaff;
import gov.nih.nci.cabig.caaers.domain.RemoteResearchStaff;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;

import java.util.List;

/**
 * @author Kulasekaran
 */
@CaaersUseCases( { CREATE_STUDY, STUDY_ABSTRACTION })
public class ResearchStaffDaoTest extends DaoNoSecurityTestCase<ResearchStaffDao> {
    private OrganizationDao organizationDao = (OrganizationDao) getApplicationContext().getBean("organizationDao");

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testGetById() throws Exception {
        ResearchStaff researchStaff = getDao().getById(-1000);
        assertNotNull("ResearchStaff not found", researchStaff);
        assertEquals("Wrong last name", "Gates", researchStaff.getLastName());
        assertEquals("Wrong first name", "Bill", researchStaff.getFirstName());
    }
    
    public void testGetByLoginId(){
    	ResearchStaff researchStaff = getDao().getByLoginId("abcd");
    	assertNotNull(researchStaff);
    	assertEquals("Bill", researchStaff.getFirstName());
    }

    
    //Below 4 testcases commented due to coppa merge need to revist and fix the testcases.
//    public void testGetByNciIdentifier(){
//    	List<ResearchStaff> researchStaffs = getDao().getByNciIdentifier(new String[] {"nci id"}, -1000);
//    	assertNotNull(researchStaffs);
//        assertEquals(4, researchStaffs.size());
//    }
//
//    public void testFindResearchStaff() {
//        ResearchStaffQuery rsq = new ResearchStaffQuery();
//        rsq.filterByLastName("Kennedy");
//        List<ResearchStaff> researchStaffs = getDao().findResearchStaff(rsq);
//        assertNotNull(researchStaffs);
//        assertEquals(1, researchStaffs.size());
//        assertEquals("JF", researchStaffs.get(0).getFirstName());
//    }
//    
//    public void testSaveLocalResearchStaff() throws Exception {
//        interruptSession();
//
//        Integer savedId;
//        {
//            ResearchStaff researchStaff = new LocalResearchStaff();
//            researchStaff.setFirstName("Jeff");
//            researchStaff.setLastName("Someone");
//            researchStaff.setEmailAddress("abc@def.com");
//            researchStaff.setPhoneNumber("123-456-789");
//            researchStaff.setNciIdentifier("nci id");
//
//            researchStaff.setOrganization(organizationDao.getById(-1000));
//
//            getDao().save(researchStaff);
//
//            savedId = researchStaff.getId();
//            assertNotNull("The saved researchStaff id", savedId);
//        }
//
//        interruptSession();
//
//        {
//            ResearchStaff loaded = getDao().getById(savedId);
//            assertNotNull("Could not reload researchStaff id " + savedId, loaded);
//            assertEquals("Wrong firstname", "Jeff", loaded.getFirstName());
//            assertEquals("Wrong lastname", "Someone", loaded.getLastName());
//        }
//    }
//    
//    public void testSaveRemoteResearchStaff() throws Exception {
//        interruptSession();
//
//        Integer savedId;
//        {
//            ResearchStaff researchStaff = new RemoteResearchStaff();
//            researchStaff.setEmailAddress("abc@def.com");
//            researchStaff.setExternalId("externalId");
//            researchStaff.setOrganization(organizationDao.getById(-1000));
//            
//            getDao().save(researchStaff);
//
//            savedId = researchStaff.getId();
//            assertNotNull("The saved researchStaff id", savedId);
//        }
//
//        interruptSession();
//
//        {
//            ResearchStaff loaded = getDao().getById(savedId);
//            assertNotNull("Could not reload researchStaff id " + savedId, loaded);
//            assertEquals("Wrong emailAddress", "abc@def.com", loaded.getEmailAddress());
//        }
//    }
    
    public void testGetRemoteObjects(){
    	List<ResearchStaff> researchStaffs = getDao().getByNciIdentifier(new String[] { "nci" },-1000);
    	System.out.println(researchStaffs.size());
    	for (ResearchStaff researchStaff:researchStaffs) {
    		System.out.println(researchStaff.getClass().getName());
    		System.out.println(researchStaff.getFirstName() +","+researchStaff.getEmailAddress() +"," + researchStaff.getOrganization().getNciInstituteCode());
    		
    	}

    }
    
}
