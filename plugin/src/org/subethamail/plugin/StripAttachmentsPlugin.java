/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.plugin;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.subethamail.pluginapi.HoldException;
import org.subethamail.pluginapi.IgnoreException;
import org.subethamail.pluginapi.ParameterDef;
import org.subethamail.pluginapi.Plugin;
import org.subethamail.pluginapi.PluginContext;
import org.subethamail.pluginapi.helper.AbstractPluginManagement;
import org.subethamail.pluginapi.helper.GenericPlugin;
import org.subethamail.pluginapi.helper.ParameterDefImpl;

/**
 * This plugin removes all attachments greater than a certain size
 * immediately upon message injection.  The attachments are never
 * stored.  The attachment can optionally be replaced with a message
 * indicating what action was taken.  
 * 
 * @author Jeff Schnitzer
 */
@Service(objectName="subetha:service=StripAttachmentsPlugin")
// TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
public class StripAttachmentsPlugin extends GenericPlugin implements AbstractPluginManagement
{
	/** */
	private static Log log = LogFactory.getLog(StripAttachmentsPlugin.class);
	
	/** */
	static ParameterDef[] PARAM_DEFS = new ParameterDef[] {
		new ParameterDefImpl(
				"Threshold in K",
				"Strip all attachments larger than this size, in kilobytes.  A value of 0 will strip all attachments.",
				Long.class,
				100
			)
	};

	/**
	 * @see PluginFactory#getParameterDefs()
	 */
	public ParameterDef[] getParameterDefs()
	{
		return PARAM_DEFS;
	}

	/**
	 * @see Plugin#onInject(MimeMessage, PluginContext)
	 */
	@Override
	public void onInject(MimeMessage msg, PluginContext ctx) throws IgnoreException, HoldException, MessagingException
	{
		// TODO:  implement this.
		log.debug("onInject()");
	}
}
