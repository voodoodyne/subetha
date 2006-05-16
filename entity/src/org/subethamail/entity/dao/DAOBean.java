/*
 * $Id$
 * $URL$
 */

package org.subethamail.entity.dao;

import java.net.URL;
import java.util.List;

import javax.ejb.Stateless;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.valid.Validator;
import org.subethamail.entity.Attachment;
import org.subethamail.entity.Config;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Role;
import org.subethamail.entity.Subscription;

/**
 * @see DAO
 * 
 * Note:  All named queries are defined on the entities.
 *
 * @author Jeff Schnitzer
 */
@Stateless(name="DAO")
public class DAOBean implements DAO
{
	/** */
	private static Log log = LogFactory.getLog(DAOBean.class);
	
	/** */
	@PersistenceContext private EntityManager em;

	/**
	 * @see DAO#persist(Object)
	 */
	public void persist(Object obj)
	{
		this.em.persist(obj);
	}

	/**
	 * @see DAO#remove(Object)
	 */
	public void remove(Object obj)
	{
		this.em.remove(obj);
	}

	/**
	 * @see DAO#flush()
	 */
	public void flush()
	{
		this.em.flush();
	}

	/**
	 * @see DAO#lock(Object, LockModeType)
	 */
	public void lock(Object obj, LockModeType lockMode)
	{
		this.em.lock(obj, lockMode);
	}

	/**
	 * @see DAO#findConfig(String)
	 */
	public Config findConfig(String id) throws NotFoundException
	{
		Config c = this.em.find(Config.class, id);
		
		if (c == null)
			throw new NotFoundException("No config " + id);
		else
			return c;
	}

	/**
	 * @see DAO#getConfigValue(String)
	 */
	public Object getConfigValue(String id)
	{
		Config c = this.em.find(Config.class, id);
		
		if (c == null)
			return null;
		else
			return c.getValue();
	}

	/**
	 * @see DAO#findEmailAddress(String)
	 */
	public EmailAddress findEmailAddress(String email) throws NotFoundException
	{
		EmailAddress e = this.getEmailAddress(email);
		
		if (e == null)
			throw new NotFoundException("No email " + email);
		else
			return e;
	}

	/**
	 * @see DAO#getEmailAddress(String)
	 */
	public EmailAddress getEmailAddress(String email)
	{
		if (log.isDebugEnabled())
			log.debug("Getting EmailAddress with id " + email);
		
		// Normalize out any weird casing in the domain part
		email = Validator.normalizeEmail(email);
		
		return this.em.find(EmailAddress.class, email);
	}

	/**
	 * @see DAO#findMail(Long)
	 */
	public Mail findMail(Long id) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Mail with id " + id);
		
		Mail m = this.em.find(Mail.class, id);
		
		if (m == null)
			throw new NotFoundException("No mail " + id);
		else
			return m;
	}

	/**
	 * @see DAO#findMailByMessageId(Long, String)
	 */
	public Mail findMailByMessageId(Long listId, String messageId) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Mail with Message-ID " + messageId);
		
		Query q = this.em.createNamedQuery("MailByMessageId");
		q.setParameter("listId", listId);
		q.setParameter("messageId", messageId);
		
		try
		{
			return (Mail)q.getSingleResult();
		}
		catch (NoResultException ex)
		{
			throw new NotFoundException(ex);
		}
	}
	
	/**
	 * @see DAO#findMailWantingParent(Long, String)
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findMailWantingParent(Long listId, String messageId)
	{
		if (log.isDebugEnabled())
			log.debug("Finding mail wanting ancestor with Message-ID " + messageId);
		
		Query q = this.em.createNamedQuery("WantsReferenceToMessageId");
		q.setParameter("listId", listId);
		q.setParameter("messageId", messageId);
		
		return q.getResultList();
	}

	/**
	 * @see DAO#findMailingList(Long)
	 */
	public MailingList findMailingList(Long id) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding MailingList with id " + id);
		
		MailingList m = this.em.find(MailingList.class, id);
		
		if (m == null)
			throw new NotFoundException("No mailing list " + id);
		else
			return m;
	}

	/**
	 * @see DAO#findMailingList(InternetAddress)
	 */
	public MailingList findMailingList(InternetAddress address) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding MailingList with email " + address.getAddress());
		
		Query q = this.em.createNamedQuery("MailingListByEmail");
		q.setParameter("email", address.getAddress());
		
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
	 * @see DAO#findMailingListByUrl(URL)
	 */
	public MailingList findMailingList(URL url) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding MailingList with url " + url);
		
		Query q = this.em.createNamedQuery("MailingListByUrl");
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
	 * @see DAO#findPerson(Long)
	 */
	public Person findPerson(Long personId) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Person with id " + personId);
		
		Person p = this.em.find(Person.class, personId);
		
		if (p == null)
			throw new NotFoundException("No person " + personId);
		else
			return p;
	}

	/**
	 * @see DAO#findPerson(Long)
	 */
	public Person findPerson(String email) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Person with email " + email);
		
		EmailAddress ea = this.em.find(EmailAddress.class, email);
		
		if (ea == null || ea.getPerson() == null)
			throw new NotFoundException("No person with email: " + email);
		else
			return ea.getPerson();
	}

	/**
	 * @see DAO#findAllLists()
	 */
	@SuppressWarnings("unchecked")
	public List<MailingList> findAllLists()
	{
		if (log.isDebugEnabled())
			log.debug("Finding all mailing lists");
		
		Query q = this.em.createNamedQuery("AllMailingLists");
		
		return q.getResultList();
	}

	/**
	 * @see DAO#findMailByList(Long, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findMailByList(Long listId, int start, int count)
	{
		if (log.isDebugEnabled())
			log.debug("Finding all mail for list " + listId);
		
		Query q = this.em.createNamedQuery("MailByList");
		q.setParameter("listId", listId);
		q.setFirstResult(start);
		q.setMaxResults(count);
		
		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findRole(java.lang.Long)
	 */
	public Role findRole(Long roleId) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Role with id " + roleId);
		
		Role r = this.em.find(Role.class, roleId);
		
		if (r == null)
			throw new NotFoundException("No role " + roleId);
		else
			return r;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findSubscriptionsByRole(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public List<Subscription> findSubscriptionsByRole(Long roleId)
	{
		if (log.isDebugEnabled())
			log.debug("Finding Subscriptions with role " + roleId);
		
		Query q = this.em.createNamedQuery("SubscriptionsByRoleId");
		q.setParameter("roleId", roleId);
		
		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findAttachment(java.lang.Long)
	 */
	public Attachment findAttachment(Long attachmentId) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Attachment with id " + attachmentId);
		
		Attachment a = this.em.find(Attachment.class, attachmentId);
		
		if (a == null)
			throw new NotFoundException("No attachment " + attachmentId);
		else
			return a;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findMailHeld(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findMailHeld(Long listId)
	{
		if (log.isDebugEnabled())
			log.debug("Finding held mail for list " + listId);
		
		Query q = this.em.createNamedQuery("HeldMail");
		q.setParameter("listId", listId);
		
		return q.getResultList();
	}

}
