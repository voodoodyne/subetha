/*
 * $Id: Login.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Login.java $
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.lists.i.Filters;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Gets the filters associated with a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public class GetFilters extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(GetFilters.class);

	/** */
	@Property Long listId;

	/** */
	public void execute() throws Exception
	{
		Filters filters = Backend.instance().getListMgr().getFilters(this.listId);
		
		this.getCtx().setModel(filters);
	}
}
