/*
 * $Id$
 * $URL$
 */

package org.subethamail.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Some static utility methods to convert between various formats. 
 * 
 * @author Jeff Schnitzer
 */
public class Converter
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(Converter.class);
	
	/** default constructor keeps util class from being created. */
	private Converter() {}
	
	/**
	 * Converts from the String version to a native object 
	 * of type clazz by calling the static valueOf(String) method.
	 * This is the opposite of an object's toString() method.
	 */
	public static Object valueOf(String stringValue, Class clazz) throws Exception
	{
		try
		{
			if (clazz.equals(String.class))
			{
				return stringValue;
			}
			else if (clazz.equals(Character.class))
			{
				return stringValue.charAt(0);
			}
			else
			{
				Method m = clazz.getMethod("valueOf", String.class);
				
				return m.invoke(null, stringValue);
			}
		}
		catch (InvocationTargetException ex)
		{
			// We want to throw the real exception instead of the one we have here.  
			if (ex.getCause() instanceof Exception)
				throw (Exception)ex.getCause();
			else if (ex.getCause() instanceof Error)
				throw (Error)ex.getCause();
			else
				throw ex; 
		}
		catch (NoSuchMethodException ex)
		{
			// More useful error message?
			throw new NoSuchMethodException("Class " + clazz + " does not have a valueOf(String) method"); 
		}
//		catch (IllegalAccessException ex)
//		{
//			// More useful error message?
//			throw new IllegalAccessException("Unable to invoke " + clazz + ".valueOf(String)");
//		}
	}
}
