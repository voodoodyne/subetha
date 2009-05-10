package org.subethamail.common;

import javax.inject.manager.Manager;

import com.caucho.config.inject.InjectManager;

/**
 * Acts as a holder for Resin's CanDI injection manager.  Gets populated with
 * the webapp manager by the Backend at startup and then anyone who has a
 * reference to this object can now get a reference to the manager. 
 */
public class ResinBridge
{
	public static final String NAME = "resin-bridge";
	
	private Manager mgr;
	
	public void setManager(Manager man)
	{
		this.mgr = man;
	}
	
	public Manager getManager()
	{
		return this.mgr == null ? InjectManager.create() : this.mgr;
	}
}
