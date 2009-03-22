/*
 * $Id: EntityManipulatorBean.java 660 2006-06-20 10:49:22Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/util/EntityManipulatorBean.java $
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
