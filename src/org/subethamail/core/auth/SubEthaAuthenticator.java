/*
 * $Id: SubEthaLoginModule.java 735 2006-08-20 04:21:14Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/SimilarityLoginModule.java,v $
 */

package org.subethamail.core.auth;

import java.security.Principal;
import java.util.logging.Level;

import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.java.Log;

import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Person;

import com.caucho.security.Authenticator;
import com.caucho.security.Credentials;
import com.caucho.security.PasswordCredentials;


/**
 * Resin Authenticator which authenticates against our user database.  Unfortunately
 * the Resin documentation is nearly nonexistant so we must infer much of this
 * behavior by looking at their examples.
 *
 * @author Jeff Schnitzer
 */
@Startup
@Singleton
@Log
public class SubEthaAuthenticator implements Authenticator
{
	/** */
	@Inject @SubEtha SubEthaEntityManager em;
	
	/**
	 * Authenticate the user by the password, returning null on failure.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Principal authenticate(Principal prince, Credentials credentials, Object detail)
	{
	    log.log(Level.FINE,"Authenticating {0}", prince);
			
		String email = prince.getName();

		EmailAddress ea = this.em.findEmailAddress(email);
		if (ea == null)
		{
		    log.log(Level.FINE,"Email address not found: {0}", email);
			return null;
		}

		StringBuilder credPassword = new StringBuilder();
		credPassword.append(((PasswordCredentials)credentials).getPassword());
		
		Person p = ea.getPerson();
		if (!p.checkPassword(credPassword.toString()))
		{
		    log.log(Level.FINE,"Wrong password: {0}", credPassword);
			
			return null;
		}
		else
		{
			SubEthaPrincipal sep = new SubEthaPrincipal(p.getId(), email, p.getRoles());
			return sep;
		}
	}

	/** */
	public boolean isUserInRole(Principal user, String role)
	{
		SubEthaPrincipal p = (SubEthaPrincipal)user;
		
		boolean hasRole = p.getRoles().contains(role);
		
		log.log(Level.FINER,"Checking {0} for role {1} {2}", new Object[]{p.getEmail(),role,(hasRole ? "(yes)" : "(no)")});
		
		return hasRole;
	}

	/** */
	public void logout(Principal user)
	{
		// Nothing special needed
	}	
}