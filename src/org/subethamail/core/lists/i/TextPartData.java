/*
 * $Id: MailData.java 263 2006-05-04 20:58:25Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/core/src/org/subethamail/core/lists/i/MailData.java $
 */

package org.subethamail.core.lists.i;

/**
 * Represents an text part for a mail message.
 * 
 * @author Scott Hernandez
 */
public class TextPartData extends InlinePartData
{
	private static final long serialVersionUID = 1L;

	/** Needed by Hessian */
	protected TextPartData() {}
	
	/**
	 */
	public TextPartData(String contents, String type, String name, int size)
	{
		super(contents, type, name, size);
		
		if (size == 0 || size == -1) this.contentSize = contents.length() * Character.SIZE;

		this.contents = contents;
	}

	public String getContents()
	{
		return (String)this.contents;
	}
	public String toString() 
	{
		return this.contentType + ":" + (String)this.contents;
	}
}
