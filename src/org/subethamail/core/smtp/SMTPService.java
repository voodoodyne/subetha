/*
 * $Id: SMTPService.java 273 2006-05-07 04:00:41Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/smtp/SMTPService.java $
 */
package org.subethamail.core.smtp;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.PermitAll;
import javax.context.ApplicationScoped;
import javax.inject.Current;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.io.LimitExceededException;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import com.caucho.config.Service;

/**
 * SubEtha's JBoss adapter for SubEthaSMTP.  The default port is 2500.  This
 * port can be overriden by a system property:
 * 
 * -Dorg.subethamail.smtp.port=NNN
 * 
 * @author Ian McFarland
 * @author Jeff Schnitzer
 * @author Jon Stevens
 * @author Scott Hernandez
 */

@Service
@ApplicationScoped
public class SMTPService implements SMTPManagement
{
	
	/** */
	private final static Logger log = LoggerFactory.getLogger(SMTPService.class);
	
	/** */
	public static final int DEFAULT_PORT = 2500;

	/** Simple Listener used to collect mail for mailing lists  */
	private SimpleMessageListener listener = new SimpleMessageListener (){
		/**
		 * @see SimpleMessageListener#accept(String, String)
		 */
		public boolean accept(String from, String recipient)
		{
			return injector.accept(recipient);
		}

		/**
		 * @see SimpleMessageListener#deliver(String, String, InputStream)
		 */
		public void deliver(String from, String recipient, InputStream input) throws TooMuchDataException, IOException
		{
			try
			{
				if (!injector.inject(from, recipient, input))
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
	};
	
	private int port = DEFAULT_PORT;
	private String hostName = null;
	private InetAddress binding = null;
	
	@Current 
	Injector injector;
	
	private SMTPServer smtpServer;

	/**
	 * Constructor looks for a port override via -Dorg.subethamail.smtp.port=NNN
	 */
	public SMTPService()
	{
		try
		{
			this.port = Integer.parseInt(System.getProperty("org.subethamail.smtp.port"));
		}
		catch (Throwable ignored) {}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.smtp.service.SMTPManagement#start()
	 */
	@PermitAll	
	@PostConstruct
	public void startServer() throws IOException
	{
		if (this.smtpServer != null)
			throw new IllegalStateException("SMTPServer already running");
		
		String bindAddress = System.getProperty("jboss.bind.address");
		if (bindAddress != null && !"0.0.0.0".equals(bindAddress))
			binding = InetAddress.getByName(bindAddress);

		log.info("Starting SMTP service: " + (binding==null ? "*" : binding) + ":" + port);
		
		Collection<SimpleMessageListener> listeners = new ArrayList<SimpleMessageListener>(); 
		listeners.add(listener);
		this.smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(listeners));
		
		this.smtpServer.setBindAddress(binding);
		this.smtpServer.setPort(this.port);
		
		if (this.hostName != null)
			this.smtpServer.setHostName(this.hostName);
		
		this.smtpServer.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.smtp.service.SMTPManagement#stop()
	 */
	@PermitAll
	@PreDestroy
	public void stopServer()
	{
		log.info("Stopping SMTP service");
		this.smtpServer.stop();
		this.smtpServer = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.smtp.service.SMTPManagement#setPort(int)
	 */
	@PermitAll
	public void setPort(int port)
	{
		if (this.smtpServer != null)
			throw new IllegalStateException("SMTPServer already running");
		
		this.port = port;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.smtp.service.SMTPManagement#getPort()
	 */
	@PermitAll
	public int getPort()
	{
		return this.port;
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.smtp.service.SMTPManagement#setHostName(java.lang.String)
	 */
	@PermitAll
	public void setHostName(String hostname)
	{
		if (this.smtpServer != null)
			throw new IllegalStateException("SMTPServer already running");
		
		this.hostName = hostname;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.smtp.service.SMTPManagement#getHostName()
	 */
	@PermitAll
	public String getHostName()
	{
		return this.hostName;
	}

	@PermitAll
	public InetAddress getBinding()
	{
		return this.binding;
	}
}