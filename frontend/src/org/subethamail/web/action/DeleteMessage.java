/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Deletes a message from a mailing list
 * 
 * @author Jeff Schnitzer
 */
public class DeleteMessage extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(DeleteMessage.class);
	
	/** */
	@Property Long listId;
	@Property Long msgId;
	
	/** */
	public void execute() throws Exception
	{
		this.listId = Backend.instance().getArchiver().deleteMail(this.msgId);
	}
}
