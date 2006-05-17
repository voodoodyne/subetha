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
	
	/** 
	 * The name of the header for detached attachment references.  The
	 * value will be the numeric id of the attachment. 
	 */
	public static String HDR_ATTACHMENT_REF = "X-SubEtha-Attachment";
	
	/** */
	@EJB DAO dao;

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.Detacher#detach(javax.mail.Part, org.subethamail.entity.Mail)
	 */
	public void detach(Part part, Mail ownerMail) throws MessagingException, IOException
	{
		if (log.isDebugEnabled())
			log.debug("Detaching " + part + " of type " + part.getContentType());
		
		Object content = part.getContent();

		if (content instanceof Multipart)
		{
			if (log.isDebugEnabled())
				log.debug("Content is multipart");
			
			Multipart multi = (Multipart)content;
			
			for (int i=0; i<multi.getCount(); i++)
				this.detach(multi.getBodyPart(i), ownerMail);
		}
		else if (content instanceof Part)
		{
			if (log.isDebugEnabled())
				log.debug("Content is part, probably a message");
			
			this.detach((Part)content, ownerMail);
		}
		else if (part.getContentType().toLowerCase().startsWith("text/"))
		{
			if (log.isDebugEnabled())
				log.debug("Leaving text alone");
			
			// Text parts can stay, but we don't want anyone faking references
			part.removeHeader(HDR_ATTACHMENT_REF);
		}
		else
		{
			// We need to detach it
			if (log.isDebugEnabled())
				log.debug("Detaching an attachment of type " + part.getContentType());
			
			InputStream input = part.getDataHandler().getInputStream();
			Blob blobby = new BlobImpl(input, input.available());
			
			Attachment attach = new Attachment(ownerMail, blobby, part.getContentType());
			this.dao.persist(attach);
			ownerMail.getAttachments().add(attach);
			
			part.setHeader(HDR_ATTACHMENT_REF, attach.getId().toString());
			part.setText(attach.getId().toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.Detacher#attach(javax.mail.Part)
	 */
	public void attach(Part part) throws MessagingException, IOException
	{
		if (log.isDebugEnabled())
			log.debug("Reattaching " + part + " of type " + part.getContentType());
		
		Object content = part.getContent();

		if (content instanceof Multipart)
		{
			if (log.isDebugEnabled())
				log.debug("Content is multipart");
			
			Multipart multi = (Multipart)content;
			
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
			String[] headers = part.getHeader(HDR_ATTACHMENT_REF);
			if (headers != null && headers.length > 0)
			{
				// There should only be one
				Long attachmentId = Long.parseLong(headers[0]);
				
				try
				{
					Attachment att = this.dao.findAttachment(attachmentId);
					
					part.removeHeader(HDR_ATTACHMENT_REF);
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
