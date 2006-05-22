/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.util;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Offers static utility methods to help when working with exceptions.
 * 
 * @author Jeff Schnitzer
 */
public class ExceptionUtils
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(ExceptionUtils.class);
	
	/**
	 * @return true if the throwable or any of its causes are
	 *  subclasses of causeClass.
	 */
	public static <T extends Throwable> boolean causedBy(Throwable t, Class<T> causeClass)
	{
		do
		{
			if (causeClass.isAssignableFrom(t.getClass()))
				return true;
			
			if (t instanceof ServletException)
				t = ((ServletException)t).getRootCause();
			else if (t instanceof JspException)
				t = ((JspException)t).getRootCause();
			else
				t = t.getCause();
		}
		while (t != null);
		
		return false;
	}
}
