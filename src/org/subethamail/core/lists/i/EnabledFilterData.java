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
public class EnabledFilterData extends FilterData
{
	private static final long serialVersionUID = 1L;

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
