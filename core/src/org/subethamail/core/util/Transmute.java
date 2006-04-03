/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.admin.i.BlueprintData;
import org.subethamail.core.plugin.i.Blueprint;



/**
 * Offers static utility methods to convert between internal entity
 * objects and public interface value objects. 
 * 
 * @author Jeff Schnitzer
 */
public class Transmute
{
	/** */
	private static Log log = LogFactory.getLog(Transmute.class);
	
	/** */
	public static List<BlueprintData> blueprints(Collection<Blueprint> rawColl)
	{
		List<BlueprintData> result = new ArrayList<BlueprintData>(rawColl.size());
		
		for (Blueprint raw: rawColl)
			result.add(blueprint(raw));
		
		return result;
	}
	
	/** */
	public static BlueprintData blueprint(Blueprint raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
			
		return new BlueprintData(
				raw.getClass().getName(),
				raw.getName(),
				raw.getDescription());
	}
	
}
