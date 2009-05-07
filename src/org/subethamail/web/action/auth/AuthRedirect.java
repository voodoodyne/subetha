/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AuthRedirect extends AutoLogin 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(AuthRedirect.class);
	
	/**
	 */
	protected void execute2() throws Exception
	{
		if (this.isLoggedIn())
		{
			String redir = this.getActionParam("dest");
			if (redir != null)
				throw new RedirectException(redir);
			else
				throw new RedirectException(
						this.getCtx().getResponse().encodeRedirectURL(
								this.getCtx().getRequest().getContextPath() + "/home.jsp"));
		}
	}
}
