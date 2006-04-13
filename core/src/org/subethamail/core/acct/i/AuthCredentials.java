/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.acct.i;


/**
 * Some token-oriented methods return auth credentials so that
 * the user who posesses the token can be automatically logged
 * in.
 * 
 * @author Jeff Schnitzer
 */
public class AuthCredentials
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
