/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.lists.i.MailHold;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Gets the (unpaginated) list of held messages.
 * 
 * @author Jeff Schnitzer
 */
public class GetHeldMessages extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(GetMySubscription.class);

	/** */
	@Property Long listId;

	/** */
	public void execute() throws Exception
	{
		Collection<MailHold> data = Backend.instance().getListMgr().getHeldMessages(this.listId);

		this.getCtx().setModel(data);
	}
}
