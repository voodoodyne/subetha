/*
 * $Id: TcpTableManagement.java 988 2008-12-30 08:51:13Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */
package org.subethamail.core.postfix;


/**
 * JMX Management interface for the TcpTableService. The start() and stop() methods
 * are magically called by JBoss.
 * 
 * @author Jon Stevens
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
public interface PostFixTcpTableManagement
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
}