/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Gets the data for a list.  Model becomes a ListData.
 * 
 * @author Jeff Schnitzer
 */
public class GetList extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(GetList.class);
	
	/** */
	@Property Long listId;
	
	/** */
	public void execute() throws Exception
	{
		ListData data = Backend.instance().getListMgr().getList(this.listId);
		this.getCtx().setModel(data);
	}
	
}
