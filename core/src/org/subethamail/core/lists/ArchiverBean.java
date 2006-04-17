/*
 * $Id: AccountMgrEJB.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/AccountMgrEJB.java $
 */

package org.subethamail.core.lists;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.EJB;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.Permission;
import org.subethamail.core.lists.i.Archiver;
import org.subethamail.core.lists.i.ArchiverRemote;
import org.subethamail.core.lists.i.MailSummary;
import org.subethamail.core.util.PersonalBean;
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Role;
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
	/** */
	private static Log log = LogFactory.getLog(ArchiverBean.class);

	/**
	 */
	@EJB DAO dao;

	/**
	 * @see Archiver#getThreads(Long)
	 */
	public List<MailSummary> getThreads(Long listId) throws NotFoundException
	{
		// Are we allowed to view archives?
		MailingList list = this.dao.findMailingList(listId);
		Role role = list.getRoleFor(this.getMe());
		
		if (!role.getPermissions().contains(Permission.READ_ARCHIVES))
			throw new IllegalStateException("Not allowed to read archives");
		
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
		boolean showEmail = role.getPermissions().contains(Permission.SEE_ADDRESSES);
		
		// Now generate the entire summary
		return Transmute.mailSummaries(roots, showEmail);
	}

}
