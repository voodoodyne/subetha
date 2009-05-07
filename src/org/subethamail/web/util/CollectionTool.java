/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.util;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Some simple static methods useful as JSP functions
 *
 * @author Jeff Schnitzer
 */
public class CollectionTool
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(CollectionTool.class);

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
