/*
 * $Id: SMTPService.java 273 2006-05-07 04:00:41Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/smtp/SMTPService.java $
 */
package org.subethamail.core.smtp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.context.ApplicationScoped;
import javax.inject.Current;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.server.SMTPServer;

import com.caucho.config.Service;

/**
 * SubEtha's adapter for SubEthaSMTP.  The default port is 2500 and by
 * default it binds to all addresses.  This can be overriden by system properties:
 * 
 * -Dorg.subethamail.smtp.port=NNN
 * -Dorg.subethamail.smtp.bind=ip_address_or_hostname
 * 
 * Note that this also handles a default destination for mail that is
 * not accepted by this server.  If defined, all unclaimed mail will
 * be sent there.  Note that mail can have multiple recipients, in which
 * case it may get split and sent both locally and remotely (multiple times,
 * even).  This is a configuration value set in the administration interface.
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

	private int port = DEFAULT_PORT;
	private String hostName = null;
	private InetAddress binding = null;
	
	private SMTPServer smtpServer;
	
	/** By getting this through injection, it gets populated with cool stuff */
	@Current private SMTPHandler smtpHandler;

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
		
		try
		{
			String bindAddress = System.getProperty("org.subethamail.smtp.bind");
			if (bindAddress != null && !"0.0.0.0".equals(bindAddress))
				binding = InetAddress.getByName(bindAddress);
		}
		catch (UnknownHostException ignored) {}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.smtp.service.SMTPManagement#start()
	 */
	@PostConstruct
	public void start() throws IOException
	{
		if (this.smtpServer != null)
			throw new IllegalStateException("SMTPServer already running");
		
		log.info("Starting SMTP service: " + (this.binding==null ? "*" : this.binding) + ":" + this.port);
		
		this.smtpServer = new SMTPServer(this.smtpHandler);
		
		this.smtpServer.setBindAddress(this.binding);
		this.smtpServer.setPort(this.port);
		
		if (this.hostName != null)
			this.smtpServer.setHostName(this.hostName);
		
		this.smtpServer.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.smtp.service.SMTPManagement#stop()
	 */
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
	public int getPort()
	{
		return this.port;
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.smtp.service.SMTPManagement#setHostName(java.lang.String)
	 */
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
	public String getHostName()
	{
		return this.hostName;
	}

	public InetAddress getBinding()
	{
		return this.binding;
	}
}