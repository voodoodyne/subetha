/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.Email;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Adds an email address to an existing account.  Actually this
 * results in an email to the address which must be confirmed.
 * 
 * @author Jeff Schnitzer
 */
public class EmailAdd extends AuthRequired 
{
	/** */
	private static Log log = LogFactory.getLog(EmailAdd.class);
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Email
		@Property String email = "";
	}
	
	/** */
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}
	
	/** */
	public void authExecute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();
		
		model.validate();
		
		if (model.getErrors().isEmpty())
		{
			try
			{
				Backend.instance().getAccountMgr().addEmailRequest(model.email);
			}
			catch (RuntimeException re)
			{
				Throwable e = re.getCause();
				if (e != null && e.getCause() instanceof javax.mail.SendFailedException)
				{
					model.setError("email", e.getCause().getMessage());
				}
			}
		}
	}
}
