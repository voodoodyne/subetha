/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;


/**
 * Stops autologin.
 * 
 * @author Jeff Schnitzer
 */
public class StopAutoLogin extends AuthAction 
{
	/**
	 */
	public void execute() throws Exception
	{
		this.stopAutoLogin();
	}
}
