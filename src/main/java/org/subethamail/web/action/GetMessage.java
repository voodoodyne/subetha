/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Gets detail information about one piece of mail.  Model becomes a MailData.
 * 
 * @author Jeff Schnitzer
 */
public class GetMessage extends AuthAction 
{
	/** */
	@Getter @Setter Long msgId;

	/** */
	public void execute() throws Exception
	{
		this.getCtx().setModel(Backend.instance().getArchiver().getMail(this.msgId));
	}
}
