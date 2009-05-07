/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import java.net.URL;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.Utils;

/**
 * @author Jeff Schnitzer
 */
public class MailingListInfoMixin
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(MailingListInfoMixin.class);

	String email;
	String name;
	InternetAddress address;
	URL url;
	String description;
	String welcomeMessage;
	
	/** */
	public MailingListInfoMixin() throws Exception
	{
		String baseEmail = Utils.uniqueString();
		
		this.name = "List Name " + baseEmail;
		this.email = baseEmail + "@localhost";
		this.url = new URL(WebApp.BASEURL + "/list/" + baseEmail);
		this.address = new InternetAddress(this.email, this.name);
		this.description = "Test list description";
		this.welcomeMessage = "Test list welcome message";
	}
	
	/** */
	public String getEmail() { return this.email; }
	public URL getUrl() { return this.url; }
	public InternetAddress getAddress() { return this.address; }
	public String getDescription() { return this.description; }

	public String getWelcomeMessage()
	{
		return this.welcomeMessage;
	}
}
