/*
 * $Id: MailUtils.java 378 2006-05-17 00:11:14Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/common/src/org/subethamail/common/MailUtils.java $
 */

package org.subethamail.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Provides Subetha Site related helper methods
 * 
 * @author Scott Hernandez
 */
public class SiteUtils
{
	/** */
	private static Log log = LogFactory.getLog(SiteUtils.class);

	public static final String URL_PATH_LIST = "/se/list";
	
	public static boolean isValidListUrl(String url) 
	{
		int pos = url.indexOf(URL_PATH_LIST);
		if (pos < 0) return false;
		else return true;
	}
}
