/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action.auth;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.java.Log;

import org.apache.commons.codec.binary.Base64;
import org.subethamail.core.auth.SubEthaPrincipal;
import org.subethamail.web.Backend;
import org.subethamail.web.action.SubEthaAction;
import org.subethamail.web.security.SubEthaLogin;
import org.tagonist.ActionContext;

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
@Log
abstract public class AuthAction extends SubEthaAction 
{
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
		SubEthaLogin rl = Backend.instance().getLogin();
		
		rl.logout(this.getCtx().getRequest());
		
		log.log(Level.FINE,"Successful authentication for:  {0}", who);
		
		if (!rl.login(who, password, this.getCtx().getRequest()))
			throw new FailedLoginException("Bad username or password");
	}
	
	/**
	 * @return true if the user has been authenticated.
	 */
	public boolean isLoggedIn()
	{
	    SubEthaPrincipal prin = this.getPrincipal();
        log.log(Level.FINE,"isLoggedIn={0}!=null",prin);
	    boolean res = prin != null;
		return res;
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
			return getPrincipal().getEmail();
		else
			return null;
	}
	
	/**
	 * Logs the user out.
	 */
	protected void logout()
	{
		Backend.instance().getLogin().logout(this.getCtx().getRequest());
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
		    log.log(Level.FINE,"Error decrypting autologin cookie:  ", ex);

			// Delete the damn thing
			this.stopAutoLogin();
			
			return null;
		}
	}
	/**
	 * helper method to consolidate {@link Principal} acquisition
	 * @return the current {@link Principal}
	 */
	protected SubEthaPrincipal getPrincipal()
	{
	    ActionContext ctx = this.getCtx();
	    log.log(Level.FINE,"ActionContext={0}",ctx);
	    if (ctx==null) return null;
	    HttpServletRequest request = ctx.getRequest();
        log.log(Level.FINE,"HttpServletRequest={0}",request);
        if (request==null) return null;
        Principal p = request.getUserPrincipal();
		log.log(Level.FINE,"Principal={0}",p);
		if (p!=null && p instanceof SubEthaPrincipal) return (SubEthaPrincipal)p;
		return null;
	}
}
