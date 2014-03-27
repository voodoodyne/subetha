/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.subethamail.core.lists.i.ListData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Gets the data for a list.  Model becomes a ListData.
 * 
 * @author Jeff Schnitzer
 */
public class GetList extends AuthAction 
{
	/** */
	@Getter @Setter Long listId;
	
	/** */
	public void execute() throws Exception
	{
		ListData data = Backend.instance().getListMgr().getList(this.listId);
		this.getCtx().setModel(data);
	}
	
}
