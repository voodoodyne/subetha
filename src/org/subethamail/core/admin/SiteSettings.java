/*
 */

package org.subethamail.core.admin;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ejb.Startup;
import javax.inject.Singleton;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import lombok.Data;

/**
 * Simple bean that holds site settings, configured using Resin CanDI. 
 * @author Jeff Schnitzer
 */
@Singleton
@Startup	// hopefully this will cause validity checking of data on startup
@Data
public class SiteSettings
{
	/** */
	InternetAddress postmaster;
	URL defaultSiteUrl;	// resin can auto convert urls
	
	/** */
	public SiteSettings()
	{
		try
		{
			this.postmaster = new InternetAddress("configure@in.subetha.xml");
		}
		catch (AddressException e) { throw new RuntimeException(e); }
		
		try
		{
			this.defaultSiteUrl = new URL("http://needs/configuration/in/subetha.xml/");
		}
		catch (MalformedURLException e) { throw new RuntimeException(e); }
	}
}
