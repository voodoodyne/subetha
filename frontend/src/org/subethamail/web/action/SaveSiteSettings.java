/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.Converter;
import org.subethamail.core.admin.i.ConfigData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Changes the site settings.
 * 
 * @author Jon Stevens
 */
public class SaveSiteSettings extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(SaveSiteSettings.class);
		
	/** */
	public void execute() throws Exception
	{
		this.getCtx().setModel(this);
		
		List<ConfigData> configData = Backend.instance().getAdmin().getSiteConfig();
		for (ConfigData config : configData)
		{
			String value = this.getCtx().getRequest().getParameter(config.getId());
			if (value != null)
			{
				config.setValue(Converter.valueOf(value, config.getType()));
				Backend.instance().getAdmin().saveConfig(config);
			}
		}
	}
	
}
