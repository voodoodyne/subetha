/*
 * $Id$
 * $URL$
 */

package org.subethamail.web;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.subethamail.common.SiteUtils;
import org.subethamail.core.acct.i.AccountMgr;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.admin.i.ListWizard;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.lists.i.Archiver;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.entity.i.Permission;
import org.subethamail.web.security.SubEthaLogin;

/**
 * Singleton which provides access to the backend EJBs.  
 * 
 * This is initialized as a servlet on startup so that it
 * can place itself in application scope; this makes it
 * available to JSPs as ${backend}.
 * 
 * Other classes in the web tier can obtain the instance
 * by calling Backend.instance().
 */
public class Backend extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/** Application-scope key */
	public static final String KEY = "backend";
	
	/** 
	 * There should only be one instance of this class, but it
	 * is created by the web container.  This static variable is
	 * initialized by the web container on init().
	 */
	static Backend singleton;
	
	/** Stateless session EJB references are all thread-safe */
	@Inject Injector injector;
	@Inject Admin admin;
	@Inject Encryptor encryptor;
	@Inject ListMgr listMgr;
	@Inject AccountMgr accountMgr;
	@Inject Archiver archiver;

	@Inject ListWizard listWizard;
	@Inject SiteUtils siteUtils;
	
	/**
	 * Allows us to login and logout to the container.
	 */
	@Inject SubEthaLogin resinLogin;
	
	/**
	 * Obtain the current instance.
	 */
	public static Backend instance() { return singleton; }
	
	/**
	 * Initialize all the ejb references and make them
	 * available in the application scope.
	 */
	@Override
	public void init() throws ServletException
	{		
		this.getServletContext().setAttribute(KEY, this);
		this.siteUtils.setContextPath(this.getServletContext().getContextPath());
		singleton = this;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy()
	{
		this.getServletContext().removeAttribute(KEY);
		
		singleton = null;
	}
	
	/**
	 * A convenient way of enumerating all the permissions from the presentation tier. 
	 */
	public Set<Permission> getAllPermissions()
	{
		return Permission.ALL;
	}
	
	/** */
	public SubEthaLogin getLogin()
	{
		return this.resinLogin;
	}

	/** */
	public Injector getInjector()
	{
		return this.injector;
	}

	/** */
	public Admin getAdmin()
	{
		return this.admin;
	}

	/** */
	public Encryptor getEncryptor()
	{
		return this.encryptor;
	}

	/** */
	public ListWizard getListWizard()
	{
		return this.listWizard;
	}

	/** */
	public AccountMgr getAccountMgr()
	{
		return this.accountMgr;
	}

	/** */
	public ListMgr getListMgr()
	{
		return this.listMgr;
	}
	
	/** */
	public Archiver getArchiver()
	{
		return this.archiver;
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
	/** @return the webapp context path ("/se/") */
	public String getContextPath()
	{
		return siteUtils.getContextPath();
	}
	/** @return {@link SiteUtils}*/
	public SiteUtils getSiteUtils()
	{
		return siteUtils;
	}
	
}
