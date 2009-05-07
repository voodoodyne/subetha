package org.subethamail.rtest.util;

public class WebApp {
	public static String HOSTPORT = "http://localhost:8080";
	public static String BASEURL = HOSTPORT + "/se";

	public static String getBaseUrl(){
		return BASEURL;
	}
	public static String getHostPort(){
		return HOSTPORT;
	}
}
