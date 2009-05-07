/*
 * $Id: MailUtils.java 378 2006-05-17 00:11:14Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/common/src/org/subethamail/common/MailUtils.java $
 */

package org.subethamail.common;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Provides Subetha Site related helper methods
 * 
 * @author Scott Hernandez
 */
public class SiteUtils
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(SiteUtils.class);

	/** Base context path for the subetha webapp */
	public static final String WEBAPP_CONTEXT_PATH = "/se/";
	
	/** Path to the list servlet */
	public static final String LIST_SERVLET_PATH = WEBAPP_CONTEXT_PATH + "list/";

	/** default constructor prevents util class from being created. */
	private SiteUtils() {}
	
	/** 
	 * Make sure that the list servlet path is the first thing after the domain.
	 */
	public static boolean isValidListUrl(URL url) 
	{
		return url.getPath().startsWith(LIST_SERVLET_PATH); 
	}
	
	/** 
	 * Make sure that the list servlet path is the first thing after the domain.
	 */
	public static boolean isValidListUrl(String url) 
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
}
