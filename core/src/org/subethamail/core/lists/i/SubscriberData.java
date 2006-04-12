/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
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
			String name,
			String[] emailAddresses,
			String roleName,
			String deliverTo)
	{
		super(null, name, emailAddresses);
		this.roleName = roleName;
		this.deliverTo = deliverTo;
	}
	
	/** */
	public String toString()
	{
		return this.getClass().getName() + ", name=" + this.name + "}";
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
