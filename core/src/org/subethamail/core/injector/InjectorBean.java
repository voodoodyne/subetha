/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.injector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.EJB;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
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
import org.subethamail.common.io.LimitingInputStream;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.admin.i.ExpiredException;
import org.subethamail.core.filter.FilterRunner;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.injector.i.InjectorRemote;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.post.PostOffice;
import org.subethamail.core.queue.i.Queuer;
import org.subethamail.core.util.EntityManipulatorBean;
import org.subethamail.core.util.OwnerAddress;
import org.subethamail.core.util.VERPAddress;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.Mail.HoldType;
import org.subethamail.entity.i.Permission;

/**
 * @author Jeff Schnitzer
 */
@Stateless(name="Injector")
@SecurityDomain("subetha")
@PermitAll
@RunAs("siteAdmin")
//@WebService(
//		name="InjectorEndpoint", 
//		targetNamespace="http://www.subethamila.org/injector",
//		serviceName="InjectorService")
public class InjectorBean extends EntityManipulatorBean implements Injector, InjectorRemote
{
	/** */
	private static Log log = LogFactory.getLog(InjectorBean.class);
	
	/**
	 * We bounce injected messages larger than this amount, in bytes.
	 * Must be hardcoded because JavaMail creates a byte[] representation,
	 * and we don't want huge files taking down our JVM.  
	 */
	public static final int MAX_MESSAGE_BYTES = 1000 * 1000 * 10;
	
	/**
	 * The oldest bounce message we are willing to consider valid.  This
	 * is to prevent anyone that finds an old bounce token for a user
	 * from using it to mailiciously unsubscribe that person.
	 */
	public static final long MAX_BOUNCE_AGE_MILLIS = 1000 * 60 * 60 * 24 * 7;

	/**
	 * The maximum bounce count at which we disable mail delivery for a user.
	 * This is not actually a literal number of bounces, it's a number that
	 * tends to go up as bounces occur and tends to go down when bounces do
	 * not occur.
	 */
	public static final long MAX_BOUNCE_THRESHOLD = 7;
	
	/**
	 * Minimum number of milliseconds between hold notification emails.  It might
	 * be longer if a steady stream arrives.  The logic actually only sends a
	 * notification if it has been more than this interval since the last message
	 * from the user was held.  Currently 24 hours.
	 */
	public static final long MIN_HOLD_NOTIFICATION_INTERVAL_MILLIS = 1000 * 60 * 60 * 24;

	/** */
	@EJB Queuer queuer;
	@EJB FilterRunner filterRunner;
	@EJB Encryptor encryptor;
	@EJB Detacher detacher;
	@EJB PostOffice postOffice;
	
