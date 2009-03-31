/*
 * $Id$
 * $URL$
 */

package org.subethamail.web;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.subethamail.entity.i.Permission;

/**
 * This is initialized as a servlet on startup so that it
 * can place itself in application scope; this makes it
 * available to JSPs as ${backend}.
 * 
 * Other classes in the web tier can obtain the instance
 * by calling Backend.instance().
 */
@SuppressWarnings("serial")
public class Backend extends HttpServlet
{
	/** Application-scope key */
	public static final String KEY = "backend";
	
	/** 
	 * There should only be one instance of this class, but it
	 * is created by the web container.  This static variable is
	 * initialized by the web container on init().
	 */
	static Backend singleton;
	
	/**
	 * Obtain the current instance.
	 */
	public static Backend instance() { return singleton; }
	
	@Override
	public void init() throws ServletException
	{
	}

	/**
	 * A convenient way of enumerating all the permissions from the presentation tier. 
	 */
	public Set<Permission> getAllPermissions()
	{
		return Permission.ALL;
	}
	
	/** @return some sense of what the whole application version # is */
	public String getVersion()
	{
		Package pkg = this.getClass().getPackage();
		return (pkg == null) ? null : pkg.getSpecificationVersion();
	}
	
	/** @return all the version numbers */
	public Package[] getVersions()
	{
		Comparator<Package> cmp = new Comparator<Package>() {
			public int compare(Package p1, Package p2)
			{
				if (p1.getName().startsWith("org.subethamail") && !p2.getName().startsWith("org.subethamail"))
					return -1;
				else if (!p1.getName().startsWith("org.subethamail") && p2.getName().startsWith("org.subethamail"))
					return 1;
				else
					return p1.getName().compareTo(p2.getName());
			}
		};
		
		Package[] packages = Package.getPackages();
		Arrays.sort(packages, cmp);
		return packages;
	}
}
