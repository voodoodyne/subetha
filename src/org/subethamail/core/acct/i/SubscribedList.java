/*
 * $Id: SubscribedList.java 673 2006-07-10 03:38:01Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/acct/i/SubscribedList.java $
 */

package org.subethamail.core.acct.i;

import org.subethamail.core.lists.i.ListData;

/**
 * Some detail about a mailing list.
 *
 * @author Jon Scott Stevens
 */
@SuppressWarnings("serial")
public class SubscribedList extends ListData
{
	String roleName;
	String deliverTo;
		
	/**
	 */
	public SubscribedList(Long id, 
					String email,
					String name,
					String url, 
					String urlBase, 
					String description,
					String ownerEmail,
					boolean subscriptionHeld,
					String roleName,
					String deliverTo)
	{
		super(id, email, name, url, urlBase, description, null, ownerEmail, subscriptionHeld);
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
