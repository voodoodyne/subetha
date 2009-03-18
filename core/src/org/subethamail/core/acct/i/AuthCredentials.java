/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.acct.i;

import java.io.Serializable;
import java.util.Set;


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
	String prettyName;
	String password;
	Set<String> roles;
	
	protected AuthCredentials()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

	/** */
	public AuthCredentials(Long id, String prettyName, String password, Set<String> roles)
	{
		this.id = id;
		this.prettyName = prettyName;
		this.password = password;
		this.roles = roles;
	}
	
	/** */
	public Long getId()
	{
		return this.id;
	}

	/** */
	public String getPrettyName()
	{
		return this.prettyName;
	}

	/** */
	public String getPassword()
	{
		return this.password;
	}

	/** */
	public Set<String> getRoles()
	{
		return this.roles;
	}
}