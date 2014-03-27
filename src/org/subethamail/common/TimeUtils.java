/*
 * $Id$
 * $URL$
 */

package org.subethamail.common;

import java.sql.Timestamp;
import java.util.Date;



/**
 * Utility methods to work with date and time objects.
 * 
 * @author Jeff Schnitzer
 */
public class TimeUtils
{
	/** default constructor prevents util class from being created. */
	private TimeUtils() {}

	/**
	 * Equivalent of compareTo() but actually works because Sun
	 * fucked up the JDK.  See:
	 * 
	 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5103041
	 * http://forum.hibernate.org/viewtopic.php?t=959124
	 * http://forum.hibernate.org/viewtopic.php?t=925275
	 * http://forum.hibernate.org/viewtopic.php?t=927602
	 */
	public static int compareDates(Date date1, Date date2)
	{
		if (date1 instanceof Timestamp)
		{
			if (date2 instanceof Timestamp)
			{
				return date1.compareTo(date2);
			}
			else
			{
				return date1.compareTo(new Timestamp(date2.getTime()));
			}
		}
		else
		{
			if (date2 instanceof Timestamp)
			{
				return new Timestamp(date1.getTime()).compareTo(date2);
			}
			else
			{
				return date1.compareTo(date2);
			}
		}
	}
}
