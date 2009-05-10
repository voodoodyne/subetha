/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import javax.inject.manager.Manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.ResinBridge;
import org.subethamail.core.acct.i.AccountMgr;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.ListWizard;
import org.subethamail.core.admin.i.Plumber;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.lists.i.Archiver;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.search.i.Indexer;

import com.caucho.resin.BeanEmbed;
import com.caucho.resin.ResinEmbed;

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
	private static Logger log = LoggerFactory.getLogger(BeanMixin.class);

	public static ResinEmbed RESIN;
	public static Manager MGR;

	/** */
	public BeanMixin() throws Exception
	{
		if(RESIN==null){
		    ResinEmbed resin = new ResinEmbed("conf/resin.xml");

//		    HttpEmbed http = new HttpEmbed(8181);
//		    resin.addPort(http);
		    
//		    WebAppEmbed webApp = new WebAppEmbed("/se-test");
//		    webApp.setArchivePath("../subetha.war");
		    ResinBridge rb = new ResinBridge();
		    resin.addBean(new BeanEmbed(rb, "resin-bridge"));
//
//		    resin.addWebApp(webApp);
		    resin.start();

		    //try to get the manager out :)
		    MGR = rb.getManager();
		    RESIN = resin;
		}
	}
	
	/** If this is null, clears all credentials */
	public String getPrincipalName() { return null; };
	
	/** */
	public String getPassword() { return null; }
	
	/**
	 * Establish administrator credentials
	 */
	@SuppressWarnings("unchecked")
	public Object getInterface(Class clazz)
	{
		return MGR.getInstanceByType(clazz);
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
