/*
 * $Id$
 * $URL$
 */

package org.subethamail.load;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import lombok.extern.java.Log;

import org.subethamail.core.admin.i.Eegor;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * Thread which creates a ton of SMTP messages and delivers them to a SMTP server. 
 * 
 * @author Jeff Schnitzer
 */
@Log
public class LoadGenerator extends Thread
{
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
		while (true)
		{
			try
			{
				//System.out.println("Sending message");
				Transport.send(this.message);
				//Thread.sleep(1);
			}
			//catch (InterruptedException ex) { throw new RuntimeException(ex); }
			catch (MessagingException ex)
			{
			    log.log(Level.FINE,"Exception sending mail", ex);
			}
		}
	}
	
	/** 
	 * Create a thread to listen for messages coming from threads generating load.
	 * 
	 * Note: This requires SubEtha running on port 2500!
	 */
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

			HessianProxyFactory fact = new HessianProxyFactory();
			fact.setOverloadEnabled(true);
			fact.setUser("root@localhost");
			fact.setPassword("password");

			String url = "http://localhost:8080/se/api/" + Eegor.class.getSimpleName();
			
			Eegor eegor = null;
			try
			{
				System.out.println("Creating eegor: " + url);
				eegor = (Eegor)fact.create(Eegor.class, url);
				
				System.out.println("Creating wiser listener on port 2525!");
				//this will start the listening wiser instance at port 2525
				Thread countingThread  = new Thread(new LoadTester("localhost",2525));
				countingThread.start();

				System.out.println("Enabling test mode!");
				eegor.enableTestMode("localhost:2525");
				
				for (int i = 0; i < 50; i++)
				{
					System.out.println("Create LoadGen Thread!");
					(new Thread(new LoadGenerator(sender, recipient, attachment))).start();
				}
				
				System.out.println("countingThread.join() -- waiting for end");
				countingThread.join();
			}
			finally
			{
				System.out.println("Disabling test mode!");
				if (eegor != null) eegor.disableTestMode();
			}
		}
	}
}
