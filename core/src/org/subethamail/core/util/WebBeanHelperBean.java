package org.subethamail.core.util;

import javax.inject.Current;
import javax.inject.manager.Manager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.jboss.ejb3.annotation.Service;

@Service(objectName="subetha:service=WebBeanHelper")
public class WebBeanHelperBean implements WebBeanHelper
{
	@Current
	private Manager manager;

	@SuppressWarnings("unchecked")
	@Override
	public Object GetWBInstance(String type) throws Exception {
		try {
			System.out.println("Looking up " + type);		
			if(manager != null)
			{
				Class typeClass = Class.forName(type);
				Object o = null;
				o = manager.getInstanceByType(typeClass);
				System.out.println(" Got a class back by type: " + o != null ? o.getClass().toString() : " -null- ");

				o = manager.getInstanceByName(type);
				System.out.println(" Got a class back by name: " + o != null ? o.getClass().toString() : " -null- ");
				return o;
			}
			else {
				System.out.println("No Manager Avail");	
			}		
		} catch (Exception e) 
		{
			System.out.println("Error: " + e.toString());
		}
		return null;
	}

	@Override
	public void start() throws Exception {		
		if(this.manager == null) System.out.println("No manager injected!");		
		//Bad, call rebind and it will get it from JNDI
		ReBindManager();
		if(this.manager == null) System.out.println("Very Bad, could not get Manager!");
	}
	
	private void UnBindManager()
	{
		try{
			Context c = new InitialContext();
			c.unbind("java:/Manager");
			System.out.println("unbound manager at java:/Manager");
			this.manager = null;
			return;
		} catch(NamingException ne){
			//do nothing
		}		
	}
	
	@Override
	public void stop() throws Exception {
		UnBindManager();
	}
	
	protected Manager GetManagerFromJNDI(){
		Manager wbManager = null;
		try {
			Context c	 = new InitialContext();
			Object obj = c.lookup("java:comp/Manager");
			wbManager = (Manager)PortableRemoteObject.narrow(obj, Manager.class);
		} catch (NamingException e) {
			System.out.println("Failed to get WebBeans Manager via JNDI lookup:" + e.toString());
		}
		return wbManager;
	}
	
	@Override
	public void ReBindManager() throws Exception {
		if(this.manager == null){ this.manager = this.GetManagerFromJNDI(); }
		
		UnBindManager();
		if(this.manager != null){
			try{
				Context c = new InitialContext();
				c.bind("java:/Manager", this.manager);
				System.out.println("bound manager at java:/Manager");
				return;
			} catch(NamingException ne){
				throw new RuntimeException(ne);
			}
		}
		System.out.println("Could not get manager to bind at java:/Manager");
	}
}
