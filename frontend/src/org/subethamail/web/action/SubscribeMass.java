/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.MailUtils;
import org.subethamail.core.lists.i.MassSubscribeType;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Subscribes many users at once.
 * 
 * @author Jeff Schnitzer
 */
public class SubscribeMass extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(SubscribeMass.class);
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Property Long listId;
		@Property String how = "";
		@Property String emails = "";
		@Property Set<String> addedEmails;
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
