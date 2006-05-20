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

		model.subscriberData = Backend.instance().getAccountMgr()
			.getSubscribersMatchingQuery(model.query, Backend.instance().getListMgr().getSubscribers(model.listId));

		// pagination
		model.setTotalCount(model.subscriberData.size());
		if (model.getSkip() > 0 && model.getCount() > 0)
			model.subscriberData = model.subscriberData.subList(model.getSkip(), model.getSkip() + model.getCount());
		
	}
}
