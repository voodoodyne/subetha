/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;

/**
 * Subscribes an existing user to a mailing list, or changes the
 * address to which delivery is enabled.  This object remains the
 * model.  Check for the held property.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class SubscribeMe extends AuthRequired 
{
	/** */
	@Getter @Setter Long listId;
	@Getter @Setter String deliverTo = "";
	@Getter @Setter boolean held;
	
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
