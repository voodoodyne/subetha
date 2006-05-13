/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.Permission;
import org.subethamail.common.PermissionException;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.lists.i.Archiver;
import org.subethamail.core.lists.i.ArchiverRemote;
import org.subethamail.core.lists.i.MailData;
import org.subethamail.core.lists.i.MailSummary;
import org.subethamail.core.util.PersonalBean;
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;

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
	/** */
	private static Log log = LogFactory.getLog(ArchiverBean.class);

	/** */
	@Resource(mappedName="java:/Mail") private Session mailSession;

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
	protected MailData makeMailData(Mail raw, boolean showEmail)
	{
		try
		{
			InternetAddress addy = raw.getFromAddress();
		
			SubEthaMessage msg = new SubEthaMessage(this.mailSession, raw.getContent());
			
			return new MailData(
					raw.getId(),
					raw.getSubject(),
					showEmail ? addy.getAddress() : null,
					addy.getPersonal(),
					raw.getDateCreated(),
					Transmute.mailSummaries(raw.getReplies(), showEmail, null),
					raw.getList().getId(),
					msg.getTextParts());
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
