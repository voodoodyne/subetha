package org.subethamail.core.util;

import javax.inject.Current;
import javax.inject.New;
import javax.inject.manager.Manager;

@New
public class InjectBeanHelper<T> {

	@Current Manager mgr;
	
	public InjectBeanHelper(){}
	
	@SuppressWarnings("unchecked")
	public T getInstance(String clazz){
		T o = null;
		Class<? extends T> tc = null;
		try {
			tc = (Class<? extends T>) Class.forName(clazz);
			o = (T)mgr.getInstanceByType(tc);
		}catch(ClassNotFoundException e){}
		
		if(o==null) o = (T)mgr.getInstanceByName(clazz);
		
		return o;		
	}
	public T getInstance(Class<? extends T> clazz){
		return this.mgr.getInstanceByType(clazz);
	}
}
