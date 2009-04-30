/*
 * $Id: Archiver.java 902 2007-01-15 03:00:15Z skot $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/Archiver.java $
 */

package org.subethamail.core.lists.i;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.ejb.Local;

import org.subethamail.common.ExportMessagesException;
import org.subethamail.common.ImportMessagesException;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.SearchException;
import org.subethamail.entity.i.PermissionException;

/**
 * Tools for viewing the list archives.
 *
 * @author Jeff Schnitzer
 * @author scotthernandez
 */
@Local
public interface Archiver
{
	/** */
	public static final String JNDI_NAME = "subetha/Archiver/local";

	/**
	 * Requires Permission.VIEW_ARCHIVES
	 * 
	 * @return a list of threads in the archive.  Most recent thread is at the bottom.
	 */
	public List<MailSummary> getThreads(Long listId, int skip, int count) throws NotFoundException, PermissionException;
	
	/**
	 * Requires Permission.VIEW_ARCHIVES
	 * 
	 * @return returns the mail thread from the current message.  Most recent thread is at the bottom.
	 */
	public MailSummary getThread(Long mailId) throws NotFoundException, PermissionException;

	/**
	 * Requires Permission.VIEW_ARCHIVES
	 * 
	 * @param mailId The message in the thread to return
	 * @return An array of MailData objects in the same thread.
	 * @throws NotFoundException Well, what can I say, it wasn't found.
	 * @throws PermissionException You know what this means if you get it!
	 */
	public MailData[] getThreadMessages(Long mailId) throws NotFoundException, PermissionException;

	/**
	 * Requires Permission.VIEW_ARCHIVES
	 * 
	 * @return a paginated list of messages that match the criteria.
	 */
	public SearchResult search(Long listId, String query, int skip, int count) throws NotFoundException, PermissionException, SearchException;
	
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

	/**
	 * Exports the messages to the output stream given.
	 * 
	 * @param msgIds The messages to write out
	 * @param format The format to write out
	 * @param outStream The stream to write to
	 * @throws  
	 */
	public void exportMessages(Long[] msgIds, ExportFormat format, OutputStream outStream) throws NotFoundException, PermissionException, ExportMessagesException;
	
	/**
	 * Exports the list messages to the output stream given.
	 * 
	 * @param msgIds The messages to write out
	 * @param format The format to write out
	 * @param outStream The stream to write to
	 */
	public void exportList(Long listId, ExportFormat format, OutputStream outStream) throws NotFoundException, PermissionException, ExportMessagesException;
		
	/**
	 * Deletes a message from an archive.
	 * 
	 * Requires Permission.DELETE_ARCHIVES
	 * 
	 * @return the id of the list to which the msg belongs
	 */
	public Long deleteMail(Long mailId) throws NotFoundException, PermissionException;
	
	/**
	 * Post a new message to the list.  Starts a new thread.
	 * 
	 * Requires Permission.POST.  Must also be logged in.
	 * 
	 * @param listId The id of the mailing list.
	 * @param fromAddress must be one of the caller's valid email addresses
	 * @param body The message body.
	 * 
	 * @throws NotFoundException if listId is not valid 
	 */
	public void post(String fromAddress, Long listId, String subject, String body) throws NotFoundException, PermissionException;

	/**
	 * Replies to an existing message on a list.
	 * 
	 * Requires Permission.POST.  Must also be logged in.
	 * 
	 * @param msgId The id of the message we are replying to
	 * @param fromAddress must be one of the caller's valid email addresses
	 * @param body The message body.
	 * @return the id of the list to which the msg belongs
	 * 
	 * @throws NotFoundException if msgId is not valid
	 */
	public Long reply(String fromAddress, Long msgId, String subject, String body) throws NotFoundException, PermissionException;

}
