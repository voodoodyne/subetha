/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.subethamail.common.SearchException;
import org.subethamail.core.lists.i.SearchHit;
import org.subethamail.core.lists.i.SearchResult;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.PaginateModel;

/**
 * Gets one page of search results for an archive.  Note that the
 * mail summaries will not have replies.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class SearchArchive extends AuthAction 
{
	public static class Model extends PaginateModel
	{
		public Model()
		{
			this.setCount(100);	// default to 100
		}
		
		/** */
		@Getter @Setter Long listId;
		@Getter @Setter String query;
		@Getter @Setter List<SearchHit> hits;

		@Getter @Setter String error;
	}

	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();

		try
		{
			SearchResult result = Backend.instance().getArchiver().search(model.listId, model.query, model.getSkip(), model.getCount());
			
			model.hits = result.getHits();
			model.setTotalCount(result.getTotal());
		}
		catch (SearchException se)
		{
			model.error = se.getMessage();
		}
	}
}
