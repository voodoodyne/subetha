/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.injector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import javax.activation.DataHandler;
import javax.annotation.EJB;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.lob.BlobImpl;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.common.io.TrivialDataSource;
import org.subethamail.entity.Attachment;
import org.subethamail.entity.Mail;
import org.subethamail.entity.dao.DAO;

/**
 * @author Jeff Schnitzer
 */
@Stateless(name="Detacher")
@SecurityDomain("subetha")
@PermitAll
@RunAs("siteAdmin")
public class DetacherBean implements Detacher
{
	/** */
	private static Log log = LogFactory.getLog(DetacherBean.class);
	
	/** */
	@EJB DAO dao;

	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.Detacher#detach(javax.mail.Part, Mail)
	 */
	public void detach(Part part, Mail ownerMail) throws MessagingException, IOException
	{
		if (log.isDebugEnabled())
			log.debug("Attempting to detach " + part + " of type " + part.getContentType());
		
		Object content = part.getContent();

		String contentType = part.getContentType();
		if(contentType != null) contentType = contentType.toLowerCase();
			
		String disposition = part.getDisposition();
		if(disposition != null) disposition = disposition.toLowerCase();
		
		if (content instanceof Multipart)
		{
			if (log.isDebugEnabled())
				log.debug("Content is multipart");
			
			Multipart multi = (Multipart)content;

			// This is necessary because of the mysterious JavaMail bug 4404733
			part.setContent(multi);
			
			for (int i=0; i<multi.getCount(); i++)
				this.detach(multi.getBodyPart(i), ownerMail);
		}
		else if (content instanceof Part)
		{
			if (log.isDebugEnabled())
				log.debug("Content is part, probably a message");
			
			this.detach((Part)content, ownerMail);
		}
		else if (	!"attachment".equals(disposition) && 
					(contentType != null) &&
					contentType.startsWith("text/"))
		{
			if (log.isDebugEnabled())
				log.debug("Leaving text alone");
			
			// Text parts can stay, but we don't want anyone faking references
			part.removeHeader(SubEthaMessage.HDR_ATTACHMENT_REF);
		}
		else
		{
			// We need to detach it
			if (log.isDebugEnabled())
				log.debug("Detaching an attachment of type " + part.getContentType());
			
			InputStream input = part.getInputStream();
			Blob blobby = new BlobImpl(input, input.available());
			
			Attachment attach = new Attachment(ownerMail, blobby, part.getContentType());
			this.dao.persist(attach);
			ownerMail.getAttachments().add(attach);
			
			//save a reference to the attachment.id
			part.setHeader(SubEthaMessage.HDR_ATTACHMENT_REF, attach.getId().toString());
			
			try 
			{
				part.setText(attach.getId().toString());
			}
			catch (NullPointerException npe)
			{
				if(log.isDebugEnabled()) log.debug("Ignoring NullPointerException from part.setText(Null)");
			}
			
			if (log.isDebugEnabled())
				log.debug("set part.text to null:" + part.getContent().getClass().toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.Detacher#attach(javax.mail.Part)
	 */
	public void attach(Part part) throws MessagingException, IOException
	{
		if (log.isDebugEnabled())
			log.debug("Attempting reattachment for " + part + " of type " + part.getContentType());
		
		Object content = part.getContent();

		if (content instanceof Multipart)
		{
			if (log.isDebugEnabled())
				log.debug("Content is multipart");
			
			Multipart multi = (Multipart)content;
			
			// This is necessary because of the mysterious JavaMail bug 4404733
			part.setContent(multi);

			for (int i=0; i<multi.getCount(); i++)
				this.attach(multi.getBodyPart(i));
		}
		else if (content instanceof Part)
		{
			if (log.isDebugEnabled())
				log.debug("Content is part, probably a message");
			
			this.attach((Part)content);
		}
		else
		{
			// Look for special header which means we must reattach.
			String[] headers = part.getHeader(SubEthaMessage.HDR_ATTACHMENT_REF);
			if (headers != null && headers.length > 0)
			{
				// There should only be one
				Long attachmentId = Long.parseLong(headers[0]);

				if (log.isDebugEnabled())
					log.debug("Reattaching attachment " + attachmentId + " for type " + part.getContentType());
				
				
				try
				{
					Attachment att = this.dao.findAttachment(attachmentId);
					
					part.removeHeader(SubEthaMessage.HDR_ATTACHMENT_REF);
					part.setDataHandler(
							new DataHandler(
									new TrivialDataSource(
											att.getContentStream(),
											att.getContentType())));
				}
				catch (NotFoundException ex)
				{
					// Log an error and otherwise leave the mime part as-is.
					if (log.isErrorEnabled())
						log.error("Missing referenced attachment " + attachmentId);
				}
			}
		}
	}
	
}
