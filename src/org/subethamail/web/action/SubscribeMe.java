/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SubscribeMe extends AuthRequired 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(SubscribeMe.class);
	
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
