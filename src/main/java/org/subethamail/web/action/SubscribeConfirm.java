/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.subethamail.core.acct.i.AuthSubscribeResult;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Subscribes an anonymous (not logged in) user to a mailing list.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class SubscribeConfirm extends AuthAction 
{
	/** */
	@Getter @Setter String token = "";
	@Getter @Setter boolean badTokenError;
	@Getter @Setter boolean held;
	@Getter @Setter Long listId;

	/** */
	public void execute() throws Exception
	{
		try
		{
			AuthSubscribeResult authResult = Backend.instance().getAccountMgr().subscribeAnonymous(this.token);
			
			this.listId = authResult.getListId();
			
			if (SubscribeResult.HELD.equals(authResult.getResult()))
			{
				this.held = true;
			}
			
			this.login(authResult.getEmail(), authResult.getPassword());
		}
		catch (BadTokenException ex)
		{
			this.badTokenError = true;
		}
	}
	
}
