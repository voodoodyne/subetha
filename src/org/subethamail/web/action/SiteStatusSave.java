/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.net.MalformedURLException;
import java.net.URL;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;

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
		@Getter @Setter String postmasterEmail;
		@Getter @Setter String defaultSiteUrl;
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
