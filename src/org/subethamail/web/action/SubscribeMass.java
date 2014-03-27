/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.subethamail.common.MailUtils;
import org.subethamail.core.lists.i.MassSubscribeType;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;

/**
 * Subscribes many users at once.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class SubscribeMass extends AuthAction 
{
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Getter @Setter Long listId;
		@Getter @Setter String how = "";
		@Getter @Setter String emails = "";
		@Getter @Setter Set<String> addedEmails;
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
		
		MassSubscribeType how = MassSubscribeType.valueOf(model.how);
		
		try
		{
			InternetAddress[] addresses = MailUtils.parseMassSubscribe(model.emails);

			Backend.instance().getListMgr().massSubscribe(model.listId, how, addresses);

			model.addedEmails = new HashSet<String>(addresses.length);
			for (InternetAddress address : addresses)
			{
				model.addedEmails.add(address.getAddress());
			}
		}
		catch (AddressException ex)
		{
			model.setError("emails", ex.getMessage());
		}
	}	
}
