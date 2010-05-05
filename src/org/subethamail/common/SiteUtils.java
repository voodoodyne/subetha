/*
 * $Id: MailUtils.java 378 2006-05-17 00:11:14Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/common/src/org/subethamail/common/MailUtils.java $
 */

package org.subethamail.common;

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides Subetha Site related helper methods
 * 
 * @author Scott Hernandez
 */
@ApplicationScoped
public class SiteUtils
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(SiteUtils.class);
	

	/** The context path */
	private String contextPath;

	/** 
	 * Make sure that the list servlet path is the first thing after the domain.
	 */
	public boolean isValidListUrl(URL url) 
	{
		return url.getPath().startsWith(getListServletPath()); 
	}
	
	/** 
	 * Make sure that the list servlet path is the first thing after the domain.
	 */
	public boolean isValidListUrl(String url) 
	{
		try
		{
			return isValidListUrl(new URL(url));
		}
		catch (MalformedURLException ex)
		{
			return false;
		}
	}

	public String getListServletPath()
	{
		return getContextPath() + "list/";
	}
	
	/** @param contextPath is the contextPath for this webapp/container */
	public void setContextPath(String contextPath)
	{
		if (!contextPath.startsWith("/"))
			contextPath = "/" + contextPath;
		
		if (!contextPath.endsWith("/"))
			contextPath =  contextPath + "/";
		
		this.contextPath = contextPath;
	}
	
	/** @return the contextPath for this webapp/container */
	public String getContextPath() { return this.contextPath; }
}