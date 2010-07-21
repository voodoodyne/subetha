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

import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.admin.i.DuplicateListDataException;
import org.subethamail.core.admin.i.InvalidListDataException;
import org.subethamail.entity.i.Validator;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;

/**
 * Changes the settings of a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public class SaveListSettings extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(SaveListSettings.class);
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Getter @Setter Long listId;
		
		/** */
		@Length(min=1, max=Validator.MAX_LIST_NAME)
		@Getter @Setter String name = "";
		
		/** */
		@Length(max=Validator.MAX_LIST_DESCRIPTION)
		@Getter @Setter String description = "";

		/** */
		@Length(max=Validator.MAX_LIST_WELCOME_MESSAGE)
		@Getter @Setter String welcomeMessage = "";

		/** */
		@Length(max=Validator.MAX_LIST_URL)
		@Getter @Setter String url;	// start null

		/** */
		@Length(max=Validator.MAX_LIST_EMAIL)
		@Getter @Setter String email;	// start null

		/** */
		@Getter @Setter boolean holdSubs;
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
				Backend.instance().getListMgr()
					.setList(model.listId, model.name, model.description, model.welcomeMessage, model.holdSubs);
		}
	}
	
}
