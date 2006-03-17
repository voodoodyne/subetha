/*
 * $Id: DAOEJB.java 91 2006-02-23 09:41:17Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/dao/DAOEJB.java $
 */

package org.subethamail.entity.dao;

import java.util.List;

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

import com.kink.entity.Account;
import com.kink.entity.Department;
import com.kink.entity.Employee;
import com.kink.entity.LineItem;
import com.kink.entity.PaymentMethod;
import com.kink.entity.PurchaseOrder;
import com.kink.entity.Vendor;
import com.kink.entity.WorkOrder;

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
