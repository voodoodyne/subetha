package org.subethamail.core.deliv;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import lombok.extern.java.Log;

import org.subethamail.common.NotFoundException;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.deliv.i.Deliverator;
import org.subethamail.core.filter.FilterRunner;
import org.subethamail.core.injector.Detacher;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.post.OutboundMTA;
import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.core.util.VERPAddress;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;

/**
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Stateless
@RolesAllowed(Person.ROLE_ADMIN)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Log
public class DeliveratorBean implements Deliverator
{
	/** */
	@Inject FilterRunner filterRunner;
	@Inject Encryptor encryptor;
	@Inject Detacher detacher;

	/** */
	@Inject @OutboundMTA Session mailSession;

	/** */
	@Inject @SubEtha SubEthaEntityManager em;

	/**
	 * @see Deliverator#deliverToEmail(Long, String)
	 */
	public void deliverToEmail(Long mailId, String email) throws NotFoundException
	{
		EmailAddress ea = this.em.getEmailAddress(email);
		Mail mail = this.em.get(Mail.class, mailId);
		this.deliverTo(mail, ea);
	}


	/**
	 * @see Deliverator#deliver(Long, Long)
	 */
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

		this.deliverTo(mail, ea);
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
	    log.log(Level.FINE,"Delivering mailId {0} to email {1}", new Object[]{mail.getId(), emailAddress.getId()});

		try
		{
			Address destination = new InternetAddress(emailAddress.getId());
			SubEthaMessage msg = new SubEthaMessage(this.mailSession, mail.getContent());

			String listEmail = mail.getList().getEmail();
			
			log.log(Level.FINE,"Delivering msg of contentType {0}", msg.getContentType());

			// Add an X-Loop header to prevent mail loops, the other
			// end is tested on injection.
			msg.addXLoop(listEmail);

			// Precedence: list
			msg.setHeader(SubEthaMessage.HDR_PRECEDENCE, "list");

			// Set up the VERP bounce address
			byte[] token = this.encryptor.encryptString(emailAddress.getId());
			String verp = VERPAddress.encodeVERP(listEmail, token);
			msg.setEnvelopeFrom(verp);
			msg.setHeader(SubEthaMessage.HDR_ERRORS_TO, verp);
			
			//Sender is always the list address. This is so that lists can send to other lists. 
			msg.setHeader(SubEthaMessage.HDR_SENDER, listEmail);

			this.filterRunner.onSend(msg, mail);

			this.detacher.attach(msg);

			Transport.send(msg, new Address[] { destination });

			emailAddress.bounceDecay();
		}
		catch (IgnoreException ex)
		{
		    LogRecord logRecord=new LogRecord(Level.FINE, "Ignoring mail {0}");
		    logRecord.setParameters(new Object[]{mail});
		    logRecord.setThrown(ex);
			log.log(logRecord);
		}
		catch (MessagingException ex)
		{
            LogRecord logRecord=new LogRecord(Level.SEVERE, "Error delivering mailId {0} to address {1}");
            logRecord.setParameters(new Object[]{mail.getId(), emailAddress.getId()});
            logRecord.setThrown(ex);
            log.log(logRecord);
			throw new RuntimeException(ex);
		}
		catch (IOException ex)
		{
            LogRecord logRecord=new LogRecord(Level.SEVERE, "Error delivering mailId {0} to address {1}");
            logRecord.setParameters(new Object[]{mail.getId(), emailAddress.getId()});
            logRecord.setThrown(ex);
            log.log(logRecord);
			throw new RuntimeException(ex);
		}
	}
}