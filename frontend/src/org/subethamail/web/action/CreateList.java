/*
 * $Id: Login.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Login.java $
 */

package org.subethamail.web.action;

import java.net.MalformedURLException;
import java.net.URL;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.valid.Validator;
import org.subethamail.core.admin.i.CreateMailingListException;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;
import org.subethamail.web.model.StringConstraint;

/**
 * Creates a mailing list.  The model starts and remains a CreateList.Model.
 * 
 * @author Jeff Schnitzer
 */
public class CreateList extends AuthRequired 
{
	/** */
	private static Log log = LogFactory.getLog(CreateList.class);
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** Will be populated if exeuction is successful */
		public Long id;
		public Long getId() { return this.id; }
		public void setId(Long value) { this.id = value; }
		
		/** */
		@StringConstraint(required=true, maxLength=Validator.MAX_LIST_NAME)
		public String name = "";
		public String getName() { return this.name; }
		public void setName(String value) { this.name = value; }
	
		/** */
		@StringConstraint(maxLength=Validator.MAX_LIST_DESCRIPTION)
		public String description = "";
		public String getDescription() { return this.description; }
		public void setDescription(String value) { this.description = value; }
	
		/** */
		@StringConstraint(maxLength=Validator.MAX_LIST_ADDRESS)
		public String address = "";
		public String getAddress() { return this.address; }
		public void setAddress(String value) { this.address = value; }
	
		/** */
		@StringConstraint(maxLength=Validator.MAX_LIST_URL)
		public String url = "";
		public String getUrl() { return this.url; }
		public void setUrl(String value) { this.url = value; }

		/** */
		public String owners = "";
		public String getOwners() { return this.owners; }
		public void setOwners(String value) { this.owners = value; }
		
		/** */
		public String blueprint = "";
		public String getBlueprint() { return this.blueprint; }
		public void setBlueprint(String value) { this.blueprint = value; }
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

		// Basic validation
		model.validate();
		
		// Check the URL
		URL url = null;
		try
		{
			url = new URL(model.getUrl());
		}
		catch (MalformedURLException ex)
		{
			model.setError("url", ex.getMessage());
		}
		
		// Check the address
		InternetAddress listAddress = null;
		try
		{
			listAddress = new InternetAddress(model.getAddress());
			listAddress.validate();
			listAddress.setPersonal(model.getName());
		}
		catch (AddressException ex)
		{
			model.setError("address", ex.getMessage());
		}
		
		// Check the list owners
		InternetAddress[] owners = null;
		try
		{
			owners = InternetAddress.parse(model.getOwners());
			for (InternetAddress owner: owners)
				owner.validate();
		}
		catch (AddressException ex)
		{
			model.setError("owners", ex.getMessage());
		}
		
		// Maybe we can proceed?
		if (model.getErrors().isEmpty())
		{
			try
			{
				Long id = Backend.instance().getListWizard().createMailingList(listAddress, url, model.getDescription(), owners, model.getBlueprint());
				model.setId(id);
			}
			catch (CreateMailingListException ex)
			{
				if (ex.isAddressTaken())
					model.setError("address", "That address is already in use");
				
				if (ex.isUrlTaken())
					model.setError("url", "That url is already in use");
			}
		}
	}
	
}
