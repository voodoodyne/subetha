/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.admin.i.ConfigData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Configures settings specific to the site.
 * 
 * @author Jon Stevens
 */
public class GetSiteSettings extends AuthAction
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(GetSiteSettings.class);

	public static class Model extends ErrorMapModel
	{
		/** */
		@Property List<ConfigData> configData;
		@Property String siteUrl;
	}

	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();
		
		model.configData = Backend.instance().getAdmin().getSiteConfig();
		
		for (ConfigData cd : model.configData)
		{
			// FIXME: get key string correctly.
			if (cd.getId().equals("siteUrl"))
			{
				model.siteUrl = cd.getValue().toString();
				break;
			}
		}
	}
}
