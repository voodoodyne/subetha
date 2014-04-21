/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;

import java.util.logging.Level;

import lombok.extern.java.Log;

import org.tagonist.RedirectException;

/**
 * If the user is logged in, issue a redirect to the
 * destination specified by actionParam "dest".  If
 * dest is not supplied, defaults to /home.jsp.
 * 
 * An auto login will occur if proper cookies are available.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class AuthRedirect extends AutoLogin 
{
	/**
	 */
	protected void execute2() throws Exception
	{
		if (this.isLoggedIn())
		{
			String redir = this.getActionParam("dest");
			if (redir != null)
			{
			    log.log(Level.FINE,"redirect to: {0}",redir);
				throw new RedirectException(redir);
			}
			else
			{
                log.log(Level.FINE,"redirecting to home page");
				throw new RedirectException(
						this.getCtx().getResponse().encodeRedirectURL(
								this.getCtx().getRequest().getContextPath() + "/home.jsp"));
			}
		}
		else
		{
		    log.log(Level.INFO,"not isLoggedIn");
		}
	}
}
