/*
 * $Id: DAO.java 90 2006-02-23 02:31:05Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/dao/DAO.java $
 */

package org.subethamail.entity.dao;

import javax.persistence.LockModeType;

import org.subethamail.common.NotFoundException;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;

/**
 * DAO interface to all persisted objects.  Use this EJB instead
 * of directly manipulating the EntityManager from other EJBs.
 * It's just a convenient layer of abstraction, usable from
 * multiple applications that share a data model.
 *
 * @author Jeff Schnitzer
 */
public interface DAO
{
	/** */
	public static final String JNDI_NAME = "DAO/local";
	
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
	 * Finds an email address with the specified address.
	 */
	public EmailAddress findEmailAddress(String address) throws NotFoundException;

	/**
	 * Finds a mailing list with the specified address.
	 * 
	 * @return a readonly MailingList entity
	 */
	public MailingList findMailingListByAddress(String address) throws NotFoundException;

	/**
	 * Tries to find a mail entity which has the specified message id.
	 */
	public Mail findMailByMessageId(String msgId) throws NotFoundException;

	/**
	 * @return the mailing list with the specified id
	 */
	public MailingList findMailingList(Long id) throws NotFoundException;

}
