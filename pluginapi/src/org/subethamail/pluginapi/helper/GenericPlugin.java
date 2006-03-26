/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.pluginapi.helper;

import javax.mail.internet.MimeMessage;

import org.subethamail.pluginapi.BounceException;
import org.subethamail.pluginapi.IgnoreException;
import org.subethamail.pluginapi.Plugin;
import org.subethamail.pluginapi.PluginContext;

/**
 * Trivial plugin implementation that does nothing.  Override
 * the methods that you want to change.   
 * 
 * @author Jeff Schnitzer
 */
public class GenericPlugin implements Plugin
{
	/** */
	protected PluginContext ctx;
	
	/**
	 */
	public GenericPlugin(PluginContext ctx)
	{
		this.ctx = ctx;
	}
	
	/**
	 * @see Plugin#onInject(MimeMessage)
	 */
	public void onInject(MimeMessage msg) throws BounceException, IgnoreException
	{
	}
	
	/**
	 * @see Plugin#onSendBeforeAttaching(MimeMessage)
	 */
	public void onSendBeforeAttaching(MimeMessage msg) throws IgnoreException
	{
	}
	
	/**
	 * @see Plugin#onSendAfterAttaching(MimeMessage)
	 */
	public void onSendAfterAttaching(MimeMessage msg) throws IgnoreException
	{
	}
}
