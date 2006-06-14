/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */
package org.subethamail.core.postfix;

import java.io.IOException;

import org.jboss.annotation.ejb.Management;

/**
 * JMX Management interface for the TcpTableService. The start() and stop() methods
 * are magically called by JBoss.
 * 
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
@Management
public interface TcpTableManagement
{
	/**
	 * Called when the service starts.
	 */
	public void start() throws IOException;

	/**
	 * Called when the service stops.
	 */
	public void stop();

	/**
	 * When the SMTP server starts, it will listen on this port.
	 */
	public int getPort();
	
	/**
	 * This can only be set on a stopped service.
	 */
	public void setPort(int port);

	/**
	 * The hostname the SMTP service reports.
	 */
	public String getHostName();
	
	/**
	 * Sets the hostname the SMTP service reports.  If null,
	 * one is guessed at.
	 */
	public void setHostName(String hostname);
}
