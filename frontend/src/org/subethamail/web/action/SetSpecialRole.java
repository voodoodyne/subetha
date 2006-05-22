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
 * Sets the defaultRole and/or the anonymousRole for a list.
 * 
 * @author Jeff Schnitzer
 */
public class SetSpecialRole extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(SetSpecialRole.class);

	/** */
	@Property Long listId;
	@Property Long defaultRoleId;
	@Property Long anonymousRoleId;

	/** */
	public void execute() throws Exception
	{
		if (this.defaultRoleId != null)
			Backend.instance().getListMgr().setDefaultRole(this.listId, this.defaultRoleId);
		
		if (this.anonymousRoleId != null)
			Backend.instance().getListMgr().setAnonymousRole(this.listId, this.anonymousRoleId);
	}
}
