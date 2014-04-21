package org.subethamail.core.injector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimePart;
import javax.mail.util.ByteArrayDataSource;

import lombok.extern.java.Log;

import org.subethamail.common.MailUtils;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.common.io.TrivialDataSource;
import org.subethamail.core.util.BlobImpl;
import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.entity.Attachment;
import org.subethamail.entity.Mail;

/**
 * @author Jeff Schnitzer
 */
@Dependent
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Log
public class DetacherBean implements Detacher
{
	/**
	 * The was an stream.reset() on the getInputStream method, was this the cause of Issue 44? Is it still needed for a specific reason?
	 */
    private static final boolean INPUTSTREAM_RESET_NOT_NEEDED_OR_ISSUE_44 = true;
	/** */
	@Inject @SubEtha SubEthaEntityManager em;
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.Detacher#detach(javax.mail.internet.MimePart, org.subethamail.entity.Mail)
	 */
	public void detach(MimePart part, Mail ownerMail) throws MessagingException, IOException
	{
	    log.log(Level.FINE,"Attempting to detach {0} of type {1}", new Object[]{part, part.getContentType()});

		String contentType = part.getContentType().toLowerCase();
		
		if (contentType.startsWith("multipart/"))
		{
		    log.log(Level.FINE,"Content is multipart");
			
			Multipart multi = (Multipart)part.getContent();
			
			// This is necessary because of the mysterious JavaMail bug 4404733
			part.setContent(multi);
			
			for (int i=0; i<multi.getCount(); i++)
				this.detach((MimeBodyPart)multi.getBodyPart(i), ownerMail);
		}
		else if (contentType.startsWith("text/") && !Part.ATTACHMENT.equals(part.getDisposition()))
		{
		    log.log(Level.FINE,"Leaving text alone");
		}
		else
		{
			// It would be better if we could do this by knowing which
			// mime types produce Part
			Object content = part.getContent();
			
			if (content instanceof MimePart)
			{
			    log.log(Level.FINE,"Content {0} is part, probably a message", contentType);
				
				this.detach((MimePart)content, ownerMail);
			}
			else
			{
				// Actually detach the sucka
			    log.log(Level.FINE,"Detaching an attachment of type {0}", contentType);
				
				InputStream input = part.getInputStream();
				
				Blob blobby = new BlobImpl(input, input.available());
				
				Attachment attach = new Attachment(ownerMail, blobby, contentType);
				this.em.persist(attach);
				ownerMail.getAttachments().add(attach);
				
				part.setContent(attach.getId(), SubEthaMessage.DETACHMENT_MIME_TYPE);
				
				part.setHeader(SubEthaMessage.HDR_ORIGINAL_CONTENT_TYPE, contentType);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.Detacher#attach(javax.mail.internet.MimePart)
	 */
    @SuppressWarnings("deprecation")
	public void attach(MimePart part) throws MessagingException, IOException
	{
	    log.log(Level.FINE,"Attempting reattachment for {0} of type {1}", new Object[]{part, part.getContentType()});

		String contentType = part.getContentType().toLowerCase();
		
		if (contentType.startsWith("multipart/"))
		{
		    log.log(Level.FINE,"Content is multipart");
			
			Multipart multi = (Multipart)part.getContent();
			
			// This is necessary because of the mysterious JavaMail bug 4404733
			part.setContent(multi);

			for (int i=0; i<multi.getCount(); i++)
				this.attach((MimePart)multi.getBodyPart(i));
		}
		else if (contentType.startsWith(SubEthaMessage.DETACHMENT_MIME_TYPE))
		{
			Long attachmentId = (Long)part.getContent();

			log.log(Level.FINE,"Reattaching attachment {0} for type {1}", new Object[]{attachmentId, contentType});

			try
			{
				Attachment att = this.em.get(Attachment.class, attachmentId);
                if (INPUTSTREAM_RESET_NOT_NEEDED_OR_ISSUE_44)
                {
                    String ct = att.getContentType();
                    ByteArrayDataSource bads = new ByteArrayDataSource(att.getContentStream(), ct);
                    //this was lazy, now it always runs. What is the performance impact (~6 string searches/operations)?
                    if (ct!=null) bads.setName(MailUtils.getNameFromContentType(ct));
                    DataHandler dh = new DataHandler(bads);
                    
                    part.setDataHandler(dh);
                }
                else
                {
    				part.setDataHandler(
    						new DataHandler(
    								new TrivialDataSource(
    										att.getContentStream(),
    										att.getContentType())));
                }
				part.removeHeader(SubEthaMessage.HDR_ORIGINAL_CONTENT_TYPE);
			}
			catch (NotFoundException ex)
			{
				// Log an error and otherwise leave the mime part as-is.
			    log.log(Level.SEVERE,"Missing referenced attachment {0}", attachmentId);
			}
		}
		else
		{
			Object content = part.getContent();
			
			if (content instanceof MimePart)
			{
			    log.log(Level.FINE,"Content {0} is part, probably a message", contentType );
				
				this.attach((MimePart)content);
			}
			else
			{
			    log.log(Level.FINE,"Ignoring part of type {0}", contentType);
			}
		}
	}
	
}
