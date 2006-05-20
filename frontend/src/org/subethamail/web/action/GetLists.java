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

		model.listData = Backend.instance().getListMgr().searchLists(model.query, model.getSkip(), model.getCount());

		if (model.query == null || model.query.length() == 0)
		{
			model.setTotalCount(Backend.instance().getListMgr().countLists());
		}
		else
		{
			// If we are doing a query, then we need to find out how many results would
			// have been returned for our query (before the limit was applied) in 
			// order to do the pagination right.
			//
			// this is highly inefficient as we are doing a full table scan again.
			// Since we know and love MySQL, it would be better to do something like this:
			// http://www.mysqlfreaks.com/statements/101.php
			//			SELECT SQL_CALC_FOUND_ROWS *
			//			FROM tbl_name
			//			WHERE id > 100 LIMIT 10;
			//
			//			SELECT FOUND_ROWS();
			//
			int size = Backend.instance().getListMgr().countLists(model.query);
			model.setTotalCount(size);
		}
	}
}
