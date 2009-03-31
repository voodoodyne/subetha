package org.subethamail.core.admin;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.inject.Current;

import net.sourceforge.stripes.util.ResolverUtil;

import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.plugin.i.BlueprintRegistry;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterRegistry;

import com.caucho.config.Service;

@Service
public class ScannerService
{
	@Current
	BlueprintRegistry	blueReg;
	
	@Current
	FilterRegistry		filtReg;

	@PostConstruct
	public void postConstruct()
	{
		Package[] pkgs = Package.getPackages();
		List<String> strPkgs = new Vector<String>();
		for(Package p: pkgs)
		{
			strPkgs.add(p.getName());
		}

		registerBlueprints((String[]) strPkgs.toArray());
		registerFilters((String[]) strPkgs.toArray());
		
	}
	
	private void registerFilters(String[] pkgs)
	{
		ResolverUtil<Filter> ru = new ResolverUtil<Filter>();
		ru.findImplementations(Filter.class, pkgs);
		Set<Class<? extends Filter>> classes = ru.getClasses();
		for(Class<? extends Filter> c : classes)
		{
			filtReg.register(c.getName());
		}		
	}

	private void registerBlueprints(String[] pkgs)
	{
		ResolverUtil<Blueprint> ru = new ResolverUtil<Blueprint>();
		ru.findImplementations(Blueprint.class, pkgs);
		Set<Class<? extends Blueprint>> classes = ru.getClasses();
		for(Class<? extends Blueprint> c : classes)
		{
			blueReg.register(c.getName());
		}	
	}
	
}
