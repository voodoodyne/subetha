/*
 * $Id: SMTPManagement.java 273 2006-05-07 04:00:41Z jon $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */
package org.subethamail.core.smtp;

import java.net.InetAddress;

/**
 * JMX Management interface for the SMTPService. The start() and stop() methods
 * are magically called by JBoss.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
public interface SMTPManagement
{
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
	
	/**
	 * The address which the server is bound to. We first attempt to
	 * lookup the 'jboss.bind.address' system property. If it is null
	 * the we have bound to *.
	 */
	public InetAddress getBinding();
}