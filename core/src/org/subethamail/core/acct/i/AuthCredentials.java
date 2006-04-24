/*
 * $Id$
 * $URL$
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
	String email;
	String password;
	
	/** */
	public AuthCredentials(String email, String password)
	{
		this.email = email;
		this.password = password;
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
