/*
 * $Id: GetMessage.java 263 2006-05-04 20:58:25Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/action/GetMessage.java $
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;

/**
 * Resends the message in email.  The address must belong
 * to the currently logged in user.
 * 
 * @author Scott Hernandez
 * @author Jeff Schnitzer
 */
public class ResendMessage extends AuthRequired 
{
	@Getter @Setter Long msgId;
	@Getter @Setter String email;
	
	/** */
	public void authExecute() throws Exception
	{
		Backend.instance().getArchiver().sendTo(this.msgId, this.email);
	}
}
