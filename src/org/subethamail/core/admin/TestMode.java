package org.subethamail.core.admin;

import javax.context.ApplicationScoped;
import javax.context.RequestScoped;

/**
 * A special {@link RequestScoped} object to control test behavior during the current request.
 * 
 * @author Scott Hernandez
 */

@ApplicationScoped
public class TestMode
{
	private boolean inTestMode = false;
	
	/**
	 * Sets up test mode. Anything that needs to change for 
	 * test to work
	 * 
	 * @param inTestMode to turn on, or off test mode. 
	 * @return if in test mode.
	 */
	public boolean setTestMode(boolean testMode)
	{
		inTestMode = testMode;
		return inTestMode;
	}

	/**
	 * 
	 * @return Are we testing, this will tell you.
	 */
	public boolean isTesting()
	{
		return true == this.inTestMode;
	}

}
