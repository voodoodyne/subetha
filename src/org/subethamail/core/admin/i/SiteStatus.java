/*
 * $Id: SiteStatus.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/i/SiteStatus.java $
 */

package org.subethamail.core.admin.i;

import java.io.Serializable;
import java.net.URL;

import javax.mail.internet.InternetAddress;

/**
 * Some random information about the site.
 *
 * @author Jeff Schnitzer
 */
public class SiteStatus implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** */
	String defaultCharset;
	int listCount;
	int personCount;
	int mailCount;
	
	/** Some site config params */
	URL defaultSiteUrl;
	InternetAddress postmasterEmail;
	String fallbackHost;
	
	protected SiteStatus()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

	/** */
	public SiteStatus(String encoding, int listCount, int personCount, int mailCount,
						URL defaultSiteUrl, InternetAddress postmasterEmail, String fallback)
	{
		this.defaultCharset = encoding;
		this.listCount = listCount;
		this.personCount = personCount;
		this.mailCount = mailCount;
		
		this.defaultSiteUrl = defaultSiteUrl;
		this.postmasterEmail = postmasterEmail;
		this.fallbackHost = fallback;
	}

	/** */
	public String getDefaultCharset()
	{
		return this.defaultCharset;
	}

	/** */
	public int getListCount()
	{
		return this.listCount;
	}

	/** */
	public int getPersonCount()
	{
		return this.personCount;
	}

	/** */
	public int getMailCount()
	{
		return this.mailCount;
	}

	/** */
	public URL getDefaultSiteUrl()
	{
		return this.defaultSiteUrl;
	}

	/** */
	public InternetAddress getPostmasterEmail()
	{
		return this.postmasterEmail;
	}
	
	/** */
	public String getFallbackHost()
	{
		return this.fallbackHost;
	}
}
