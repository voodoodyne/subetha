/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.lists.i.SubscriberData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.PaginateModel;
import org.tagonist.propertize.Property;

/**
 * Gets a list of subscribers to the list
 * 
 * @author Jeff Schnitzer
 * @author Jon Stevens
 */
public class GetSubscribers extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(GetMyListRelationship.class);

	/** */
	public static class Model extends PaginateModel
	{
		/** */
		@Property Long listId;
		@Property String query = "";
		@Property List<SubscriberData> subscribers;
	}

	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();
		
		if (model.query.trim().length() == 0)
		{
			model.subscribers = Backend.instance().getListMgr().getSubscribers(model.listId, model.getSkip(), model.getCount());
			model.setTotalCount(Backend.instance().getListMgr().countSubscribers(model.listId));
		}
		else
		{
			model.subscribers = Backend.instance().getListMgr().searchSubscribers(model.listId, model.query, model.getSkip(), model.getCount());
			model.setTotalCount(Backend.instance().getListMgr().countSubscribersQuery(model.listId, model.query));
			// See the comment in GetLists
		}
	}
}
