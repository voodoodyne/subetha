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
import org.subethamail.core.acct.i.AccountMgrRemote;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.AdminRemote;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.lists.i.ListMgrRemote;

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
	private static Log log = LogFactory.getLog(BeanMixin.class);

	private Admin admin;
	private AccountMgr accountMgr;
	private ListMgr listMgr;
	
	
	/** */
	public BeanMixin() throws Exception
	{
		Context ctx = new InitialContext();
		this.admin = (Admin)ctx.lookup(AdminRemote.JNDI_NAME);
		this.accountMgr = (AccountMgr)ctx.lookup(AccountMgrRemote.JNDI_NAME);
		this.listMgr = (ListMgr)ctx.lookup(ListMgrRemote.JNDI_NAME);
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
		return this.listMgr;
	}
}
