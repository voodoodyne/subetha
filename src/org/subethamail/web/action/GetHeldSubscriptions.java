/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.lists.i.SubscriberData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Gets the (unpaginated) list of held subscriptions.
 * 
 * @author Jeff Schnitzer
 */
public class GetHeldSubscriptions extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(GetMyListRelationship.class);

	/** */
	@Getter @Setter Long listId;

	/** */
	public void execute() throws Exception
	{
		List<SubscriberData> data = Backend.instance().getListMgr().getHeldSubscriptions(this.listId);

		this.getCtx().setModel(data);
	}
}
