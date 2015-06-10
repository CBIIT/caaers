/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.CaaersContextLoader;
import gov.nih.nci.cabig.caaers.dao.security.passwordpolicy.PasswordPolicyDao;
import gov.nih.nci.cabig.caaers.domain.security.passwordpolicy.LoginPolicy;
import gov.nih.nci.cabig.caaers.domain.security.passwordpolicy.PasswordPolicy;
import gov.nih.nci.cabig.caaers.tools.Messages;
import gov.nih.nci.cabig.caaers.web.WebTestCase;
import gov.nih.nci.cabig.ctms.suite.authorization.ProvisioningSessionFactory;
import gov.nih.nci.cabig.ctms.suite.authorization.SuiteRole;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Monish
 *
 */
public class UserCommandTest extends WebTestCase {

	private UserCommand command;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        switchToSuperUser();
        command = new UserCommand();



    }

    public void testGetAllGlobalRoles(){
		
		List<SuiteRole> globalRoles = command.getAllGlobalRoles();

		assertEquals(4,globalRoles.size());
		for(SuiteRole role : globalRoles){
			assertFalse(role.isScoped());
		}
	}
	
	
	public void testGetAllSiteScopedRoles(){
		
		List<SuiteRole> siteScopedRoles = command.getAllSiteScopedRoles();

		assertEquals(19,siteScopedRoles.size());
		for(SuiteRole role : siteScopedRoles){
			assertTrue(role.isSiteScoped());
		}
	}

	
	public void testGetAllSiteAndStudyScopedRoles(){
		
		List<SuiteRole> siteAndStudyScopedRoles = command.getAllStudyScopedRoles();

		assertEquals(10,siteAndStudyScopedRoles.size());
		for(SuiteRole role : siteAndStudyScopedRoles){
			assertTrue(role.isStudyScoped());
		}
	}
	
	
	public void testAddOneSuiteRoleMembership(){
		ProvisioningSessionFactory factory = new ProvisioningSessionFactory();
		command.addRoleMembership(factory.createSuiteRoleMembership(SuiteRole.AE_REPORTER));
		assertNotNull(command.getRoleMemberships());
		assertEquals(1, command.getRoleMemberships().size());
		assertTrue(command.getRoleMemberships().get(0).getRole().equals(SuiteRole.AE_REPORTER));
	}
	
	
	public void testAddManySuiteRoleMembership(){
		ProvisioningSessionFactory factory = new ProvisioningSessionFactory();
		command.addRoleMembership(factory.createSuiteRoleMembership(SuiteRole.AE_REPORTER));
		command.addRoleMembership(factory.createSuiteRoleMembership(SuiteRole.BUSINESS_ADMINISTRATOR));
		command.addRoleMembership(factory.createSuiteRoleMembership(SuiteRole.REGISTRAR));
		
		assertNotNull(command.getRoleMemberships());
		assertEquals(3, command.getRoleMemberships().size());
	}

    public void testPasswordExpiryDate() {
        Timestamp today = new Timestamp(new Date().getTime());
        PasswordPolicy passwordPolicy = new PasswordPolicy();
        LoginPolicy loginPolicy = new LoginPolicy();
        loginPolicy.setMaxPasswordAge(7776000);
        passwordPolicy.setLoginPolicy(loginPolicy);
        command.setPasswordLastSet(today);
        command.setPasswordExpiryDate(passwordPolicy);
        Timestamp expiryDate = command.getPasswordExpiryDate();
        //Actual expirty date
        int days = (int) TimeUnit.SECONDS.toDays(passwordPolicy.getLoginPolicy().getMaxPasswordAge());
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_WEEK, days);
        Timestamp actualExpiryDate = new Timestamp(cal.getTime().getTime());
        assertEquals(expiryDate.toString(), actualExpiryDate.toString());

    }

}
