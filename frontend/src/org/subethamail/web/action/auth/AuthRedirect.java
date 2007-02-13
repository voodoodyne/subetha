/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.web.Backend;
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
	private static Log log = LogFactory.getLog(AuthRedirect.class);
	
	/**
	 */
	protected void execute2() throws Exception
	{
		if (this.isLoggedIn())
		{
			String loc = null;
			String siteUrl = Backend.instance().getAdmin().getDefaultSiteUrl().toString();
			if ("http://needsconfiguration/se/".equals(siteUrl))
				loc = this.getCtx().getRequest().getContextPath();
			else
				loc = siteUrl;

			String redir = this.getActionParam("dest");
			if (redir != null)
				loc = loc + redir;
			else
				loc = loc + "home.jsp";

			throw new RedirectException(this.getCtx().getResponse().encodeRedirectURL(loc));
		}
	}
}
