/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.lists.i.Filters;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Gets the filters associated with a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public class GetFilters extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(GetFilters.class);

	/** */
	@Getter @Setter Long listId;

	/** */
	public void execute() throws Exception
	{
		Filters filters = Backend.instance().getListMgr().getFilters(this.listId);
		
		this.getCtx().setModel(filters);
	}
}
