/*
 * $Id: $
 * $URL: $
 */

package org.subethamail.common;

import javax.inject.Produces;
import javax.inject.manager.InjectionPoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Creates loggers for any classes (from webbeans)
 * 
 * Used like this: @Current static Log log;
 * 
 * @author Scott Hernandez
 *
 */

public class SubethaLogFactory {
	@Produces
	Log createLogger(InjectionPoint injectionPoint) {
		return LogFactory.getLog(injectionPoint.getMember().getDeclaringClass()
				.getName());
	}

}
