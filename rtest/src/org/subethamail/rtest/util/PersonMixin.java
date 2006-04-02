/*
 * $Id: PersonMixin.java 88 2006-02-22 13:51:08Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/util/PersonMixin.java $
 */

package org.subethamail.rtest.util;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SecurityAssociation;
import org.jboss.security.SimplePrincipal;
import org.subethamail.core.acct.i.AccountMgr;
import org.subethamail.core.acct.i.AccountMgrRemote;

/**
 * @author Jeff Schnitzer
 */
public class PersonMixin extends PersonInfoMixin
{
	/** */
	private static Log log = LogFactory.getLog(PersonMixin.class);

	Long id;
	
	AccountMgr accountMgr;
	
	/** */
	public PersonMixin(AdminMixin adminMixin) throws Exception
	{
		super();
		
		Context ctx = new InitialContext();
		this.accountMgr = (AccountMgr)ctx.lookup(AccountMgrRemote.JNDI_NAME);
		
		this.id = adminMixin.getAdmin().establishPerson(this.email, this.name, this.password);
	}
	
	/** */
	public Long getId() { return this.id; }
	
	/** */
	public void establish()
	{
		SecurityAssociation.setPrincipal(new SimplePrincipal(this.email));
		SecurityAssociation.setCredential(this.password);
	}
	
	/** */
	public AccountMgr getAccountMgr()
	{
		this.establish();
		return this.accountMgr;
	}
}
