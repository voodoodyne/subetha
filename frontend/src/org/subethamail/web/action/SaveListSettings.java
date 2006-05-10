/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.Length;
import org.subethamail.common.valid.Validator;
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
		@Property String url = "";

		/** */
		@Length(max=Validator.MAX_LIST_EMAIL)
		@Property String email = "";
		
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
			Backend.instance().getListMgr().setList(model.listId, model.name, model.description, model.url, model.email, model.holdSubs);
		}
	}
	
}
