/*
 * $Id: Injector.java 201 2006-04-26 08:07:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/core/src/org/subethamail/core/injector/i/Injector.java $
 */

package org.subethamail.core.injector;

import javax.ejb.Local;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.subethamail.entity.Mail;

/**
 * The detacher processes binary attachments in JavaMail messages.
 * When it finds an attachment, it saves the binary data as a blob
 * in the database and replaces the content with a special indicator.
 * 
 * A binary attachment is any mime type that is not multipart/* or text/*.
 * 
 * TODO:  describe the structure of the indicator here.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface Detacher
{
	/** */
	public static final String JNDI_NAME = "subetha/Detacher/local";

	/**
	 * Removes attachments from the mime message, stores the attachments
	 * as blobs, and substitutes in a special content type that indicates
	 * a link back to the database object. 
	 */
	public void detach(MimeMessage msg, Mail ownerMail) throws MessagingException;
	
	/** 
	 * Looks through the mime message for any of the special indicator
	 * link attachments and replaces them with the actual binary content
	 * of the attachment.
	 */
	public void attach(MimeMessage msg) throws MessagingException;
	
}

