/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.acct.i;


/**
 * When a user anonymously subscribes to a list, they get back
 * a set of auth credentials as well as a result.  This allows
 * them to be automatically logged in.
 * 
 * @author Jeff Schnitzer
 */
public class AuthSubscribeResult
{
	/** */
	SubscribeResult result;
	Long listId;
	String email;
	String password;
	
	/** */
	public AuthSubscribeResult(SubscribeResult result, Long listId, String email, String password)
	{
		this.result = result;
		this.listId = listId;
		this.email = email;
		this.password = password;
	}

	/** */
	public Long getListId()
	{
		return this.listId;
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

	/** */
	public SubscribeResult getResult()
	{
		return this.result;
	}
}
