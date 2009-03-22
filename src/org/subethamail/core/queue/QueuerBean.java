/*
 * $Id: QueuerBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/queue/QueuerBean.java $
 */

package org.subethamail.core.queue;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.mdb.ProducerManager;
import org.jboss.ejb3.mdb.ProducerObject;
import org.subethamail.core.queue.i.Queuer;
import org.subethamail.core.queue.i.QueuerRemote;

/**
 * @author Jeff Schnitzer
 */
@Stateless(name="Queuer")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class QueuerBean implements Queuer, QueuerRemote
{
	/** */
	private static Log log = LogFactory.getLog(QueuerBean.class);
	
	/** */
	// TODO: remove when fixed http://jira.jboss.com/jira/browse/EJBTHREE-518
	//@Resource(mappedName=Inbound.JNDI_NAME) Inbound inbound;
	//@Resource(mappedName=Outbound.JNDI_NAME) Outbound outbound;
	
	/** TODO: remove when fixed http://jira.jboss.com/jira/browse/EJBTHREE-518 */
	Inbound getInbound()
	{
		try
		{
			Context ctx = new InitialContext();
			return (Inbound)ctx.lookup(Inbound.JNDI_NAME);
		}
		catch (NamingException ex) { throw new RuntimeException(ex); }
	}
	
	/** TODO: remove when fixed http://jira.jboss.com/jira/browse/EJBTHREE-518 */
	Outbound getOutbound()
	{
		try
		{
			Context ctx = new InitialContext();
			return (Outbound)ctx.lookup(Outbound.JNDI_NAME);
		}
		catch (NamingException ex) { throw new RuntimeException(ex); }
	}
	
	/**
	 * @see Queuer#queueForDelivery(Long)
	 */
	public void queueForDelivery(Long mailId)
	{
		if (log.isDebugEnabled())
			log.debug("Queuing mailId " + mailId + " for distribution");
		
		Inbound inbound = this.getInbound();
		
		try
		{
			ProducerManager manager = (ProducerManager)((ProducerObject)inbound).getProducerManager();
			manager.connect();
			try
			{
				inbound.deliver(mailId);
			}
			finally
			{
				manager.close();
			}
		}
		catch (JMSException ex) { throw new EJBException(ex); }
	}
	
	/**
	 * @see Queuer#queueForDelivery(Long, Long)
	 */
	public void queueForDelivery(Long mailId, Long personId)
	{
		if (log.isDebugEnabled())
			log.debug("Queuing mailId " + mailId + " for delivery to personId " + personId);
		
		Outbound outbound = this.getOutbound();
		
		try
		{
			ProducerManager manager = (ProducerManager)((ProducerObject)outbound).getProducerManager();
			manager.connect();
			try
			{
				outbound.deliver(mailId, personId);
			}
			finally
			{
				manager.close();
			}
		}
		catch (JMSException ex) { throw new EJBException(ex); }
	}
	
	/**
	 * @see Queuer#queueForDelivery(Long, List)
	 */
	public void queueForDelivery(Long mailId, List<Long> personIds)
	{
		if (log.isDebugEnabled())
			log.debug("Queuing mailId " + mailId + " for delivery to personIds " + personIds);
		
		Outbound outbound = this.getOutbound();
		
		try
		{
			ProducerManager manager = (ProducerManager)((ProducerObject)outbound).getProducerManager();
			manager.connect();
			try
			{
				outbound.deliver(mailId, personIds);
			}
			finally
			{
				manager.close();
			}
		}
		catch (JMSException ex) { throw new EJBException(ex); }
	}
}

