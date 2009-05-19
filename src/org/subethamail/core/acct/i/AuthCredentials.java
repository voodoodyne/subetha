/*
 * $Id: AuthCredentials.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/acct/i/AuthCredentials.java $
 */

package org.subethamail.core.acct.i;

import java.io.Serializable;


/**
 * Some token-oriented methods return auth credentials so that
 * the user who posesses the token can be automatically logged
 * in.
 * 
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class AuthCredentials implements Serializable
{
	/** */
	Long id;
	String email;
	String password;
	
	protected AuthCredentials()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

	/** */
	public AuthCredentials(Long id, String prettyName, String password)
	{
		this.id = id;
		this.email = prettyName;
		this.password = password;
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

	/** */
	public String getPassword()
	{
		return this.password;
	}
}
