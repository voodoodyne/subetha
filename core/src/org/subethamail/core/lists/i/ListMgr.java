/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.lists.i;

import java.net.URL;

import javax.ejb.Local;

import org.subethamail.common.NotFoundException;

/**
 * Tools for querying and modifying list configurations.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface ListMgr
{
	/** */
	public static final String JNDI_NAME = "subetha/ListMgr/local";

	/**
	 * Finds the id for a particular list URL.
	 * 
	 * No access control.
	 */
	public Long lookup(URL url) throws NotFoundException;
	
	/**
	 * Gets some data about a mailing list.  Includes the subscriber
	 * status (including role and permissions) of the person calling
	 * this method.
	 * 
	 * If not authenticated, subscriber status will reflect that state.
	 * If authed as site admin, all permissions are granted no matter
	 * what the actual role.
	 * 
	 * No access control.
	 */
	public MySubscription getMySubscription(Long listId) throws NotFoundException;
}

