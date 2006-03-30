/*
 * $Id: AuthRedirect.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/AuthRedirect.java $
 */

package org.subethamail.web.action.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tagonist.RedirectException;

/**
 * If the user is logged in, issue a redirect to the
 * destination specified by actionParam "dest".
 * 
 * An auto login will occur if proper cookies are available.
 * 
 * @author Jeff Schnitzer
 */
public class AuthRedirect extends AutoLogin 
{
	/** */
	private static Log log = LogFactory.getLog(AuthRedirect.class);
	
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
				throw new IllegalStateException("Missing actionParam 'dest'");
		}
	}
}
