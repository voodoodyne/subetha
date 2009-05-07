/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.hibernate.validator.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Adds a site admin.
 *
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
public class AdminAdd extends AuthRequired
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(AdminAdd.class);

	public class Model extends ErrorMapModel
	{
		@Email
		@Property String email;
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

		if (log.isDebugEnabled())
			log.debug("Adding site admin: " + model.email);

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
