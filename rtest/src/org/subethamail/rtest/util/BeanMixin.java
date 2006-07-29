/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import java.security.Principal;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SecurityAssociation;
import org.jboss.security.SimplePrincipal;
import org.subethamail.core.acct.i.AccountMgr;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.ListWizard;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.search.i.Indexer;

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

	private Admin admin;
	private AccountMgr accountMgr;
	private ListMgr listMgr;
	private ListWizard listWizard;
	private Indexer indexer;
	private Injector injector;
	
	
	/** */
	public BeanMixin() throws Exception
	{
		Context ctx = new InitialContext();
		this.admin = (Admin)ctx.lookup(Admin.JNDI_NAME);
		this.accountMgr = (AccountMgr)ctx.lookup(AccountMgr.JNDI_NAME);
		this.listMgr = (ListMgr)ctx.lookup(ListMgr.JNDI_NAME);
		this.listWizard = (ListWizard)ctx.lookup(ListWizard.JNDI_NAME);
		this.indexer = (Indexer)ctx.lookup(Indexer.JNDI_NAME);
		this.injector = (Injector)ctx.lookup(Injector.JNDI_NAME);
	}
	
	/** If this is null, clears all credentials */
	public String getEmail() { return null; };
	
	/** */
	public String getPassword() { return null; }
	
	/**
	 * Establish administrator credentials
	 */
	public void establish()
	{
		if (this.getEmail() == null)
		{
	        SecurityAssociation.setPrincipal(null);
	        SecurityAssociation.setCredential(null);
		}
		else
		{
			Principal p = new SimplePrincipal(this.getEmail());
	        SecurityAssociation.setPrincipal(p);
	        SecurityAssociation.setCredential(this.getPassword());
		}
	}
	
	/** */
	public Admin getAdmin() throws Exception
	{
		this.establish();
		return this.admin;
	}
	
	/** */
	public AccountMgr getAccountMgr()
	{
		this.establish();
		return this.accountMgr;
	}
	
	/** */
	public ListMgr getListMgr()
	{
		this.establish();
		return this.listMgr;
	}

	/** */
	public ListWizard getListWizard()
	{
		this.establish();
		return this.listWizard;
	}
	
	/** */
	public Indexer getIndexer()
	{
		this.establish();
		return this.indexer;
	}
	
	/** */
	public Injector getInjector()
	{
		this.establish();
		return this.injector;
	}
}
