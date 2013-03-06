/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.DaoTestCase;
import gov.nih.nci.cabig.caaers.domain.DiseaseCategory;

import java.util.List;

/**
 * @author Ion C. Olaru
 */

public class DiseaseCategoryDaoTest extends DaoTestCase<DiseaseCategoryDao> {

    public void testDomainClass() throws Exception {
        Class<DiseaseCategory> dcc = getDao().domainClass();
        assertNotNull(dcc);
    }

    public void testGetById() throws Exception {
        DiseaseCategory dc = getDao().getById(-1);
        assertNotNull(dc);
        assertEquals("Category One", dc.getName());
    }

    public void testGetAll() throws Exception {
        List all = getDao().getAll();
        assertNotNull(all);
        assertEquals(4, all.size());
    }

    public void testGetBySubnameNoParent() throws Exception {
        List all = getDao().getBySubname(new String[] {"Category One"}, null);
        assertNotNull(all);
        assertEquals(0, all.size());
    }

    public void testGetBySubname() throws Exception {
        List all = getDao().getBySubname(new String[] {"Category Three"}, new Integer(-10));
        assertNotNull(all);
        assertEquals(1, all.size());
    }

    public void testGetByParentId() throws Exception {
        List all = getDao().getByParentId(-20);
        assertNotNull(all);
        assertEquals(0, all.size());
    }

}
