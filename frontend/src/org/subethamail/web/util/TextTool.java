/*
 * $Id: TextTool.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/util/TextTool.java $
 */

package org.subethamail.web.util;

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
					
				case '\n': buf.append("<br/>"); break;
					
				default: buf.append(c);
			}
		}
		
		return buf.toString(); 
	}
}
