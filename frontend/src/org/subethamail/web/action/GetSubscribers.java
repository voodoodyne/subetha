/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.ArrayList;
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

		model.subscriberData = Backend.instance().getListMgr().getSubscribers(model.listId);

		// do some basic searching. keeps the load off the database.
		if (model.query != null && model.query.length() > 0)
		{
			List<SubscriberData> queryResults = new ArrayList<SubscriberData>(model.subscriberData.size());

			for (SubscriberData subscriber : model.subscriberData)
			{
				boolean match = false;
				for (String email : subscriber.getEmailAddresses())
				{
					if (email.contains(model.query))
					{
						queryResults.add(subscriber);
						match = true;
						continue;
					}
				}

				if (!match && subscriber.getName().contains(model.query))
				{
					queryResults.add(subscriber);
				}
			}

			model.subscriberData = queryResults;
		}
		
		model.setTotalCount(model.subscriberData.size());
		if (model.getSkip() > 0 && model.getCount() > 0)
			model.subscriberData = model.subscriberData.subList(model.getSkip(), model.getSkip() + model.getCount());
		
	}
}
