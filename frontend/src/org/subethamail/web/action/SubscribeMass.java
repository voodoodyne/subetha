/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
		@Property String how = "INVITE";
		@Property String emails = "";
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
			InternetAddress[] addresses = InternetAddress.parse(model.emails);
			
			Backend.instance().getListMgr().massSubscribe(model.listId, how, addresses);
		}
		catch (AddressException ex)
		{
			model.setError("emails", ex.getMessage());
		}
	}
	
}
