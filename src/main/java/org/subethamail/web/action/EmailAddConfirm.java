/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.Length;
import org.subethamail.core.acct.i.AuthCredentials;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;

/**
 * Confirms the adding of an email address to an existing account.
 * 
 * @author Jeff Schnitzer
 */
public class EmailAddConfirm extends AuthAction 
{
	public static class Model extends ErrorMapModel
	{
		/** */
		@Length(min=1)
		@Getter @Setter String token = "";
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
		// Basic validation
		model.validate();

		// Maybe we can proceed?
		if (model.getErrors().isEmpty())
		{
			try
			{
				AuthCredentials creds = Backend.instance().getAccountMgr().addEmail(model.token);
				
				if (!this.isLoggedIn())
					this.login(creds.getEmail(), creds.getPassword());
			}
			catch (BadTokenException ex)
			{
				model.setError("badtoken", "The token is invalid.");
			}
		}
	}
}
