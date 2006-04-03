/*
 * $Id: AuthRequired.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/AuthRequired.java $
 */

package org.subethamail.web.action.auth;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.acct.i.AccountMgr;
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
	private static Log log = LogFactory.getLog(AuthRequired.class);
	
	/** */
	public static final String LOGIN_REQUIRED_PAGE = "/login_required.jsp";
	public static final String LOGIN_REQUIRED_MODEL_ATTR = "loginModel";

	/** Thread-safe, so we should be able to cache this as static. */
	protected static AccountMgr acctMgr;
	static
	{
		try
		{
			InitialContext ctx = new InitialContext();
			acctMgr = (AccountMgr)ctx.lookup(AccountMgr.JNDI_NAME);
		}
		catch (NamingException ex) { throw new RuntimeException(ex); }
	}
	
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
			
			model.setDest(this.getUsefulRequestURI());
			
			if (log.isDebugEnabled())
				log.debug("Destination will be: " + model.getDest());
			
			this.getCtx().getRequest().setAttribute(LOGIN_REQUIRED_MODEL_ATTR, model);
			
			throw new ForwardException(LOGIN_REQUIRED_PAGE);
		}
	}
}
