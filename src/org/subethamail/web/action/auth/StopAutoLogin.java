/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stops autologin.
 * 
 * @author Jeff Schnitzer
 */
public class StopAutoLogin extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(StopAutoLogin.class);
	
	/**
	 */
	public void execute() throws Exception
	{
		this.stopAutoLogin();
	}
}
