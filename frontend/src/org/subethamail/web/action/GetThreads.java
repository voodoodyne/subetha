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
 * Gets one page of an archive.  Model becomes a List<MessageSummary>.
 * 
 * @author Jeff Schnitzer
 */
public class GetThreads extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(GetThreads.class);

	/** */
	@Property Long listId;

	/** */
	public void execute() throws Exception
	{
		this.getCtx().setModel(Backend.instance().getArchiver().getThreads(this.listId));
	}
}
