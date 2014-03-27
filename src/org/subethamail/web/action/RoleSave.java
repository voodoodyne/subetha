/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.hibernate.validator.constraints.Length;
import org.subethamail.entity.i.Permission;
import org.subethamail.entity.i.Validator;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;

/**
 * Adds a role to a mailing list or saves an existing role,
 * depending on whether listId or roleId has been set.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class RoleSave extends AuthAction 
{
	/** */
	public static class Model extends ErrorMapModel
	{
		/** If this is not null, create a new role */
		@Getter @Setter Long listId;
		
		/** If this is not null, save an existing role */
		@Getter @Setter Long roleId;
		
		/** */
		@Length(min=1, max=Validator.MAX_ROLE_NAME)
		@Getter @Setter String name = "";
		
		/** */
		@Getter String[] permissions;
		
		/**
		 * This is mildly hackish; the presentation tier needs a Set
		 * interface so that it can determine if checkboxes should be
		 * checked.  We also need it internally, so we always keep
		 * it synced to permissions.
		 */
		@Getter @Setter Set<Permission> realPermissions;
		
		/**
		 * Whenever permissions is set, set realPermissions too.
		 * This method gets called by the bean populator.
		 */
		public void setPermissions(String[] value)
		{
			this.permissions = value;
			
			this.realPermissions = new HashSet<Permission>();
			
			if (this.permissions != null)
				for (String permString: this.permissions)
					this.realPermissions.add(Permission.valueOf(permString));
			
		}
	}
	
	/** */
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}
	
	/** */
	@SuppressWarnings("unchecked")
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();
		
		model.validate();
		
		Set<Permission> perms = (model.realPermissions == null) ? Collections.EMPTY_SET : model.realPermissions;
		
		if (model.getErrors().isEmpty())
		{
			if (model.roleId != null)
				model.listId = Backend.instance().getListMgr().setRole(model.roleId, model.name, perms);
			else
				model.roleId = Backend.instance().getListMgr().addRole(model.listId, model.name, perms);
		}
	}
	
}
