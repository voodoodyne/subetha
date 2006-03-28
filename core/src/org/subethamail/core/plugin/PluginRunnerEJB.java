/*
 * $Id: AccountMgrRemote.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgrRemote.java $
 */

package org.subethamail.core.plugin;

import javax.annotation.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.entity.EnabledPlugin;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.dao.DAO;
import org.subethamail.pluginapi.HoldException;
import org.subethamail.pluginapi.IgnoreException;
import org.subethamail.pluginapi.Plugin;
import org.subethamail.pluginapi.PluginContext;

/**
 * @author Jeff Schnitzer
 */
@Stateless(name="PluginRunner")
//@SecurityDomain("subetha")
public class PluginRunnerEJB implements PluginRunner
{
	/** */
	private static Log log = LogFactory.getLog(PluginRunnerEJB.class);

	/** */
	@EJB DAO dao;
	@EJB PluginRegistry registry;

	/**
	 * @see PluginRunner#onInject(MimeMessage, MailingList)
	 */
	public void onInject(MimeMessage msg, MailingList list) throws IgnoreException, HoldException, MessagingException
	{
		// TODO:  factor in global plugins
		
		HoldException holdException = null;
		
		for (EnabledPlugin enPlugin: list.getEnabledPlugins())
		{
			Plugin plugin = this.registry.getPlugin(enPlugin.getClassName());
			if (plugin == null)
			{
				// Log and ignore
				this.logUnregisteredPluginError(enPlugin, list);
			}
			else
			{
				PluginContext ctx = new PluginContextImpl(enPlugin, fact);
				
				try
				{
					plugin.onInject(msg, ctx);
				}
				catch (HoldException ex)
				{
					// We only track the first one
					if (holdException == null)
						holdException = ex;
				}
			}
		}
		
		if (holdException != null)
			throw holdException;
	}

	/**
	 * @see PluginRunner#onSendBeforeAttaching(MimeMessage)
	 */
	public void onSendBeforeAttaching(MimeMessage msg, MailingList list) throws IgnoreException
	{
		//TODO
	}

	/**
	 * @see PluginRunner#onSendAfterAttaching(MimeMessage)
	 */
	public void onSendAfterAttaching(MimeMessage msg, MailingList list) throws IgnoreException
	{
		//TODO
	}
	
	/**
	 * Puts a nasty note in the logs when we find a plugin which has been
	 * enabled on a list but is not (or no longer) registered.  It's not
	 * a fatal error; we can just continue and ignore the plugin.
	 */
	protected void logUnregisteredPluginError(EnabledPlugin enPlugin, MailingList list)
	{
		if (log.isErrorEnabled())
			log.error("Unregistered plugin '" + enPlugin.getClassName() + 
				"' is enabled on list '" + list.getAddress() + "'");
	}
}
