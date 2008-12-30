/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */
package org.subethamail.core.smtp;

import javax.ejb.Local;

import org.subethamail.smtp.helper.SimpleMessageListener;

/**
 * Listeners that wish to partake of inbound SMTP traffic must register
 * themselves with this interface.
 * 
 * @author Jeff Schnitzer
 */
@Local
public interface MessageListenerRegistry
{
	/**
	 * Register a listener. The listener will become active immediately.
	 */
	public void register(SimpleMessageListener listener);

	/**
	 * Deregister a listener. The listener will no longer receive notifications.
	 */
	public void deregister(SimpleMessageListener listener);
}
