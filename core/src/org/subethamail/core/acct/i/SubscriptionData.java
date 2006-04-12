/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
 */

package org.subethamail.core.acct.i;

import org.subethamail.core.lists.i.MailingListData;

/**
 * Some detail about a mailing list.
 *
 * @author Jon Scott Stevens
 */
@SuppressWarnings("serial")
public class SubscriptionData extends MailingListData
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
