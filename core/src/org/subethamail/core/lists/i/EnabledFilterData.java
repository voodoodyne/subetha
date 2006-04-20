/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
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
