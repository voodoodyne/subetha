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
/*
		if (model.query == null || model.query.trim().length() == 0)
			return;
*/
		model.subscriberData = Backend.instance().getListMgr().getSubscribers(model.listId);
		model.setTotalCount(model.subscriberData.size());

/*		
		SearchResult result = this.getSearcher().search(model.getQuery(), model.getSkip(), model.getCount());
*/
	}
}
