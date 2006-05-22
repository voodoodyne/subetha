/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.admin.i;

import java.io.Serializable;

/**
 * Some random information about the site.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class SiteStatus implements Serializable
{
	/** */
	String defaultCharset;
	int listCount;
	
	/** Some site config params */
	String defaultSiteUrl;
	String postmasterEmail;
	
	/** */
	public SiteStatus(String encoding, int listCount, String defaultSiteUrl, String postmasterEmail)
	{
		this.defaultCharset = encoding;
		this.listCount = listCount;
		
		this.defaultSiteUrl = defaultSiteUrl;
		this.postmasterEmail = postmasterEmail;
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
	public String getDefaultSiteUrl()
	{
		return this.defaultSiteUrl;
	}

	/** */
	public String getPostmasterEmail()
	{
		return this.postmasterEmail;
	}
}
