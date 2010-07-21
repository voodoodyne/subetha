/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Gets the roles associated with a mailing list.  Model becomes
 * a ListRoles.
 * 
 * @author Jeff Schnitzer
 */
public class GetRoles extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(GetRoles.class);

	/** */
	@Getter @Setter Long listId;

	/** */
	public void execute() throws Exception
	{
		this.getCtx().setModel(Backend.instance().getListMgr().getRoles(this.listId));
	}
}
