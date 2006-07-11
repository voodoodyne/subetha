/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/Geometry.java,v $
 */

package org.subethamail.core.util;

import java.net.URL;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.entity.Config;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.i.Validator;

/**
 * The methods we add to a regular entity manager to give ourselves
 * a "prettier" interface.
 * 
 * NOTE THE CONVENTION:  getXXX methods throw a NotFoundException,
 * while findXXX methods return null or an empty list.  I think
 * it would make more sense the 'other way but EntityManager
 * already defines a find() method that returns null.
 * 
 * @author Jeff Schnitzer
 */
public class SubEthaEntityManager extends EntityManagerWrapper
{
	/** */
	private static Log log = LogFactory.getLog(SubEthaEntityManager.class);
	
	/**
	 * A normal entity manager is wrapped, providing the new methods.
	 */
	public SubEthaEntityManager(EntityManager base)
	{
		super(base);
	}
	
	/**
	 * Similar to find(), but throws a NotFoundException instead of returning null
	 * when the key does not exist.
	 */
	public <T> T get(Class<T> entityClass, Object primaryKey) throws NotFoundException
	{
		T val = this.find(entityClass, primaryKey);
		if (val == null)
			throw new NotFoundException("No such " + entityClass.getName() + primaryKey);
		else
			return val;
	}
	
	
	/**
	 * @return the value of a config entity with the specified id,
	 *  or null if there is no entity with that id.  This method
	 *  does not distinguish between missing entities and actual
	 *  null values stored as the config value.  The return type
	 *  will be the stored type of the value.
	 */
	public Object findConfigValue(String id)
	{
		Config c = this.find(Config.class, id);
		
		if (c == null)
			return null;
		else
			return c.getValue();
	}

	/**
	 * Creates or updates the config entity with the specified value.
	 */
	public void setConfigValue(String id, Object value)
	{
		Config cfg = this.find(Config.class, id);
		if (cfg == null)
		{
			cfg = new Config(id, value);
			this.persist(cfg);
		}
		else
		{
			cfg.setValue(value);
		}
	}
	
	/**
	 * Finds an email address with the specified address.  Normalizes
	 * the address properly before querying.
	 * 
	 * @return null if no email address associated with that address
	 */
	public EmailAddress getEmailAddress(String email) throws NotFoundException
	{
		email = Validator.normalizeEmail(email);

		return this.get(EmailAddress.class, email);
	}

	/**
	 * Finds an email address with the specified address.  Normalizes
	 * the address properly before querying.
	 */
	public EmailAddress findEmailAddress(String email)
	{
		email = Validator.normalizeEmail(email);
		
		return this.find(EmailAddress.class, email);
	}

	/**
	 * Tries to find a mail entity which has the specified message id
	 * in the given mailing list.
	 */
	public Mail getMailByMessageId(Long listId, String messageId) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Mail with Message-ID " + messageId);
		
		Query q = this.createNamedQuery("MailByMessageId");
		q.setParameter("listId", listId);
		q.setParameter("messageId", messageId);
		
