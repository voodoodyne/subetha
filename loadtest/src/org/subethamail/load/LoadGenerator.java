/*
 * $Id$
 * $URL$
 */

package org.subethamail.load;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Thread which creates a ton of SMTP messages and delivers them to a SMTP server. 
 * 
 * @author Jeff Schnitzer
 */
public class LoadGenerator extends Thread
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(LoadGenerator.class);
	
	/** */
	InternetAddress recipient;
	Session mailSession;
	
	/** */
	MimeMessage message;
	
	/** */
	public LoadGenerator(InternetAddress sender, InternetAddress recipient) throws MessagingException
	{
		this.recipient = recipient;
		
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", "localhost");
		props.setProperty("mail.smtp.port", "2500");
		
		this.mailSession = Session.getDefaultInstance(props);
		
		this.message = new MimeMessage(this.mailSession);
		
		this.message.setSubject("load testing");
		this.message.setFrom(sender);
		this.message.setRecipient(RecipientType.TO, this.recipient);
		
		this.message.setText("Blah blah blah blah blah this is a boring load test message body");
	}
	
	/** */
	public void run()
	{
		try
		{
			while (true)
			{
				System.out.println("Sending message");
				Transport.send(this.message);
				Thread.sleep(5000);
			}
		}
		catch (InterruptedException ex) { throw new RuntimeException(ex); }
		catch (MessagingException ex) { throw new RuntimeException(ex); }
	}
	
	/** */
	public static void main(String[] args) throws Exception
	{
		if (args.length != 2)
		{
			System.err.println("Usage:  org.subethamail.loadtest.LoadGenerator from@address to@address");
		}
		else
		{
			InternetAddress sender = new InternetAddress(args[0]);
			InternetAddress recipient = new InternetAddress(args[1]);
			LoadGenerator gen = new LoadGenerator(sender, recipient);
			gen.run();
		}
	}
}
