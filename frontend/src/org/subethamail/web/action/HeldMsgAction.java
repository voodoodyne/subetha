/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Performs an action on a held message.
 * 
 * @author Jeff Schnitzer
 */
public class HeldMsgAction extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(HeldMsgAction.class);

	@Property Long msgId;
	@Property String action;
	
	/** out */
	@Property Long listId;
	
	/** */
	public void execute() throws Exception
	{
		if ("Approve".equals(this.action))
		{
			this.listId = Backend.instance().getListMgr().approveHeldMessage(this.msgId);
		}
		else if ("Discard".equals(this.action))
		{
			this.listId = Backend.instance().getListMgr().discardHeldMessage(this.msgId);
		}
	}
}
