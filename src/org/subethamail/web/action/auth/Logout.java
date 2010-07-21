/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;

import lombok.Getter;


/**
 * Very simple - logs the user out.  Also indicates whether or
 * not the user has autologin enabled (although doesn't check
 * for validity of the actual credentials).
 *  
 * @author Jeff Schnitzer
 */
public class Logout extends AuthAction 
{
	/** */
	@Getter boolean autoLoginEnabled;
	
	/** */
	public void execute() throws Exception
	{
		this.logout();
		
		this.autoLoginEnabled = (this.getCookie(AUTO_LOGIN_COOKIE_KEY) != null);
	}
}
