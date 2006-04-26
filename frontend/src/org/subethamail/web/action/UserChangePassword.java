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
import org.subethamail.web.action.CreateList.Model;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Changes a user's password.
 * 
 * @author Jon Stevens
 */
public class UserChangePassword extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(UserChangePassword.class);
	
	/** */
	@Length(min=Validator.MIN_PERSON_PASSWORD, max=Validator.MAX_PERSON_PASSWORD)
	@Property String password = "";

	@Length(min=Validator.MIN_PERSON_PASSWORD, max=Validator.MAX_PERSON_PASSWORD)
	@Property String confirm = "";

	/** */
	public void execute() throws Exception
	{
		if (password.equals(confirm))
		{
			Backend.instance().getAccountMgr().setPassword(this.password);
		}
		else
		{
			Model model = (Model)this.getCtx().getModel();
			model.setError("password", "The passwords do not match.");
		}
	}
}
