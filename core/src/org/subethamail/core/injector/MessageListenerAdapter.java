/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.injector;

import javax.annotation.EJB;
import javax.annotation.security.RunAs;
import javax.mail.MessagingException;

import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.plugin.i.helper.Lifecycle;
import org.subethamail.smtp.i.MessageListener;
import org.subethamail.smtp.i.MessageListenerRegistry;

/**
 * This acts as an SMTP listener and injects any interesting messages
 * into the Injector. 
 * 
 * @author Jeff Schnitzer
 */
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class MessageListenerAdapter implements MessageListener, Lifecycle
{
	/**
	 */
	@EJB MessageListenerRegistry registry;
	@EJB Injector injector;

	/**
	 * @see Lifecycle#start()
	 */
	public void start() throws Exception
	{
		this.registry.register(this);
	}
	
	/**
	 * @see Lifecycle#stop()
	 */
	public void stop()
	{
		this.registry.deregister(this);
	}

	/**
	 * @see MessageListener#accept(String, String)
	 */
	public boolean accept(String from, String recipient)
	{
		try
		{
			return this.injector.accept(from);
		}
		catch (MessagingException ex)
		{
			// Bad address?  Maybe return false instead?
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see MessageListener#deliver(String, String, byte[])
	 */
	public void deliver(String from, String recipient, byte[] data)
	{
		try
		{
			if (!this.injector.inject(recipient, data))
				throw new RuntimeException("Data no longer wanted");
		}
		catch (MessagingException ex)
		{
			// Problem parsing the data
			throw new RuntimeException(ex);
		}
	}
}
