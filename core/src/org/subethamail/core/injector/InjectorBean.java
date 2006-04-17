/*
 * $Id: AccountMgrRemote.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgrRemote.java $
 */

package org.subethamail.core.injector;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
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
		
		// Make sure we have a unique message id
		this.checkMessageId(toList, msg);
		
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

		Mail mail = new Mail(msg, toList, hold);
		this.dao.persist(mail);
		
		if (mail.getHold() != null)
		{
			// Send instructions so that user can self-moderate
			// Or send a message saying "you must wait for admin approval", use holdMsg if available
		}
		else
		{
			this.threadMail(mail, msg);
			this.queuer.queueForDelivery(mail.getId());
		}
	}
	
	/**
	 * Ensures that the message has a unique message id.
	 */
	protected void checkMessageId(MailingList list, SubEthaMessage msg) throws MessagingException
	{
		String messageId = msg.getMessageID();
		
		if (messageId == null)
		{
			msg.replaceMessageID();
		}
		else
		{
			try
			{
				this.dao.findMailByMessageId(list.getId(), messageId);
				msg.replaceMessageID();
			}
			catch (NotFoundException ex) {}
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
				parent = this.dao.findMailByMessageId(mail.getList().getId(), candidate);
				
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
		mail.setParent(parent);
		mail.setWantedReference(wantedReference);
		
		//
		// STEP THREE:  Find anyone looking for us as a parent and insert us in
		// their thread ancestry.
		//
		List<Mail> descendants = this.dao.findMailWantingParent(mail.getList().getId(), mail.getMessageId());
		for (Mail descendant: descendants)
		{
			if (log.isDebugEnabled())
				log.debug("Replacing parent of " + descendant);
			
			// Remove from the old parent
			Mail oldParent = descendant.getParent();
			oldParent.getReplies().remove(descendant);
			
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
}
