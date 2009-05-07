/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.hibernate.validator.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.lists.i.SubscriberData;
import org.subethamail.entity.i.Validator;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Saves changes to a subscriber.
 * 
 * @author Jeff Schnitzer
 */
public class SaveSubscription extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(GetMyListRelationship.class);

	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Property Long listId;
		@Property Long personId;
		
		/** starts as null, will be set if it was part of the save form UI */
		@Property String deliverTo;
		@Property Long roleId;
		
		@Length(max=Validator.MAX_SUBSCRIPTION_NOTE)
		@Property String note;
		
		/** Populated initially and when validation fails */
		@Property SubscriberData data;
	}

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
			// We know that parts were set when they are not null
			
			if (model.roleId != null)
			{
				Backend.instance().getListMgr().setSubscriptionRole(model.listId, model.personId, model.roleId);
			}
			
			if (model.deliverTo != null)
			{
				// If it was the empty value, we really want it to be null
				String actualDeliverTo = model.deliverTo.length() == 0 ? null : model.deliverTo;
				Backend.instance().getListMgr().setSubscriptionDelivery(model.listId, model.personId, actualDeliverTo);
			}
			
			if (model.note != null)
			{
				Backend.instance().getListMgr().setSubscriptionNote(model.listId, model.personId, model.note);
			}
		}
		else
		{
			model.data = Backend.instance().getListMgr().getSubscription(model.listId, model.personId);
		}
	}
}
