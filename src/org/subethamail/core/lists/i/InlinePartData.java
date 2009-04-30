/*
 * $Id: MailData.java 263 2006-05-04 20:58:25Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/core/src/org/subethamail/core/lists/i/MailData.java $
 */

package org.subethamail.core.lists.i;

/**
 * Represents an inline part (not an attachment) for a mail message.
 * 
 * @author Scott Hernandez
 */
@SuppressWarnings("serial")
public class InlinePartData extends PartData
{
	Object contents = null;
	/**
	 */
	public InlinePartData(Object contents, String type, String name, int size)
	{
		super(type, name, size);
		this.contents = contents;
	}

	public Object getContents()
	{
		return this.contents;
	}

	public String toString() 
	{
		return this.contentType + ":" + this.contents.getClass().toString();
	}
}
