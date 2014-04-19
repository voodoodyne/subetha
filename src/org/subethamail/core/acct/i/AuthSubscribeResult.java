package org.subethamail.core.acct.i;

import java.util.Set;


/**
 * When a user anonymously subscribes to a list, they get back
 * a set of auth credentials as well as a result.  This allows
 * them to be automatically logged in.
 * 
 * @author Jeff Schnitzer
 */
public class AuthSubscribeResult extends AuthCredentials
{
	private static final long serialVersionUID = 1L;

	/** */
	SubscribeResult result;
	Long listId;
	
	protected AuthSubscribeResult()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

	/** */
	public AuthSubscribeResult(Long id, String email, String password, Set<String> roles, SubscribeResult result, Long listId)
	{
		super(id, email, password);
		
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
