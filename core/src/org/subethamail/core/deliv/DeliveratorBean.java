/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.deliv;

import java.io.IOException;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
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
import org.subethamail.core.util.EntityManipulatorBean;
import org.subethamail.core.util.VERPAddress;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;

/**
 * @author Jeff Schnitzer
 */
@Stateless(name="Deliverator")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
@WebService(name="Deliverator", targetNamespace="http://ws.subethamail.org/", serviceName="DeliveratorService")
@SOAPBinding(style=SOAPBinding.Style.RPC)
public class DeliveratorBean extends EntityManipulatorBean implements Deliverator, DeliveratorRemote
{
	/** */
	private static Log log = LogFactory.getLog(DeliveratorBean.class);
	
	/** */
	@EJB FilterRunner filterRunner;
	@EJB Encryptor encryptor;
	@EJB Detacher detacher;
	
	/** */
	@Resource(mappedName="java:/Mail") private Session mailSession;
	
	/**
	 * @see Deliverator#deliverToEmail(Long, String)
	 */
	@WebMethod
	public void deliverToEmail(Long mailId, String email) throws NotFoundException
	{
		EmailAddress ea = this.em.getEmailAddress(email);
		Mail mail = this.em.get(Mail.class, mailId);		
		deliverTo(mail, ea);
	}
	
	
	/**
	 * @see Deliverator#deliver(Long, Long)
	 */
	@WebMethod
	public void deliver(Long mailId, Long personId) throws NotFoundException
	{

		Mail mail = this.em.get(Mail.class, mailId);
		Person person = this.em.get(Person.class, personId);
		Subscription sub = person.getSubscription(mail.getList().getId());

		if (sub == null || sub.getDeliverTo() == null)
		{
			// User has unsubscribed or decided they don't want mail after all.
			return;
		}

		EmailAddress ea = sub.getDeliverTo();

		deliverTo(mail, ea);
	}
	
	/**
	 * Send a mail directly to an email
	 * 
	 * @param mail the message to send
	 * @param emailAddress the person to send to, if there is one.
	 * 
	 * @throws NotFoundException if something can't be found
	 */
	protected void deliverTo(Mail mail, EmailAddress emailAddress) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Delivering mailId " + mail.getId() + " to email " + emailAddress.getId());
		
		try
		{
			Address destination = new InternetAddress(emailAddress.getId());
			SubEthaMessage msg = new SubEthaMessage(this.mailSession, mail.getContent());
			
			if (log.isDebugEnabled())
				log.debug("Delivering msg of contentType " + msg.getContentType());
			
			// Add an X-Loop header to prevent mail loops, the other
			// end is tested on injection.
			msg.addXLoop(mail.getList().getEmail());
			
			// Precedence: list
			msg.setHeader(SubEthaMessage.HDR_PRECEDENCE, "list");
			
			// Set up the VERP bounce address
			byte[] token = this.encryptor.encryptString(emailAddress.getId());
			String verp = VERPAddress.encodeVERP(mail.getList().getEmail(), token);
			msg.setEnvelopeFrom(verp);
			msg.setHeader(SubEthaMessage.HDR_ERRORS_TO, verp);
			msg.setHeader(SubEthaMessage.HDR_SENDER, verp);

			this.filterRunner.onSend(msg, mail);
			
			this.detacher.attach(msg);
			
			Transport.send(msg, new Address[] { destination });
			
			emailAddress.bounceDecay();
		}
		catch (IgnoreException ex)
		{
			if (log.isDebugEnabled())
				log.debug("Ignoring mail " + mail, ex);
		}
		catch (MessagingException ex)
		{
			log.error("Error delivering mailId " + mail.getId() + " to address " + emailAddress.getId(), ex);
			throw new RuntimeException(ex);
		}
		catch (IOException ex)
		{
			log.error("Error delivering mailId " + mail.getId() + " to address " + emailAddress.getId(), ex);
			throw new RuntimeException(ex);
		}
	}
}

