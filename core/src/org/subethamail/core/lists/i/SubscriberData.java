/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;

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

	/**
	 */
	public SubscriberData(
			Long id,
			String name,
			String[] emailAddresses,
			String roleName,
			String deliverTo)
	{
		super(id, name, emailAddresses);
		this.roleName = roleName;
		this.deliverTo = deliverTo;
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
}
