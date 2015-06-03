/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.accesscontrol.filter.query;

import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.dao.index.AbstractIndexDao;
import gov.nih.nci.cabig.caaers.dao.query.SiteResearchStaffQuery;
import gov.nih.nci.cabig.caaers.dao.query.StudySitesQuery;
import gov.nih.nci.cabig.caaers.domain.UserGroupType;
import gov.nih.nci.cabig.caaers.security.SecurityTestUtils;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * QuerySecurityFilterer Tester.
 *
 * @author Biju Joseph
 * @since <pre>07/13/2010</pre>
 * 
 */
public class QuerySecurityFiltererTest extends AbstractTestCase {

    SiteResearchStaffQuery q;
    QuerySecurityFilterer filterer;
    AbstractIndexDao indexDao;

    public void setUp() throws Exception {
        super.setUp();
        indexDao = registerMockFor(AbstractIndexDao.class);
        q = new SiteResearchStaffQuery();
        filterer = new QuerySecurityFilterer();
        filterer.setIndexDao(indexDao);
        SecurityTestUtils.switchToSuperuser();
    }

    public void testApplyFilter(){
        filterer.setEntityName("SiteResearchStaff");
        filterer.setIndexAlias("rsIdx");
        filterer.setEntityAssociation("researchStaff");
        assertEquals("SELECT distinct srs from SiteResearchStaff srs left join fetch srs.researchStaff rs left join fetch srs.organization org  order by srs.id",
                q.getQueryString());
        filterer.applyFilter(q);
        assertEquals("SELECT distinct srs from  rsIdx left join fetch srs.researchStaff rs left join fetch srs.organization org join rsIdx.researchStaff srs  WHERE rsIdx.loginId = :loginId AND bitand(rsIdx.role, 0) > 0  order by srs.id", q.getQueryString());
    }
    


    public void testApplyFilterForStudySiteQuery(){
        StudySitesQuery q = new StudySitesQuery();
        q.filterByOrganizationId(5);
        filterer.setEntityName("StudySite");
        filterer.setIndexAlias("stuIdx");
        filterer.setEntityAssociation("study.studySite");
        filterer.setIndexName("StudyIndex");
        assertEquals("SELECT distinct ss FROM StudySite ss join ss.study AS study left join study.identifiers as identifier join ss.organization AS o  WHERE ss.retiredIndicator = :retiredIndicator AND o.id = :orgId", q.getQueryString());
        
        filterer.applyFilter(q);
        assertEquals("SELECT distinct ss FROM StudyIndex stuIdx join ss.study AS study left join study.identifiers as identifier join ss.organization AS o join stuIdx.study.studySite ss  WHERE ss.retiredIndicator = :retiredIndicator AND o.id = :orgId AND stuIdx.loginId = :loginId AND bitand(stuIdx.role, 0) > 0", q.getQueryString());
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    

}
