/*
 * $Id: AccountMgrRemote.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgrRemote.java $
 */

package org.subethamail.core.injector;

import javax.annotation.EJB;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.injector.i.InjectorRemote;
import org.subethamail.core.plugin.PluginRunner;
import org.subethamail.core.queue.i.Queuer;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Mail.ModerationState;
import org.subethamail.entity.dao.DAO;
import org.subethamail.pluginapi.IgnoreException;

/**
 * @author Jeff Schnitzer
 */
@Stateless
//@SecurityDomain("subetha")
//@RunAs("god")
public class InjectorEJB implements Injector, InjectorRemote
{
	/** */
	private static Log log = LogFactory.getLog(InjectorEJB.class);

	/** */
	@EJB DAO dao;
	@EJB Queuer queuer;
	@EJB PluginRunner pluginRunner;
	
	/** */
	@Resource(mappedName="java:/Mail") private Session mailSession;


	/**
	 * @see Injector#inject(String)
	 */
	public void inject(String toAddress, byte[] mailData) throws MessagingException
	{
		if (log.isDebugEnabled())
			log.debug("Injecting message sent to " + toAddress);
		
		// Figure out which list this is for
		InternetAddress addy = new InternetAddress(toAddress);
		
		// Must split this out and check for -bounces, -subscribe, etc
		
		MailingList toList;
		try
		{
			toList = this.dao.findMailingListByAddress(addy.getAddress());
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
		
		// Start with a moderationstate of FREE
		ModerationState modState = null;
		
		// Run it through the plugin stack
		try
		{
			if (this.pluginRunner.onInject(msg, toList))
				modState = ModerationState.ADMIN;
		}
		catch (IgnoreException ex)
		{
			if (log.isDebugEnabled())
				log.debug("Plugin ignoring message", ex);
			
			return;
		}
		
		// Figure out who sent it, if we know
		Person author = this.findPersonFrom(msg);
		
		if (log.isDebugEnabled())
			log.debug("Message author is: " + author);
		
		// Figure out parent, if there is one
		Mail parent = this.findParentMail(msg);
		
		if (log.isDebugEnabled())
			log.debug("Message parent is: " + parent);
		
		// Find out if the message should be held for moderation
		if (modState == null)
		{
			if (author == null)
				modState = ModerationState.SELF;
			else
			{
				// TODO:  if author is not subscribed, it should also be self
			}
		}
		
		if (log.isDebugEnabled())
			log.debug("Moderate this message:  " + modState);

		// Create a mail object
		Mail mail = new Mail(msg, toList, parent, modState);
		
		// Create associated entity
		this.dao.persist(mail);

		if (mail.getModerationState() != null)
		{
			// Send instructions so that user can self-moderate
			// Or send a message saying "you must wait for admin approval"
		}
		else
		{
			this.queuer.queueForDelivery(mail.getId());
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
					return this.dao.findEmailAddressByAddress(fromAddy.getAddress()).getOwner();
				}
				catch (NotFoundException ex) {}
			}
		}
		
		return null;
	}
	
	/**
	 * Figures out, best we can, what the parent Mail object is.
	 * 
	 * @return null if parent cannot be identified.
	 */
	Mail findParentMail(SubEthaMessage msg) throws MessagingException
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
		
		// Next try going through the References
		String[] refs = msg.getReferences();
		if (refs != null)
		{
			for (String ref: refs)
			{
				try
				{
					return this.dao.findMailByMessageId(ref);
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
