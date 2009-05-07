/*
 * $Id: SubEthaLoginModule.java 735 2006-08-20 04:21:14Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/SimilarityLoginModule.java,v $
 */

package org.subethamail.core.util;

import java.security.Principal;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Person;

import com.caucho.config.Name;
import com.caucho.security.Authenticator;
import com.caucho.security.Credentials;
import com.caucho.security.PasswordCredentials;
import com.caucho.server.security.CachingPrincipal;


/**
 * Resin Authenticator which authenticates against our user database.  Unfortunately
 * the Resin documentation is nearly nonexistant so we must infer much of this
 * behavior by looking at their examples.
 *
 * @author Jeff Schnitzer
 */
public class SubEthaAuthenticator implements Authenticator
{
	/** */
	@Name("subetha") SubEthaEntityManager em;
	
	/**
	 * Authenticate the user by the password, returning null on failure.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Principal authenticate(Principal prince, Credentials credentials, Object detail)
	{

		String email = prince instanceof SubethaPrincipal ? ((SubethaPrincipal)prince).getEmail() : prince.getName();

		EmailAddress ea = this.em.findEmailAddress(email);

		StringBuilder credPassword = new StringBuilder();
		credPassword.append(((PasswordCredentials)credentials).getPassword());
		
		Person p = ea.getPerson();
		if (p == null)
			return null;
		else if (!p.checkPassword(credPassword.toString()))
			return null;
		else
		{
			SubethaPrincipal sp = new SubethaPrincipal(p.getId().toString(), email);
			for (String role: p.getRoles())
				sp.addRole(role);
			
			return sp;
		}
	}

	/** */
	public boolean isUserInRole(Principal user, String role)
	{
		CachingPrincipal p = (CachingPrincipal)user;
		
		return p.isInRole(role);
	}

	/** */
	public void logout(Principal user)
	{
		// Nothing special needed
	}	
}