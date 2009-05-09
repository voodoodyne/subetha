package org.subethamail.common;

import javax.inject.manager.Manager;
import com.caucho.config.inject.InjectManager;

public class ResinBridge {
	private Manager mgr;
	
	public void setManager(Manager man){
		this.mgr = man;
	}
	
	public Manager getManager(){
		return this.mgr == null ? InjectManager.create(): this.mgr;
	}
}
