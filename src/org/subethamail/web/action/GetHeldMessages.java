/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.lists.i.MailHold;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.PaginateModel;

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
	private final static Logger log = LoggerFactory.getLogger(GetMyListRelationship.class);

	public static class Model extends PaginateModel
	{
		/** */
		@Getter @Setter Long listId;
		@Getter @Setter Collection<MailHold> holds;
	}

	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/**
	 * @throws Exception 
	 **/
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();

		model.holds = Backend.instance().getListMgr().getHeldMessages(model.listId, model.getSkip(), model.getCount());
		model.setTotalCount(Backend.instance().getListMgr().countHeldMessages(model.listId));
	}
}
