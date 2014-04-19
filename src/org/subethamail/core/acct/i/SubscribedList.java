package org.subethamail.core.acct.i;

import org.subethamail.core.lists.i.ListData;

/**
 * Some detail about a mailing list.
 *
 * @author Jon Scott Stevens
 */
public class SubscribedList extends ListData
{
	private static final long serialVersionUID = 1L;

	String roleName;
	String deliverTo;
	
	/** Needed by Hessian */
	protected SubscribedList() {}
		
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
