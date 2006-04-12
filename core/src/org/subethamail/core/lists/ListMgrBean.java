/*
 * $Id: AccountMgrEJB.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/AccountMgrEJB.java $
 */

package org.subethamail.core.lists;

import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.annotation.EJB;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.lists.i.ListMgrRemote;
import org.subethamail.core.lists.i.SubscriberData;
import org.subethamail.core.util.PersonalBean;
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.dao.DAO;

/**
 * Implementation of the AccountMgr interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="ListMgr")
@SecurityDomain("subetha")
@RolesAllowed("user")
@RunAs("siteAdmin")
public class ListMgrBean extends PersonalBean implements ListMgr, ListMgrRemote
{
	/** */
	private static Log log = LogFactory.getLog(ListMgrBean.class);

	/**
	 */
	@EJB DAO dao;

	/**
	 * @see ListMgr#lookup(URL)
	 */
	@PermitAll
	public Long lookup(URL url) throws NotFoundException
	{
		return this.dao.findMailingList(url).getId();
	}
	
	/**
	 * @see ListMgr#getSubscribers(Long)
	 */
	public List<SubscriberData> getSubscribers(Long listId) throws NotFoundException
	{
		MailingList list = this.dao.findMailingList(listId);
		Set<Subscription> listSubscriptions = list.getSubscriptions();
		return Transmute.subscribers(listSubscriptions);
	}
}
