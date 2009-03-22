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
 * Deletes a mailing list
 * 
 * @author Jeff Schnitzer
 */
public class DeleteList extends AuthRequired 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(DeleteList.class);
	
	/** */
	@Property Long listId;
	@Property String password;
	@Property boolean wrongPassword;
	
	/** */
	public void authExecute() throws Exception
	{
		this.wrongPassword = !Backend.instance().getAdmin().deleteList(this.listId, this.password);
	}
}
