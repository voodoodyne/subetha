/*
 * $Id: $
 * $URL: $
 */

package org.subethamail.core.admin;

import java.util.Set;

import javax.inject.Current;
import javax.inject.manager.Manager;

import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.plugin.i.BlueprintRegistry;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterRegistry;

import com.caucho.config.inject.AbstractBean;
import com.caucho.config.inject.InjectManager;


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
	@Current
	BlueprintRegistry	blueReg;
	
	@Current
	FilterRegistry		filtReg;

	@Current Manager 	mgr;
	
	/** */
	@SuppressWarnings("unchecked")
	public void scan()
	{
		Set<AbstractBean<? extends Filter>> filtBeans = ((InjectManager)mgr).resolve(Filter.class, null);
		
		for (AbstractBean<? extends Filter> bean : filtBeans)
		{
			filtReg.register((Class<? extends Filter>)bean.getTargetClass());
		}
		
		Set<AbstractBean<? extends Blueprint>> blueBeans = ((InjectManager)mgr).resolve(Blueprint.class, null);
		
		for (AbstractBean<? extends Blueprint> bean : blueBeans)
		{
			blueReg.register((Class<? extends Blueprint>)bean.getTargetClass());
		}
	}
}
