/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Remove a filter from a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public class FilterDelete extends AuthAction 
{
	/** */
	@Getter @Setter Long listId;
	
	/** */
	@Getter @Setter String className;
	
	/** */
	public void execute() throws Exception
	{
		Backend.instance().getListMgr().disableFilter(this.listId, this.className);
	}
}
