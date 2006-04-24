/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.subethamail.common.valid.Validator;
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
		@Length(max=Validator.MAX_EMAIL_ADDRESS)
		@Property String deliverTo = "";
		
		/** */
		@Length(max=Validator.MAX_PERSON_NAME)
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
		
		model.validate();
		
		if (model.getErrors().isEmpty())
		{
			Backend.instance().getAccountMgr().subscribeAnonymousRequest(model.listId, model.deliverTo, model.name);
		}
	}
	
}
