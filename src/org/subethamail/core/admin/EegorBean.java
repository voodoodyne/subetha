/*
 * $Id: PlumberBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/PlumberBean.java $
 */

package org.subethamail.core.admin;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.mail.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.admin.i.Eegor;
import org.subethamail.core.post.OutboundMTA;

import com.caucho.remote.HessianService;

/**
 * Implements some basic plumbing methods for testing.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Singleton
@Named("eegor")
@HessianService(urlPattern="/api/Eegor")
public class EegorBean implements Eegor
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(EegorBean.class);

	/** */
	@Inject @OutboundMTA Session mailSession;
	
	String mailSmtpHost;
	String mailSmtpPort;
	
	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.i.EegorBringMeAnotherBrain#log(java.lang.String)
	 */
	public void log(String msg)
	{
		log.info(msg);
	}
	
	/*
	 * (non-Javadoc)
	 */
	@RolesAllowed("siteAdmin")
	public void enableTestMode(String mtaHost)
	{
		log.debug("#### Enabling test mode to " + mtaHost);
		
		if (!this.isTestModeEnabled())
		{
			this.mailSmtpHost = this.mailSession.getProperties().getProperty("mail.smtp.host");
			this.mailSmtpPort = this.mailSession.getProperties().getProperty("mail.smtp.port");
		}
		
		// If there was a port, separate the two
		String[] parts = mtaHost.split(":");
		String newHost = parts[0];
		String newPort = (parts.length > 1) ? parts[1] : "25";

		//store old value, and update the overrides
		this.mailSession.getProperties().setProperty("mail.smtp.host", newHost);
		this.mailSession.getProperties().setProperty("mail.smtp.port", newPort);
	}

	/*
	 * (non-Javadoc)
	 */
	@RolesAllowed("siteAdmin")
	public void disableTestMode()
	{
		//if (!this.isTestModeEnabled())
		if (this.mailSmtpHost == null)
		{
			log.warn("Test mode already disabled");
		}
		else
		{
			log.info("Restoring base mail configuration");

			this.mailSession.getProperties().setProperty("mail.smtp.host", this.mailSmtpHost);
			this.mailSession.getProperties().setProperty("mail.smtp.port", this.mailSmtpPort);

			this.mailSmtpHost = null;
			this.mailSmtpPort = null;
		}
	}

	/** */
	public boolean isTestModeEnabled()
	{
		//return this.mailSmtpHost != null;
		return true;
	}
}
