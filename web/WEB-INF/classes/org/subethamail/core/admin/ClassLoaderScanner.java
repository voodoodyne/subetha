/*
 * $Id: PlumberBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/PlumberBean.java $
 */

package org.subethamail.core.admin;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Current;

import net.sourceforge.stripes.util.ResolverUtil;

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
 * 
 */

public class ClassLoaderScanner
{
	@Current
	BlueprintRegistry	blueReg;
	
	@Current
	FilterRegistry		filtReg;

	/*
	 * Creates a list of all the packages loaded and 
	 * calls the scanAndRegister methods
	 * 
	 */
	public void scan()
	{
		Package[] pkgs = Package.getPackages();
		List<String> foundPackages = new ArrayList<String>(pkgs.length);
		for(Package p: pkgs)
		{
			String packageName = p.getName();
			if(!packageName.startsWith("java.") 	&&
			   !packageName.startsWith("javax.")  	&&
			   !packageName.startsWith("org.apache.")  	&&
			   !packageName.startsWith("org.hibernate")  	&&
			   !packageName.startsWith("org.omg.")  	&&
			   !packageName.startsWith("org.xml.")  	&&
			   !packageName.startsWith("org.w3c.")  	&&
			   !packageName.startsWith("sun.")  	&&
			   !packageName.startsWith("com.sun.")  &&
			   !packageName.startsWith("com.caucho")
					) 
			{
				foundPackages.add(p.getName());
			}
		}
		
		String[] packages = (String[])Array.newInstance(String.class, foundPackages.size());
		
		int i = 0;
		for(String p : foundPackages){ packages[i]=p;i++; }
		
		scanAndRegisterBlueprints(packages);
		scanAndRegisterFilters(packages);
	}
	
	/**
	 * 
	 * Looks for anything derived from Filter, and 
	 * adds it to the FilterRegistry
	 * 
	 * @param pkgs The list of packages to scan
	 */
	private void scanAndRegisterFilters(String[] pkgs)
	{
		ResolverUtil<Filter> ru = new ResolverUtil<Filter>();
		ru.findImplementations(Filter.class, pkgs);
		Set<Class<? extends Filter>> classes = ru.getClasses();
		for(Class<? extends Filter> c : classes)
		{
			if(!c.isInterface()) filtReg.register(c);
		}		
	}

	/**
	 * 
	 * Looks for anything derived from Blueprint, and 
	 * adds it to the BlueprintRegistry
	 * 
	 * @param pkgs The list of packages to scan
	 */

	private void scanAndRegisterBlueprints(String[] pkgs)
	{
		ResolverUtil<Blueprint> ru = new ResolverUtil<Blueprint>();
		ru.findImplementations(Blueprint.class, pkgs);
		Set<Class<? extends Blueprint>> classes = ru.getClasses();
		for(Class<? extends Blueprint> c : classes)
		{
			if(!c.isInterface()) blueReg.register(c);
		}	
	}	
}