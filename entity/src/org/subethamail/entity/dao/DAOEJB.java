/*
 * $Id: DAOEJB.java 91 2006-02-23 09:41:17Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/dao/DAOEJB.java $
 */

package org.subethamail.entity.dao;

import java.net.URL;
import java.util.List;

import javax.ejb.Local;
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
import org.subethamail.entity.Config;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;

/**
 * @see DAO
 * 
 * Note:  All named queries are defined on the entities.
 *
 * @author Jeff Schnitzer
 */
@Stateless(name="DAO")
@Local(DAO.class)
public class DAOEJB implements DAO
{
	/** */
	private static Log log = LogFactory.getLog(DAOEJB.class);
	
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
	public EmailAddress findEmailAddress(String address) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding EmailAddress with id " + address);
		
		// Normalize out any weird casing in the domain part
		address = Validator.normalizeEmail(address);
		
		EmailAddress e = this.em.find(EmailAddress.class, address);
		
		if (e == null)
			throw new NotFoundException("No email " + address);
		else
			return e;
	}

	/**
	 * @see DAO#findMailByMessageId(String)
	 */
	public Mail findMailByMessageId(String messageId) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Mail with Message-ID " + messageId);
		
		Query q = this.em.createNamedQuery("MailByMessageId");
		q.setParameter("messageId", messageId);
		
		try
		{
			return (Mail)q.getSingleResult();
		}
		catch (NoResultException ex)
		{
			log.debug("Not found");
			throw new NotFoundException(ex);
		}
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
			log.debug("Finding MailingList with address " + address.getAddress());
		
		Query q = this.em.createNamedQuery("MailingListByAddress");
		q.setParameter("address", address.getAddress());
		
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
	 * @see DAO#findAllLists()
	 */
	public List<MailingList> findAllLists()
	{
		if (log.isDebugEnabled())
			log.debug("Finding all mailing lists");
		
		Query q = this.em.createNamedQuery("AllMailingLists");
		
		return q.getResultList();
	}

}
