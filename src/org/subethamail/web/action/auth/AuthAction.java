/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;


import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.acct.i.AuthCredentials;
import org.subethamail.web.Backend;
import org.subethamail.web.action.SubEthaAction;

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
	
	/** Name of autologin cookie */
	protected static final String AUTO_LOGIN_COOKIE_KEY = "subetha.auth";
	
	/** The j2ee security role for administrative users */
	public final static String SITE_ADMIN_ROLE = "siteAdmin";
	
	/** Key in http session to the pretty auth name */
	protected static final String AUTH_NAME_KEY = "subetha.authName";
	
	/**
	 * Actually perform the login logic by calling into the JAAS stack.
	 *
	 * @throws LoginException if it didn't work.
	 */
	public void login(String who, String password) throws LoginException
	{
		Backend.instance().getLogin().logout(this.getCtx().getRequest());
		
		AuthCredentials creds = Backend.instance().getAccountMgr().authenticate(who, password);
		
		if (log.isDebugEnabled())
			log.debug("Successful authentication for:  " + who);
		
		this.markLoggedIn(creds);
	}
	
	/**
	 * Utility method that makes the user logged in as the specified credentials.
	 */
	protected void markLoggedIn(AuthCredentials creds)
	{
		Backend.instance().getLogin().login(creds.getId().toString(), creds.getPassword(), this.getCtx().getRequest());
		
		this.getCtx().getSession().setAttribute(AUTH_NAME_KEY, creds.getPrettyName());
	}
	
	/**
	 * @return true if the user has been authenticated.
	 */
	public boolean isLoggedIn()
	{
		return this.getCtx().getRequest().getUserPrincipal() != null;
	}

	/**
	 * @return whether or not the user is the most powerful kind of administrator
	 */
	public boolean isSiteAdmin()
	{
		return this.getCtx().getRequest().isUserInRole(SITE_ADMIN_ROLE);
	}
	
	/**
	 * @return the email of the currently logged-in user, or null if not logged in.
	 */
	public String getAuthName()
	{
		if (this.isLoggedIn())
			return (String)this.getCtx().getSession().getAttribute(AUTH_NAME_KEY);
		else
			return null;
	}
	
	/**
	 * Logs the user out.
	 */
	protected void logout()
	{
		Backend.instance().getLogin().logout(this.getCtx().getRequest());
		this.getCtx().getSession().removeAttribute(AUTH_NAME_KEY);
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
		
		return new String(Base64.encodeBase64(cipherText));
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
			byte[] cipherText = Base64.decodeBase64(cookieText.getBytes());
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
