/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.Email;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;

/**
 * Adds an email address to an existing account.  Actually this
 * results in an email to the address which must be confirmed.
 * 
 * @author Jeff Schnitzer
 */
public class EmailAdd extends AuthRequired 
{
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Email
		@Getter @Setter String email = "";
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
