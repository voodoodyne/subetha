/*
 * $Id: Login.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Login.java $
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.Email;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Subscribes an anonymous (not logged in) user to a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public class SubscribeAnon extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(SubscribeAnon.class);
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Property Long listId;
		
		/** */
		@Email
		@Property String deliverTo = "";
		
		/** */
		@Property String name = "";
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
		
		//Backend.instance().getAccountMgr().subscribe(this.listId, this.deliverTo);
	}
	
}
