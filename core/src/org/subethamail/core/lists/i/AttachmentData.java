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
public class AttachmentData
{
	Long id;
	String contentType;
	int contentSize;
	String name;
	
	/**
	 */
	public AttachmentData(Long id, String type, String name, int size)
	{
		this.id = id;
		this.contentType = type;
		this.contentSize = size;
	}
	
	public String getName() 
	{
		return this.name;
	}
	public Long getId() 
	{
		return this.id;
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
