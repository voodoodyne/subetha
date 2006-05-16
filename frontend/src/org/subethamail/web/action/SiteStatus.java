/*
 * $Id: SaveListSettings.java 318 2006-05-10 04:58:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/action/SaveListSettings.java $
 */

package org.subethamail.web.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Infomation about the site.
 * 
 * This includes system encoding, number of lists, users, etc.
 * 
 * @author Scott Hernandez
 */
public class SiteStatus extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(SiteStatus.class);

	@Property
	String systemEncoding = System.getProperty("file.encoding");
	
	@Property
	List<ListData> lists = Backend.instance().getAdmin().getAllLists();
	
	/** */
	public void execute() throws Exception
	{
	}
	
}
