/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.blueprint;

import org.subethamail.core.plugin.i.Blueprint;

/**
 * Constructs a trivial list with no frills.
 * 
 * @author Jeff Schnitzer
 */
public class BarebonesBlueprint  implements Blueprint
{
	/** */
	public String getName()
	{
		return "Barebones List";
	}

	/** */
	public String getDescription()
	{
		return 
			"Create a list with no additional configuration.  No plugins" +
			" will be added and the default role will have no permissions.";
	}
	
	/** */
	public void configureMailingList(Long listId)
	{
		// we're done
	}
}