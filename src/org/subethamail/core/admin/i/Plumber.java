/*
 * $Id: Plumber.java 948 2007-04-26 06:27:45Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/i/Plumber.java $
 */

package org.subethamail.core.admin.i;

import javax.ejb.Local;


/**
 * Miscellaneous administrative tools requiring admin role.
 * 
 * @author Jeff Schnitzer
 */
@Local
public interface Plumber
{
	/** */
	public static final String JNDI_NAME = "subetha/Plumber/local";
	
	/**
	 * Puts a log message in the adminstrator log at level INFO.
	 */
	public void log(String msg);
	
	/**
	 * Temporarily override the smtp server used.
	 * 
	 * @param host can be hostname:port
	 */
	public void overrideSmtpServer(String host);
	
	/**
	 * Restore default stmp server.
	 */
	public void restoreStmpServer();
	
	/**
	 * Sets up test mode. Anything that needs to change for 
	 * test to work
	 * 
	 * @param inTestMode to turn on, or off test mode. 
	 * @return if in test mode.
	 */
	public boolean setTestMode(boolean inTestMode);
}
