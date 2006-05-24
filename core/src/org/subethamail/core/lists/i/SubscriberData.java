/*
 * $Id$
 * $URL$
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
@SuppressWarnings("serial")
public class SubscriberData extends PersonData
{	
	RoleData role;
	String deliverTo;
	Date dateSubscribed;
	
	/**
	 */
	public SubscriberData(
			Long id,
			String name,
			List<String> emailAddresses,
			RoleData role,
			String deliverTo,
			Date dateSubscribed)
	{
		super(id, name, emailAddresses);
		this.role = role;
		this.deliverTo = deliverTo;
		this.dateSubscribed = dateSubscribed;
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
}
