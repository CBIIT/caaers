/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao;

import edu.nwu.bioinformatics.commons.testing.CoreTestCase;
import gov.nih.nci.cabig.caaers.DaoNoSecurityTestCase;
import gov.nih.nci.cabig.caaers.domain.Search;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Sameer Sawant
 */
public class SearchDaoTest extends DaoNoSecurityTestCase<SearchDao> {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        {
            Search s = new Search();
            s.setLoginId("SYSTEM_ADMIN");
            s.setId(-1);
            s.setName("Search1");
            s.setDescription("Description1");
            s.setCreatedDate(new Date());
            s.setCriteriaXml("Criteria1");
            getDao().save(s);
        }
        {
            Search s = new Search();
            s.setLoginId("SYSTEM_ADMIN");
            s.setId(-2);
            s.setName("Search2");
            s.setCreatedDate(new Date());
            s.setDescription("Description2");
            s.setCriteriaXml("Criteria2");
            getDao().save(s);
        }
        {
            Search s = new Search();
            s.setLoginId("SYSTEM_ADMIN");
            s.setId(-3);
            s.setName("Search3");
            s.setCreatedDate(new Date());
            s.setDescription("Description3");
            s.setCriteriaXml("Criteria3");
            getDao().save(s);
        }
        {
            Search s = new Search();
            s.setLoginId("SYSTEM_ADMIN");
            s.setId(-4);
            s.setName("Search4");
            s.setCreatedDate(new Date());
            s.setDescription("Description4");
            s.setCriteriaXml("Criteria4");
            getDao().save(s);
        }
        {
            Search s = new Search();
            s.setLoginId("LOGIN_ID");
            s.setId(-5);
            s.setName("Search5");
            s.setDescription("Description5");
            s.setCreatedDate(new Date());
            s.setCriteriaXml("Criteria5");
            getDao().save(s);
        }
        getDao().flush();

    }


    @Override
    public void tearDown() throws Exception {
        List<Search> searches = getDao().findAll("id");
        for(Search s : searches) getDao().deleteByLoginIdAndName(s.getName(), s.getLoginId());
        getDao().flush();
        super.tearDown();
    }

    public void testSave() throws Exception {
		Search search = new Search();
		search.setLoginId("testLogin");
		search.setName("testName");
		search.setDescription("testDescription");
		search.setCriteriaXml("testCriteria");
		search.setCreatedDate(new Date());
		getDao().save(search);
		assertNotNull("No ID for new search", search.getId());
        int saveId = search.getId();
        interruptSession();
        
        Search s = getDao().getById(saveId);
        CoreTestCase.assertNotNull("Search wasnt saved successfully", s);
	}
	
	public void testGetByLogin() throws Exception {
		List<Search> searchList = getDao().getByLogin("SYSTEM_ADMIN");
		CoreTestCase.assertNotNull("testGetbyLogin didnt fetch the search list", searchList);
		CoreTestCase.assertEquals("Incorrect number of searches fetched by getByLogin method", 4, searchList.size());
	}
	
	public void testGetByLoginAndName() throws Exception{
		List<Search> searchList = getDao().getByLoginAndName("SYSTEM_ADMIN", "Search1");
		CoreTestCase.assertNotNull("testGetbyLoginAndName didnt fetch the search", searchList);
		CoreTestCase.assertEquals("Incorrect number of searches fetched by getByLoginAndName method", 1, searchList.size());
	}
	
	public void testDeleteByLoginIdAndName() throws Exception{
		boolean successfull = getDao().deleteByLoginIdAndName("Search1", "SYSTEM_ADMIN");
		List<Search> searchList = getDao().getByLogin("SYSTEM_ADMIN");
		assertTrue("deleteByLoginIdAndName failed", successfull);
		assertEquals("Incorrect number of searches on deletion", 3, searchList.size());
	}
}
