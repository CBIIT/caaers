package gov.nih.nci.cabig.caaers.domain;

import java.util.Date;

import junit.framework.TestCase;
/**
 * 
 * @author Biju Joseph
 *
 */
public class SiteResearchStaffTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testIsActive() {
		assertTrue(new SiteResearchStaff().isActive());
	}
	
	public void testGetActiveSiteResearchStaffRoles() {
		assertNotNull(new SiteResearchStaff().getActiveSiteResearchStaffRoles());
	}

	public void testIsActive_ReallyActive(){
		SiteResearchStaffRole role1 = new SiteResearchStaffRole();
		role1.setStartDate(new Date());
		SiteResearchStaff srs = new SiteResearchStaff();
		srs.addSiteResearchStaffRole(role1);
		assertTrue(srs.isActive());
	}
	
	public void testGetActiveDate(){
		assertNotNull(new SiteResearchStaff().getActiveDate());
	}
	
}
