/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.lists.i.RoleData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Gets a single subscription from a list, suitable for editing.  The model
 * becomes a SaveSubscription.Model.
 * 
 * @author Jeff Schnitzer
 */
public class GetSubscriptionForEdit extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(GetMyListRelationship.class);

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
		
		RoleData role = model.data.getRole();
		
		model.roleId = role == null ? null : role.getId();
	}
}
