/*
 * $Id: BlornAction.java 95 2006-02-23 23:41:33Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/BlornAction.java $
 */

package org.subethamail.web.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;

import org.subethamail.core.acct.i.Receptionist;
import org.subethamail.core.admin.i.Encryptor;
import org.tagonist.AbstractAction;

/**
 * Adds some handy methods.
 * 
 * @author Jeff Schnitzer
 */
abstract public class SubEthaAction extends AbstractAction 
{
	/** Thread-safe, so we should be able to cache this as static. */
	protected static Receptionist receptionist;
	static
	{
		try
		{
			InitialContext ctx = new InitialContext();
			receptionist = (Receptionist)ctx.lookup(Receptionist.JNDI_NAME);
		}
		catch (NamingException ex) { throw new RuntimeException(ex); }
	}
	
	/** Thread-safe, so we should be able to cache this as static. */
	protected static Encryptor encryptor;
	static
	{
		try
		{
			InitialContext ctx = new InitialContext();
			encryptor = (Encryptor)ctx.lookup(Encryptor.JNDI_NAME);
		}
		catch (NamingException ex) { throw new RuntimeException(ex); }
	}
	
	/**
	 * @return the action param specified by the key, as a String
	 */
	protected String getActionParam(String key)
	{
		return this.getCtx().getActionParams().get(key).toString();
	}

	/**
	 * @return null if cookie is not present
	 */
	protected Cookie getCookie(String name)
	{
		Cookie[] cookies = this.getCtx().getRequest().getCookies();
		
		if (cookies != null)
		{
			for (int i=0; i<cookies.length; i++)
			{
				if (cookies[i].getName().equals(name))
					return cookies[i];
			}
		}
		
		return null;
	}
	
	/**
	 * @return null if cookie is not present
	 */
	protected String getCookieValue(String name)
	{
		Cookie cook = this.getCookie(name);
		if (cook == null)
			return null;
		else
			return cook.getValue();
	}
	
	/**
	 */
	protected void setCookie(String name, String value, int maxAge)
	{
		Cookie cook = new Cookie(name, value);
		cook.setMaxAge(maxAge);
		
		this.getCtx().getResponse().addCookie(cook);
	}
	
	/**
	 * @return the full URI, including the query string.  Works even
	 *  if an http POST was submitted.
	 */
	protected String getFullRequestURI()
	{
		if ("POST".equals(this.getCtx().getRequest().getMethod().toUpperCase()))
		{
			Map<String, String[]> params = this.getCtx().getRequest().getParameterMap();
			
			if (params.isEmpty())
			{
				return this.getCtx().getRequest().getRequestURI();
			}
			else
			{
				// We have to build the query string from the parameters by hand
				StringBuffer queryBuf = new StringBuffer(1024);
				queryBuf.append(this.getCtx().getRequest().getRequestURI());
	
				boolean afterFirst = false;
				for (Map.Entry<String, String[]> entry: params.entrySet())
				{
					for (String value: entry.getValue())
					{
						if (afterFirst)
						{
							queryBuf.append('&');
						}
						else
						{
							queryBuf.append('?');
							afterFirst = true;
						}
						
						try
						{
							queryBuf.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
							queryBuf.append('=');
							queryBuf.append(URLEncoder.encode(value, "UTF-8"));
						}
						catch (UnsupportedEncodingException ex)
						{
							// Should be impossible
							throw new RuntimeException(ex);
						}
					}
				}
				
				return queryBuf.toString();
			}
		}
		else
		{
			String query = this.getCtx().getRequest().getQueryString();
			if (query == null)
				return this.getCtx().getRequest().getRequestURI();
			else
				return this.getCtx().getRequest().getRequestURI() + "?" + query;
		}
	}
}
