/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */
package org.subethamail.smtp;

import java.io.IOException;
import org.jboss.annotation.ejb.Management;
import org.subethamail.smtp.server.ServerRejectedException;

/**
 * JMX Management interface for the SMTPService. The start() and stop() methods
 * are magically called by JBoss.
 * 
 * @author Jeff Schnitzer
 */
@Management
public interface SMTPManagement
{
	/**
	 * Called when the service starts.
	 */
	public void start() throws IOException, ServerRejectedException;

	/**
	 * Called when the service stops.
	 */
	public void stop();

	/**
	 * Sets the port the SMTP service will listen on.
	 * 
	 * @param port
	 */
	public void setPort(int port);

	public int getPort();

	/**
	 * Sets the hostname the SMTP service will report itself to be running on.
	 * Also sets the default accept host.
	 * 
	 * @param hostname
	 */
	public void setHostname(String hostname);

	public String getHostname();

	/**
	 * Turns host resolution on or off. Turn off for better performance. Turn on
	 * for better security.
	 * 
	 * @param state
	 */
	public void setHostResolutionEnabled(boolean state);

	public boolean getHostResolutionEnabled();

	/**
	 * Changes the behavior of RCPT TO: to filter or not filter based on a list
	 * of known local domains.
	 * 
	 * @param state
	 */
	public void setRecipientDomainFilteringEnabled(boolean state);

	public boolean getRecipientDomainFilteringEnabled();
}
