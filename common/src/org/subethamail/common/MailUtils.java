/*
 * $Id$
 * $URL$
 */

package org.subethamail.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

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

	public static final Pattern SUBJECT_PATTERN = Pattern.compile("((RE|AW|SV)(\\[\\d+\\])*:\\s*)+", Pattern.CASE_INSENSITIVE);

	/** default constructor prevents util class from being created. */
	private MailUtils() {}

	/**
	 * Returns the specified line with any From_ line encoding removed.
	 */
	public static String decodeMboxFrom(String line)
	{
		if (line != null)
		{
			int len = line.length();
			for (int i = 0; i < (len - 5); i++)
			{
				char c = line.charAt(i);
				if (i > 0
						&& (c == 'F' && line.charAt(i + 1) == 'r' && line.charAt(i + 2) == 'o'
								&& line.charAt(i + 3) == 'm' && line.charAt(i + 4) == ' '))
					return line.substring(1);
				if (c != '>') break;
			}
		}
		return line;
	}

	/**
	 * Returns the email address in the From_ line.
	 */
	public static String getMboxFrom(String line)
	{
		String prettyLine = decodeMboxFrom(line);
		String[] parts = prettyLine.split(" ");
		if (prettyLine.indexOf('@')>0)
		{
			return parts[1];
		}
		
		for (int i = 0; i < parts.length; i++)
		{
			String s = parts[i];
			if("at".equals(s))
				return parts[i-1] + "@" + parts[i+1];
		}
		
		return null;
	}
	
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
		// null contentType results an exception
		if (contentType == null) throw new IllegalArgumentException();

		// figure out the name, if there is one.
		String name = "";
		int namestart = contentType.indexOf("name=");
		if (namestart > 0)
		{
			// add the number of chars in 'name='
			namestart += "name=".length();
			int endnamevalue = contentType.indexOf("\"", namestart + 1);

			// we have a quoted value
			if (endnamevalue > namestart) return contentType.substring(namestart + 1, endnamevalue);

			endnamevalue = contentType.indexOf(";", namestart);

			// we have a ; ended value
			if (endnamevalue > namestart) return contentType.substring(namestart, endnamevalue);

			endnamevalue = contentType.indexOf("\n", namestart);

			// we have a newline ended value, maybe with whitespace
			if (endnamevalue > namestart) return contentType.substring(namestart, endnamevalue).trim();
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

	/**
	 * Converts:  Re: Re: Foo to Re: Foo
	
	 * @param subject the subject message
	 * @param prefix null or the stuff in the []... Re: [List Name] Subject
	 * @param isReply if you know this is a reply, then always append Re:
	 * @return A perfect subject.
	 */
	public static String cleanRe(String subject, String prefix, boolean isReply)
	{
		if (prefix == null)
			prefix = new String();

		Matcher matcher = SUBJECT_PATTERN.matcher(subject);
		String result = null;
		if (matcher.find())
		{
			subject = subject.substring(matcher.end());
			result = "Re: " + prefix + subject;
		}
		else
		{
			if (isReply)
				result = "Re: " + prefix + subject;
			else
				result = prefix + subject;
		}
		return result;
	}

	/**
	 * Pass in either a comma and/or newline separated list of emails and it returns a list of InternetAddress
	 * @param input
	 * @return
	 * @throws AddressException if there is an error parsing the emails.
	 */
	public static InternetAddress[] parseMassSubscribe(String input) throws AddressException
	{
		String[] split = input.split("[\r\n\t]");
		
		List<InternetAddress> list = new ArrayList<InternetAddress>();
		for (String token : split)
		{
			token = token.trim();
			if (token.length() > 0)
			{
				InternetAddress[] addresses = InternetAddress.parse(token);
				for (InternetAddress address : addresses)
				{
					list.add(address);
				}
			}
		}
		return list.toArray(new InternetAddress[list.size()]);
	}
}
