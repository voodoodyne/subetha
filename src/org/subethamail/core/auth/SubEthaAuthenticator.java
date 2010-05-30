/*
 * $Id: SubEthaLoginModule.java 735 2006-08-20 04:21:14Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/SimilarityLoginModule.java,v $
 */

package org.subethamail.core.auth;

import java.security.Principal;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Singleton
public class SubEthaAuthenticator implements Authenticator
{
	/** */
	private static Logger log = LoggerFactory.getLogger(SubEthaAuthenticator.class);

	/** */
	@Inject @SubEtha SubEthaEntityManager em;
	
	/**
	 * Authenticate the user by the password, returning null on failure.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Principal authenticate(Principal prince, Credentials credentials, Object detail)
	{
		if (log.isDebugEnabled())
			log.debug("Authenticating " + prince);
			
		String email = prince.getName();

		EmailAddress ea = this.em.findEmailAddress(email);
		if (ea == null)
		{
			log.debug("Email address not found: " + email);
			return null;
		}

		StringBuilder credPassword = new StringBuilder();
		credPassword.append(((PasswordCredentials)credentials).getPassword());
		
		Person p = ea.getPerson();
		if (!p.checkPassword(credPassword.toString()))
		{
			if (log.isDebugEnabled())
				log.debug("Wrong password: " + credPassword);
			
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
		
		if (log.isTraceEnabled())
			log.trace("Checking " + p.getEmail() + " for role " + role + (hasRole ? " (yes)" : " (no)"));
		
		return hasRole;
	}

	/** */
	public void logout(Principal user)
	{
		// Nothing special needed
	}	
}