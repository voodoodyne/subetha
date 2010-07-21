/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SearchArchive extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(SearchArchive.class);

	public static class Model extends PaginateModel
	{
		public Model()
		{
			this.setCount(50);	// default to 50
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
