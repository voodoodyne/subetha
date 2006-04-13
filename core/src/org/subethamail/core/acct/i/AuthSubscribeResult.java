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
public class AuthSubscribeResult extends AuthCredentials
{
	/** */
	SubscribeResult result;
	Long listId;
	
	/** */
	public AuthSubscribeResult(String email, String password, SubscribeResult result, Long listId)
	{
		super(email, password);
		
		this.result = result;
		this.listId = listId;
	}

	/** */
	public Long getListId()
	{
		return this.listId;
	}

	/** */
	public SubscribeResult getResult()
	{
		return this.result;
	}
}
