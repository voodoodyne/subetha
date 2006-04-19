/*
 * $Id: Login.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Login.java $
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static Log log = LogFactory.getLog(GetListSettings.class);
	
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
		
		this.getCtx().setModel(model);
	}
	
}
