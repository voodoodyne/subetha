/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Some simple static methods useful as JSP functions
 *  
 * @author Jeff Schnitzer
 */
public class TextTool 
{
	/** */
	private static Log log = LogFactory.getLog(TextTool.class);
	
	/** What we want to delete from exception messages */
	private static final String EXCEPTION_MSG = "Exception: ";
	
	/**
	 * Provides a nicely formatted version of the exception message,
	 * without all the unfriendly java crap.
	 */
	public static String exceptionMessage(Throwable t)
	{
		String msg = t.getMessage();
		
		int badIndex = msg.lastIndexOf(EXCEPTION_MSG);
		if (badIndex < 0)
			return msg;
		else
			return msg.substring(badIndex + EXCEPTION_MSG.length()).trim();
	}
	
	/**
	 * URLEncodes some text.
	 */
	public static String urlEncode(String orig)
	{
		try
		{
			return URLEncoder.encode(orig, "UTF-8");
		}
		catch (UnsupportedEncodingException ex)
		{
			// Should be impossible
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Escapes all xml characters, but also converts newlines to br tags.
	 */
	public static String escapeText(String orig)
	{
		StringBuffer buf = new StringBuffer();
		
		for (int i=0; i<orig.length(); i++)
		{
			char c = orig.charAt(i);
			
			switch(c)
			{
				case '>': buf.append("&gt;"); break;
					
				case '<': buf.append("&lt;"); break;
					
				case '\'': buf.append("&apos;"); break;
					
				case '"': buf.append("&quot;"); break;
					
				case '&': buf.append("&amp;"); break;
					
				case '\n': buf.append("<br />"); break;
					
				default: buf.append(c);
			}
		}
		
		return buf.toString(); 
	}
}
