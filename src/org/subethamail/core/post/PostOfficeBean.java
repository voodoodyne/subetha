/*
 * $Id: PostOfficeBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/post/PostOfficeBean.java $
 */

package org.subethamail.core.post;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import lombok.extern.java.Log;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.admin.SiteSettings;
import org.subethamail.core.admin.i.Eegor;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.post.i.Constant;
import org.subethamail.core.post.i.MailType;
import org.subethamail.core.util.OwnerAddress;
import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.core.util.VERPAddress;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.SubscriptionHold;

/**
 * Implementation of the PostOffice interface.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Stateless(name="PostOffice")
@Log
public class PostOfficeBean implements PostOffice
{
	/** */
	@Inject @OutboundMTA Session mailSession;

	/** */
	@Inject Encryptor encryptor;

	/** */
	@Inject Eegor brainBringer;
	
	/** */
	@Inject @SubEtha SubEthaEntityManager em;
	
	/** */
	@Inject SiteSettings settings;
	
	/** 
	 * Builds a message from a velocity template, context, and some
	 * information about sender and recipient. 
	 */
	class MessageBuilder
	{
		SubEthaMessage message;
		InternetAddress toAddress;
		InternetAddress fromAddress;
		String senderEmail;
		
		/** */
		public MessageBuilder(MailType kind, VelocityContext vctx)
		{
			StringWriter writer = new StringWriter(4096);
			
			try
			{
				Velocity.mergeTemplate(kind.getTemplate(), "UTF-8", vctx, writer);
			}
			catch (Exception ex)
			{
			    LogRecord logRecord=new LogRecord(Level.SEVERE,"Error merging {0}");;
			    logRecord.setParameters(new Object[]{kind.getTemplate()});
			    logRecord.setThrown(ex);
                log.log(logRecord);			    
				throw new EJBException(ex);
			}

			String mailSubject = (String)vctx.get("subject");
			String mailBody = writer.toString();
			
			// If in dev mode, annotate the subject for unit tests
			if (brainBringer.isTestModeEnabled())
				mailSubject = kind.name() + " " + mailSubject;

			try
			{
				this.message = new SubEthaMessage(mailSession);
				this.message.setSubject(mailSubject);
				this.message.setText(mailBody);
			}
			catch (MessagingException ex) { throw new RuntimeException(ex); }
		}
		
		/** */
		public void setTo(EmailAddress to)
		{
			to.bounceDecay();
			try
			{
				this.toAddress = new InternetAddress(to.getId(), to.getPerson().getName());
			}
			catch (UnsupportedEncodingException ex) { throw new RuntimeException(ex); }
		}
		
		/** */
		public void setTo(String to)
		{
			try
			{
				this.toAddress = new InternetAddress(to);
			}
			catch (AddressException ex) { throw new RuntimeException(ex); }
		}
		
		/** Must call setTo first */
		public void setFrom(MailingList list)
		{
			if (this.toAddress == null)
				throw new IllegalStateException("Must call setTo() first");
			
			// Set the list owner as the pretty from field
			String ownerAddress = OwnerAddress.makeOwner(list.getEmail());
			try
			{
				this.fromAddress = new InternetAddress(ownerAddress, list.getName());
			}
			catch (UnsupportedEncodingException ex) { throw new RuntimeException(ex); }
			
			// Set up the VERP bounce address as the envelope sender
			byte[] token = encryptor.encryptString(this.toAddress.getAddress());
			this.senderEmail = VERPAddress.encodeVERP(list.getEmail(), token);
		}
		
		/** Must call setTo first */
		public void setFrom(InternetAddress from)
		{
			if (this.toAddress == null)
				throw new IllegalStateException("Must call setTo() first");
			
			this.fromAddress = from;
		}
		
		/** Must call setTo first */
		public void setFrom(String from)
		{
			if (this.toAddress == null)
				throw new IllegalStateException("Must call setTo() first");
			
			try
			{
				this.fromAddress = new InternetAddress(from);
			}
			catch (AddressException ex) { throw new RuntimeException(ex); }
		}
		
		/**
		 * @return the message we have built.
		 */
		public SubEthaMessage getMessage() throws MessagingException
		{
			this.message.setRecipient(Message.RecipientType.TO, this.toAddress);
			this.message.setFrom(this.fromAddress);
			this.message.setEnvelopeFrom(this.senderEmail);
			
			// This minimizes the likelyhood of getting autoreplies
			this.message.setHeader("Precedence", "junk");

			return this.message;
		}
		
		/**
		 * Sends the message through JavaMail
		 */
		public void send()
		{
			try
			{
				Transport.send(this.getMessage());
			}
			catch (MessagingException ex) { throw new RuntimeException(ex); }
		}
	}
	/**
	 * Maybe modifies the token with developer/test information which can be picked out
	 * by the unit tester.
	 */
	protected String token(String tok)
	{
		return !this.brainBringer.isTestModeEnabled() ? tok : Constant.DEBUG_TOKEN_BEGIN + tok + Constant.DEBUG_TOKEN_END;
	}
	
	/**
	 * @see PostOffice#sendPassword(EmailAddress)
	 */
	public void sendPassword(EmailAddress addy)
	{
	    log.log(Level.FINE,"Sending password for {0}", addy.getId());
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("addy", addy);

		if (addy.getPerson().getSubscriptions().size() == 1)
		{
			// If only one list, make it appear that mail comes from there
			Subscription sub = addy.getPerson().getSubscriptions().values().iterator().next();
			vctx.put("url", sub.getList().getUrlBase());
			
			MessageBuilder builder = new MessageBuilder(MailType.FORGOT_PASSWORD, vctx);
			builder.setTo(addy);
			builder.setFrom(sub.getList());
			builder.send();
		}
		else
		{
			URL url = this.settings.getDefaultSiteUrl();
			vctx.put("url", url.toString());
			
			MessageBuilder builder = new MessageBuilder(MailType.FORGOT_PASSWORD, vctx);
			builder.setTo(addy);
			builder.setFrom(this.settings.getPostmaster());
			
			builder.send();
		}
	}

	/**
	 * @see PostOffice#sendConfirmSubscribeToken(MailingList, String, String)
	 */
	public void sendConfirmSubscribeToken(MailingList list, String email, String token)
	{
	    log.log(Level.FINE,"Sending subscribe token to {0}", email);
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("token", this.token(token));
		vctx.put("email", email);
		vctx.put("list", list);

		MessageBuilder builder = new MessageBuilder(MailType.CONFIRM_SUBSCRIBE, vctx);
		builder.setTo(email);
		builder.setFrom(list);
		builder.send();
	}

	/**
	 * @see PostOffice#sendSubscribed(MailingList, Person, EmailAddress)
	 */
	public void sendSubscribed(MailingList relevantList, Person who, EmailAddress deliverTo)
	{
	    log.log(Level.FINE,"Sending welcome to list msg to {0}", who);

		String email;
		if (deliverTo != null)
			email = deliverTo.getId();
		else
			email = who.getEmailAddresses().values().iterator().next().getId();
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("list", relevantList);
		vctx.put("person", who);
		vctx.put("email", email);
		
		MessageBuilder builder = new MessageBuilder(MailType.YOU_SUBSCRIBED, vctx);
		builder.setTo(email);
		builder.setFrom(relevantList);
		builder.send();
	}

	/**
	 * @see PostOffice#sendOwnerNewMailingList(MailingList, EmailAddress)
	 */
	public void sendOwnerNewMailingList(MailingList relevantList, EmailAddress address)
	{
	    log.log(Level.FINE,"Sending notification of new mailing list {0} to owner {1}", new Object[]{relevantList, address});
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("addy", address);
		vctx.put("list", relevantList);
		
		MessageBuilder builder = new MessageBuilder(MailType.NEW_MAILING_LIST, vctx);
		builder.setTo(address);
		builder.setFrom(relevantList);
		builder.send();
	}

	/**
	 * @see PostOffice#sendAddEmailToken(Person, String, String)
	 */
	public void sendAddEmailToken(Person me, String email, String token)
	{
	    log.log(Level.FINE,"Sending add email token to {0}", email);
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("token", this.token(token));
		vctx.put("email", email);
		vctx.put("person", me);
		
		if (me.getSubscriptions().size() == 1)
		{
			// If only one list, make it appear to come from that list
			Subscription sub = me.getSubscriptions().values().iterator().next();
			vctx.put("url", sub.getList().getUrlBase());
			
			MessageBuilder builder = new MessageBuilder(MailType.CONFIRM_EMAIL, vctx);
			builder.setTo(email);
			builder.setFrom(sub.getList());
			builder.send();
		}
		else
		{
			URL url = this.settings.getDefaultSiteUrl();
			vctx.put("url", url.toString());
			
			MessageBuilder builder = new MessageBuilder(MailType.CONFIRM_EMAIL, vctx);
			builder.setTo(email);
			builder.setFrom(this.settings.getPostmaster());
			
			builder.send();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.post.PostOffice#sendModeratorSubscriptionHeldNotice(org.subethamail.entity.EmailAddress, org.subethamail.entity.SubscriptionHold)
	 */
	public void sendModeratorSubscriptionHeldNotice(EmailAddress moderator, SubscriptionHold hold)
	{
	    log.log(Level.FINE,"Sending sub held notice for {0} to {1}", new Object[]{hold, moderator});
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("hold", hold);
		vctx.put("moderator", moderator);
		
		MessageBuilder builder = new MessageBuilder(MailType.SUBSCRIPTION_HELD, vctx);
		
		builder.setTo(moderator);
		builder.setFrom(hold.getList());
		builder.send();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.post.PostOffice#sendPosterMailHoldNotice(org.subethamail.entity.MailingList, java.lang.String, org.subethamail.entity.Mail, java.lang.String)
	 */
	public void sendPosterMailHoldNotice(MailingList relevantList, String posterEmail, Mail mail, String holdMsg)
	{
	    log.log(Level.FINE,"Sending mail held notice to {0}", posterEmail);
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("list", relevantList);
		vctx.put("mail", mail);
		vctx.put("holdMsg", holdMsg);
		vctx.put("email", posterEmail);
		
		MessageBuilder builder = new MessageBuilder(MailType.YOUR_MAIL_HELD, vctx);
		builder.setTo(posterEmail);
		builder.setFrom(relevantList);
		builder.send();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.post.PostOffice#sendModeratorMailHoldNotice(org.subethamail.entity.EmailAddress, org.subethamail.entity.MailingList, org.subethamail.entity.Mail, org.subethamail.common.SubEthaMessage, java.lang.String)
	 */
	public void sendModeratorMailHoldNotice(EmailAddress moderator, MailingList relevantList, Mail mail, SubEthaMessage msg, String holdMsg)
	{
	    log.log(Level.FINE,"Sending mail held notice to moderator {0}", moderator);
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("list", relevantList);
		vctx.put("mail", mail);
		vctx.put("msg", msg);
		vctx.put("holdMsg", holdMsg);
		vctx.put("moderator", moderator);
		
		MessageBuilder builder = new MessageBuilder(MailType.MAIL_HELD, vctx);
		builder.setTo(moderator);
		builder.setFrom(relevantList);
		builder.send();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.post.PostOffice#sendModeratorSubscriptionNotice(org.subethamail.entity.EmailAddress, org.subethamail.entity.Subscription, boolean)
	 */
	public void sendModeratorSubscriptionNotice(EmailAddress moderator, Subscription sub, boolean unsub)
	{
	    log.log(Level.FINE,"Sending {0} notice for {1} to {2}", new Object[]{(unsub ? "unsub" : "sub"), sub, moderator});
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("sub", sub);
		vctx.put("moderator", moderator);
		vctx.put("unsub", unsub);
		
		MessageBuilder builder = new MessageBuilder(MailType.PERSON_SUBSCRIBED, vctx);
		
		builder.setTo(moderator);
		builder.setFrom(sub.getList());
		builder.send();
	}
}