	/** */
	@Resource(mappedName="java:/Mail") private Session mailSession;

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.i.Injector#accept(java.lang.String)
	 */
	public boolean accept(String toAddress) throws MessagingException
	{
		if (log.isDebugEnabled())
			log.debug("Checking if we want address " + toAddress);
		
		InternetAddress addy = new InternetAddress(toAddress);
		
		// Maybe it's a VERP bounce?
		VERPAddress verp = VERPAddress.getVERPBounce(addy.getAddress());
		if (verp != null)
			addy = new InternetAddress(verp.getEmail());	// check if this is for a list here
		else
		{
			// Maybe it's an owner address?
			String ownerList = OwnerAddress.getList(addy.getAddress());
			if (ownerList != null)
				addy = new InternetAddress(ownerList);
		}
		
		try
		{
			this.em.getMailingList(addy);
			return true;
		}
		catch (NotFoundException ex)
		{
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.i.Injector#inject(java.lang.String, java.lang.String, byte[])
	 */
//	@WebMethod
	public boolean inject(String fromAddress, String toAddress, byte[] mailData) throws MessagingException, IOException
	{
		return this.inject(fromAddress, toAddress, new ByteArrayInputStream(mailData));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.i.Injector#inject(java.lang.String, java.lang.String, byte[])
	 */
	public boolean inject(String envelopeSender, String envelopeRecipient, InputStream mailData) throws MessagingException, IOException
	{
		if (log.isDebugEnabled())
			log.debug("Injecting message sent to " + envelopeRecipient);
		
		InternetAddress senderAddy = new InternetAddress(envelopeSender);
		InternetAddress recipientAddy = new InternetAddress(envelopeRecipient);
		
		// Immediately check to see if the envelope sender is a verp address.  If it is,
		// convert it into an -owner address.  This magic allows lists to subscribe to lists.
		VERPAddress senderVerp = VERPAddress.getVERPBounce(senderAddy.getAddress());
		if (senderVerp != null)
			senderAddy = new InternetAddress(OwnerAddress.makeOwner(senderAddy.getAddress()));
		
		// Must check for recipient VERP bounce
		VERPAddress recipientVerp = VERPAddress.getVERPBounce(recipientAddy.getAddress());
		if (recipientVerp != null)
		{
			this.handleBounce(recipientVerp);
			return true;
		}
		
		// Check for -owner mail
		String listForOwner = OwnerAddress.getList(envelopeRecipient);
		if (listForOwner != null)
			recipientAddy = new InternetAddress(listForOwner);
		
		// Figure out which list this is for
		MailingList toList;
		try
		{
			toList = this.em.getMailingList(recipientAddy);
		}
		catch (NotFoundException ex)
		{
			log.error("Unknown destination: " + recipientAddy);
			return false;
		}
		
		if (log.isDebugEnabled())
			log.debug("Message is for list: " + toList);

		// Parse up the message
		mailData = new LimitingInputStream(mailData, MAX_MESSAGE_BYTES);
		SubEthaMessage msg = new SubEthaMessage(this.mailSession, mailData);
		
		// If the message is looping, silently accept and drop it.  The
		// X-Loop header is added by the deliverator.
		if (msg.hasXLoop(toList.getEmail()))
			return true;
		
		// Now that we have the basic building blocks, see if we
		// should be forwarding the mail to owners instead
		if (listForOwner != null)
		{
			this.handleOwnerMail(toList, msg);
			return true;
		}
		
		// Make sure we have a unique message id
		if (this.isDuplicateMessage(toList, msg))
			msg.replaceMessageID();
		
		// If it stays null, no moderation required
		HoldType hold = null;
		String holdMsg = null;
		
		// Run it through the plugin stack
		try
		{
			this.filterRunner.onInject(msg, toList);
		}
		catch (IgnoreException ex)
		{
			if (log.isDebugEnabled())
				log.debug("Plugin ignoring message", ex);
			
			// Maybe we should return false instead?
			return true;
		}
		catch (HoldException ex)
		{
			if (log.isDebugEnabled())
				log.debug("Plugin holding message", ex);
			
			hold = HoldType.HARD;
			holdMsg = ex.getMessage();
		}
		
		// Find out if the message should be held for moderation
		if (hold == null)
		{
			// Figure out who sent it, if we know
			Person author = null;
			
			EmailAddress addy = this.em.findEmailAddress(senderAddy.getAddress());
			if (addy != null)
				author = addy.getPerson();
			
			if (log.isDebugEnabled())
				log.debug("Message author is: " + author);
			
			if (!toList.getPermissionsFor(author).contains(Permission.POST))
				hold = HoldType.SOFT;
		}
		
		if (log.isDebugEnabled())
			log.debug("Hold?  " + hold);

		Mail mail = new Mail(senderAddy, msg, toList, hold);
		this.em.persist(mail);
		
		// Convert all binary attachments to references and then set the content
		this.detacher.detach(msg, mail);
		mail.setContent(msg);
		
		if (mail.getHold() != null)
		{
			// We don't want to send too many of these notification messages
			// because they might cause too much backscatter from spam.  We
			// compromise on sending at most one per time period.
			Mail lastMail = this.em.findLastMailHeldFrom(senderAddy.getAddress());
			if (lastMail.getArrivalDate().getTime()
					< System.currentTimeMillis() - MIN_HOLD_NOTIFICATION_INTERVAL_MILLIS)
			{
				// Send instructions so that user can self-moderate
				// Or send a message saying "you must wait for admin approval", use holdMsg if available
				this.postOffice.sendPosterMailHoldNotice(toList, senderAddy.getAddress(), mail, holdMsg);
			}
			
			if (hold == HoldType.HARD)
			{
				for (Subscription sub: toList.getSubscriptions())
				{
					if (sub.getRole().getPermissions().contains(Permission.APPROVE_MESSAGES))
						if (sub.getDeliverTo() != null)
							this.postOffice.sendModeratorMailHoldNotice(sub.getDeliverTo(), toList, mail, msg, holdMsg);
				}
			}
		}
		else
		{
			this.threadMail(mail, msg);
			this.queuer.queueForDelivery(mail.getId());
		}
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.i.Injector#importMessage(java.lang.Long, java.lang.String, java.io.InputStream, boolean, java.util.Date)
	 */
	public Date importMessage(Long listId, String envelopeSender, InputStream mailData, boolean ignoreDuplicate, Date fallbackDate) throws NotFoundException, MessagingException, IOException
	{
		if (log.isDebugEnabled())
			log.debug("Importing message from " + envelopeSender + " into list " + listId);

		InternetAddress senderAddy = new InternetAddress(envelopeSender);

		// Figure out which list this is for
		MailingList toList = this.em.get(MailingList.class, listId);
		
		// Parse up the message
		mailData = new LimitingInputStream(mailData, MAX_MESSAGE_BYTES);
		SubEthaMessage msg = new SubEthaMessage(this.mailSession, mailData);
		
		// Check to see if the message is a duplicate.
		if (this.isDuplicateMessage(toList, msg)) 
		{
			// Are we dropping duplicates? 
			if (ignoreDuplicate)
				return msg.getSentDate();
			else
				msg.replaceMessageID();
		}

		// If it stays null, no moderation required
		HoldType hold = null;
		
		// Run it through the plugin stack
		try
		{
			this.filterRunner.onInject(msg, toList);
		}
		catch (IgnoreException ex)
		{
			if (log.isDebugEnabled())
				log.debug("Plugin ignoring message", ex);
			
			return msg.getSentDate();
		}
		catch (HoldException ex)
		{
			if (log.isDebugEnabled())
				log.debug("Plugin holding message", ex);
			
			hold = HoldType.HARD;
		}

		Date sentDate = msg.getSentDate();
		if (sentDate == null)
			sentDate = fallbackDate;
				
		Mail mail = new Mail(senderAddy, msg, toList, hold, sentDate);
		this.em.persist(mail);
		
		// Convert all binary attachments to references and then set the content
		this.detacher.detach(msg, mail);
		mail.setContent(msg);
		
		if (mail.getHold() == null)
			this.threadMail(mail, msg);
		
		return msg.getSentDate();
	}
	
	/**
	 * Forwards the message to all owners of the list.  The return
	 * address will be VERPed.
	 * 
	 * @param list list whose owner(s) should receive the mail
	 * @param msg is the message to forward
	 */
	private void handleOwnerMail(MailingList list, SubEthaMessage msg) throws MessagingException
	{
		for (Subscription sub: list.getSubscriptions())
		{
			if (sub.getRole().isOwner() && sub.getDeliverTo() != null)
			{
				Address destination = new InternetAddress(sub.getDeliverTo().getId());
				
				// Set up the VERP bounce address
				byte[] token = this.encryptor.encryptString(sub.getDeliverTo().getId());
				msg.setEnvelopeFrom(VERPAddress.encodeVERP(list.getEmail(), token));
				
				Transport.send(msg, new Address[] { destination });
				
				sub.getDeliverTo().bounceDecay();
			}
		}
	}

	/**
	 * @return true if a message with the id already exists in the list
	 */
	protected boolean isDuplicateMessage(MailingList list, SubEthaMessage msg) throws MessagingException
	{
		String messageId = msg.getMessageID();
		
		if (messageId != null)
		{
			try
			{
				this.em.getMailByMessageId(list.getId(), messageId);
				return true;
			}
			catch (NotFoundException ex) 
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Handle a bounce message.  Note we don't really care what the
	 * message data was, just what the VERP address was.
	 */
	protected void handleBounce(VERPAddress verp)
	{
		if (log.isDebugEnabled())
			log.debug("Handling bounce from list " + verp.getEmail());
		
		try
		{
			String originalEmail = this.encryptor.decryptString(verp.getToken(), MAX_BOUNCE_AGE_MILLIS);

			if (log.isDebugEnabled())
				log.debug("Bounced from " + originalEmail);
			
			EmailAddress found = this.em.getEmailAddress(originalEmail);
			
			found.bounceIncrement();
			
			if (found.getBounces() > MAX_BOUNCE_THRESHOLD)
			{
				// Unsubscribe the address from any delivery
				for (Subscription sub: found.getPerson().getSubscriptions().values())
				{
					if (found.equals(sub.getDeliverTo()))
					{
						sub.setDeliverTo(null);

						if (log.isWarnEnabled())
							log.warn("Stopping delivery of " + sub.getList().getName() + " to " + found.getId() + " due to excessive bounces");
					}
				}
				
				// TODO: somehow notify the person, perhaps at one of their other addresses?
				// TODO: notify the administrator?
			}
		}
		catch (NotFoundException ex)
		{
			// User already unsubscribed the address, great
			log.debug("Address already removed");
		}
		catch (ExpiredException ex)
		{
			// Token is too old, ignore it
			log.debug("Token is too old");
		}
		catch (GeneralSecurityException ex)
		{
			// Problem decoding the token?  Someone's messing with us.
			log.debug("Token is invalid");
		}
	}

	/**
	 * Places the mail in a thread hierarchy, possibly updating a variety
	 * of other mails which are part of the hierarchy.
	 * 
	 * When this completes, the mail may have a parent and children.
	 */
	void threadMail(Mail mail, SubEthaMessage msg) throws MessagingException
	{
		if (log.isDebugEnabled())
			log.debug("Threading mail " + mail);
		
		//
		// STEP ONE:  Figure who we want to be our parent.
		//
		List<String> wantedReference = new ArrayList<String>();
		
		// If there is an InReplyTo, that tops the list
		String inReplyTo = msg.getInReplyTo();
		if (inReplyTo != null)
			wantedReference.add(inReplyTo);
			
		// Now add the references field.  Careful that the
		// last reference might be duplicate of inReplyTo
		String[] references = msg.getReferences();
		if (references != null)
		{
			// Walk it backwards because recent refs are at the end
			for (int i=(references.length-1); i>=0; i--)
			{
				if (!wantedReference.contains(references[i]))
					wantedReference.add(references[i]);
			}
		}
		
		if (log.isDebugEnabled())
			log.debug("Wanted references is " + wantedReference);
		
		//
		// STEP TWO:  Find an acceptable candidate to be our thread parent,
		// and eliminate any candidates that are inferior.
		//
		Mail parent = null;
		
		for (int i=0; i<wantedReference.size(); i++)
		{
			String candidate = wantedReference.get(i);
			
			try
			{
				parent = this.em.getMailByMessageId(mail.getList().getId(), candidate);
				
				// Got one, eliminate anything from wantedReference that is at this
				// level or later.  Do it from the end to make ArrayList happy.
				for (int delInd=wantedReference.size()-1; delInd>=i; delInd--)
					wantedReference.remove(delInd);
				
				break;
			}
			catch (NotFoundException ex) {}
		}
		
		if (log.isDebugEnabled())
			log.debug("Found thread ancestor " + parent);
		
		// Intermission - we can now set the mail's parent and wantedReference
		if (parent != null)
		{
			mail.setParent(parent);
			parent.getReplies().add(mail);
		}
		
		mail.setWantedReference(wantedReference);
		
		//
		// STEP THREE:  Find anyone looking for us as a parent and insert us in
		// their thread ancestry.  Watch out for loops.
		//
		List<Mail> descendants = this.em.findMailWantingParent(mail.getList().getId(), mail.getMessageId());
		outer: for (Mail descendant: descendants)
		{
			if (log.isDebugEnabled())
				log.debug("Replacing parent of " + descendant);
			
			// Check for a loop
			Mail checkForLoop = descendant;
			while (checkForLoop != null)
			{
				if (checkForLoop == mail)
				{
					if (log.isWarnEnabled())
						log.warn("Found a mail loop, parent=" + mail + ", child=" + descendant);
					
					// Ignore this link and remove the wanted rerference
					descendant.getWantedReference().remove(mail.getMessageId());

					continue outer;
				}
				
				checkForLoop = checkForLoop.getParent();
			}
			
			// Remove from the old parent, if exists
			if (descendant.getParent() != null)
			{
				Mail oldParent = descendant.getParent();
				oldParent.getReplies().remove(descendant);
			}
			
			// Add the new parent
			descendant.setParent(mail);
			mail.getReplies().add(descendant);
			
			// Prune the wantedReference collection, possibly to empty
			List<String> wanted = descendant.getWantedReference();
			for (int i=wanted.size()-1; i>=0; i--)
			{
				String removed = wanted.remove(i);
				if (mail.getMessageId().equals(removed))
					break;
			}
		}
	}
}
