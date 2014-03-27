/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.extern.java.Log;

import org.subethamail.core.lists.i.RoleData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Gets a single subscription from a list, suitable for editing.  The model
 * becomes a SaveSubscription.Model.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class GetSubscriptionForEdit extends AuthAction 
{
	public void initialize()
	{
		this.getCtx().setModel(new SaveSubscription.Model());
	}

	/** */
	public void execute() throws Exception
	{
		SaveSubscription.Model model = (SaveSubscription.Model)this.getCtx().getModel();
		
		model.data = Backend.instance().getListMgr().getSubscription(model.listId, model.personId);
		
		model.deliverTo = model.data.getDeliverTo();
		model.note = model.data.getNote();
		model.name = model.data.getName();
		
		RoleData role = model.data.getRole();
		
		model.roleId = role == null ? null : role.getId();
	}
}
