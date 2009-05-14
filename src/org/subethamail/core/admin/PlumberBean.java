/*
 * $Id: PlumberBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/PlumberBean.java $
 */

package org.subethamail.core.admin;

import javax.annotation.security.RolesAllowed;
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
		if (this.mailSmtpHost != null)
			throw new IllegalStateException("Smtp server override already in effect");

		// Backup the old values
		this.mailSmtpHost = System.getProperty("mail.smtp.host");
		this.mailSmtpPort = System.getProperty("mail.smtp.port");
		
		// If there was a port, separate the two
		String port = null;
		
		String[] parts = host.split(":");		
		host=parts[0];
		if(parts.length > 1) port=parts[1];
		
		mailSession.getProperties().setProperty("mail.smtp.host", host);
		mailSession.getProperties().setProperty("mail.smtp.port", port);
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
}
