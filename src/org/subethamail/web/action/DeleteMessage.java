/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final static Logger log = LoggerFactory.getLogger(DeleteMessage.class);
	
	/** */
	@Property Long listId;
	@Property Long msgId;
	
	/** */
	public void execute() throws Exception
	{
		this.listId = Backend.instance().getArchiver().deleteMail(this.msgId);
	}
}
