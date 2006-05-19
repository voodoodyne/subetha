/*
 * $Id: MailData.java 263 2006-05-04 20:58:25Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/core/src/org/subethamail/core/lists/i/MailData.java $
 */

package org.subethamail.core.lists.i;

/**
 * Adds the mail body and thread root to a mail summary.
 * 
 * @author Scott Hernandez
 */
@SuppressWarnings("serial")
public abstract class PartData
{
	String contentType;
	int contentSize;
	String name;
	
	/**
	 */
	public PartData(String type, String name, int size)
	{
		this.contentType = type;
		this.contentSize = size;
		this.name = name;
	}
	
	public String getName() 
	{
		return this.name;
	}

	public String getContentType()
	{
		return this.contentType;
	}

	public int getContentSize()
	{
		return this.contentSize;
	}
}
