/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.acct.i.AuthSubscribeResult;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Subscribes an anonymous (not logged in) user to a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public class SubscribeConfirm extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(SubscribeConfirm.class);
	
	/** */
	@Property String token = "";
	@Property boolean badTokenError;
	@Property boolean held;
	@Property Long listId;

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
			
			this.login(authResult.getPrettyName(), authResult.getPassword());
		}
		catch (BadTokenException ex)
		{
			this.badTokenError = true;
		}
	}
	
}
