/*
 * $Id: Plumber.java 948 2007-04-26 06:27:45Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/i/Plumber.java $
 */

package org.subethamail.core.admin.i;



/**
 * Methods that assist in performing... experiments.  Or "testing" as the kids call it.
 * 
 * @author Jeff Schnitzer
 */
public interface EegorBringMeAnotherBrain
{
	/**
	 * Puts a log message in the adminstrator log at level INFO.
	 */
	public void log(String msg);
	
	/**
	 * Temporarily override the smtp server used and put us in the
	 * mode where emails are annotated with an easily recognizable
	 * tag.
	 * 
	 * @param mtaHost can be hostname:port
	 */
	public void enableTestMode(String mtaHost);
	
	/**
	 * Restore default stmp server.
	 */
	public void disableTestMode();

	/**
	 * Self-explanatory.
	 */
	public boolean isTestModeEnabled();
}
