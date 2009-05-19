/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.SaveListSettings.Model;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Pre-populates a model for SaveListSettings.
 * 
 * @author Jeff Schnitzer
 */
public class GetListSettings extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(GetListSettings.class);
	
	/** */
	@Property Long listId;
	
	/** */
	public void execute() throws Exception
	{
		Model model = new Model();
		
		ListData data = Backend.instance().getListMgr().getList(this.listId);
		
		model.listId = data.getId();
		model.name = data.getName();
		model.description = data.getDescription();
		model.welcomeMessage = data.getWelcomeMessage();
		model.url = data.getUrl();
		model.email = data.getEmail();
		model.holdSubs = data.isSubscriptionHeld();
		
		this.getCtx().setModel(model);
	}
	
}
