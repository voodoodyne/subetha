package org.subethamail.core.admin;

import java.util.logging.Level;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.mail.Session;

import lombok.extern.java.Log;

import org.subethamail.core.admin.i.Eegor;
import org.subethamail.core.post.OutboundMTA;
import org.subethamail.core.smtp.SMTPService;

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
@Log
public class EegorBean implements Eegor
{
	/** */
	@Inject @OutboundMTA Session mailSession;
	
	String mailSmtpHost;
	String mailSmtpPort;
	
	/** Needed to get/set the fallback host */
	@Inject SMTPService smtpService;

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
	    log.log(Level.FINE,"#### Enabling test mode to {0}", mtaHost);
		
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
			log.warning("Test mode already disabled");
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
		return this.mailSmtpHost != null;
		//return true;
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Eegor#setFallbackHost(java.lang.String)
	 */
	@Override
	public void setFallbackHost(String host)
	{
		this.smtpService.setFallbackHost(host);
	}
}
