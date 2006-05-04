/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Remove a filter from a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public class FilterDelete extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(FilterDelete.class);

	/** */
	@Property Long listId;
	
	/** */
	@Property String className;
	
	/** */
	public void execute() throws Exception
	{
		Backend.instance().getListMgr().disableFilter(this.listId, this.className);
	}
}
