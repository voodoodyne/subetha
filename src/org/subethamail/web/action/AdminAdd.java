/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.hibernate.validator.constraints.Email;
import org.subethamail.common.NotFoundException;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;

/**
 * Adds a site admin.
 *
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
@Log
public class AdminAdd extends AuthRequired
{
	public class Model extends ErrorMapModel
	{
		@Getter @Setter
		@Email String email;
	}

	/** */
	@Override
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/** */
	@Override
	public void authExecute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();

		if (log.isLoggable(Level.FINE))
		    log.log(Level.FINE,"Adding site admin: {0}", model.email);

		model.validate();

		if (model.getErrors().isEmpty())
		{
			try
			{
				Backend.instance().getAdmin().setSiteAdminByEmail(model.email, true);
			}
			catch (NotFoundException nfe)
			{
				model.setError("email", "Could not find email address.");
			}
		}
	}
}
