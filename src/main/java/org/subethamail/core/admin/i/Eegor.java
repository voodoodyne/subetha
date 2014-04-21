package org.subethamail.core.admin.i;



/**
 * Methods that assist in performing... experiments.  Or "testing" as the kids call it.
 * 
 * @author Jeff Schnitzer
 */
public interface Eegor
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

	/**
	 * Sets the fallthrough host, or null to clear it.  This doesn't create
	 * a persistent situation; it's really only for unit testing.
	 */
	public void setFallbackHost(String host);
}
