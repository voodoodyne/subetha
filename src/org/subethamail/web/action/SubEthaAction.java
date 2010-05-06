/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tagonist.AbstractAction;

/**
 * Adds some handy methods.
 *
 * @author Jeff Schnitzer
 */
abstract public class SubEthaAction extends AbstractAction
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(SubEthaAction.class);

	/** Keep all the validators around */
	private static Map<Class<?>, ClassValidator<?>> validators = new ConcurrentHashMap<Class<?>, ClassValidator<?>>();

	/**
	 * @return the action param specified by the key, as a String
	 */
	protected String getActionParam(String key)
	{
		Object value = this.getCtx().getActionParams().get(key);
		if (value == null)
			return null;
		else
			return value.toString();
	}

	/**
	 * @return null if cookie is not present
	 */
	protected Cookie getCookie(String name)
	{
		Cookie[] cookies = this.getCtx().getRequest().getCookies();

		if (cookies != null)
		{
			for (Cookie cookie : cookies)
			{
				if (cookie.getName().equals(name))
					return cookie;
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

		// Irritatingly, if we have an error the getRequestURI() method
		// returns the URI of the error page.  The original page is saved
		// as an attribute in the request.
		String requestURI = (String)this.getCtx().getRequest().getAttribute("javax.servlet.error.request_uri");
		if (requestURI == null)
			requestURI = this.getCtx().getRequest().getRequestURI();

		if (log.isDebugEnabled())
			log.debug("Getting contextless request URI.  contextPath is " + contextPath + ", requestURI is " + requestURI);

		return requestURI.substring(contextPath.length());
	}

	/**
	 * @return the contextless URI plus the query string.  Works even
	 *  if an http POST was submitted.  This is what you can safely
	 *  use <c:redirect> with.
	 */
	public String getUsefulRequestURI()
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

	/**
	 * Reflect any public fields that have been annotated with contraints
	 * and modify the error map accordingly.  The public requirement is
	 * inherent to java reflection, unfortunately.
	 */
	@SuppressWarnings("unchecked")
	public void validate() throws IllegalAccessException
	{

		Object model = this.getCtx().getModel();
		ClassValidator val = validators.get(model.getClass());
		if (val == null)
		{
			val = new ClassValidator(model.getClass());
			validators.put(this.getClass(), val);
		}

		for (InvalidValue invalid: val.getInvalidValues(model))
		{
			Object existingError = this.getCtx().getError(invalid.getPropertyPath());
			if (existingError == null)
				this.getCtx().setError(invalid.getPropertyPath(), invalid.getMessage());
			else
				this.getCtx().setError(invalid.getPropertyPath(), existingError + "\n" + invalid.getMessage());
		}
	}
}
