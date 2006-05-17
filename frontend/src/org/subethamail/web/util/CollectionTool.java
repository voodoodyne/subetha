/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.util;

import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Some simple static methods useful as JSP functions
 *  
 * @author Jeff Schnitzer
 */
public class CollectionTool 
{
	/** */
	private static Log log = LogFactory.getLog(CollectionTool.class);
	
	/**
	 * Does the set contain the object?
	 */
	public static boolean contains(Set set, Object obj)
	{
		if (set == null)
			return false;
		else
			return set.contains(obj);
	}
}
