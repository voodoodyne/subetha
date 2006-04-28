/*
 * $Id$
 * $URL$
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
	Map<String, String> documentation;

	/**
	 */
	public EnabledFilterData(
			String className,
			String name,
			String description,
			FilterParameter[] parameters,
			Long listId,
			Map<String, Object> arguments,
			Map<String, String> documentation)
	{
		super(className, name, description, parameters);
		
		this.listId = listId;
		this.arguments = arguments;
		this.documentation = documentation;
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

	public Map<String, String> getDocumenation()
	{
		return this.documentation;
	}

}
