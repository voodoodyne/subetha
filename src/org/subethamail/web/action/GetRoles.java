/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Gets the roles associated with a mailing list.  Model becomes
 * a ListRoles.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class GetRoles extends AuthAction 
{
	/** */
	@Getter @Setter Long listId;

	/** */
	public void execute() throws Exception
	{
		this.getCtx().setModel(Backend.instance().getListMgr().getRoles(this.listId));
	}
}
