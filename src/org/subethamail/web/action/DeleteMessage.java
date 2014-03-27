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
 * Deletes a message from a mailing list
 * 
 * @author Jeff Schnitzer
 */
public class DeleteMessage extends AuthAction 
{
	/** */
	@Getter @Setter Long listId;
	@Getter @Setter Long msgId;
	
	/** */
	public void execute() throws Exception
	{
		this.listId = Backend.instance().getArchiver().deleteMail(this.msgId);
	}
}
