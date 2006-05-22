/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Jeff Schnitzer
 */
public class PersonInfoMixin extends BeanMixin
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(PersonInfoMixin.class);

	String email;
	String password;
	String name;
	InternetAddress address;
	
	/** */
	public PersonInfoMixin() throws Exception
	{
		String objectId = this.toString();
		// looks like:  com.similarity.rtest.PersonInfoMixin@bb0d0d
		objectId = objectId.substring(objectId.lastIndexOf('@') + 1);
		
		String baseName = Long.toString(System.currentTimeMillis(), 36); 
		String name =  baseName + "-" + objectId;
		
		this.password = "asdf" + objectId;	// only needs be unique for JVM session
		this.email = "subetha-" + name + "@localhost";
		
		this.name = "Test User";
		
		this.address = new InternetAddress(this.email, this.name);
	}
	
	/** */
	@Override
	public String getEmail() { return this.email; }
	
	@Override
	public String getPassword() { return this.password; }
	
	public String getName() { return this.name; }
	
	public InternetAddress getAddress() { return this.address; }
	
	/** Used to modify credentials */
	public void setPassword(String value) { this.password = value; }
}
