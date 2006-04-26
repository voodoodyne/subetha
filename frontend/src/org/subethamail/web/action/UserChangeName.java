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
import org.tagonist.propertize.Property;

/**
 * Changes a user's name.
 * 
 * @author Jon Stevens
 */
public class UserChangeName extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(UserChangeName.class);
	
	/** */
	@Length(max=Validator.MAX_PERSON_NAME)
	@Property String name = "";

	/** */
	public void execute() throws Exception
	{
		Backend.instance().getAccountMgr().setName(this.name);
	}
}
