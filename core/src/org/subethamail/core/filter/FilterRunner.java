/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.filter;

import java.util.Map;

import javax.ejb.Local;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.entity.MailingList;

/**
 * Interface for running filters on a mime message.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface FilterRunner
{
	/** */
	public static final String JNDI_NAME = "subetha/FilterRunner/local";

	/**
	 * Runs the message through all the filters associated with the list.
	 * 
	 * @see Filter#onInject(MimeMessage)
	 */
	public void onInject(MimeMessage msg, MailingList list) throws IgnoreException, HoldException, MessagingException;
	
	/**
	 * Runs the message through all the filters associated with the list.
	 * 
	 * @see Filter#onSendBeforeAttaching(MimeMessage)
	 */
	public void onSendBeforeAttaching(MimeMessage msg, MailingList list) throws IgnoreException;
	
	/**
	 * Runs the message through all the filters associated with the list.
	 * 
	 * @see Filter#onSendAfterAttaching(MimeMessage)
	 */
	public void onSendAfterAttaching(MimeMessage msg, MailingList list) throws IgnoreException;
	
	/**
	 * @return all the available filters.
	 */
	public Map<String, Filter> getFilters();
}

