/*
 * $Id: MessageListenerAdapter.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/injector/MessageListenerAdapter.java $
 */

package org.subethamail.core.injector;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.Depends;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.subethamail.common.io.LimitExceededException;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.plugin.i.helper.Lifecycle;
import org.subethamail.core.smtp.MessageListenerRegistry;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;

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
@Local(SimpleMessageListener.class)
public class MessageListenerAdapter implements SimpleMessageListener, Lifecycle
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
	 * @see SimpleMessageListener#accept(String, String)
	 */
	public boolean accept(String from, String recipient)
	{
		return this.injector.accept(recipient);
	}

	/**
	 * @see SimpleMessageListener#deliver(String, String, InputStream)
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
