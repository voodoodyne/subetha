/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;

import javax.security.auth.login.LoginException;

import lombok.Data;

/**
 * Performs a login.  The model starts as (and remains)
 * a Login.Model.
 * 
 * @author Jeff Schnitzer
 */
public class Login extends AuthAction 
{
	/** */
	@Data
	public static class Model
	{
		String email = "";
		String password = "";
		boolean remember;
		String dest = "";
		String error = "";
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
		
		try
		{
			this.login(model.email, model.password);
			
			if (model.remember)
				this.setAutoLogin(model.email, model.password);
		}
		catch (LoginException ex)
		{
			model.password = "";
			model.error = "Invalid username or password";
		}
	}
}
