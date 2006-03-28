/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.pluginapi.helper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.subethamail.pluginapi.HoldException;
import org.subethamail.pluginapi.IgnoreException;
import org.subethamail.pluginapi.ParameterDef;
import org.subethamail.pluginapi.Plugin;
import org.subethamail.pluginapi.PluginContext;

/**
 * Trivial plugin implementation that has no parameters and does
 * nothing.  Override the methods that you want to change.   
 * 
 * @author Jeff Schnitzer
 */
public class GenericPlugin extends AbstractPlugin
{
	/**
	 * @see Plugin#getParameterDefs()
	 */
	public ParameterDef[] getParameterDefs()
	{
		final ParameterDef[] defs = new ParameterDef[0];
		return defs;
	}
	
	/**
	 * @see Plugin#onInject(MimeMessage, PluginContext)
	 */
	public void onInject(MimeMessage msg, PluginContext ctx) throws IgnoreException, HoldException, MessagingException
	{
	}
	
	/**
	 * @see Plugin#onSendBeforeAttaching(MimeMessage, PluginContext)
	 */
	public void onSendBeforeAttaching(MimeMessage msg, PluginContext ctx) throws IgnoreException
	{
	}
	
	/**
	 * @see Plugin#onSendAfterAttaching(MimeMessage, PluginContext)
	 */
	public void onSendAfterAttaching(MimeMessage msg, PluginContext ctx) throws IgnoreException
	{
	}
}
