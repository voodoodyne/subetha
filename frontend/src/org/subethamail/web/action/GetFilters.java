/*
 * $Id: Login.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Login.java $
 */

package org.subethamail.web.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.lists.i.EnabledFilterData;
import org.subethamail.core.lists.i.FilterData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Gets the filters associated with a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public class GetFilters extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(GetFilters.class);

	/** */
	@Property Long listId;
	@Property List<FilterData> available;
	@Property List<EnabledFilterData> enabled;

	/** */
	public void execute() throws Exception
	{
		this.available = Backend.instance().getListMgr().getAvailableFilters(this.listId);
		this.enabled = Backend.instance().getListMgr().getEnabledFilters(this.listId);
	}
}
