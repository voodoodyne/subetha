/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.post.i;


/**
 * An enumeration of all the different "types" of mail that might
 * get sent out.  This also defines the various templates.
 * 
 * This class is part of the interface (in a .i. package) only for
 * unit testing and debugging.
 * 
 * When in debug mode (the log level for PostOfficeBean is set to
 * debug), every outbound message will include the name of this
 * enumeration at the start of the subject.  That way it can be
 * matched against by the unit tests.
 *
 * @author Jeff Schnitzer
 */
public enum MailType
{
	NEW_MAILING_LIST("org/subethamail/core/post/new_mailing_list.vm"),
	CONFIRM_SUBSCRIBE("org/subethamail/core/post/confirm_subscribe.vm"),
	SUBSCRIBED("org/subethamail/core/post/subscribed.vm"),
	FORGOT_PASSWORD("org/subethamail/core/post/forgot_password.vm"),
	CONFIRM_EMAIL("org/subethamail/core/post/confirm_email.vm"),
	MAIL_HELD("org/subethamail/core/post/mail_held.vm");

	/** */
	private final String template;
	
	/** */
	MailType(String template)
	{
		this.template = template;
	}
	
	/** */
	public String getTemplate()
	{
		return this.template;
	}
}
