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
 * Sets the defaultRole and/or the anonymousRole for a list.
 * 
 * @author Jeff Schnitzer
 */
public class SetSpecialRole extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(SetSpecialRole.class);

	/** */
	@Getter @Setter Long listId;
	@Getter @Setter Long defaultRoleId;
	@Getter @Setter Long anonymousRoleId;

	/** */
	public void execute() throws Exception
	{
		if (this.defaultRoleId != null)
			Backend.instance().getListMgr().setDefaultRole(this.listId, this.defaultRoleId);
		
		if (this.anonymousRoleId != null)
			Backend.instance().getListMgr().setAnonymousRole(this.listId, this.anonymousRoleId);
	}
}
