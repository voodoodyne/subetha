package org.subethamail.core.lists.i;

import java.io.Serializable;
import java.util.List;

/**
 * This class provides information about all the filters that
 * are available and enabled on a list.
 *
 * @author Jeff Schnitzer
 */
public class Filters implements Serializable
{
	private static final long serialVersionUID = 1L;

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
