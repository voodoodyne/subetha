/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.hibernate.validator.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.entity.i.Validator;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Changes a user's password.
 * 
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
public class UserChangePassword extends AuthRequired 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(UserChangePassword.class);
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Length(min=Validator.MIN_PERSON_PASSWORD, max=Validator.MAX_PERSON_PASSWORD)
		@Property String password = "";

		/** Don't need to explicitly validate confirm because it must match password */
		@Property String confirm = "";
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
