/*
 * $Id$
 * $URL$
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
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(StopAutoLogin.class);
	
	/**
	 */
	public void execute() throws Exception
	{
		this.stopAutoLogin();
	}
}
