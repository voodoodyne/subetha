/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.acct.i;


/**
 * When a user anonymously subscribes to a list, they get back
 * a set of auth credentials as well as a result.  This allows
 * them to be automatically logged in.
 * 
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
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
