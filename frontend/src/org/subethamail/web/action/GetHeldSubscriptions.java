/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.lists.i.SubscriberData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Gets the (unpaginated) list of held subscriptions.
 * 
 * @author Jeff Schnitzer
 */
public class GetHeldSubscriptions extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(GetMySubscription.class);

	/** */
	@Property Long listId;

	/** */
	public void execute() throws Exception
	{
		List<SubscriberData> data = Backend.instance().getListMgr().getHeldSubscriptions(this.listId);

		this.getCtx().setModel(data);
	}
}
