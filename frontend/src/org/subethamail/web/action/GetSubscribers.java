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
import org.subethamail.web.model.PaginateModel;
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

	public static class Model extends PaginateModel
	{
		/** */
		@Property Long listId;
		@Property String query;
		@Property List<SubscriberData> subscriberData;
	}

	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();

		// Backend.instance().getListMgr().getSubscribers(model.listId)
		model.subscriberData = Backend.instance().getAccountMgr()
			.searchSubscribers(model.query, model.listId, model.getSkip(), model.getCount());

		if (model.query == null || model.query.length() == 0)
		{
			model.setTotalCount(Backend.instance().getAccountMgr().countSubscribers(model.listId));
		}
		else
		{
			// If we are doing a query, then we need to find out how many results would
			// have been returned for our query (before the limit was applied) in 
			// order to do the pagination right.
			//
			// this is highly inefficient as we are doing a full table scan again.
			// Since we know and love MySQL, it would be better to do something like this:
			// http://www.mysqlfreaks.com/statements/101.php
			//			SELECT SQL_CALC_FOUND_ROWS *
			//			FROM tbl_name
			//			WHERE id > 100 LIMIT 10;
			//
			//			SELECT FOUND_ROWS();
			//
			int size = Backend.instance().getAccountMgr().countSubscribers(model.listId, model.query);
			model.setTotalCount(size);
		}
	}
}
