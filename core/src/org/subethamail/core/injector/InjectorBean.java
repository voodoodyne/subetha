/*
 * $Id: AccountMgrRemote.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgrRemote.java $
 */

package org.subethamail.core.injector;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.annotation.EJB;
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
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.admin.i.ExpiredException;
import org.subethamail.core.filter.FilterRunner;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.injector.i.InjectorRemote;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.queue.i.Queuer;
import org.subethamail.core.util.VERPAddress;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.Mail.HoldType;
import org.subethamail.entity.dao.DAO;

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
public class InjectorBean implements Injector, InjectorRemote
{
	/** */
	private static Log log = LogFactory.getLog(InjectorBean.class);
	
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
	public static final long MAX_BOUNCE_THRESHOLD = 20;

	/** */
	@EJB DAO dao;
	@EJB Queuer queuer;
	@EJB FilterRunner filterRunner;
	@EJB Encryptor encryptor;
	
	/** */
	@Resource(mappedName="java:/Mail") private Session mailSession;


	/**
	 * @see Injector#inject(String)
	 */
//	@WebMethod
	public void inject(String toAddress, byte[] mailData) throws MessagingException
	{
		if (log.isDebugEnabled())
			log.debug("Injecting message sent to " + toAddress);
		
		// Figure out which list this is for
		InternetAddress addy = new InternetAddress(toAddress);
		
		// Must check for VERP bounce
		VERPAddress verp = VERPAddress.getVERPBounce(addy);
		if (verp != null)
		{
			this.handleBounce(verp);
			return;
		}
		
		MailingList toList;
		try
		{
			toList = this.dao.findMailingList(addy);
		}
		catch (NotFoundException ex)
		{
			// Cautiously ignore.  Maybe we should propagate the exception instead?
			log.error("Unknown destination: " + addy);
			return;
		}
		
		if (log.isDebugEnabled())
			log.debug("Message is for list: " + toList);

		// Parse up the message
		SubEthaMessage msg = new SubEthaMessage(this.mailSession, mailData);
		
		// This will set a messageid if none exists
		msg.saveChanges();
		
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
			
			return;
		}
		catch (HoldException ex)
		{
			if (log.isDebugEnabled())
				log.debug("Plugin holding message", ex);
			
			hold = HoldType.MODERATOR;
			holdMsg = ex.getMessage();
		}
		
		// Figure out who sent it, if we know
		Person author = this.findPersonFrom(msg);
		
		if (log.isDebugEnabled())
			log.debug("Message author is: " + author);
		
		// Find out if the message should be held for moderation
		if (hold == null)
		{
			if (author == null || !author.isSubscribed(toList))
				hold = HoldType.SELF;
		}
		
		if (log.isDebugEnabled())
			log.debug("Moderate this message:  " + hold);

		Mail mail = this.createMail(msg, toList, hold);
		
		if (mail.getHold() != null)
		{
			// Send instructions so that user can self-moderate
			// Or send a message saying "you must wait for admin approval", use holdMsg if available
		}
		else
		{
			this.queuer.queueForDelivery(mail.getId());
		}
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
			
			EmailAddress found = this.dao.findEmailAddress(originalEmail);
			
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
	 * Creates a piece of mail and hooks it into the thread hierarchy (if it can).
	 */
	Mail createMail(SubEthaMessage msg, MailingList toList, HoldType hold) throws MessagingException
	{
		// Figure out parent, if there is one
		Mail parent = null;
		String parentMessageId;
		
		try
		{
			parent = this.findParentMail(msg);
			parentMessageId = parent.getMessageId();
			
			if (log.isDebugEnabled())
				log.debug("Message parent is: " + parent);
		}
		catch (UnknownParentException ex)
		{
			parentMessageId = ex.getParentMessageId();
		}
		
		// Create a mail object
		Mail mail = new Mail(msg, toList, parent, parentMessageId, hold);
		this.dao.persist(mail);
		
		if (parent != null)
			parent.getReplies().add(mail);

		// Now look for any children of this mail
		if (parentMessageId != null)
		{
			List<Mail> replies = this.dao.findRepliesToMail(parentMessageId);
			for (Mail reply: replies)
			{
				reply.setParent(mail);
				mail.getReplies().add(reply);
			}
		}
		
		return mail;
	}
	
	/**
	 * Figures out which person sent the message, if it was anyone we know.
	 * @return null if we can't figure it out
	 */
	Person findPersonFrom(SubEthaMessage msg) throws MessagingException
	{
		InternetAddress[] froms = (InternetAddress[])msg.getFrom();
		if (froms != null)
		{
			for (InternetAddress fromAddy: froms)
			{
				try
				{
					return this.dao.findEmailAddress(fromAddy.getAddress()).getPerson();
				}
				catch (NotFoundException ex) {}
			}
		}
		
		return null;
	}
	
	/**
	 * Figures out, best we can, what the parent Mail object is.
	 * 
	 * @throws UnknownParentException if the parent cannot be identified.
	 */
	Mail findParentMail(SubEthaMessage msg) throws UnknownParentException, MessagingException
	{
		// First try message id
		String inReplyTo = msg.getInReplyTo();
		if (inReplyTo != null)
		{
			try
			{
				return this.dao.findMailByMessageId(inReplyTo);
			}
			catch (NotFoundException ex) {}
		}
		
		// Next try the first reference.  Everything else in the references
		// list should be farther up the hierarchy and so we don't want them.
		String reference = null;
		
		String[] refs = msg.getReferences();
		if (refs != null && refs.length > 0)
			reference = refs[refs.length-1];
		
		if (reference != null)
		{
			try
			{
				return this.dao.findMailByMessageId(reference);
			}
			catch (NotFoundException ex) {}
		}
		
		// The reference is more likely to be a correctly
		// formatted message id, even though it is less likely
		// to be the one we want.
		String preferred = (reference != null) ? reference : inReplyTo;
		
		throw new UnknownParentException(preferred);
	}
	
	/**
	 * @see Injector#log(byte[])
	 */
	public void log(byte[] mailData) throws MessagingException
	{
		SubEthaMessage msg = new SubEthaMessage(this.mailSession, mailData);
		
		log.info("Message-ID is: " + msg.getMessageID());
		log.info("In-Reply-To is: " + msg.getInReplyTo());
		
		String[] refs = msg.getReferences();
		if (refs == null)
			log.info("Null References");
		else
			for (String ref: refs)
				log.info("Has Reference: " + ref);
	}
	
	/**
	 * Internal exception thrown when no parent Mail for a message
	 * could be found.  The exception message will be the best guess
	 * at what the parent Message-ID will be.
	 * 
	 * It might also be null if nothing could be figured out!
	 */
	@SuppressWarnings("serial")
	static class UnknownParentException extends Exception
	{
		/** */
		public UnknownParentException(String parentMessageId)
		{
			super(parentMessageId);
		}
		
		/**
		 * @return the best guess, or null if nothing could be determined.  
		 */
		public String getParentMessageId()
		{
			return this.getMessage();
		}
	}
}
