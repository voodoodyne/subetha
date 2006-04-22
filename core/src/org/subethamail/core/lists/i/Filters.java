/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
 */

package org.subethamail.core.lists.i;

import java.io.Serializable;
import java.util.List;

/**
 * This class provides information about all the filters that
 * are available and enabled on a list.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class Filters implements Serializable
{
	/** */
	List<FilterData> available;
	List<EnabledFilterData> enabled;
	
	/**
	 */
	public Filters(List<FilterData> available, List<EnabledFilterData> enabled)
	{
		this.available = available;
		this.enabled = enabled;
	}

	/** */
	public List<FilterData> getAvailable()
	{
		return this.available;
	}

	/** */
	public List<EnabledFilterData> getEnabled()
	{
		return this.enabled;
	}
}
