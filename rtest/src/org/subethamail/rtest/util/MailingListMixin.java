/*
 * $Id: PersonMixin.java 88 2006-02-22 13:51:08Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/util/PersonMixin.java $
 */

package org.subethamail.rtest.util;

import java.util.Collections;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SecurityAssociation;
import org.jboss.security.SimplePrincipal;
import org.subethamail.core.acct.i.AccountMgr;

/**
 * @author Jeff Schnitzer
 */
public class MailingListMixin extends MailingListInfoMixin
{
	/** */
	private static Log log = LogFactory.getLog(MailingListMixin.class);

	Long id;
	List<InternetAddress> initialOwners;
	
	/** */
	public MailingListMixin(AdminMixin adminMixin, PersonInfoMixin personMixin) throws Exception
	{
		super();
		
		this.initialOwners = Collections.singletonList(new InternetAddress(personMixin.getEmail()));
		
		this.id = adminMixin.getAdmin().createMailingList(this.address, this.url, this.initialOwners);
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
