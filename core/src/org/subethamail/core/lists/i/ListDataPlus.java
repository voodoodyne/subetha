/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;


/**
 * Some extra detail about a mailing list.
 *
 * @author Jeff Schnitzer
 * @author Jon Stevens
 */
@SuppressWarnings("serial")
public class ListDataPlus extends ListData
{
	int subscriberCount;
	int messageCount;

	protected ListDataPlus()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

	/**
	 */
	public ListDataPlus(Long id, 
					String email,
					String name,
					String url,
					String urlBase,
					String description,
					String welcomeMessage,
					String ownerEmail,
					boolean subscriptionHeld,
					int subscriberCount,
					int messageCount)
	{
		super(id, email, name, url, urlBase, description, welcomeMessage, ownerEmail, subscriptionHeld);
		this.subscriberCount = subscriberCount;
		this.messageCount = messageCount;
	}
	
	/** */
	public String toString()
	{
		return this.getClass().getName() + " {id=" + this.id + ", name=" + this.name + "}";
	}

	/** */
	public int compareTo(Object o)
	{
		ListDataPlus other = (ListDataPlus)o;
		
		// Only return 0 if they are actually identical to make TreeMap happy
		if (this.id.equals(other.id))
			return 0;
		
		int result = this.name.compareTo(((ListDataPlus)o).getName());
		if (result == 0)
			return this.id.compareTo(other.id);
		else
			return result;
	}

	public int getMessageCount()
	{
		return this.messageCount;
	}

	public int getSubscriberCount()
	{
		return this.subscriberCount;
	}
}
