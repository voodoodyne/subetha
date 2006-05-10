/*
 * $Id: .java 310 2006-05-09 19:39:28Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/action/EmailAdd.java $
 */

package org.subethamail.web.action;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Removes a Subscriber from a list.
 * 
 * @author Scott Hernandez
 */
public class ListRemoveSubscriber extends AuthRequired 
{
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Property Long subId;
		@Property Long listId;
	}
	
	/** */
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}
	
	/** */
	public void authExecute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();
		
		model.validate();
		
		if (model.getErrors().isEmpty())
		{
			Backend.instance().getListMgr().unsubscribe(model.listId, model.subId);
		}
	}
}
