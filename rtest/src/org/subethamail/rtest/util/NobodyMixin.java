/*
 * $Id: AdminMixin.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/util/AdminMixin.java $
 */

package org.subethamail.rtest.util;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SecurityAssociation;
import org.subethamail.core.acct.i.Receptionist;
import org.subethamail.core.acct.i.ReceptionistRemote;

/**
 * This class makes it easy to get the Receptionist - it clears credentials
 * before providing the receptionist interface.
 * 
 * @author Jeff Schnitzer
 */
public class NobodyMixin
{
	/** */
	private static Log log = LogFactory.getLog(NobodyMixin.class);

	/** */
	private Receptionist receptionist;
	
	/** */
	public NobodyMixin() throws Exception
	{
		Context ctx = new InitialContext();
		this.receptionist = (Receptionist)ctx.lookup(ReceptionistRemote.JNDI_NAME);
	}
	
	/**
	 * Get rid of all credentials
	 */
	public void clearCredentials()
	{
        SecurityAssociation.setPrincipal(null);
        SecurityAssociation.setCredential(null);
	}
	
	/** */
	public Receptionist getReceptionist() throws Exception
	{
		this.clearCredentials();
		
		return this.receptionist;
	}
}
