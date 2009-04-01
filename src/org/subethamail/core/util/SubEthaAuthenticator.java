/*
 * $Id: SubEthaLoginModule.java 735 2006-08-20 04:21:14Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/SimilarityLoginModule.java,v $
 */

package org.subethamail.core.util;

import java.security.Principal;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.subethamail.entity.Person;

import com.caucho.config.Name;
import com.caucho.security.Authenticator;
import com.caucho.security.Credentials;
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
	public Principal authenticate(Principal user, Credentials credentials, Object detail)
	{
		String idStr = user.getName();
		Long id = Long.valueOf(idStr);
		
		Person p = this.em.find(Person.class, id);
		if (p == null)
			return null;
		else if (!p.checkPassword(credentials.toString()))
			return null;
		else
		{
			CachingPrincipal prince = new CachingPrincipal(idStr);
			
			for (String role: p.getRoles())
				prince.addRole(role);
			
			return prince;
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