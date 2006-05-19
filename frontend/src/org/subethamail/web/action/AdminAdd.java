/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.PersonData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Adds a site admin.
 * 
 * @author Jon Stevens
 */
public class AdminAdd extends AuthRequired 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(AdminAdd.class);

	public class Model extends ErrorMapModel
	{
		@Property String email;
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
		
		if (log.isDebugEnabled())
			log.debug("Adding site admin: " + model.email);

		List<PersonData> admins = Backend.instance().getAdmin().findSiteAdmins();
		try
		{
			Backend.instance().getAccountMgr().setSiteAdmin(model.email, true);
		}
		catch (NotFoundException nfe)
		{
			model.setError("email", "Could not find email address.");
		}
	}
}
