/*
 * $Id: SubscriberData.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/SubscriberData.java $
 */

package org.subethamail.core.lists.i;

import java.util.Date;
import java.util.List;

import org.subethamail.core.acct.i.PersonData;

/**
 * Some detail about a subscriber.
 *
 * @author Scott Hernandez
 * @author Jon Scott Stevens
 */
public class SubscriberData extends PersonData
{	
	private static final long serialVersionUID = 1L;

	RoleData role;
	String deliverTo;
	Date dateSubscribed;
	String note;	// will be null if you don't have permission to see it
	
	protected SubscriberData()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

	/**
	 */
	public SubscriberData(
			Long id,
			String name,
			List<String> emailAddresses,
			RoleData role,
			String deliverTo,
			Date dateSubscribed,
			String note)
	{
		super(id, name, emailAddresses);
		this.role = role;
		this.deliverTo = deliverTo;
		this.dateSubscribed = dateSubscribed;
		this.note = note;
	}
	
	/** */
	public String getRoleName()
	{
		return this.role.getName();
	}
	
	public RoleData getRole() 
	{
		return this.role;
	}

	public String getDeliverTo()
	{
		return this.deliverTo;
	}

	/** */
	public Date getDateSubscribed()
	{
		return this.dateSubscribed;
	}

	/** will be null if caller does not have permission to view notes */
	public String getNote()
	{
		return this.note;
	}
}
