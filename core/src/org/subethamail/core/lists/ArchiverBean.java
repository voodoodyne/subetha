/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.EJB;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.MailUtils;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.Permission;
import org.subethamail.common.PermissionException;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.deliv.i.Deliverator;
import org.subethamail.core.filter.FilterRunner;
import org.subethamail.core.injector.Detacher;
import org.subethamail.core.lists.i.Archiver;
import org.subethamail.core.lists.i.ArchiverRemote;
import org.subethamail.core.lists.i.AttachmentPartData;
import org.subethamail.core.lists.i.InlinePartData;
import org.subethamail.core.lists.i.MailData;
import org.subethamail.core.lists.i.MailSummary;
import org.subethamail.core.lists.i.TextPartData;
import org.subethamail.core.util.PersonalBean;
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.Attachment;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.dao.DAO;

/**
 * Implementation of the Archiver interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="Archiver")
@SecurityDomain("subetha")
@PermitAll
@RunAs("siteAdmin")
public class ArchiverBean extends PersonalBean implements Archiver, ArchiverRemote
{

	@EJB Deliverator deliverator;
	@EJB FilterRunner filterRunner;
	@EJB Detacher detacher;
	@EJB DAO dao;
	
	/** */
	private static Log log = LogFactory.getLog(ArchiverBean.class);

	/** */
	@Resource(mappedName="java:/Mail") private Session mailSession;

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.Archiver#sendTo(java.lang.Long, String email)
	 */
	public void sendTo(Long mailId, String email) throws NotFoundException
	{
		this.deliverator.deliver(mailId, email);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.Archiver#getThreads(java.lang.Long)
	 */
	public List<MailSummary> getThreads(Long listId) throws NotFoundException, PermissionException
	{
		Person me = this.getMe();
		
		// Are we allowed to view archives?
		MailingList list = this.getListFor(listId, Permission.READ_ARCHIVES, me);
		
		List<Mail> mails = this.dao.findMailByList(listId, 0, 100000);
		
		// This is fun.  Assemble the thread relationships.
		SortedSet<Mail> roots = new TreeSet<Mail>();
		for (Mail mail: mails)
		{
			Mail parent = mail;
			while (parent.getParent() != null)
				parent = parent.getParent();

			roots.add(parent);
		}
		
		// Figure out if we're allowed to see emails
		boolean showEmail = list.getPermissionsFor(me).contains(Permission.VIEW_ADDRESSES);
		
		// Now generate the entire summary
		return Transmute.mailSummaries(roots, showEmail, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.Archiver#getMessage(java.lang.Long, OutputStream)
	 */
	public void writeMessage(Long mailId, OutputStream stream) throws NotFoundException, PermissionException
	{
		Mail mail = this.getMailFor(mailId, Permission.READ_ARCHIVES);

		try 
		{
			SubEthaMessage msg = new SubEthaMessage(this.mailSession, mail.getContent());

			this.filterRunner.onSend(msg, mail);
			
			this.detacher.attach(msg);
			
			msg.writeTo(stream);
		} 
		catch (Exception e)
		{
			if (log.isDebugEnabled()) log.debug("error getting exception getting mail#" + mailId + "\n" + e.toString());
		}	
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.Archiver#writeAttachment(java.lang.Long, java.io.OutputStream)
	 */
	public void writeAttachment(Long attachmentId, OutputStream stream) throws NotFoundException, PermissionException
	{
		Attachment a = this.dao.findAttachment(attachmentId);
		a.getMail().getList().checkPermission(getMe(), Permission.READ_ARCHIVES);

		Blob data = a.getContent();
		try
		{
			BufferedInputStream bis = new BufferedInputStream(data.getBinaryStream());	

			int stuff;
			while ((stuff = bis.read()) >= 0)
				stream.write(stuff);
		}
		catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		catch (IOException ex) 
		{
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.Archiver#getAttachmentContentType(java.lang.Long)
	 */
	public String getAttachmentContentType(Long attachmentId) throws NotFoundException, PermissionException
	{
		Attachment a = this.dao.findAttachment(attachmentId);
		a.getMail().getList().checkPermission(getMe(), Permission.READ_ARCHIVES);
		return a.getContentType();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.Archiver#getMail(java.lang.Long)
	 */
	public MailData getMail(Long mailId) throws NotFoundException, PermissionException
	{
		Person me = this.getMe();
		
		// Are we allowed to view archives?
		Mail mail = this.getMailFor(mailId, Permission.READ_ARCHIVES, me);
		
		// Figure out if we're allowed to see emails
		boolean showEmail = mail.getList().getPermissionsFor(me).contains(Permission.VIEW_ADDRESSES);
		
		MailData data = this.makeMailData(mail, showEmail);
		
		Mail root = mail;
		while (root.getParent() != null)
			root = root.getParent();
		
		// This trick inserts us into the thread hierarchy we create.
		data.setThreadRoot(Transmute.mailSummary(root, showEmail, data));
		
		return data;
	}

	/**
	 * Makes the base mail data.  Doesn't set the threadRoot.
	 */
	protected MailData makeMailData(Mail raw, boolean showEmail) throws NotFoundException
	{
		try
		{
			InternetAddress addy = raw.getFromAddress();
		
			SubEthaMessage msg = new SubEthaMessage(this.mailSession, raw.getContent());
			
			List<InlinePartData> inlineParts = new ArrayList<InlinePartData>();
			List<AttachmentPartData> attachmentParts = new ArrayList<AttachmentPartData>();

			for (Part part : msg.getParts()) 
			{
				Object content = part.getContent();
				
				//get an id to see if it is an attachment
				Long id =  null;	

				String[] idHeader = part.getHeader(SubEthaMessage.HDR_ATTACHMENT_REF);
				if (idHeader != null && idHeader.length > 0) id = Long.parseLong(idHeader[0]);
	
				String contentType = (id == null || id.equals("")) ? part.getContentType() : this.dao.findAttachment(id).getContentType();

				//figure out the name, if there is one.
				String name = part.getFileName();
				if (name == null || name.equals(""))
				{
					name = MailUtils.getNameFromContentType(contentType);
				}
				
				// not an attachment cause it isn't stored as a detached part.
				if (id == null) 
				{
					InlinePartData ipd;
					if (content instanceof String)
					{
						ipd = new TextPartData((String)content, part.getContentType(), name, part.getSize());
					}
					else 
					{
						ipd = new InlinePartData(content, part.getContentType(), name, part.getSize());
					}
						
					inlineParts.add(ipd);
				}
				//it has an id so it is an attachment.
				else
				{					
					AttachmentPartData apd = new AttachmentPartData(id, contentType, name, part.getSize());
					attachmentParts.add(apd);
				}
			}
			
			return new MailData(
					raw.getId(),
					raw.getSubject(),
					showEmail ? addy.getAddress() : null,
					addy.getPersonal(),
					raw.getDateCreated(),
					Transmute.mailSummaries(raw.getReplies(), showEmail, null),
					raw.getList().getId(),
					inlineParts,
					attachmentParts);
		}
		catch (MessagingException ex)
		{
			// Should be impossible since everything was already run through
			// JavaMail when the data was imported.
			throw new RuntimeException(ex);
		}
		catch (IOException ex)
		{
			// Ditto
			throw new RuntimeException(ex);
		}
	}
}
