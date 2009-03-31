/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import javax.inject.Current;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.web.action.auth.AuthRequired;
import org.tagonist.propertize.Property;

/**
 * Deletes a mailing list
 * 
 * @author Jeff Schnitzer
 */
public class DeleteList extends AuthRequired 
{
	@Current Admin admin;
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
		this.wrongPassword = admin.deleteList(this.listId, this.password);
	}
}
