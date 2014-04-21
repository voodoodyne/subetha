/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Performs an action on a held subscription.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class HeldSubAction extends AuthAction 
{
	@Getter @Setter Long listId;
	@Getter @Setter Long personId;
	@Getter @Setter String action;
	
	/** */
	public void execute() throws Exception
	{
		if ("Approve".equals(this.action))
		{
			Backend.instance().getListMgr().approveHeldSubscription(this.listId, this.personId);
		}
		else if ("Discard".equals(this.action))
		{
			Backend.instance().getListMgr().discardHeldSubscription(this.listId, this.personId);
		}
	}
}
