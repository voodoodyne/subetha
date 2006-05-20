/*
 * $Id$
 * $URL$
 */

package org.subethamail.entity.dao;

import java.net.URL;
import java.util.List;
import javax.ejb.Local;
import javax.mail.internet.InternetAddress;
import javax.persistence.LockModeType;
import org.subethamail.common.NotFoundException;
import org.subethamail.entity.Attachment;
import org.subethamail.entity.Config;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Role;
import org.subethamail.entity.Subscription;

/**
 * DAO interface to all persisted objects.  Use this EJB instead
 * of directly manipulating the EntityManager from other EJBs.
 * It's just a convenient layer of abstraction, usable from
 * multiple applications that share a data model.
 * 
 * By convention, findXXX methods (that return single objects) throw
 * a NotFoundException if there is no data matching the criteria, but
 * getXXX methods return null.  findXXX methods that return collections
 * will return empty collections.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface DAO
{
	/** */
	public static final String JNDI_NAME = "subetha/DAO/local";
	
	/**
	 * Persists the object in the database
	 */
	public void persist(Object obj);
	
	/**
	 * Removes the object from the database
	 */
	public void remove(Object obj);
	
	/**
	 * Flush the current state of the session cache
	 */
	public void flush();
	
	/**
	 * Lock an entity.
	 */
	public void lock(Object obj, LockModeType lockMode);

	/**
	 * Finds a config entity.
	 */
	public Config findConfig(String id) throws NotFoundException;
	
	/**
	 * @return the value of a config entity with the specified id,
	 *  or null if there is no entity with that id.  This method
	 *  does not distinguish between missing entities and actual
	 *  null values stored as the config value.  The return type
	 *  will be the stored type of the value.
	 */
	public Object getConfigValue(String id);

	/**
	 * Finds an email address with the specified address.
	 */
	public EmailAddress findEmailAddress(String email) throws NotFoundException;

	/**
	 * Finds an email address with the specified address.
	 * @return null if no email address associated with that address
	 */
	public EmailAddress getEmailAddress(String email);

	/**
	 * Gets a mail by its id (not message-id).
	 */
	public Mail findMail(Long mailId) throws NotFoundException;
	
	/**
	 * Tries to find a mail entity which has the specified message id
	 * in the given mailing list.
	 */
	public Mail findMailByMessageId(Long listId, String msgId) throws NotFoundException;

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
	public List<Mail> findMailWantingParent(Long listId, String msgId);

	/**
	 * @return the mailing list with the specified id
	 */
	public MailingList findMailingList(Long id) throws NotFoundException;

	/**
	 * Finds a mailing list with the specified address.
	 * 
	 * @return a readonly MailingList entity
	 */
	public MailingList findMailingList(InternetAddress address) throws NotFoundException;

	/**
	 * Finds a mailing list with the specified url
	 *  
	 * @return a readonly MailingList entity
	 */
	public MailingList findMailingList(URL url) throws NotFoundException;

	/**
	 * Finds a mailing list with the specified query.
	 * @param query
	 * @param skip
	 * @param count
	 * @return
	 */
	public List<MailingList> findMailingLists(String query, int skip, int count);

	/**
	 * Finds a mailing list with the specified query.
	 * @param query
	 * @return
	 */
	public List<MailingList> findMailingLists(String query);

	/**
	 * @return the identified person.
	 */
	public Person findPerson(Long personId) throws NotFoundException;

	/**
	 * @return a list of all mailing lists on the site.
	 */
	public List<MailingList> findAllLists();

	/**
	 * @return all the threads for the list
	 */
	public List<Mail> findMailByList(Long listId, int start, int count);

	/**
	 * @return the role
	 */
	public Role findRole(Long roleId) throws NotFoundException;

	/**
	 * @return any subscriptions which have the specified role,
	 *  in writable form.
	 */
	public List<Subscription> findSubscriptionsByRole(Long roleId);

	/**
	 * @return the specified attachment.
	 */
	public Attachment findAttachment(Long attachmentId) throws NotFoundException;

	/**
	 * @return all Mail for the list which has a not null HoldType
	 */
	public List<Mail> findMailHeld(Long listId);
	
	/**
	 * @return all Person's who are site admins
	 */
	public List<Person> findSiteAdmins();
	
	/**
	 * @return the total number of lists on this server
	 */
	public int countLists();

	/**
	 * @return the total number of lists on this server based on the string query
	 */
	public int countLists(String query);
	
	/**
	 * @return the number of subscribers on a list
	 */
	public int countSubscribers(Long listId);
	
	/**
	 * @return the number of subscribers on a list based on a String query
	 */
	public int countSubscribers(Long listId, String query);

	/**
	 * @return limit the number of subscribers on a list based on a String query
	 */
	public List<Subscription> findSubscribers(Long listId, String query, int skip, int count) throws NotFoundException;

	/**
	 * @return the number of subscribers on a list based on a String query
	 */
	public List<Subscription> findSubscribers(Long listId, String query) throws NotFoundException;

	/**
	 * @return all the soft holds associated with any email address
	 *  that the person owns.  The resulting mail objects are editable.
	 */
	public List<Mail> findSoftHoldsForPerson(Long personId);
}
