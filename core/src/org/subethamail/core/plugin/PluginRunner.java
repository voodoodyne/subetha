/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.plugin;

import javax.ejb.Local;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.subethamail.entity.MailingList;
import org.subethamail.pluginapi.HoldException;
import org.subethamail.pluginapi.IgnoreException;
import org.subethamail.pluginapi.Plugin;

/**
 * Interface for running plugins on a mime message.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface PluginRunner
{
	/** */
	public static final String JNDI_NAME = "PluginRunner/local";

	/**
	 * Runs the message through all the plugins associated with
	 * the list.
	 * 
	 * @see Plugin#onInject(MimeMessage)
	 */
	public void onInject(MimeMessage msg, MailingList list) throws IgnoreException, HoldException, MessagingException;
	
	/**
	 * Runs the message through all the plugins associated with
	 * the list.
	 * 
	 * @see Plugin#onSendBeforeAttaching(MimeMessage)
	 */
	public void onSendBeforeAttaching(MimeMessage msg, MailingList list) throws IgnoreException;
	
	/**
	 * Runs the message through all the plugins associated with
	 * the list.
	 * 
	 * @see Plugin#onSendAfterAttaching(MimeMessage)
	 */
	public void onSendAfterAttaching(MimeMessage msg, MailingList list) throws IgnoreException;
}

