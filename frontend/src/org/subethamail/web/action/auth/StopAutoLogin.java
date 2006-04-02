/*
 * $Id: StopAutoLogin.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/StopAutoLogin.java $
 */

package org.subethamail.web.action.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Stops autologin.
 * 
 * @author Jeff Schnitzer
 */
public class StopAutoLogin extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(StopAutoLogin.class);
	
	/**
	 */
	public void execute() throws Exception
	{
		this.stopAutoLogin();
	}
}
