/*
 * $Id: DAOEJB.java 91 2006-02-23 09:41:17Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/dao/DAOEJB.java $
 */

package org.subethamail.entity.dao;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.valid.Validator;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;

/**
 * @see DAO
 * 
 * Note:  All named queries are defined on the entities.
 *
 * @author Jeff Schnitzer
 */
@Stateless
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
	 * @see DAO#findMailingListByAddress(String)
	 */
	public MailingList findMailingListByAddress(String address) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding MailingList with address " + address);
		
		// Normalize out any weird casing in the domain part
		address = Validator.normalizeEmail(address);
		
		Query q = this.em.createNamedQuery("MailingListByAddress");
		q.setParameter("address", address);
		
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
	 * @see DAO#findEmailAddressByAddress(String)
	 */
	public EmailAddress findEmailAddressByAddress(String address) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding EmailAddress with address " + address);
		
		// Normalize out any weird casing in the domain part
		address = Validator.normalizeEmail(address);
		
		Query q = this.em.createNamedQuery("EmailByAddress");
		q.setParameter("address", address);
		
		try
		{
			return (EmailAddress)q.getSingleResult();
		}
		catch (NoResultException ex)
		{
			log.debug("Not found");
			throw new NotFoundException(ex);
		}
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

}
