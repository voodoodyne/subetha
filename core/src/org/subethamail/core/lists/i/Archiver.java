/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.lists.i;

import java.util.List;

import javax.ejb.Local;

import org.subethamail.common.NotFoundException;

/**
 * Tools for viewing the list archives.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface Archiver
{
	/** */
	public static final String JNDI_NAME = "subetha/Archiver/local";

	/**
	 * @return a list of threads in the archive.  Most recent thread
	 *  is at the top.
	 */
	public List<MailSummary> getThreads(Long listId) throws NotFoundException; 
}
