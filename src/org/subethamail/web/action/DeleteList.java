/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;

/**
 * Deletes a mailing list
 * 
 * @author Jeff Schnitzer
 */
public class DeleteList extends AuthRequired 
{
	/** */
	@Getter @Setter Long listId;
	@Getter @Setter String password;
	@Getter @Setter boolean wrongPassword;
	
	/** */
	public void authExecute() throws Exception
	{
		this.wrongPassword = !Backend.instance().getAdmin().deleteList(this.listId, this.password);
	}
}
