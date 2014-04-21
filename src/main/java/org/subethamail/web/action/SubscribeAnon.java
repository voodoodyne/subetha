/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.hibernate.validator.constraints.Length;
import org.subethamail.entity.i.Validator;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;

/**
 * Subscribes an anonymous (not logged in) user to a mailing list.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class SubscribeAnon extends AuthAction 
{
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Getter @Setter Long listId;
		
		/** */
		@Length(max=Validator.MAX_EMAIL_ADDRESS)
		@Getter @Setter String deliverTo = "";
		
		/** */
		@Length(max=Validator.MAX_PERSON_NAME)
		@Getter @Setter
		String name = "";

		/** */
		@Override
		public void validate() throws IllegalAccessException
		{
			super.validate();
			if (!Validator.validEmail(deliverTo))
				this.setError("deliverTo", "That is not a valid email address");
		}
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
