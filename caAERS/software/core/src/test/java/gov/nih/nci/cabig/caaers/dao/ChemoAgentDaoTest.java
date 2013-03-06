/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.DaoTestCase;
import gov.nih.nci.cabig.caaers.domain.ChemoAgent;

import java.util.List;

/**
 * @author Ion C. Olaru
 * @author Biju Joseph
 */

public class ChemoAgentDaoTest extends DaoTestCase<ChemoAgentDao> {

    public void testGetDomainClass() {
        Object obj = getDao().domainClass();
        assertNotNull(obj);
    }

    public void testGetAll() throws Exception {
        List<ChemoAgent> all = getDao().getAll();
        assertNotNull(all);
        assertEquals(2, all.size());
    }

    public void testGetBySubnames() throws Exception {
        List<ChemoAgent> all = getDao().getBySubname(new String[] {"Test"});
        assertNotNull(all);
        assertEquals(1, all.size());
        assertEquals("Test1 (hello)", all.get(0).getFullName());
    }
    
    public void testGetBySubnames_OnGenericName() throws Exception {
        List<ChemoAgent> all = getDao().getBySubname(new String[] {"hell"});
        assertNotNull(all);
        assertEquals(1, all.size());
        assertEquals("Test1 (hello)", all.get(0).getFullName());
    }

    public void testGetById() throws Exception {
        ChemoAgent loaded = getDao().getById(1002);
        assertNotNull(loaded);
        assertEquals("Wrong id", 1002, (int) loaded.getId());
    }

    public void testGetBySubnameMatchesTermSubstring() throws Exception {
        List<ChemoAgent> matches = getDao().getBySubname(new String[] { "test" });
        assertEquals("Wrong number of matches", 1, matches.size());
        assertEquals("Wrong match", 1001, (int) matches.get(0).getId());
    }
}
