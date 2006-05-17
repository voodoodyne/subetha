/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;

import javax.security.auth.login.LoginException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tagonist.propertize.Property;

/**
 * Performs a login.  The model starts as (and remains)
 * a Login.Model.
 * 
 * @author Jeff Schnitzer
 */
public class Login extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(Login.class);
	
	/** */
	public static class Model
	{
		@Property String email = "";
		@Property String password = "";
		@Property boolean remember;
		@Property String dest = "";
		@Property String error = "";
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
