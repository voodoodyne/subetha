/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final static Logger log = LoggerFactory.getLogger(SiteStatusSave.class);

	public static class Model extends ErrorMapModel
	{
		@Property String postmasterEmail;
		@Property String defaultSiteUrl;
		@Property String fallthroughHost;
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

		// Check the fallthrough host
		model.fallthroughHost = model.fallthroughHost.trim();
		if (model.fallthroughHost.length() > 0)
		{
			String[] fallthroughSplit = model.fallthroughHost.split(":");
			
			try
			{
				InetAddress.getByName(fallthroughSplit[0]);
			}
			catch (UnknownHostException ex)
			{
				model.setError("fallthroughHost", "Unknown host");
				return;
			}
			
			if (fallthroughSplit.length > 1)
			{
				try
				{
					Integer.parseInt(fallthroughSplit[1]);
				}
				catch (NumberFormatException ex)
				{
					model.setError("fallthroughHost", "Invalid port #");
					return;
				}
			}
		}
		
		Admin admin = Backend.instance().getAdmin();
		admin.setPostmaster(address);
		admin.setDefaultSiteUrl(url);
		
		if (model.fallthroughHost.length() > 0)
			admin.setFallthroughHost(model.fallthroughHost);
		else
			admin.setFallthroughHost(null);
	}
	
}
