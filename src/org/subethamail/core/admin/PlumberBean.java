/*
 * $Id: PlumberBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/PlumberBean.java $
 */

package org.subethamail.core.admin;

import javax.annotation.security.RolesAllowed;
import javax.inject.Current;
import javax.mail.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.admin.i.Plumber;
import org.subethamail.core.post.OutboundMTA;

/**
 * Implements some basic plumbing methods.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
public class PlumberBean implements Plumber
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(PlumberBean.class);

	// Neither of these work in resin 4.0.0
	//@Resource SessionContext sessionContext;
	//@Current SessionContext sessionContext;
	
	/** Holds the mail config when overriding */
	String mailSmtpHost;
	String mailSmtpPort;
	
	/** TODO: This should be done using a Deployment Descriptors (JSR299) so that 
	 *  the "test" Deployment Descriptor bings to a anouther mail session 
	 *  on the current test host and port. */
	@OutboundMTA Session mailSession;
	
	@Current TestMode testMode;
	
	/* (non-Javadoc)
	 * @see Plumber#log(java.lang.String)
	 */
	public void log(String msg)
	{
		log.info(msg);
	}

	
	/*
	 * (non-Javadoc)
	 */
	@RolesAllowed("siteAdmin")
	public void overrideSmtpServer(String host)
	{
		
		String oldPort, oldHost, newHost, newPort;
		// Backup the old values
		oldHost = mailSession.getProperties().getProperty("mail.smtp.host");
		oldPort = mailSession.getProperties().getProperty("mail.smtp.port");
		
		// If there was a port, separate the two
		String[] parts = host.split(":");
		newHost=parts[0];
		
		if(parts.length > 1) 
		{
			newPort=parts[1];
		} 
		else
		{
			throw new IllegalArgumentException("You must supply a 'host:port' string.");
		}

		if(oldHost.equals(newHost) && oldPort.equals(newPort))
			return;
		
		if (this.mailSmtpHost != null)
			throw new IllegalStateException("Smtp server override already in effect");

		//store old value, and update the overrides
		this.mailSmtpHost = oldHost;
		this.mailSmtpPort = oldPort;
		mailSession.getProperties().setProperty("mail.smtp.host", newHost);
		mailSession.getProperties().setProperty("mail.smtp.port", newPort);
	}

	/*
	 * (non-Javadoc)
	 */
	@RolesAllowed("siteAdmin")
	public void restoreStmpServer()
	{
		if (this.mailSmtpHost == null)
		{
			log.warn("No override in effect; ignoring restoreSmtpServer()");
		}
		else
		{
			log.info("Restoring base mail configuration");

			mailSession.getProperties().setProperty("mail.smtp.host", this.mailSmtpHost);
			mailSession.getProperties().setProperty("mail.smtp.port", this.mailSmtpPort);

			this.mailSmtpHost = null;
			this.mailSmtpPort = null;
		}
	}


	@Override
	public boolean setTestMode(boolean testMode)
	{
		return this.testMode.setTestMode(testMode);
	}
}
