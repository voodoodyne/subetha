/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.util;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for session EJBs.  Provides access to
 * the SubEthaEntityManager.
 * 
 * @author Jeff Schnitzer
 */
public class EntityManipulatorBean
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(EntityManipulatorBean.class);

	/** */
	protected SubEthaEntityManager em;
	
	@PersistenceContext(unitName="subetha")
	public void setEntityManager(EntityManager value) { this.em = new SubEthaEntityManager(value); }
}
