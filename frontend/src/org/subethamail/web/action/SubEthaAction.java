/*
 * $Id: BlornAction.java 95 2006-02-23 23:41:33Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/BlornAction.java $
 */

package org.subethamail.web.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tagonist.AbstractAction;

/**
 * Adds some handy methods.
 * 
 * @author Jeff Schnitzer
 */
abstract public class SubEthaAction extends AbstractAction 
{
	/** */
	private static Log log = LogFactory.getLog(SubEthaAction.class);
	
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
	 * @return the request URI without the context path.
	 */
	protected String getContextlessRequestURI()
	{
		String contextPath = this.getCtx().getRequest().getContextPath();
		String requestURI = this.getCtx().getRequest().getRequestURI();
		
		if (log.isDebugEnabled())
			log.debug("Getting contextless request URI.  contextPath is " + contextPath + ", requestURI is " + requestURI);
		
		return requestURI.substring(contextPath.length());
	}
	
	/**
	 * @return the contextless URI plus the query string.  Works even
	 *  if an http POST was submitted.  This is what you can safely
	 *  use <c:redirect> with.
	 */
	protected String getUsefulRequestURI()
	{
		if ("POST".equals(this.getCtx().getRequest().getMethod().toUpperCase()))
		{
			Map<String, String[]> params = this.getCtx().getRequest().getParameterMap();
			
			if (params.isEmpty())
			{
				return this.getContextlessRequestURI();
			}
			else
			{
				// We have to build the query string from the parameters by hand
				StringBuffer queryBuf = new StringBuffer(1024);
				queryBuf.append(this.getContextlessRequestURI());
	
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
				return this.getContextlessRequestURI();
			else
				return this.getContextlessRequestURI() + "?" + query;
		}
	}
}
