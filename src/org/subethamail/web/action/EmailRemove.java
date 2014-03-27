/*
 * $Id: EmailAdd.java 310 2006-05-09 19:39:28Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/action/EmailAdd.java $
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;

/**
 * Adds an email address to an existing account.  Actually this
 * results in an email to the address which must be confirmed.
 * 
 * @author Scott Hernandez
 */
public class EmailRemove extends AuthRequired 
{
	@Getter @Setter String email = "";

	/** */
	public void authExecute() throws Exception
	{
		Backend.instance().getAccountMgr().removeEmail(this.email);
	}
}
