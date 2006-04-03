/*
 * $Id: PersonInfoMixin.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/util/PersonInfoMixin.java $
 */

package org.subethamail.rtest.util;

import java.net.URL;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Jeff Schnitzer
 */
public class MailingListInfoMixin
{
	/** */
	private static Log log = LogFactory.getLog(MailingListInfoMixin.class);

	String email;
	String name;
	InternetAddress address;
	URL url;
	String description;
	
	/** */
	public MailingListInfoMixin() throws Exception
	{
		String objectId = this.toString();
		// looks like:  com.similarity.rtest.PersonInfoMixin@bb0d0d
		objectId = objectId.substring(objectId.lastIndexOf('@') + 1);
		
		String baseEmail = Long.toString(System.currentTimeMillis(), 36) + objectId;
		
		this.name = "List Name " + objectId;	// deliberately not very unique
		this.email = baseEmail + "@localhost";
		this.url = new URL("http://localhost:8080/se/list/" + baseEmail);
		this.address = new InternetAddress(this.email, this.name);
		this.description = "Test list description";
	}
	
	/** */
	public String getEmail() { return this.email; }
	public URL getUrl() { return this.url; }
	public InternetAddress getAddress() { return this.address; }
	public String getDescription() { return this.description; }
}
