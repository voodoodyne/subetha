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
	public String defaultCharset;
	public int listCount;
	
	/** */
	public SiteStatus(String encoding, int listCount)
	{
		this.defaultCharset = encoding;
		this.listCount = listCount;
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
}
