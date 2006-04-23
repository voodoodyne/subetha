/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.web.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;



/**
 * Offers static utility methods to help when working with enums.
 * 
 * @author Jeff Schnitzer
 */
public class EnumUtils
{
	/** */
	private static Log log = LogFactory.getLog(EnumUtils.class);
	
	/**
	 * @param enumClass must be derived from java.lang.Enum
	 * 
	 * @return the array of values associated with the enum class.
	 */
	public static Enum[] getValues(Class enumClass)
	{
		try
		{
			Method m = enumClass.getMethod("values");
			
			return (Enum[])m.invoke(null);
		}
		catch (NoSuchMethodException ex)
		{
			throw new HibernateException("Class " + enumClass + " is not an Enum", ex); 
		}
		catch (InvocationTargetException ex)
		{
			throw new HibernateException(enumClass + ".values() threw an exception:  " + ex.getCause(), ex); 
		}
		catch (IllegalAccessException ex)
		{
			throw new HibernateException("Unable to invoke " + enumClass + ".values()", ex);
		}
	}
}
