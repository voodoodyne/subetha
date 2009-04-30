/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.tagonist.propertize.Property;

/**
 * Subscribes an existing user to a mailing list, or changes the
 * address to which delivery is enabled.  This object remains the
 * model.  Check for the held property.
 * 
 * @author Jeff Schnitzer
 */
public class SubscribeMe extends AuthRequired 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(SubscribeMe.class);
	
	/** */
	@Property Long listId;
	@Property String deliverTo = "";
	@Property boolean held;
	
	/** */
	public void authExecute() throws Exception
	{
		if (this.deliverTo.length() == 0)
			this.deliverTo = null;
		
		SubscribeResult result = Backend.instance().getAccountMgr().subscribeMe(this.listId, this.deliverTo);
		
		if (result == SubscribeResult.HELD)
			this.held = true;
	}
	
}
