/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.util;

import java.util.Set;

import lombok.extern.java.Log;


/**
 * Some simple static methods useful as JSP functions
 *
 * @author Jeff Schnitzer
 */
@Log
public class CollectionTool
{
	/**
	 * Does the set contain the object?
	 */
	public static boolean contains(Set<?> set, Object obj)
	{
		if (set == null)
			return false;
		else
			return set.contains(obj);
	}
}
