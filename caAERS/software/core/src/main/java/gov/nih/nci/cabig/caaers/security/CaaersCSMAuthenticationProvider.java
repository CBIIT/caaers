/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.security;

import gov.nih.nci.cabig.caaers.domain.User;
import gov.nih.nci.cabig.caaers.domain.repository.UserRepository;
import gov.nih.nci.cabig.caaers.domain.security.passwordpolicy.PasswordPolicy;
import gov.nih.nci.cabig.caaers.service.security.passwordpolicy.PasswordPolicyService;
import gov.nih.nci.cabig.caaers.service.security.passwordpolicy.validators.LoginPolicyValidator;
import gov.nih.nci.cabig.caaers.service.security.user.Credential;
import gov.nih.nci.cabig.ctms.acegi.csm.authentication.CSMAuthenticationProvider;

import java.util.Date;

import org.acegisecurity.AccountExpiredException;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.CredentialsExpiredException;
import org.acegisecurity.DisabledException;
import org.acegisecurity.LockedException;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Ram Seethiraju
 */
public class CaaersCSMAuthenticationProvider extends CSMAuthenticationProvider {
	
	private static final Log logger = LogFactory.getLog(CaaersCSMAuthenticationProvider.class);	
	private PasswordPolicyService passwordPolicyService;
	private UserRepository userRepository;
	
	public PasswordPolicyService getPasswordPolicyService() {
		return passwordPolicyService;
	}

	public void setPasswordPolicyService(PasswordPolicyService passwordPolicyService) {
		this.passwordPolicyService = passwordPolicyService;
	}

	/**
	 * This method will do Login policy validations and authentication checks.
	 */
	@Override
    @Transactional
	protected void additionalAuthenticationChecks(UserDetails user, UsernamePasswordAuthenticationToken token) 
	throws AuthenticationException {

		User caaersUser = null;
		Credential credential =  new Credential(user.getUsername(), user.getPassword());
		PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy();
		LoginPolicyValidator loginPolicyValidator = new LoginPolicyValidator();		
		
		logger.debug((new StringBuilder()).append("Authenticating ").append(user.getUsername()).append("...").toString());
		if(!user.isAccountNonExpired()){
			throw new AccountExpiredException((new StringBuilder()).append("Error authenticating: User is InActive").toString());
		}		
		
		caaersUser = userRepository.getUserByLoginName(user.getUsername());
		
		try {
			// If the user is a caAERS user, then apply Login Policy Validations
			if(caaersUser!=null) {	
				// check if the account is locked
					if(caaersUser.isLocked()){
						throw new LockedException("Account is locked.");
					}
					
					if(caaersUser.getSecondsPastLastFailedLoginAttempt() > passwordPolicy.getLoginPolicy().getLockOutDuration()) {
						if(passwordPolicy.getLoginPolicy().getAllowedLoginTime() <= caaersUser.getSecondsPastLastFailedLoginAttempt()) {
							caaersUser.setFailedLoginAttempts(0);
							caaersUser.setLastFailedLoginAttemptTime(null);
						}
					}
				credential.setUser(caaersUser);
				loginPolicyValidator.validate(passwordPolicy, credential, null);
				if(caaersUser.getFailedLoginAttempts()==-1)	caaersUser.setFailedLoginAttempts(0);
				if(passwordPolicy.getLoginPolicy().getAllowedLoginTime() <= caaersUser.getSecondsPastLastFailedLoginAttempt())	caaersUser.setFailedLoginAttempts(0);

			}
			
			super.additionalAuthenticationChecks(user, token);
			
			if(caaersUser!=null){
				// If the caAERS user passes the checks and validations, then do the following
				caaersUser.setFailedLoginAttempts(0);
				caaersUser.setLastFailedLoginAttemptTime(null);
			}
			
		} catch (DisabledException attemptsEx) {
			// This exception is thrown when too many failed login attempts occur.
			caaersUser.setLastFailedLoginAttemptTime(new Date());
			caaersUser.setFailedLoginAttempts(-1);
			throw attemptsEx;
		} catch (LockedException lockEx) {
			// This exception is thrown when user tries to login while the account is locked.
			throw lockEx;
		}catch (CredentialsExpiredException oldEx) {
			// This exception is thrown when the password is too old.
			caaersUser.setFailedLoginAttempts(0);
			caaersUser.setLastFailedLoginAttemptTime(null);
			throw oldEx;
		} catch (AuthenticationException authEx) {
			// This exception is thrown when invalid credentials are used to login.
			if(caaersUser!=null) {
				caaersUser.setFailedLoginAttempts(caaersUser.getFailedLoginAttempts()+1);
				if(caaersUser.getFailedLoginAttempts()==1)	caaersUser.setLastFailedLoginAttemptTime(new Date());
			}
			throw new BadCredentialsException("Invalid login credentials");
		} finally {
			// save the caAERS user properties.
			if(caaersUser!=null) {
				userRepository.save(caaersUser);
			}
		}
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
}
