/*
 * $Id: Login.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Login.java $
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Gets data about a mailing list and the current user.
 * Model becomes a MySubscription.
 * 
 * @author Jeff Schnitzer
 */
public class GetSubscribersDataForList extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(GetMySubscription.class);

	public static class Model extends ErrorMapModel
	{
		/** */
		@Property Long listId;
	}

	/** */
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}
	
	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();
		this.getCtx().setModel(Backend.instance().getListMgr().getSubscribers(model.listId));
	}
}
