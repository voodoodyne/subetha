/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.net.MalformedURLException;
import java.net.URL;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Changes the site settings.
 * 
 * @author Jon Stevens
 */
public class SiteStatusSave extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(SiteStatusSave.class);

	public static class Model extends ErrorMapModel
	{
		@Property String postmasterEmail;
		@Property String defaultSiteUrl;
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
		
		// Check the InternetAddress
		InternetAddress address = null;
		try
		{
			address = new InternetAddress(model.postmasterEmail);
			address.validate();
		}
		catch (AddressException ex)
		{
			model.setError("postmasterEmail", ex.getMessage());
			return;
		}

		// Check the URL
		URL url = null;
		try
		{
			url = new URL(model.defaultSiteUrl);
		}
		catch (MalformedURLException ex)
		{
			model.setError("defaultSiteUrl", ex.getMessage());
			return;
		}

		Admin admin = Backend.instance().getAdmin();
		admin.setPostmaster(address);
		admin.setDefaultSiteUrl(url);
	}
	
}
