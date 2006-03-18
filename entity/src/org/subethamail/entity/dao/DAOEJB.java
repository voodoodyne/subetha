/*
 * $Id: DAOEJB.java 91 2006-02-23 09:41:17Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/dao/DAOEJB.java $
 */

package org.subethamail.entity.dao;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

}
