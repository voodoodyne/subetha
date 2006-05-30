/*
 * $Id$
 * $URL$
 */

package org.subethamail.load;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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
	
	/**
	 * Attachment can be null for a simple text body
	 */
	public LoadGenerator(InternetAddress sender, InternetAddress recipient, File attachment) throws MessagingException
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
	
		if (attachment == null)
		{
			this.message.setText("Blah blah blah blah blah this is a boring load test message body");
		}
		else
		{
	 		MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Blah blah blah blah");
	
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
	
			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(attachment);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(attachment.toString());
			multipart.addBodyPart(messageBodyPart);
	
			// Put parts in message
			this.message.setContent(multipart);
		}
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
		if (args.length < 2 || args.length > 3)
		{
			System.err.println("Usage:  org.subethamail.loadtest.LoadGenerator from@address to@address [attachment]");
		}
		else
		{
			InternetAddress sender = new InternetAddress(args[0]);
			InternetAddress recipient = new InternetAddress(args[1]);
			
			File attachment = (args.length == 3) ? new File(args[2]) : null;
			
			LoadGenerator gen = new LoadGenerator(sender, recipient, attachment);
			gen.run();
		}
	}
}
