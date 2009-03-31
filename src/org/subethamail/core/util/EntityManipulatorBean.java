/*
 * $Id: EntityManipulatorBean.java 660 2006-06-20 10:49:22Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/util/EntityManipulatorBean.java $
 */

package org.subethamail.core.util;

import javax.inject.Current;

/**
 * Base class for session EJBs.  Provides access to
 * the SubEthaEntityManager.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
public class EntityManipulatorBean
{
	/** */
	@Current
	protected SubEthaEntityManager em;
}
