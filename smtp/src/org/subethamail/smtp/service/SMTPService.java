/*
 * $Id: SMTPService.java 273 2006-05-07 04:00:41Z jon $
 * $URL$
 */
package org.subethamail.smtp.service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.smtp.SMTPServer;
import org.subethamail.smtp.i.MessageListener;
import org.subethamail.smtp.i.MessageListenerRegistry;

/**
 * @author Ian McFarland
 * @author Jeff Schnitzer
 * @author Jon Stevens
 */
@Service(name = "SMTPService", objectName = "subetha:service=SMTP2")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class SMTPService implements SMTPManagement, MessageListenerRegistry
{
	/** */
	private static Log log = LogFactory.getLog(SMTPService.class);

	/**
	 * There is no ConcurrentHashSet, so we make up our own by mapping the
	 * object to itself.
	 */
	private Map<MessageListener, MessageListener> listeners = new ConcurrentHashMap<MessageListener, MessageListener>();
	private int port;
	private String hostname;
	private SMTPServer smtpServer;

	private List<String> validRecipientHosts = new ArrayList<String>();
	private boolean hostResolutionEnabled = true;
	private boolean recipientDomainFilteringEnabled = false;

	/**
	 * @see MessageListenerRegistry#register(MessageListener)
	 */
	public void register(MessageListener listener)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + listener);
		this.listeners.put(listener, listener);
	}

	/**
	 * @see MessageListenerRegistry#deregister(MessageListener)
	 */
	public void deregister(MessageListener listener)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + listener);
		this.listeners.remove(listener);
	}

	@PermitAll
	public void setHostResolutionEnabled(boolean state)
	{
		hostResolutionEnabled = state;
	}

	@PermitAll
	public boolean getHostResolutionEnabled()
	{
		return hostResolutionEnabled;
	}

	public List<String> getValidRecipientHosts()
	{
		return validRecipientHosts;
	}

	/**
	 * @throws IOException 
	 * @throws AppException 
	 * @see SMTPManagement#start()
	 */
	@PermitAll
	public void start() throws IOException
	{
//	 FIXME: On my box, this was returning the public ip address so
//	 hard code it to be 127.0.0.1 for now.
//			try
//			{
//				hostname = InetAddress.getLocalHost().getCanonicalHostName();
//			}
//			catch (UnknownHostException e)
//			{
//				hostname = "localhost";
//			}
		hostname = "127.0.0.1";
		port = 2500;

		log.info("Starting SMTP service: " + hostname + ":" + port);
		smtpServer = new SMTPServer(hostname, port, listeners);
		smtpServer.start();
	}

	/**
	 * @throws AppException 
	 * @see SMTPManagement#stop()
	 */
	@PermitAll
	public void stop()
	{
		log.info("Stopping SMTP service");
		smtpServer.stop();
	}

	/**
	 * @see SMTPManagement#setPort
	 * @param port
	 */
	@PermitAll
	public void setPort(int port)
	{
		this.port = port;
	}

	@PermitAll
	public int getPort()
	{
		return this.port;
	}
	
	/**
	 * @see SMTPManagement#setHostname
	 * 
	 * @param hostname
	 */
	@PermitAll
	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}

	/**
	 * @see SMTPManagement#getHostname()
	 * @return hostname
	 */
	@PermitAll
	public String getHostname()
	{
		return hostname;
	}

	public String resolveHost(String hostname) throws IOException,
			ServerRejectedException
	{
		if (hostResolutionEnabled)
		{
			return hostname.trim() + "/"
					+ InetAddress.getByName(hostname).getHostAddress();
		}
		else
		{
			return hostname;
		}
	}

	@PermitAll
	public void setRecipientDomainFilteringEnabled(
			boolean recipientDomainFilteringEnabled)
	{
		this.recipientDomainFilteringEnabled = recipientDomainFilteringEnabled;
	}

	@PermitAll
	public boolean getRecipientDomainFilteringEnabled()
	{
		return recipientDomainFilteringEnabled;
	}
}
