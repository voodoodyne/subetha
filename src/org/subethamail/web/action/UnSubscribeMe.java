/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;

/**
 * UnSubscribes an existing user from a mailing list.
 * 
 * @author Jon Stevens
 */
@Log
public class UnSubscribeMe extends AuthRequired 
{
	/** */
	@Getter @Setter Long listId;
	
	/** */
	public void authExecute() throws Exception
	{
		Backend.instance().getAccountMgr().unsubscribeMe(this.listId);
	}	
}
