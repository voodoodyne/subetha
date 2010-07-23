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
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.smtp.server.SMTPServer;

/**
 * SubEtha's adapter for SubEthaSMTP.  The default port is 2500 and by
 * default it binds to all addresses.  This can be overriden by setting
 * the various properties in the subetha.xml configuration file.
 * 
 * Note that this also handles a fallback destination for mail that is
 * not accepted by this server.  If defined, all unclaimed mail will
 * be sent there.  Note that mail can have multiple recipients, in which
 * case it may get split and sent both locally and remotely (multiple times,
 * even).
 * 
 * @author Ian McFarland
 * @author Jeff Schnitzer
 * @author Jon Stevens
 * @author Scott Hernandez
 */
@Startup
@ApplicationScoped
public class SMTPService
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(SMTPService.class);
	
	/** */
	public static final int DEFAULT_PORT = 2500;

	/** */
	private int port = DEFAULT_PORT;
	private String hostName = null;
	private String bindAddress = null;
	private String fallbackHost = null;
	
	/** */
	private SMTPServer smtpServer;
	
	/** */
	@Inject Injector injector;

	/**
	 */
	public SMTPService()
	{
	}
	
	/** This is used privately by the Handler */
	Injector getInjector()
	{
		return this.injector;
	}
	
	/**
	 */
	@PostConstruct
	public void start() throws IOException
	{
		if (this.smtpServer != null)
			throw new IllegalStateException("SMTPServer already running");
		
		log.info("Starting SMTP service: " + (this.bindAddress==null ? "*" : this.bindAddress) + ":" + this.port);
		
		this.smtpServer = new SMTPServer(new SMTPHandler(this));

		InetAddress binding = this.getBinding();
		if (binding != null)
			this.smtpServer.setBindAddress(binding);
		
		this.smtpServer.setPort(this.port);
		
		if (this.hostName != null)
			this.smtpServer.setHostName(this.hostName);
		
		this.smtpServer.start();
	}
	
	/** @return a more processed form of the address, or null if none can be determined */
	public InetAddress getBinding()
	{
		if (this.bindAddress != null)
		{
			try
			{
				return InetAddress.getByName(this.bindAddress);
			}
			catch (UnknownHostException ignored) {}
		}
		
		return null;
	}

	/**
	 */
	@PreDestroy
	public void stop()
	{
		log.info("Stopping SMTP service");
		this.smtpServer.stop();
		this.smtpServer = null;
	}

	/**
	 * This can only be set on a stopped service.
	 */
	public void setPort(int port)
	{
		if (this.smtpServer != null)
			throw new IllegalStateException("SMTPServer already running");
		
		this.port = port;
	}

	/**
	 * When the SMTP server starts, it will listen on this port.
	 */
	public int getPort()
	{
		return this.port;
	}

	/**
	 * Sets the hostname the SMTP service reports.  If null, one is guessed at.
	 */
	public void setHostName(String hostname)
	{
		if (this.smtpServer != null)
			throw new IllegalStateException("SMTPServer already running");
		
		this.hostName = hostname;
	}

	/**
	 * The hostname the SMTP service reports.  Defaults to null (system takes a guess).
	 */
	public String getHostName()
	{
		return this.hostName;
	}

	/**
	 * The address which the server is bound to.
	 */
	public String getBindAddress()
	{
		return this.bindAddress;
	}
	
	/**
	 * The address which the server is bound to.
	 */
	public void setBindAddress(String addy)
	{
		if (this.smtpServer != null)
			throw new IllegalStateException("SMTPServer already running");
		
		this.bindAddress = addy;
	}

	/**
	 * The host to which all non-list mail that goes through the SMTP intake
	 * is forwarded.  Null means all non-list mail will be rejected.
	 */
	public String getFallbackHost()
	{
		return this.fallbackHost;
	}
	
	/**
	 * The host to which all non-list mail that goes through the SMTP intake
	 * is forwarded.  Null means all non-list mail will be rejected.
	 * 
	 * This can be set while the server is running.
	 */
	public void setFallbackHost(String value)
	{
		this.fallbackHost = value;
	}
}