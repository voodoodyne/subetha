package org.subethamail.core.util;

import javax.ejb.Local;

import org.jboss.ejb3.annotation.Management;

@Local
@Management
public interface WebBeanHelper {
	public static final String JNDI_NAME = "subetha/WebBeanHelper/local";

	public void start() throws Exception;
	public void stop() throws Exception;
	
	public Object GetWBInstance(String type) throws Exception;
	public void ReBindManager() throws Exception;
}