/*
 * $Id: SMTPService.java 273 2006-05-07 04:00:41Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/smtp/SMTPService.java $
 */
package org.subethamail.core.smtp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.PermitAll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 */
@Service
public class SMTPService implements SMTPManagement, MessageListenerRegistry
{
	/** */
	private static Log log = LogFactory.getLog(SMTPService.class);
	
	/** */
	public static final int DEFAULT_PORT = 2500;

	/**
	 * There is no ConcurrentHashSet, so we make up our own by mapping the
	 * object to itself.
	 */
	private Map<SimpleMessageListener, SimpleMessageListener> listeners = new ConcurrentHashMap<SimpleMessageListener, SimpleMessageListener>();
	
	private int port = DEFAULT_PORT;
	private String hostName = null;
	private InetAddress binding = null;
	
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
	 * @see org.subethamail.smtp.i.MessageListenerRegistry#register(org.subethamail.smtp.i.MessageListener)
	 */
	public void register(SimpleMessageListener listener)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + listener);
		
		this.listeners.put(listener, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.smtp.i.MessageListenerRegistry#deregister(org.subethamail.smtp.i.MessageListener)
	 */
	public void deregister(SimpleMessageListener listener)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + listener);
		
		this.listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.smtp.service.SMTPManagement#start()
	 */
	@PermitAll
	@PostConstruct
	public void start() throws IOException
	{
		if (this.smtpServer != null)
			throw new IllegalStateException("SMTPServer already running");
		
		String bindAddress = System.getProperty("jboss.bind.address");
		if (bindAddress != null && !"0.0.0.0".equals(bindAddress))
			binding = InetAddress.getByName(bindAddress);

		log.info("Starting SMTP service: " + (binding==null ? "*" : binding) + ":" + port);
		
		this.smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(listeners.values()));
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
	public void stop()
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