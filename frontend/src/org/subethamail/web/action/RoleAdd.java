/*
 * $Id: Login.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Login.java $
 */

package org.subethamail.web.action;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.Length;
import org.subethamail.common.Permission;
import org.subethamail.common.valid.Validator;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Adds a role to a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public class RoleAdd extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(RoleAdd.class);
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Property Long listId;
		
		/** */
		@Length(min=1, max=Validator.MAX_ROLE_NAME)
		@Property String name = "";
		
		/** */
		@Property String[] permissions;
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
		
		Set<Permission> perms = new HashSet<Permission>();
		
		if (model.permissions != null)
			for (String permString: model.permissions)
				perms.add(Permission.valueOf(permString));
		
		if (model.getErrors().isEmpty())
		{
			Backend.instance().getListMgr().addRole(model.listId, model.name, perms);
		}
	}
	
}
