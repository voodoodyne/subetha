/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;

import java.util.logging.Level;

import lombok.extern.java.Log;

import org.tagonist.ForwardException;

/**
 * Requires authorization and provides information about the player.
 * Forwards user to login_required.jsp if user is not logged in. 
 * 
 * If login is required, a special attribute will be set to a
 * Login.Model with an appropriately specified dest.  The attribute
 * is "model", which must agree with the contents of the login_required.jsp
 * form.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class AuthRequired extends AutoLogin 
{
	/** */
	public static final String LOGIN_REQUIRED_PAGE = "/login_required.jsp";
	public static final String LOGIN_REQUIRED_MODEL_ATTR = "loginModel";

	/**
	 * Override this method to implement behavior. 
	 */
	protected void authExecute() throws Exception
	{
		// By default do nothing
	}
	
	/**
	 * Override authExecute() instead. 
	 */
	protected final void execute2() throws Exception
	{
		if (this.isLoggedIn())
		{
			this.authExecute();
		}
		else
		{
			Login.Model model = new Login.Model();
			
			model.dest = this.getUsefulRequestURI();
			
			if (log.isLoggable(Level.FINE))
			    log.log(Level.FINE,"Destination will be: {0}", model.dest);
			
			this.getCtx().getRequest().setAttribute(LOGIN_REQUIRED_MODEL_ATTR, model);
			
			throw new ForwardException(LOGIN_REQUIRED_PAGE);
		}
	}
}
