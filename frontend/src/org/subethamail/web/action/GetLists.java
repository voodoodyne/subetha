/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.PaginateModel;
import org.tagonist.propertize.Property;

/**
 * Gets all lists (paginated).
 * 
 * @author Jon Stevens
 */
public class GetLists extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(GetLists.class);
	
	public static class Model extends PaginateModel
	{
		/** */
		@Property String query;
		@Property List<ListData> listData;
	}
	
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();

		model.listData = Backend.instance().getListMgr()
			.getListsMatchingQuery(model.query, Backend.instance().getAdmin().getAllLists());

		// we are using pagination
		model.setTotalCount(model.listData.size());
		if (model.getSkip() > 0 && model.getCount() > 0)
			model.listData = model.listData.subList(model.getSkip(), model.getSkip() + model.getCount());
	}	
}
