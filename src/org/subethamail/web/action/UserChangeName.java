/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.hibernate.validator.constraints.Length;
import org.subethamail.entity.i.Validator;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;

/**
 * Changes a user's name.
 * 
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
@Log
public class UserChangeName extends AuthRequired 
{
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Length(max=Validator.MAX_PERSON_NAME)
		@Getter @Setter String name = "";
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
		
		Backend.instance().getAccountMgr().setName(model.name);
	}
}
