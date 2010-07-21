/*
 * $Id: InjectorBean.java 993 2009-03-07 01:36:59Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/injector/InjectorBean.java $
 */

package org.subethamail.core.injector;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.MailUtils;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.common.io.LimitExceededException;
import org.subethamail.common.io.LimitingInputStream;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.admin.i.ExpiredException;
import org.subethamail.core.filter.FilterRunner;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.post.OutboundMTA;
import org.subethamail.core.post.PostOffice;
import org.subethamail.core.queue.InjectQueue;
import org.subethamail.core.queue.InjectedQueueItem;
import org.subethamail.core.util.OwnerAddress;
import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.core.util.VERPAddress;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.Mail.HoldType;
import org.subethamail.entity.i.Permission;

import com.caucho.remote.HessianService;


/**
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Stateless(name="Injector")
@RolesAllowed(Person.ROLE_ADMIN)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@HessianService(urlPattern="/api/Injector")
public class InjectorBean implements Injector
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(InjectorBean.class);

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

	/**
	 * How far back to look for the parent of a message by subject.
	 */
	public static final long MAX_SUBJECT_THREAD_PARENT_AGE_MILLIS = 1000L * 60L * 60L * 24L * 30L;

	/** The "inbound queue" which processes injections, unfortunately Resin's CDI trips on the generic */
	//@Inject @InjectQueue BlockingQueue<InjectedQueueItem> inboundQueue;
	@SuppressWarnings("rawtypes")
	@Inject @InjectQueue BlockingQueue inboundQueue;
	
	@Inject FilterRunner filterRunner;
	@Inject Encryptor encryptor;
	@Inject Detacher detacher;
	@Inject PostOffice postOffice;

	/** */
	@Inject @OutboundMTA Session mailSession;

	/** */
	@Inject @SubEtha SubEthaEntityManager em;

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.i.Injector#accept(java.lang.String)
	 */
	public boolean accept(String toAddress)
	{
		if (log.isDebugEnabled())
			log.debug("Checking if we want address " + toAddress);

		try
		{
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
		catch (MessagingException ex) { throw new RuntimeException(ex); }
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.i.Injector#inject(java.lang.String, java.lang.String, byte[])
	 */
	public boolean inject(String fromAddress, String toAddress, byte[] mailData) throws LimitExceededException
	{
		return this.inject(fromAddress, toAddress, new ByteArrayInputStream(mailData));
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.i.Injector#inject(java.lang.String, java.lang.String, byte[])
	 */
	public boolean inject(String envelopeSender, String envelopeRecipient, InputStream mailData) throws LimitExceededException
	{
		if (log.isDebugEnabled())
		{
			if (!mailData.markSupported())
				mailData = new BufferedInputStream(mailData);

			mailData.mark(8192);
		}

		try
		{
			return this.injectImpl(envelopeSender, envelopeRecipient, mailData);
		}
		catch (LimitExceededException ex) { throw ex; }
		catch (Exception ex)
		{
			if (log.isDebugEnabled())
			{
				try
				{
					mailData.reset();
					Reader reader = new InputStreamReader(mailData);
					StringBuilder builder = new StringBuilder();
					while (reader.ready())
						builder.append((char)reader.read());

					log.debug("Mail body was: " + builder);
				}
				catch (IOException e) {}
			}

			if (ex instanceof RuntimeException)
				throw (RuntimeException)ex;
			else
				throw new RuntimeException(ex);
		}
	}

	/**
	 * Factors out the exception catching.
	 */
	@SuppressWarnings("unchecked")
	protected boolean injectImpl(String envelopeSender, String envelopeRecipient, InputStream mailData) throws MessagingException, LimitExceededException, IOException
	{
		if (log.isDebugEnabled())
			log.debug("Injecting message sent to " + envelopeRecipient);

		InternetAddress recipientAddy = new InternetAddress(envelopeRecipient);

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
			hold = this.holdOrNot(msg, envelopeSender, toList);

		if (log.isDebugEnabled())
			log.debug("Hold?  " + hold);

		Mail mail = new Mail(envelopeSender, msg, toList, hold);
		this.em.persist(mail);
		this.em.flush();

		// Convert all binary attachments to references and then set the content
		this.detacher.detach(msg, mail);
		mail.setContent(msg);

		if (mail.getHold() != null)
		{
			InternetAddress senderAddy = msg.getSenderWithFallback(envelopeSender);
			if (senderAddy != null)
			{
				// We don't want to send too many of these notification messages
				// because they might cause too much backscatter from spam.  We
				// compromise on sending at most one per time period.
				Date cutoff = new Date(System.currentTimeMillis() - MIN_HOLD_NOTIFICATION_INTERVAL_MILLIS);

				int recentHolds = this.em.countRecentHeldMail(senderAddy.getAddress(), cutoff);
				if (recentHolds <= 1)	// count includes the current held msg
				{
					// Send instructions so that user can self-moderate
					// Or send a message saying "you must wait for admin approval", use holdMsg if available
					this.postOffice.sendPosterMailHoldNotice(toList, senderAddy.getAddress(), mail, holdMsg);
				}
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
			
			try
			{
				this.inboundQueue.put(new InjectedQueueItem(mail));
			}
			catch (InterruptedException ex) { throw new RuntimeException(ex); }
		}

		return true;
	}

	/**
	 * Note that holding a message is an "or" process, checking the
	 * Sender field, each of the From fields (there can be more than
	 * one), and the envelope sender.  Anything that allows the message
	 * to go through is ok.  Also note that HARD overrides
	 * SOFT.
	 * 
	 * @return the appropriate hold for the message to the given list, or null
	 *  if no hold is necessary and the email can go through.
	 */
	protected HoldType holdOrNot(SubEthaMessage msg, String envelopeSender, MailingList toList) throws MessagingException
	{
		// Note that any and all of these fields could be null.
		HoldType hold = this.holdOrNot((InternetAddress)msg.getSender(), toList);
		if (hold == null)
			return null;

		Address[] froms = msg.getFrom();
		if (froms != null)
		{
			for (Address addy: froms)
			{
				HoldType fromHold = this.holdOrNot((InternetAddress)addy, toList);
				if (fromHold == null)
					return null;
				else
					hold = this.prioritize(hold, fromHold);
			}
		}
		
		InternetAddress envelope = new InternetAddress(envelopeSender);
		HoldType envHold = this.holdOrNot(envelope, toList);
		if (envHold == null)
			return null;
		else
			return this.prioritize(hold, envHold);
	}
	
	/**
	 * The "priorities" of holds - HARD overrides SOFT, but null overrides both
	 */
	protected HoldType prioritize(HoldType a, HoldType b)
	{
		if (a == null || b == null)
			return null;
		else if (a.equals(HoldType.HARD) || b.equals(HoldType.HARD))
			return HoldType.HARD;
		else
			return HoldType.SOFT;
	}
	
	/**
	 * @return the appropriate hold for the given sender to the specified list,
	 *  or null if there should be no hold.
	 */
	protected HoldType holdOrNot(InternetAddress senderAddy, MailingList toList)
	{
		// Figure out who sent it, if we know
		Person author = null;

		if (senderAddy != null)
		{
			EmailAddress addy = this.em.findEmailAddress(senderAddy.getAddress());
			if (addy != null)
				author = addy.getPerson();
		}

		if (log.isDebugEnabled())
			log.debug("Checking hold status for " + author + " to list " + toList.getName());

		if (toList.getPermissionsFor(author).contains(Permission.POST))
		{
			return null;
		}
		else
		{
			// No permission - if the user is anonymous, this is a SOFT hold (ie spam)
			// If the user is actually a subscriber, let's make it a HARD hold so the
			// moderators are alerted.
			if (author != null && author.getSubscription(toList.getId()) != null)
				return HoldType.HARD;
			else
				return HoldType.SOFT;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.i.Injector#importMessage(java.lang.Long, java.lang.String, java.io.InputStream, boolean, java.util.Date)
	 */
	public Date importMessage(Long listId, String envelopeSender, InputStream mailData, boolean ignoreDuplicate, Date fallbackDate) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Importing message from " + envelopeSender + " into list " + listId);

		try
		{
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

			Mail mail = new Mail(envelopeSender, msg, toList, hold, sentDate);
			this.em.persist(mail);

			// Convert all binary attachments to references and then set the content
			this.detacher.detach(msg, mail);
			mail.setContent(msg);

			if (mail.getHold() == null)
				this.threadMail(mail, msg);

			return msg.getSentDate();
		}
		catch (MessagingException ex) { throw new RuntimeException(ex); }
		catch (IOException ex) { throw new RuntimeException(ex); }
	}

	/**
	 * Forwards the message to all owners of the list.  The return
	 * address will be VERPed.
	 *
	 * @param list list whose owner(s) should receive the mail
	 * @param msg is the message to forward
	 */
	protected void handleOwnerMail(MailingList list, SubEthaMessage msg) throws MessagingException
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
	protected void threadMail(Mail mail, SubEthaMessage msg) throws MessagingException
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

				if (log.isDebugEnabled())
					log.debug("Found parent at choice " + i + ", max " + (wantedReference.size()-1));

				// Got one, eliminate anything from wantedReference that is at this
				// level or later.  Do it from the end to make ArrayList happy.
				for (int delInd=wantedReference.size()-1; delInd>=i; delInd--)
					wantedReference.remove(delInd);

				break;
			}
			catch (NotFoundException ex) {}
		}

		// If that didn't help us, try the desperate step of matching on subject
		if (parent == null)
		{
			String subj = mail.getSubject();
			subj = MailUtils.cleanRe(subj);

			Date cutoff = new Date(System.currentTimeMillis() - MAX_SUBJECT_THREAD_PARENT_AGE_MILLIS);

			// This is a little ugly - grab two because the current mail will show up in the search
			List<Mail> found = this.em.findRecentMailBySubject(mail.getList().getId(), subj, cutoff, 2);
			for (Mail foundMail: found)
			{
				// The current mail itself should show up in the search
				if (foundMail == mail)
					continue;

				parent = foundMail;

				if (log.isDebugEnabled())
					log.debug("Found parent using subject match");

				break;
			}
		}

		if (log.isDebugEnabled())
			log.debug("Best thread ancestor found is " + parent);

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