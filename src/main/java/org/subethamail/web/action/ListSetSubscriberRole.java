
/*
 * $Id: .java 310 2006-05-09 19:39:28Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/action/EmailAdd.java $
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;

/**
 * Removes a Subscriber from a list.
 * 
 * @author Scott Hernandez
 */
public class ListSetSubscriberRole extends AuthRequired 
{
	/** */
	@Getter @Setter Long personId;
	@Getter @Setter Long listId;
	@Getter @Setter Long roleId;
	
	/** */
	public void authExecute() throws Exception
	{
		Backend.instance().getListMgr().setSubscriptionRole(this.listId, this.personId, this.roleId);
	}
}

