/**
 * $Id: FavoritePK.java 86 2006-02-22 03:36:01Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/om/CategoryBean.java,v $
 */

package org.subethamail.entity;

import javax.persistence.Embeddable;

/**
 * Primary key for a Subscription.  Since Subscription really
 * just defines a relationship between Person and MailingList,
 * we use a natural key here.
 * 
 * This is how relationship attributes are modeled in EJB3.
 * 
 * In the future, Hibernate should allow the PK to be
 * Person and MailingList references themselves instead
 * of ids that overlap with real fields in the entity.
 * 
 * @author jeff
 */
@Embeddable
public class SubscriptionPK implements java.io.Serializable
{
	/** */
	Long personId;
	Long listId;
	
	/** */
	public SubscriptionPK() {}
	
	/** */
	public SubscriptionPK(Long personId, Long listId)
	{
		this.personId = personId;
		this.listId = listId;
	}
	
	/**
	 */
	public Long getPersonId() { return this.personId; }
	public Long getListId() { return this.listId; }

	/**
	 */
	public int hashCode()
	{
		return this.personId.hashCode() ^ this.listId.hashCode();
	}
	
	/**
	 */
	public boolean equals(Object o)
	{
		if (o instanceof SubscriptionPK)
			return this.personId.equals(((SubscriptionPK)o).getPersonId()) && this.listId.equals(((SubscriptionPK)o).getListId());
		else
			return false;
	}
	
	/**
	 */
	public String toString()
	{
		return this.getClass().getName() + " {personId=" + this.personId + ", listId=" + this.listId + "}";
	}
}
