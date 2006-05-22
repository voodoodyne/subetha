/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.lists.i.MailSummary;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.PaginateModel;
import org.tagonist.propertize.Property;

/**
 * Gets one page of an archive.  Model becomes a List<MessageSummary>.
 * 
 * @author Jeff Schnitzer
 */
public class GetThreads extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(GetThreads.class);

	public static class Model extends PaginateModel
	{
		/** */
		@Property Long listId;
		@Property String query;
		@Property List<MailSummary> messages;
	}

	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();

		model.messages = Backend.instance().getArchiver().getThreads(model.listId);
		model.setTotalCount(model.messages.size());
		if (model.getSkip() > 0 && model.getCount() > 0)
			model.messages = model.messages.subList(model.getSkip(), model.getSkip() + model.getCount());
	}
}
