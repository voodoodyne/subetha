/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;

/**
 * Sends a password reminder back to the email owner.
 * 
 * @author Jeff Schnitzer
 */
public class ForgotPassword extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(ForgotPassword.class);
	
	/** */
	public static class Model extends ErrorMapModel
	{
		@Email
		@Getter @Setter String email;
	}
	
	/** */
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}
	
	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();
		
		model.validate();
		
		if (model.getErrors().isEmpty())
		{
			try
			{
				Backend.instance().getAccountMgr().forgotPassword(model.email);
			}
			catch (NotFoundException ex)
			{
				model.setError("email", "Unknown email address");
			}
		}
	}
	
}
