/*
 * $Id: Self.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/Self.java $
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
	List<SubscriptionData> subscriptions;
	
	/** */
	boolean siteAdmin;

	/**
	 */
	public Self(Long id, 
				String name,
				String[] emailAddresses,
				boolean siteAdmin,
				List<SubscriptionData> subscriptions)
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
	
	public List<SubscriptionData> getSubscriptions()
	{
		return this.subscriptions;
	}
}
