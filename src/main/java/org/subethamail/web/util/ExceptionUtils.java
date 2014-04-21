/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.util;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import lombok.extern.java.Log;



/**
 * Offers static utility methods to help when working with exceptions.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class ExceptionUtils
{
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
				t = ((JspException)t).getCause();
			else
				t = t.getCause();
		}
		while (t != null);
		
		return false;
	}
}