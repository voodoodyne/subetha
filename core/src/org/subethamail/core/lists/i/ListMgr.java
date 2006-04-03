/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.lists.i;

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
	 * Gets some data about a mailing list.  Anyone can call this method.
	 */
	public MailingListData getMailingList(Long id) throws NotFoundException;
}

