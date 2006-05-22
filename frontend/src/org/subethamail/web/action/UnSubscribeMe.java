/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.tagonist.propertize.Property;

/**
 * UnSubscribes an existing user from a mailing list.
 * 
 * @author Jon Stevens
 */
public class UnSubscribeMe extends AuthRequired 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(UnSubscribeMe.class);
	
	/** */
	@Property Long listId;
	
	/** */
	public void authExecute() throws Exception
	{
		Backend.instance().getAccountMgr().unsubscribeMe(this.listId);
	}	
}
