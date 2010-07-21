/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Performs an action on a held subscription.
 * 
 * @author Jeff Schnitzer
 */
public class HeldSubAction extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(HeldSubAction.class);

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
