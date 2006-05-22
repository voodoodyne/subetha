/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.injector;

import java.io.IOException;

import javax.ejb.Local;
import javax.mail.MessagingException;
import javax.mail.internet.MimePart;

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
	 * a link back to the database object.  Recursively descends the mime
	 * tree.
	 */
	public void detach(MimePart part, Mail ownerMail) throws MessagingException, IOException;
	
	/** 
	 * Looks through the mime message for any of the special indicator
	 * link attachments and replaces them with the actual binary content
	 * of the attachment.  Recursively descends the mime tree.
	 */
	public void attach(MimePart part) throws MessagingException, IOException;
	
}

