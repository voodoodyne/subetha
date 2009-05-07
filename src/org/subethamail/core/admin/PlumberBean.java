/*
 * $Id: PlumberBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/PlumberBean.java $
 */

package org.subethamail.core.admin;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.admin.i.Plumber;
import org.subethamail.core.post.PostOffice;

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

	@EJB PostOffice postOffice;

	/** Holds the mail config when overriding */
	String mailSmtpHost;
	String mailSmtpPort;
	
	/* (non-Javadoc)
	 * @see com.kink.heart.biz.admin.i.Plumber#log(java.lang.String)
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
		
		int colon = host.indexOf(':');
		if (colon > 0)
		{
			host = host.substring(colon + 1);
			port = host.substring(0, colon);
		}

		System.setProperty("mail.smtp.host", host);
		System.setProperty("mail.smtp.port", port);
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
			System.setProperty("mail.smtp.host", this.mailSmtpHost);
			System.setProperty("mail.smtp.port", this.mailSmtpPort);

			this.mailSmtpHost = null;
			this.mailSmtpPort = null;
		}
	}
}
