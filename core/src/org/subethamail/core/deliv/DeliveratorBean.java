/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.deliv;

import javax.annotation.EJB;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.deliv.i.Deliverator;
import org.subethamail.core.deliv.i.DeliveratorRemote;
import org.subethamail.entity.Mail;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.dao.DAO;

/**
 * @author Jeff Schnitzer
 */
@Stateless(name="Deliverator")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class DeliveratorBean implements Deliverator, DeliveratorRemote
{
	/** */
	private static Log log = LogFactory.getLog(DeliveratorBean.class);
	
	/** */
	@EJB DAO dao;
	
	/** */
	@Resource(mappedName="java:/Mail") private Session mailSession;
	
	/**
	 * @see Deliverator#deliver(Long, Long)
	 */
	public void deliver(Long mailId, Long personId) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Delivering mailId " + mailId + " to personId " + personId);
		
		Mail mail = this.dao.findMail(mailId);
		Person person = this.dao.findPerson(personId);
		
		Subscription sub = person.getSubscription(mail.getList().getId());
		if (sub == null || sub.getDeliverTo() == null)
		{
			// User has unsubscribed or decided they don't want mail after all.
			return;
		}
		
		String destination = sub.getDeliverTo().getId();
		
		try
		{
			SubEthaMessage msg = new SubEthaMessage(this.mailSession, mail.getContent());
			// TODO
		}
		catch (MessagingException ex)
		{
			log.error("Error delivering mailId " + mailId + " to personId " + personId, ex);
			throw new RuntimeException(ex);
		}
	}
}

