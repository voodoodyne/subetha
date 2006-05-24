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
import org.subethamail.web.model.PaginateModel;
import org.tagonist.propertize.Property;

/**
 * Gets the list of held messages.
 * 
 * @author Jeff Schnitzer
 * @author Jon Stevens
 */
public class GetHeldMessages extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(GetMySubscription.class);

	public static class Model extends PaginateModel
	{
		/** */
		@Property Long listId;
		@Property Collection<MailHold> holds;
	}

	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();

		model.holds = Backend.instance().getListMgr().getHeldMessages(model.listId, model.getSkip(), model.getCount());
		model.setTotalCount(Backend.instance().getListMgr().countHeldMessages(model.listId));
	}
}
