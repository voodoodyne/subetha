/*
 * $Id: Login.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Login.java $
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.acct.i.AuthCredentials;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Confirms the adding of an email address to an existing account.
 * 
 * @author Jeff Schnitzer
 */
public class EmailAddConfirm extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(EmailAddConfirm.class);
	
	/** */
	@Property String token = "";
	@Property boolean badTokenError;

	/** */
	public void execute() throws Exception
	{
		try
		{
			AuthCredentials creds = Backend.instance().getAccountMgr().addEmail(this.token);
			
			if (!this.isLoggedIn())
				this.login(creds.getEmail(), creds.getPassword());
		}
		catch (BadTokenException ex)
		{
			this.badTokenError = true;
		}
	}
	
}
