/*
 * $Id: Filters.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/Filters.java $
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
	
	protected Filters()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

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
