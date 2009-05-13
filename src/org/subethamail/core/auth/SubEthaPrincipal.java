/*
 * $Id: AuthCredentials.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/acct/i/AuthCredentials.java $
 */

package org.subethamail.core.auth;

import java.security.Principal;
import java.util.Set;


/**
 * Some token-oriented methods return auth credentials so that
 * the user who posesses the token can be automatically logged
 * in.
 * 
 * @author Jeff Schnitzer
 */
public class SubEthaPrincipal implements Principal
{
	/** */
	Long id;
	String email;
	Set<String> roles;
	
	protected SubEthaPrincipal()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

	/** */
	public SubEthaPrincipal(Long id, String email, Set<String> roles)
	{
		this.id = id;
		this.email = email;
		this.roles = roles;
	}
	
	/** */
	public Long getId()
	{
		return this.id;
	}

	/** */
	public String getEmail()
	{
		return this.email;
	}

	/** This is an alias for getEmail() */
	public String getName()
	{
		return this.getEmail();
	}

	/** */
	public Set<String> getRoles()
	{
		return this.roles;
	}
	
	/** */
	public String toString()
	{
		return "SubEthaPrincipal {id=" + this.id + ", email=" + this.email + "}";
	}
}
