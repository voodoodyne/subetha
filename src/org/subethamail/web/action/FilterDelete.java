/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Remove a filter from a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public class FilterDelete extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(FilterDelete.class);

	/** */
	@Property Long listId;
	
	/** */
	@Property String className;
	
	/** */
	public void execute() throws Exception
	{
		Backend.instance().getListMgr().disableFilter(this.listId, this.className);
	}
}