		try
		{
			return (Mail)q.getSingleResult();
		}
		catch (NoResultException ex) { throw new NotFoundException(ex); }
	}
	
	/**
	 * Tries to find mail entites which are looking for an ancestor
	 * with the specified message id.
	 * 
	 * @param listId is the id of a mailing list in whose archives to search.
	 * @param messageId is the Message-ID to search for.
	 * 
	 * The return values are not readonly.
	 * Will prefetch the wantedReference collection.
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findMailWantingParent(Long listId, String messageId)
	{
		if (log.isDebugEnabled())
			log.debug("Finding mail wanting ancestor with Message-ID " + messageId);
		
		Query q = this.createNamedQuery("WantsReferenceToMessageId");
		q.setParameter("listId", listId);
		q.setParameter("messageId", messageId);
		
		return q.getResultList();
	}

	/**
	 * Gets a mailing list with the specified address.
	 */
	public MailingList getMailingList(InternetAddress address) throws NotFoundException
	{
		String email = Validator.normalizeEmail(address.getAddress());
		
		if (log.isDebugEnabled())
			log.debug("Finding MailingList with email " + email);
		
		Query q = this.createNamedQuery("MailingListByEmail");
		q.setParameter("email", email);
		
		try
		{
			return (MailingList)q.getSingleResult();
		}
		catch (NoResultException ex)
		{
			log.debug("Not found");
			throw new NotFoundException(ex);
		}
	}

	/**
	 * Gets a mailing list with the specified url
	 */
	public MailingList getMailingList(URL url) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding MailingList with url " + url);
		
		Query q = this.createNamedQuery("MailingListByUrl");
		q.setParameter("url", url.toString());
		
		try
		{
			return (MailingList)q.getSingleResult();
		}
		catch (NoResultException ex)
		{
			log.debug("Not found");
			throw new NotFoundException(ex);
		}
	}
	
	/**
	 * Finds all mailing lists
	 * @param skip can be -1 for no skip
	 * @param count can be -1 for no limit
	 */
	@SuppressWarnings("unchecked")
	public List<MailingList> findMailingLists(int skip, int count)
	{
		Query q = this.createNamedQuery("AllMailingLists");
		
		if (skip >= 0)
			q.setFirstResult(skip);
		
		if (count >= 0)
			q.setMaxResults(count);
		
		return q.getResultList();
	}
	
	/**
	 * Finds a mailing list with the specified query.
	 * @param query is a literal string to match in the text
	 * @param skip can be -1 for no skip
	 * @param count can be -1 for no limit
	 */
	@SuppressWarnings("unchecked")
	public List<MailingList> findMailingLists(String query, int skip, int count)
	{
		if (log.isDebugEnabled())
			log.debug("Finding MailingLists with query: " + query);

		Query q = this.createNamedQuery("SearchMailingLists");
		q.setParameter("name", like(query));
		q.setParameter("email", like(query));
		q.setParameter("url", like(query));
		q.setParameter("description", like(query));
		
		if (skip >= 0)
			q.setFirstResult(skip);
		
		if (count >= 0)
			q.setMaxResults(count);
		
		return q.getResultList();
	}

	/**
	 * @return the total number of lists on this server
	 */
	public int countLists()
	{
		if (log.isDebugEnabled())
			log.debug("Counting all mailing lists");

		Query q = this.createNamedQuery("CountMailingLists");
		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/**
	 * @return the total number of lists on this server based on the string query
	 */
	public int countLists(String query)
	{
		if (log.isDebugEnabled())
			log.debug("Counting mailing lists with query: " + query);

		Query q = this.createNamedQuery("CountMailingListsQuery");
		q.setParameter("name", like(query));
		q.setParameter("email", like(query));
		q.setParameter("url", like(query));
		q.setParameter("description", like(query));
		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/**
	 * @return all the threads for the list
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findMailByList(Long listId, int skip, int count)
	{
		if (log.isDebugEnabled())
			log.debug("Finding all mail for list " + listId);
		
		Query q = this.createNamedQuery("MailByList");
		q.setParameter("listId", listId);

		if (skip >= 0)
			q.setFirstResult(skip);
		
		if (count >= 0)
			q.setMaxResults(count);

		return q.getResultList();
	}

	/**
	 * @return all of the mail on a list
	 */
	public int countMailByList(Long listId)
	{
		if (log.isDebugEnabled())
			log.debug("Counting all mail for list " + listId);
		
		Query q = this.createNamedQuery("CountMailByList");
		q.setParameter("listId", listId);

		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/**
	 * @return any subscriptions which have the specified role.
	 */
	@SuppressWarnings("unchecked")
	public List<Subscription> findSubscriptionsByRole(Long roleId)
	{
		if (log.isDebugEnabled())
			log.debug("Finding Subscriptions with role " + roleId);
		
		Query q = this.createNamedQuery("SubscriptionsByRoleId");
		q.setParameter("roleId", roleId);
		
		return q.getResultList();
	}

	/**
	 * @return all Mail for the list which has a not null HoldType
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findMailHeld(Long listId, int skip, int count)
	{
		if (log.isDebugEnabled())
			log.debug("Finding held mail for list " + listId);

		Query q = this.createNamedQuery("HeldMail");
		q.setParameter("listId", listId);
		if (skip >= 0)
			q.setFirstResult(skip);
		
		if (count >= 0)
			q.setMaxResults(count);
		
		return q.getResultList();
	}

	/**
	 * @return the number of messages in a held state on a list.
	 */
	public int countHeldMessages(Long listId)
	{
		if (log.isDebugEnabled())
			log.debug("Counting held mail for list " + listId);

		Query q = this.createNamedQuery("HeldMailCount");
		q.setParameter("listId", listId);		

		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/**
	 * @return the number of held subscriptions on the list
	 */
	public int countHeldSubscriptions(Long listId)
	{
		if (log.isDebugEnabled())
			log.debug("Counting held subscriptions for list " + listId);

		Query q = this.createNamedQuery("HeldSubscriptionCount");
		q.setParameter("listId", listId);		

		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/**
	 * @return all Persons who are site admins
	 */
	@SuppressWarnings("unchecked")
	public List<Person> findSiteAdmins()
	{
		log.debug("Finding all site admins");

		Query q = this.createNamedQuery("SiteAdmin");
		return q.getResultList();
	}
	
	/**
	 * @return the number of subscribers on a list
	 */
	public int countSubscribers(Long listId)
	{
		if (log.isDebugEnabled())
			log.debug("Counting subscribers of list: " + listId);

		Query q = this.createNamedQuery("CountSubscribersOnList");
		q.setParameter("listId", listId);
		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/**
	 * @return the total number of mails on the server.
	 */
	public int countMail()
	{
		if (log.isDebugEnabled())
			log.debug("Counting all mail");

		Query q = this.createNamedQuery("CountMail");
		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/**
	 * @return the total number of Persons on this server.
	 */
	public int countPeople()
	{
		if (log.isDebugEnabled())
			log.debug("Counting all people");

		Query q = this.createNamedQuery("CountPerson");
		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/**
	 * @return the number of subscribers on a list based on a String query
	 */
	public int countSubscribers(Long listId, String query)
	{
		if (log.isDebugEnabled())
			log.debug("Counting subscribers on list: " + listId + " with query: " + query);

		Query q = this.createNamedQuery("CountSubscribersOnListQuery");
		q.setParameter("listId", listId);
		q.setParameter("name", like(query));
		q.setParameter("email", like(query));
		//q.setParameter("note", like(query));
		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/**
	 * @param skip can be -1 for no skip
	 * @param count can be -1 for no limit
	 * 
	 * @return the paginated list of subscibers
	 */
	@SuppressWarnings("unchecked")
	public List<Subscription> findSubscribers(Long listId, int skip, int count)
	{
		Query q = this.createNamedQuery("SubscribersOnList");
		q.setParameter("listId", listId);
		
		if (skip >= 0)
			q.setFirstResult(skip);
		
		if (count >= 0)
			q.setMaxResults(count);
		
		return q.getResultList();
	}

	/**
	 * Limit the number of subscribers on a list based on a String query
	 * 
	 * @param skip can be -1 for no skip
	 * @param count can be -1 for no limit
	 */
	@SuppressWarnings("unchecked")
	public List<Subscription> findSubscribers(Long listId, String query, int skip, int count)
	{
		Query q = this.createNamedQuery("SubscribersOnListQuery");
		q.setParameter("listId", listId);
		q.setParameter("name", like(query));
		q.setParameter("email", like(query));
		//q.setParameter("note", like(query));
		
		if (skip >= 0)
			q.setFirstResult(skip);
		
		if (count >= 0)
			q.setMaxResults(count);
		
		return q.getResultList();
	}

	/**
	 * @return all the soft holds associated with any email address
	 *  that the person owns.  The resulting mail objects are editable.
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findSoftHoldsForPerson(Long personId)
	{
		if (log.isDebugEnabled())
			log.debug("Finding soft mail holds for person " + personId);
		
		Query q = this.createNamedQuery("SoftHoldsByPerson");
		q.setParameter("personId", personId);
		
		return q.getResultList();
	}
	
	/** Helper method makes "like" queries possible */
	private final String like(String query)
	{
		return "%" + query + "%";
	}
}
