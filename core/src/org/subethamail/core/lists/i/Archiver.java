/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;

import java.util.List;
import javax.ejb.Local;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.PermissionException;

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
	 * Requires Permission.READ_ARCHIVES
	 * 
	 * @return a list of threads in the archive.  Most recent thread is at the top.
	 */
	public List<MailSummary> getThreads(Long listId) throws NotFoundException, PermissionException;
	
	/**
	 * Requires Permission.READ_ARCHIVES
	 * 
	 * @return a whole lot of information about the message.
	 */
	public MailData getMail(Long mailId) throws NotFoundException, PermissionException;

	/**
	 * Sends the mail to the current user.
	 * 
	 * @param mailId the mail to resend
	 * @param email the email to send to
	 * 
	 * @throws NotFoundException if the mail cannot be found or the user is unknown
	 */
	public void sendTo(Long mailId, String email) throws NotFoundException;
}
