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
 * @author Jon Scott Stevens
 */
@SuppressWarnings("serial")
public class SubscriberData extends PersonData
{	
	String roleName;
	String deliverTo;
	Date dateSubscribed;

	/**
	 */
	public SubscriberData(
			Long id,
			String name,
			List<String> emailAddresses,
			String roleName,
			String deliverTo,
			Date dateSubscribed)
	{
		super(id, name, emailAddresses);
		this.roleName = roleName;
		this.deliverTo = deliverTo;
		this.dateSubscribed = dateSubscribed;
	}
	
	/** */
	public String getRoleName()
	{
		return this.roleName;
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
