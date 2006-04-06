/*
 * $Id: Logout.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Logout.java $
 */

package org.subethamail.web.action.auth;

import org.tagonist.propertize.Property;


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
	@Property(set=false) boolean autoLoginEnabled;
	
	/** */
	public void execute() throws Exception
	{
		this.logout();
		
		this.autoLoginEnabled = (this.getCookie(AUTO_LOGIN_COOKIE_KEY) != null);
	}
}
