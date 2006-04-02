/*
 * $Id: PersonInfoMixin.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/util/PersonInfoMixin.java $
 */

package org.subethamail.rtest.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Jeff Schnitzer
 */
public class MailingListInfoMixin
{
	/** */
	private static Log log = LogFactory.getLog(MailingListInfoMixin.class);

	String address;
	String url;
	
	/** */
	public MailingListInfoMixin() throws Exception
	{
		String objectId = this.toString();
		// looks like:  com.similarity.rtest.PersonInfoMixin@bb0d0d
		objectId = objectId.substring(objectId.lastIndexOf('@') + 1);
		
		String baseName = Long.toString(System.currentTimeMillis(), 36); 
		String name =  baseName + objectId;
		
		this.address = name + "@localhost";
		this.url = "http://localhost:8080/list/" + name;
	}
	
	/** */
	public String getAddress() { return this.address; }
	public String getUrl() { return this.url; }
}
