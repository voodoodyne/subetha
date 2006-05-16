
/*
 * $Id: .java 310 2006-05-09 19:39:28Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/action/EmailAdd.java $
 */

package org.subethamail.web.action;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.tagonist.propertize.Property;

/**
 * Removes a Subscriber from a list.
 * 
 * @author Scott Hernandez
 */
public class ListSetSubscriberRole extends AuthRequired 
{
	/** */
	@Property Long personId;
	@Property Long listId;
	@Property Long roleId;
	
	/** */
	public void authExecute() throws Exception
	{
		Backend.instance().getListMgr().setSubscriberRole(this.listId, this.personId, this.roleId);
	}
}

