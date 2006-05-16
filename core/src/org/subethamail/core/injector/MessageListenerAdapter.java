/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.injector;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.EJB;
import javax.annotation.security.RunAs;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Depends;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.io.LimitExceededException;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.plugin.i.helper.Lifecycle;
import org.subethamail.smtp.i.MessageListener;
import org.subethamail.smtp.i.MessageListenerRegistry;
import org.subethamail.smtp.i.TooMuchDataException;

/**
 * This acts as an SMTP listener and injects any interesting messages
 * into the Injector. 
 * 
 * @author Jeff Schnitzer
 */
@Service(objectName="subetha:service=MessageListenerAdapter")
// This depends annotation can be removed when JBoss fixes dependency bug.
@Depends({
	"jboss.j2ee:ear=subetha.ear,jar=core.jar,name=Injector,service=EJB3",
	"subetha:service=SMTP"
})
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class MessageListenerAdapter implements MessageListener, Lifecycle
{
	/** */
	private static Log log = LogFactory.getLog(MessageListenerAdapter.class);
	
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
			return this.injector.accept(recipient);
		}
		catch (MessagingException ex)
		{
			// Bad address?  Maybe return false instead?
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see MessageListener#deliver(String, String, InputStream)
	 */
	public void deliver(String from, String recipient, InputStream input) throws TooMuchDataException, IOException
	{
		try
		{
			if (!this.injector.inject(from, recipient, input))
			{
				if (log.isWarnEnabled())
					log.warn("Accepted data no longer wanted for " + recipient);
				
				throw new RuntimeException("Data no longer wanted");
			}
		}
		catch (MessagingException ex)
		{
			if (log.isWarnEnabled())
				log.warn("Trouble parsing input data", ex);
			
			// Problem parsing the data
			throw new RuntimeException(ex);
		}
		catch (LimitExceededException ex)
		{
			if (log.isWarnEnabled())
				log.warn("Too much input data", ex);
			
			throw new TooMuchDataException();
		}
		catch (RuntimeException ex)
		{
			log.error("Some kind of error", ex);
			throw ex;
		}
	}
}
