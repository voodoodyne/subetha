/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.acct.i.PersonData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Removes a site admin.
 * 
 * @author Jon Stevens
 */
public class AdminRemove extends AuthRequired 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(AdminRemove.class);

	public class Model extends ErrorMapModel
	{
		@Property Long id;
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
			log.debug("Removing site admin person id: " + model.id);
		
		List<PersonData> admins = Backend.instance().getAdmin().findSiteAdmins();
		if (admins.size() == 1)
		{
			model.setError("remove", "Remove failed: Need to have at least one administrator.");
		}
		else
		{
			Backend.instance().getAccountMgr().setSiteAdmin(model.id, false);
		}
	}	
}
