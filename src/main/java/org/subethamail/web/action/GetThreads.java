/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.subethamail.core.lists.i.MailSummary;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.PaginateModel;

/**
 * Gets one page of an archive.  Model becomes a List<MessageSummary>.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class GetThreads extends AuthAction 
{
	public static class Model extends PaginateModel
	{
		public Model()
		{
			this.setCount(100);	// default to 100
		}
		
		/** */
		@Getter @Setter Long listId;
		@Getter @Setter List<MailSummary> messages;
	}

	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();

		model.messages = Backend.instance().getArchiver().getThreads(model.listId, model.getSkip(), model.getCount());
		model.setTotalCount(Backend.instance().getArchiver().countMailByList(model.listId));
	}
}
