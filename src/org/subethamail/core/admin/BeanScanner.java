/*
 * $Id: $
 * $URL: $
 */

package org.subethamail.core.admin;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.plugin.i.BlueprintRegistry;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterRegistry;


/**
 * This class looks for any classes that are derived from
 * Blueprint or Filter and adds them to registered list.
 * 
 * If any plugins are to be added they must be done before 
 * the application is deployed.
 * 
 * @author Scott Hernandez
 */
public class BeanScanner
{
	@Inject	BlueprintRegistry	blueReg;
	@Inject	FilterRegistry		filtReg;

	/* Changed behavior to scan on creation, not method call. */
	@Inject @Any Instance<Filter> _filters;
	@Inject @Any Instance<Blueprint> _blueprints;
	
	/** Just registers classes. */
	public void registerScannedClasses()
	{
		for (Filter filt: _filters)
		{
			filtReg.register(filt.getClass());
		}
		for (Blueprint print: _blueprints)
		{
			blueReg.register(print.getClass());
		}
	}
}
