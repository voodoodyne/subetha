/*
 * $Id$
 * $URL$
 */

package org.subethamail.common;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Offers static utility methods to help processing javamail
 * objects. 
 * 
 * @author Jeff Schnitzer
 */
public class MailUtils
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(MailUtils.class);
	
	/**
	 * Assumes that the contentType contains a name header in 
	 * the form of quoted value ('name="thename"')
	 * semicolon ended value ('name=name;'), or newline 
	 * ended value ('name=name  \n')
	 * 
	 * Note: A null argument creates an exception.
	 * 
	 * @param contentType the contentType string
	 * @return the name
	 */
	public static String getNameFromContentType(String contentType) 
	{
		//null contentType results an exception
		if (contentType == null) throw new IllegalArgumentException();
		
		//figure out the name, if there is one.
		String name = "";
		int namestart = contentType.indexOf("name=");
		if(namestart > 0)
		{
			//add the number of chars in 'name='
			namestart += 5;
			int endnamevalue = contentType.indexOf("\"", namestart + 1);
			
			//we have a quoted value
			if(endnamevalue > namestart) return contentType.substring(namestart + 1, endnamevalue);
			
			endnamevalue = contentType.indexOf(";", namestart);
			
			//we have a ; ended value
			if(endnamevalue > namestart) return contentType.substring(namestart, endnamevalue);

			endnamevalue = contentType.indexOf("\n", namestart);
			
			//we have a newline ended value, maybe with whitespace
			if(endnamevalue > namestart) return contentType.substring(namestart, endnamevalue).trim();
		}
		
		return name;	
	}
	
	/**
	 * @return a rfc222-compliant comma-separated list of addresses, or
	 *  null if no from field was available.
	 */
	public static String getFrom(Message msg) throws MessagingException
	{
		Address[] froms = msg.getFrom();
		
		if (froms == null || froms.length == 0)
			return null;
		
		if (froms.length == 1)
		{
			return froms[0].toString();
		}
		else
		{
			StringBuffer buf = new StringBuffer();
			
			for (int i=0; i<froms.length; i++)
			{
				if (i != 0)
					buf.append(", ");
				
				buf.append(froms[i].toString());
			}
			
			return buf.toString();
		}
	}
}
