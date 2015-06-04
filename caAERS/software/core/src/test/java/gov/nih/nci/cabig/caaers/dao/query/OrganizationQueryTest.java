/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao.query;

import junit.framework.TestCase;

public class OrganizationQueryTest extends TestCase {

    public void testQueryConstructor() throws Exception {
        OrganizationQuery organizationQuery = new OrganizationQuery();
        assertEquals("wrong parsing for constructor",
                        "SELECT distinct o from Organization o  order by o.name", organizationQuery
                                        .getQueryString());

    }

    public void testFilterByTitle() throws Exception {
        OrganizationQuery organizationQuery = new OrganizationQuery();
        organizationQuery.filterByOrganizationName("a");
        assertEquals(
                        "SELECT distinct o from Organization o  WHERE lower(o.name) LIKE :name  order by o.name",
                        organizationQuery.getQueryString());
        assertEquals("wrong number of parameters", organizationQuery.getParameterMap().size(), 1);
        assertTrue("missing paramenter name", organizationQuery.getParameterMap().containsKey(
                        "name"));
        assertEquals("wrong parameter value", organizationQuery.getParameterMap().get("name"),
                        "%a%");

        organizationQuery.filterByNciInstituteCode("b");
        assertEquals("wrong number of parameters", organizationQuery.getParameterMap().size(), 2);
        assertTrue("missing paramenter name", organizationQuery.getParameterMap().containsKey(
                        "nciInstituteCode"));
        assertEquals("wrong parameter value", organizationQuery.getParameterMap().get(
                        "nciInstituteCode"), "%b%");

    }

    public void testFilterByNCICode() throws Exception {
        OrganizationQuery organizationQuery = new OrganizationQuery();
        organizationQuery.filterByNciInstituteCode("a");
        assertEquals(
                        "SELECT distinct o from Organization o  WHERE lower(o.nciInstituteCode) LIKE :nciInstituteCode  order by o.name",
                        organizationQuery.getQueryString());
        assertEquals("wrong number of parameters", organizationQuery.getParameterMap().size(), 1);
        assertTrue("missing paramenter name", organizationQuery.getParameterMap().containsKey(
                        "nciInstituteCode"));
        assertEquals("wrong parameter value", organizationQuery.getParameterMap().get(
                        "nciInstituteCode"), "%a%");

    }
    
    public void testFilterByNCICodeOrName() throws Exception {
        OrganizationQuery organizationQuery = new OrganizationQuery();
        organizationQuery.filterByOrganizationNameOrNciCode("a");
        System.out.println(organizationQuery.getQueryString());

    }

    public void testFilterByOrganizationTypesOrNull() {
        OrganizationQuery q = new OrganizationQuery();
        q.filterByOrganizationTypes(new String[]{"CCR", "CLC", "NCP", "ITN"});
        assertEquals("SELECT distinct o from Organization o  WHERE (lower(o.type) = 'ccr' or lower(o.type) = 'clc' or lower(o.type) = 'ncp' or lower(o.type) = 'itn')  order by o.name", q.getQueryString());
    }

}
