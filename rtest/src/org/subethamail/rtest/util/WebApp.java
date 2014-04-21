package org.subethamail.rtest.util;

public class WebApp
{
    public static String HOSTPORT_PROPERTY=System.getProperty("appserver.url");
	public static String HOSTPORT = HOSTPORT_PROPERTY==null?"http://localhost:8080":HOSTPORT_PROPERTY;
	public static String BASEURL = HOSTPORT + "/se";

	public static String getBaseUrl()
	{
		return BASEURL;
	}
	
	public static String getHostPort()
	{
		return HOSTPORT;
	}
}
