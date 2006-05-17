/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import javax.mail.internet.InternetAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Easy way to create a mailing list from a unit test.
 * 
 * @author Jeff Schnitzer
 */
public class MailingListMixin extends MailingListInfoMixin
{
	/** */
	private static Log log = LogFactory.getLog(MailingListMixin.class);

	Long id;
	InternetAddress[] initialOwners;
	
	/**
	 * @param initialOwner can be null to create an ownerless list. 
	 */
	public MailingListMixin(AdminMixin adminMixin, InternetAddress initialOwner) throws Exception
	{
		super();
		
		if (initialOwner == null)
			this.initialOwners = new InternetAddress[0];
		else
			this.initialOwners = new InternetAddress[] { initialOwner };
		
		this.id = adminMixin.getAdmin().createMailingList(this.address, this.url, this.description, this.initialOwners);
	}
	
	/** */
	public Long getId() { return this.id; }
}
