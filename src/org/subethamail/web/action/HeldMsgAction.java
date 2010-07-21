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
 * Performs an action on a held message.
 * 
 * @author Jeff Schnitzer
 */
public class HeldMsgAction extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(HeldMsgAction.class);

	/** in */
	@Getter @Setter Long msgId;
	@Getter @Setter String action;
	
	/** out */
	@Getter @Setter Long listId;
	
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
		else if ("Subscribe".equals(this.action))
		{
			this.listId = Backend.instance().getListMgr().approveHeldMessageAndSubscribe(this.msgId);
		}
	}
}
