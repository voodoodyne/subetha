/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.ArrayList;
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
		
		model.listData = Backend.instance().getAdmin().getAllLists();

		// do some basic searching. keeps the load off the database.
		if (model.query != null && model.query.length() > 0)
		{
			List<ListData> queryResults = new ArrayList<ListData>(model.listData.size());

			for (ListData list : model.listData)
			{
				if (list.getName().contains(model.query) ||
					list.getDescription().contains(model.query) ||
					list.getUrl().contains(model.query) ||
					list.getEmail().contains(model.query))
				{
					queryResults.add(list);
				}
			}

			model.listData = queryResults;
		}		
		
		model.setTotalCount(model.listData.size());
		if (model.getSkip() > 0 && model.getCount() > 0)
			model.listData = model.listData.subList(model.getSkip(), model.getSkip() + model.getCount());
	}	
}
