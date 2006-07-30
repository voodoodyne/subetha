/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.ejb.Remote;

import org.subethamail.common.ImportMessagesException;
import org.subethamail.common.NotFoundException;
import org.subethamail.entity.i.PermissionException;

/**
 * Tools for viewing the list archives.
 *
 * @author Jeff Schnitzer
 */
@Remote
public interface Archiver
{
	/** */
	public static final String JNDI_NAME = "subetha/Archiver/remote";

	/**
	 * Requires Permission.VIEW_ARCHIVES
	 * 
	 * @return a list of threads in the archive.  Most recent thread is at the bottom.
	 */
	public List<MailSummary> getThreads(Long listId, int skip, int count) throws NotFoundException, PermissionException;
	
	/**
	 * Requires Permission.VIEW_ARCHIVES
	 * 
	 * @return a paginated list of messages that match the criteria.
	 */
	public SearchResult search(Long listId, String query, int skip, int count) throws NotFoundException, PermissionException;
	
	/**
	 * Required Permission.VIEW_ARCHIVES
	 * 
	 * @return the number of messages on a list.
	 */
	public int countMailByList(Long listId);

	/**
	 * Requires Permission.VIEW_ARCHIVES
	 * 
	 * @return a whole lot of information about the message.
	 */
	public MailData getMail(Long mailId) throws NotFoundException, PermissionException;

	/**
	 * Requires Permission.VIEW_ARCHIVES
	 * Writes the Mail to the stream. 
	 */
	public void writeMessage(Long mailId, OutputStream stream) throws NotFoundException, PermissionException;

	/**
	 * Requires Permission.VIEW_ARCHIVES
	 * Writes the Attachement to the stream.
	 * @return a byte[] of the message as it would be sent to the user.
	 */
	public void writeAttachment(Long attachmentId, OutputStream stream) throws NotFoundException, PermissionException;

	/**
	 * 
	 * @param attachmentId the attachment to get
	 * @return the content type for the attachment
	 * @throws NotFoundException if the attachement can not be found
	 * @throws PermissionException if your permission is not enough
	 */
	public String getAttachmentContentType(Long attachmentId) throws NotFoundException, PermissionException;

	/**
	 * Sends the mail to the current user.
	 * 
	 * @param mailId the mail to resend
	 * @param email the email to send to
	 * 
	 * @throws NotFoundException if the mail cannot be found or the user is unknown
	 */
	public void sendTo(Long mailId, String email) throws NotFoundException;
	
	/**
	 * Imports messages into a List.
	 * 
	 * @param listId the list to insert the messages into
	 * @param mboxStream the mbox stream to read messages from
	 * 
	 * @return the number of messages imported
	 * 
	 * @throws NotFoundException if the list is not found
	 * @throws PermissionException if the user has not IMPORT_MESSAGES permissions
	 */
	public int importMessages(Long listId, InputStream mboxStream) throws NotFoundException, PermissionException, ImportMessagesException;
}
