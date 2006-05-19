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
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.PaginateModel;
import org.tagonist.propertize.Property;

/**
 * Gets all lists (paginated).
 * 
 * @author Jon Stevens
 */
public class GetSiteAdministrators extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(GetSiteAdministrators.class);
	
	public static class Model extends PaginateModel
	{
		/** */
		@Property List<PersonData> personData;
	}
	
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();
		
		model.personData = Backend.instance().getAdmin().findSiteAdmins();
	}	
}
