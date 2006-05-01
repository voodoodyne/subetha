/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.deliv;

import java.io.IOException;

import javax.annotation.EJB;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.deliv.i.Deliverator;
import org.subethamail.core.deliv.i.DeliveratorRemote;
import org.subethamail.core.filter.FilterRunner;
import org.subethamail.core.injector.Detacher;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.util.VERPAddress;
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
	@EJB FilterRunner filterRunner;
	@EJB Encryptor encryptor;
	@EJB Detacher detacher;
	
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
		
		try
		{
			Address destination = new InternetAddress(sub.getDeliverTo().getId());
			
			SubEthaMessage msg = new SubEthaMessage(this.mailSession, mail.getContent());

			// Set up the VERP bounce address
			byte[] token = this.encryptor.encryptString(sub.getDeliverTo().getId());
			msg.setEnvelopeFrom(VERPAddress.encodeVERP(mail.getList().getEmail(), token));
			
			this.filterRunner.onSend(msg, mail);
			
			this.detacher.attach(msg);
			
			Transport.send(msg, new Address[] { destination });
			
			sub.getDeliverTo().bounceDecay();
		}
		catch (IgnoreException ex)
		{
			if (log.isDebugEnabled())
				log.debug("Ignoring mail " + mail, ex);
		}
		catch (MessagingException ex)
		{
			log.error("Error delivering mailId " + mailId + " to personId " + personId, ex);
			throw new RuntimeException(ex);
		}
		catch (IOException ex)
		{
			log.error("Error delivering mailId " + mailId + " to personId " + personId, ex);
			throw new RuntimeException(ex);
		}
	}
}

