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
 * Gets data about a mailing list and the current user.
 * Model becomes a MySubscription.
 * 
 * @author Jeff Schnitzer
 */
public class GetSubscribers extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(GetMySubscription.class);

	/** */
	@Property Long listId;

	/** */
	public void execute() throws Exception
	{
		this.getCtx().setModel(Backend.instance().getListMgr().getSubscribers(listId));
	}
}
