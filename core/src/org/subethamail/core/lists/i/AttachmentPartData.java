/*
 * $Id: MailData.java 263 2006-05-04 20:58:25Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/core/src/org/subethamail/core/lists/i/MailData.java $
 */

package org.subethamail.core.lists.i;

/**
 * Represents an attachment for a mail message.
 * 
 * @author Scott Hernandez
 */
@SuppressWarnings("serial")
public class AttachmentPartData extends PartData
{
	Long id = null;
	/**
	 */
	public AttachmentPartData(Long id, String type, String name, int size)
	{
		super(type, name, size);
		this.id = id;
	}

	public Long getId()
	{
		return this.id;
	}
	
	public String toString() 
	{
		return this.contentType + " stored as attachment id " + this.id;
	}
	
}
