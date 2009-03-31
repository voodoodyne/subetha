/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.acct.i.AccountMgr;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.ListWizard;
import org.subethamail.core.admin.i.Plumber;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.lists.i.Archiver;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.search.i.Indexer;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * This class makes it easy to obtain and use the various
 * EJBs.  Extending this class provides accessors that
 * establish the correct identity first.
 * 
 * Subclasses should override getEmail() and getPassword().
 * 
 * @author Jeff Schnitzer
 */
public class BeanMixin
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(BeanMixin.class);

	/** */
	public BeanMixin() throws Exception
	{
	}
	
	/** If this is null, clears all credentials */
	public String getPrincipalName() { return null; };
	
	/** */
	public String getPassword() { return null; }
	
	/**
	 * Establish administrator credentials
	 */
	public Object getInterface(Class clazz)
	{
		HessianProxyFactory fact = new HessianProxyFactory();
		if (this.getPrincipalName() != null)
		{
			fact.setUser(this.getPrincipalName());
			fact.setPassword(this.getPassword());
		}
		
		String url = "http://localhost:8080/api/" + clazz.getSimpleName();
		
		try
		{
			return fact.create(clazz, url);
		}
		catch (MalformedURLException ex) { throw new RuntimeException(ex); }
	}
	
	/** */
	public Admin getAdmin() throws Exception
	{
		return (Admin)this.getInterface(Admin.class);
	}
	
	/** */
	public AccountMgr getAccountMgr()
	{
		return (AccountMgr)this.getInterface(AccountMgr.class);
	}
	
	/** */
	public ListMgr getListMgr()
	{
		return (ListMgr)this.getInterface(ListMgr.class);
	}

	/** */
	public ListWizard getListWizard()
	{
		return (ListWizard)this.getInterface(ListWizard.class);
	}
	
	/** */
	public Indexer getIndexer()
	{
		return (Indexer)this.getInterface(Indexer.class);
	}
	
	/** */
	public Injector getInjector()
	{
		return (Injector)this.getInterface(Injector.class);
	}
	
	/** */
	public Archiver getArchiver()
	{
		return (Archiver)this.getInterface(Archiver.class);
	}
	
	/** */
	public Plumber getPlumber()
	{
		return (Plumber)this.getInterface(Plumber.class);
	}
}
