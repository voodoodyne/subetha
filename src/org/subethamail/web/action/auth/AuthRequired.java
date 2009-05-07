/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AuthRequired extends AutoLogin 
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(AuthRequired.class);
	
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
			
			if (log.isDebugEnabled())
				log.debug("Destination will be: " + model.dest);
			
			this.getCtx().getRequest().setAttribute(LOGIN_REQUIRED_MODEL_ATTR, model);
			
			throw new ForwardException(LOGIN_REQUIRED_PAGE);
		}
	}
}
