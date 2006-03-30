/*
 * $Id: Login.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Login.java $
 */

package org.subethamail.web.action.auth;

import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
		/** */
		String email = "";
		public String getEmail() { return this.email; }
		public void setEmail(String value) { this.email = value; }
	
		/** */
		String password = "";
		public String getPassword() { return this.password; }
		public void setPassword(String value) { this.password = value; }
	
		/** */
		boolean remember;
		public boolean getRemember() { return this.remember; }
		public void setRemember(boolean value) { this.remember = value; }
		
		/** */
		String dest = "";
		public String getDest() { return this.dest; }
		public void setDest(String value) { this.dest = value; }
	
		/** */
		String error = "";
		public String getError() { return this.error; }
		public void setError(String value) { this.error = value; }
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
			this.login(model.getEmail(), model.getPassword());
			
			if (model.getRemember())
				this.setAutoLogin(model.getEmail(), model.getPassword());
		}
		catch (LoginException ex)
		{
			model.setPassword("");
			model.setError("Invalid username or password");
		}
	}
}
