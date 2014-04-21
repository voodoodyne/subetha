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
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;

/**
 * Changes a user's password.
 * 
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
@Log
public class UserChangePassword extends AuthRequired 
{
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Length(min=Validator.MIN_PERSON_PASSWORD, max=Validator.MAX_PERSON_PASSWORD)
		@Getter @Setter String password = "";

		/** Don't need to explicitly validate confirm because it must match password */
		@Getter @Setter String confirm = "";
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

		// Basic validation
		model.validate();
		
		if (!model.password.equals(model.confirm))
			model.setError("password", "The passwords do not match.");
			
		if (model.getErrors().isEmpty())
			Backend.instance().getAccountMgr().setPassword(model.password);
	}
}
