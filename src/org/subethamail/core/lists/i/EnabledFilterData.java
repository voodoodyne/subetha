/*
 * $Id: EnabledFilterData.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/EnabledFilterData.java $
 */

package org.subethamail.core.lists.i;

import java.util.Map;

import org.subethamail.core.plugin.i.FilterParameter;

/**
 * Information about a filter that has been enabled, including
 * its parameters.  Enabled filters have an id (they are
 * entities).
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class EnabledFilterData extends FilterData
{
	Long listId;
	Map<String, Object> arguments;

	protected EnabledFilterData()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

	/**
	 */
	public EnabledFilterData(
			String className,
			String name,
			String description,
			FilterParameter[] parameters,
			Long listId,
			Map<String, Object> arguments)
	{
		super(className, name, description, parameters);
		
		this.listId = listId;
		this.arguments = arguments;
	}

	/** */
	public Map<String, Object> getArguments()
	{
		return this.arguments;
	}

	/** */
	public Long getListId()
	{
		return this.listId;
	}
}
