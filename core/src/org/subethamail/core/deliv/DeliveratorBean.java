/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.deliv;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.admin.BootstrapperBean;
import org.subethamail.core.deliv.i.Deliverator;
import org.subethamail.core.deliv.i.DeliveratorRemote;

/**
 * @author Jeff Schnitzer
 */
@Stateless(name="Deliverator")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class DeliveratorBean implements Deliverator, DeliveratorRemote
{
	/** */
	private static Log log = LogFactory.getLog(BootstrapperBean.class);
	
	/**
	 * @see Deliverator#deliver(Long, Long)
	 */
	public void deliver(Long mailId, Long personId)
	{
		if (log.isDebugEnabled())
			log.debug("Delivering mailId " + mailId + " to personId " + personId);
	}
}

