/*
 * $Id: SubEthaEntityManager.java 875 2006-11-13 02:45:41Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/Geometry.java,v $
 */

package org.subethamail.core.util;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.entity.Config;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.SubscriptionHold;
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
 * @author Scott Hernandez
 */
@SubEtha
public class SubEthaEntityManager implements EntityManager
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(SubEthaEntityManager.class);
	
	/** The entity manager to use under the covers */
	@Inject private EntityManager base;

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
	 * Deletes a config value if it exists
	 */
	public void removeConfigValue(String id)
	{
		Config cfg = this.find(Config.class, id);
		if (cfg != null)
			this.remove(cfg);
	}
	
	/**
	 * Finds an email address with the specified address.  Normalizes
	 * the address properly before querying.
	 */
	public EmailAddress getEmailAddress(String email) throws NotFoundException
	{
		email = Validator.normalizeEmail(email);

		return this.get(EmailAddress.class, email);
	}

	/**
	 * Finds an email address with the specified address.  Normalizes
	 * the address properly before querying.
	 * 
	 * @return null if no email address associated with that address
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
	
	/**
	 * @return all the mail created since a particular arrival date
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findMailSince(Date when)
	{
		if (log.isDebugEnabled())
			log.debug("Finding mail since " + when);
		
		Query q = this.createNamedQuery("MailSince");
		q.setParameter("since", when);
		
		return q.getResultList();
	}

	/**
	 * Gets all subscriptionholds older than the specified date.
	 */
	@SuppressWarnings("unchecked")
	public List<SubscriptionHold> findHeldSubscriptionsOlderThan(Date cutoff)
	{
		if (log.isDebugEnabled())
			log.debug("Finding held subs older than " + cutoff);
		
		Query q = this.createNamedQuery("HeldSubscriptionsOlderThan");
		q.setParameter("cutoff", cutoff);
		
		return q.getResultList();
	}

	/**
	 * Gets all held mail older than a certain date.
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findHeldMailOlderThan(Date cutoff)
	{
		if (log.isDebugEnabled())
			log.debug("Finding held mail older than " + cutoff);
		
		Query q = this.createNamedQuery("HeldMailOlderThan");
		q.setParameter("cutoff", cutoff);
		
		return q.getResultList();
	}

	/**
	 * Gets the last piece of held mail sent by the person to any list.
	 * @param excludeMailId is the id of mail that shouldn't be considered.
	 * @return null if there wasn't any.  
	 */
	public Mail findLastMailHeldFrom(String senderEmail, Long excludeMailId)
	{
		if (log.isDebugEnabled())
			log.debug("Finding the last held mail from " + senderEmail);
		
		senderEmail = Validator.normalizeEmail(senderEmail);
		
		Query q = this.createNamedQuery("HeldMailFrom");
		q.setParameter("sender", senderEmail);
		q.setParameter("excluding", excludeMailId);
		q.setMaxResults(1);
		
		return (Mail)q.getSingleResult();
	}
	
	/**
	 * Counts the number of held messages from an address since a particular date.
	 */
	public int countRecentHeldMail(String senderEmail, Date since)
	{
		if (log.isDebugEnabled())
			log.debug("Counting recent held mail from " + senderEmail);
		
		senderEmail = Validator.normalizeEmail(senderEmail);
		
		Query q = this.createNamedQuery("CountRecentHeldMailFrom");
		q.setParameter("sender", senderEmail);
		q.setParameter("since", since);
		
		return ((Number)q.getSingleResult()).intValue();
	}

	/**
	 * Finds most recent mail with the constraints.
	 * 
	 * @param listId the mailing list to look in
	 * @param subj the subject to match
	 * @param cutoff the oldest mail to consider
	 * @param count the max number of results to return 
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findRecentMailBySubject(Long listId, String subj, Date cutoff, int count)
	{
		if (log.isDebugEnabled())
			log.debug("Finding mail with subject " + subj + " younger than " + cutoff);
		
		Query q = this.createNamedQuery("RecentMailBySubject");
		q.setParameter("listId", listId);
		q.setParameter("subject", subj);
		q.setParameter("cutoff", cutoff);
		q.setMaxResults(count);
		
		return q.getResultList();
	}

	@Override
	public void persist(Object arg0) {
		this.base.persist(arg0);
	}

	@Override
	public <T> T merge(T arg0) {
		return this.base.merge(arg0);
	}

	@Override
	public void remove(Object arg0) {
		this.base.remove(arg0);
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1) {
		return this.base.find(arg0, arg1);
	}

	@Override
	public <T> T getReference(Class<T> arg0, Object arg1) {
		return this.base.getReference(arg0, arg1);
	}

	@Override
	public void flush() {
		this.base.flush();
	}

	@Override
	public void setFlushMode(FlushModeType arg0) {
		this.base.setFlushMode(arg0);
	}

	@Override
	public FlushModeType getFlushMode() {
		return this.base.getFlushMode();
	}

	@Override
	public void lock(Object arg0, LockModeType arg1) {
		this.base.lock(arg0, arg1);
	}

	@Override
	public void refresh(Object arg0) {
		this.base.refresh(arg0);
	}

	@Override
	public void clear() {
		this.base.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return this.base.contains(arg0);
	}

	@Override
	public Query createQuery(String arg0) {
		return this.base.createQuery(arg0);
	}

	@Override
	public Query createNamedQuery(String arg0) {
		return this.base.createNamedQuery(arg0);
	}

	@Override
	public Query createNativeQuery(String arg0) {
		return this.base.createNativeQuery(arg0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Query createNativeQuery(String arg0, Class arg1) {
		return this.base.createNativeQuery(arg0, arg1);
	}

	@Override
	public Query createNativeQuery(String arg0, String arg1) {
		return this.base.createNativeQuery(arg0, arg1);
	}

	@Override
	public void joinTransaction() {
		this.base.joinTransaction();
	}

	@Override
	public Object getDelegate() {
		return this.base.getDelegate();
	}

	@Override
	public void close() {
		this.base.close();
	}

	@Override
	public boolean isOpen() {
		return this.base.isOpen();
	}

	@Override
	public EntityTransaction getTransaction() {
		return this.base.getTransaction();
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2) {
		return this.base.find(arg0, arg1, arg2);
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		return this.getEntityManagerFactory();
	}

	@Override
	public LockModeType getLockMode(Object arg0) {
		return this.base.getLockMode(arg0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map getProperties() {
		return this.base.getProperties();
	}

	@Override
	public Set<String> getSupportedProperties() {
		return this.base.getSupportedProperties();
	}

	@Override
	public void refresh(Object arg0, LockModeType arg1) {
		this.base.refresh(arg0, arg1);		
	}

	@Override
	public <T> TypedQuery<T> createNamedQuery(String arg0, Class<T> arg1)
	{
		return this.base.createNamedQuery(arg0, arg1);
	}

	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> arg0)
	{
		return this.base.createQuery(arg0);
	}

	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> arg0, Class<T> arg1)
	{
		return this.base.createQuery(arg0, arg1);
	}

	@Override
	public void detach(Object arg0)
	{
		this.base.detach(arg0);
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, Map<String, Object> arg2)
	{
		return this.base.find(arg0, arg1, arg2);
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2, Map<String, Object> arg3)
	{
		return this.base.find(arg0, arg1, arg2);
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder()
	{
		return this.base.getCriteriaBuilder();
	}

	@Override
	public Metamodel getMetamodel()
	{
		return this.base.getMetamodel();
	}

	@Override
	public void lock(Object arg0, LockModeType arg1, Map<String, Object> arg2)
	{
		this.base.lock(arg0, arg1, arg2);
	}

	@Override
	public void refresh(Object arg0, Map<String, Object> arg1)
	{
		this.base.refresh(arg0, arg1);
	}

	@Override
	public void refresh(Object arg0, LockModeType arg1, Map<String, Object> arg2)
	{
		this.base.refresh(arg0, arg1, arg2);
	}

	@Override
	public void setProperty(String arg0, Object arg1)
	{
		this.base.setProperty(arg0, arg1);
	}

	@Override
	public <T> T unwrap(Class<T> arg0)
	{
		return this.base.unwrap(arg0);
	}
}
