/*
 * $Id: PostOfficeEJB.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/post/PostOfficeEJB.java $
 */

package org.subethamail.core.post;

import java.io.StringWriter;

import javax.annotation.EJB;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.PostConstruct;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.post.i.Constant;
import org.subethamail.core.post.i.MailType;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.dao.DAO;

/**
 * Implementation of the PostOffice interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="PostOffice")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class PostOfficeBean implements PostOffice
{
	/** */
	private static Log log = LogFactory.getLog(PostOfficeBean.class);
	
	/** */
	@Resource(mappedName="java:/Mail") Session mailSession;

	/** */
	@EJB DAO dao;
	
	/**
	 * Simply initialize the Velocity engine
	 */
	@PostConstruct
	public void init()
	{
		try
		{
			Velocity.setProperty(Velocity.RESOURCE_LOADER, "cp");
			Velocity.setProperty("cp.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			Velocity.setProperty("cp.resource.loader.cache", "true");
			Velocity.setProperty("cp.resource.loader.modificationCheckInterval ", "0");
			Velocity.init();
		}
		catch (Exception ex)
		{
			log.fatal("Unable to initialize Velocity", ex);
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Does the work of sending an email using a velocity template.
	 */
	protected void sendMail(MailType kind, VelocityContext vctx, String email)
	{
		StringWriter writer = new StringWriter(4096);
		
		try
		{
			Velocity.mergeTemplate(kind.getTemplate(), "UTF-8", vctx, writer);
		}
		catch (Exception ex)
		{
			log.fatal("Error merging " + kind.getTemplate(), ex);
			throw new EJBException(ex);
		}
		
		String mailSubject = (String)vctx.get("subject");
		String mailBody = writer.toString();
		
		// If we're in debug mode, annotate the subject.
		if (log.isDebugEnabled())
			mailSubject = kind.toString() + " " + mailSubject;

		try
		{
			InternetAddress toAddress = new InternetAddress(email);
			InternetAddress fromAddress;
			try
			{
				//TODO:  figure out something good for this
				fromAddress = new InternetAddress("donotreply@nowhere.com", "Someone");
			}
			catch (java.io.UnsupportedEncodingException ex)
			{
				// Impossible
				throw new AddressException(ex.toString());
			}
	
			Message message = new MimeMessage(this.mailSession);
			message.setRecipient(Message.RecipientType.TO, toAddress);
			message.setFrom(fromAddress);
			message.setReplyTo(new InternetAddress[0]);	// reply to nobody
			message.setSubject(mailSubject);
			message.setText(mailBody);
	
			Transport.send(message);
		}
		catch (MessagingException ex) { throw new RuntimeException(ex); }
	}
	
	/**
	 * Modifies the token with debug information which can be picked out
	 * by the unit tester.
	 */
	protected String token(String tok)
	{
		if (log.isDebugEnabled())
			return Constant.DEBUG_TOKEN_BEGIN + tok + Constant.DEBUG_TOKEN_END;
		else
			return tok;
	}
	
	/**
	 * @see PostOffice#sendPassword(MailingList, EmailAddress)
	 */
	public void sendPassword(MailingList list, EmailAddress addy)
	{
		if (log.isDebugEnabled())
			log.debug("Sending password for " + addy.getId());
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("addy", addy);
		vctx.put("list", list);
		
		this.sendMail(MailType.FORGOT_PASSWORD, vctx, addy.getId());
	}

	/**
	 * @see PostOffice#sendConfirmSubscribeToken(MailingList, String, String)
	 */
	public void sendConfirmSubscribeToken(MailingList list, String email, String token)
	{
		if (log.isDebugEnabled())
			log.debug("Sending subscribe token to " + email);
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("token", this.token(token));
		vctx.put("email", email);
		vctx.put("list", list);
		
		this.sendMail(MailType.CONFIRM_SUBSCRIBE, vctx, email);
	}

	/**
	 * @see PostOffice#sendSubscribed(MailingList, Person, EmailAddress)
	 */
	public void sendSubscribed(MailingList list, Person who, EmailAddress deliverTo)
	{
		if (log.isDebugEnabled())
			log.debug("Sending welcome to list msg to " + who);

		String email;
		if (deliverTo != null)
			email = deliverTo.getId();
		else
			email = who.getEmailAddresses().values().iterator().next().getId();
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("list", list);
		vctx.put("person", who);
		vctx.put("email", email);
		
		this.sendMail(MailType.SUBSCRIBED, vctx, email);
	}

	/**
	 * @see PostOffice#sendOwnerNewMailingList(EmailAddress, MailingList)
	 */
	public void sendOwnerNewMailingList(EmailAddress address, MailingList list)
	{
		if (log.isDebugEnabled())
			log.debug("Sending notification of new mailing list " + list + " to owner " + address);
		
		VelocityContext vctx = new VelocityContext();
		vctx.put("addy", address);
		vctx.put("list", list);
		
		this.sendMail(MailType.NEW_MAILING_LIST, vctx, address.getId());
	}
}