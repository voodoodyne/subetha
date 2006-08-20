/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.acct.i;

import java.util.Set;


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
	public AuthSubscribeResult(Long id, String prettyName, String password, Set<String> roles, SubscribeResult result, Long listId)
	{
		super(id, prettyName, password, roles);
		
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
