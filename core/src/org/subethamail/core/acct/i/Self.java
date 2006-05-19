/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.acct.i;

import java.util.List;


/**
 * Some detail about the current user, suitable for display on
 * a page for editing data.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class Self extends PersonData
{
	List<SubscribedList> subscriptions;
	
	/** */
	boolean siteAdmin;

	/**
	 */
	public Self(Long id, 
				String name,
				List<String> emailAddresses,
				boolean siteAdmin,
				List<SubscribedList> subscriptions)
	{
		super(id, name, emailAddresses);
		
		this.siteAdmin = siteAdmin;
		this.subscriptions = subscriptions;
	}

	/** */
	public boolean isSiteAdmin()
	{
		return this.siteAdmin;
	}
	
	public List<SubscribedList> getSubscriptions()
	{
		return this.subscriptions;
	}
}
