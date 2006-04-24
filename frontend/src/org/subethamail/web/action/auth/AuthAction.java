/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.jboss.util.Base64;
import org.subethamail.web.Backend;
import org.subethamail.web.action.SubEthaAction;
import org.subethamail.web.security.Security;

/**
 * Provides basic authentication services to action subclasses.
 * 
 * Unfortunately there are some serious defects in J2EE integrated
 * security, so we will maintain the "authenticated" state ourselves
 * by putting a value, PRINCIPAL_KEY, in the user session attrs.
 * 
 * Some helpful (or not) URLs:
 * 
 * http://www.jboss.org/index.html?module=bb&op=viewtopic&t=52152
 * http://www.jboss.org/index.html?module=bb&op=viewtopic&p=3874200
 * http://www.luminis.nl/publications/websecurity.html
 * 
 * The autologin cookie is a string "username/password" which has
 * been DES encrypted and Base64 encoded.
 * 
 * @author Jeff Schnitzer
 */
abstract public class AuthAction extends SubEthaAction 
{
	/** */
	private static Log log = LogFactory.getLog(AuthAction.class);
	
	/** The name of the jboss security context of this application. */
	public static final String SECURITY_CONTEXT_NAME = "subetha";
	
	/** Name of autologin cookie */
	protected static final String AUTO_LOGIN_COOKIE_KEY = "subetha.auth";
	
	/** The j2ee security role for administrative users */
	public final static String SITE_ADMIN_ROLE = "siteAdmin";
	
	/**
	 * Actually perform the login logic by calling into the JAAS stack.
	 *
	 * @throws LoginException if it didn't work.
	 */
	public void login(String who, String password) throws LoginException
	{
		Security.logout(this.getCtx().getSession());
		
		CallbackHandler handler = new UsernamePasswordHandler(who, password);
		LoginContext lc = new LoginContext(SECURITY_CONTEXT_NAME, handler);
		
		lc.login();
		
		if (log.isDebugEnabled())
			log.debug("Successful authentication for:  " + who);

		Security.login(this.getCtx().getSession(), who, password);
	}
	
	/**
	 * @return true if the user has been authenticated.
	 */
	public boolean isLoggedIn()
	{
		return Security.isLoggedIn(this.getCtx().getRequest());
	}

	/**
	 * @return whether or not the user is the most powerful kind of administrator
	 */
	public boolean isSiteAdmin()
	{
		return Security.isUserInRole(SITE_ADMIN_ROLE, this.getCtx().getRequest());
	}
	
	/**
	 * @return the email of the currently logged-in user, or null if not logged in.
	 */
	public String getAuthName()
	{
		Principal p = Security.getUserPrincipal(this.getCtx().getRequest());
		if (p == null)
			return null;
		else
			return p.getName();
	}
	
	/**
	 * Logs the user out.
	 */
	protected void logout()
	{
		Security.logout(this.getCtx().getSession());
		
		// Invalidating the session clears anything else
		this.getCtx().getSession().invalidate();
	}
	
	/**
	 * Stops auto-login from working by clearing the cookie credentials.
	 */
	protected void stopAutoLogin()
	{
		this.setCookie(AUTO_LOGIN_COOKIE_KEY, "", 0);
	}
	
	/**
	 */
	protected void setAutoLogin(String name, String password) throws Exception
	{
		this.setCookie(AUTO_LOGIN_COOKIE_KEY, this.encryptAutoLogin(name, password), Integer.MAX_VALUE);
	}
	
	/**
	 * Tries to execute an autologin.  Not guaranteed to work, in which case nothing happens.
	 * Side effects might include the user being logged in, or an invalid autlogin cookie
	 * being deleted.
	 */
	protected void tryAutoLogin() throws Exception
	{
		Cookie cook = this.getCookie(AUTO_LOGIN_COOKIE_KEY);
		if (cook != null)
		{
			String[] nameAndPass = this.decryptAutoLogin(cook.getValue());
			if (nameAndPass != null)
			{
				try
				{
					this.login(nameAndPass[0], nameAndPass[1]);
				}
				catch (LoginException ex)
				{
					this.stopAutoLogin();
				}
			}
		}
	}
	
	/**
	 * The cookie string is "name/password" but with both parts URLEncoded first.
	 * 
	 * @return a value suitable for a cookie.
	 */
	protected String encryptAutoLogin(String name, String password) throws Exception
	{
		List<String> pair = new ArrayList<String>(2);
		pair.add(name);
		pair.add(password);
		
		byte[] cipherText = Backend.instance().getEncryptor().encryptList(pair);
		
		return Base64.encodeBytes(cipherText);
	}
	
	/**
	 * @param cookieText was encrypted with encryptAutoLogin()
	 * @return the cookie translated into username (index 0) and password (index 1),
	 *  or null if anything at all went wrong.
	 */
	protected String[] decryptAutoLogin(String cookieText)
	{
		try
		{
			byte[] cipherText = Base64.decode(cookieText);
			List<String> pair = Backend.instance().getEncryptor().decryptList(cipherText);
			
			return new String[] { pair.get(0), pair.get(1) };
		}
		catch (Exception ex)
		{
			if (log.isDebugEnabled())
				log.debug("Error decrypting autologin cookie:  " + ex);

			// Delete the damn thing
			this.stopAutoLogin();
			
			return null;
		}
	}
}
