/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.acct.i;

import org.subethamail.core.lists.i.ListData;

/**
 * Some detail about a mailing list.
 *
 * @author Jon Scott Stevens
 */
@SuppressWarnings("serial")
public class SubscriptionData extends ListData
{
	String roleName;
	String deliverTo;
		
	/**
	 */
	public SubscriptionData(Long id, 
					String email,
					String name,
					String url, 
					String description,
					String roleName,
					String deliverTo)
	{
		super(id, email, name, url, description);
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
