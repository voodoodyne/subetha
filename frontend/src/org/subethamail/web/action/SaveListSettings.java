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
import org.hibernate.validator.Length;
import org.subethamail.common.valid.Validator;
import org.subethamail.core.admin.i.DuplicateListDataException;
import org.subethamail.core.admin.i.InvalidListDataException;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Changes the settings of a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public class SaveListSettings extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(SaveListSettings.class);
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Property Long listId;
		
		/** */
		@Length(min=1, max=Validator.MAX_LIST_NAME)
		@Property String name = "";
		
		/** */
		@Length(max=Validator.MAX_LIST_DESCRIPTION)
		@Property String description = "";

		/** */
		@Length(max=Validator.MAX_LIST_URL)
		@Property String url;	// start null

		/** */
		@Length(max=Validator.MAX_LIST_EMAIL)
		@Property String email;	// start null
		
		/** */
		@Property boolean holdSubs;
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
		
		model.validate();
		
		if (model.getErrors().isEmpty())
		{
			// Do this part first 'cause it could fail
			if (model.email != null || model.url != null)
			{
				// Check the URL
				URL url = null;
				try
				{
					url = new URL(model.url);
				}
				catch (MalformedURLException ex)
				{
					model.setError("url", ex.getMessage());
				}
				
				// Check the address
				InternetAddress listAddress = null;
				try
				{
					listAddress = new InternetAddress(model.email);
					listAddress.validate();
					listAddress.setPersonal(model.name);
				}
				catch (AddressException ex)
				{
					model.setError("email", ex.getMessage());
				}
				
				if (model.getErrors().isEmpty())
				{
					try
					{
						Backend.instance().getAdmin().setListAddresses(model.listId, listAddress, url);
					}
					catch (InvalidListDataException ex)
					{
						if (ex.isOwnerAddress())
							model.setError("email", "Addresses cannot end with -owner");
						
						if (ex.isVerpAddress())
							model.setError("email", "Conflicts with the VERP address format");
					}
					catch (DuplicateListDataException ex)
					{
						if (ex.isAddressTaken())
							model.setError("email", "That address is already in use");
						
						if (ex.isUrlTaken())
							model.setError("url", "That url is already in use");
					}
				}
			}
			
			if (model.getErrors().isEmpty())
				Backend.instance().getListMgr().setList(model.listId, model.name, model.description, model.holdSubs);
		}
	}
	
}
