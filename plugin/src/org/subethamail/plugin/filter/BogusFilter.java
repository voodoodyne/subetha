/*
 * $Id: StripAttachmentsFilter.java 169 2006-04-24 08:01:03Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/plugin/src/org/subethamail/plugin/filter/StripAttachmentsFilter.java $
 */

package org.subethamail.plugin.filter;

import javax.annotation.security.RunAs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.plugin.i.FilterParameter;
import org.subethamail.core.plugin.i.helper.FilterParameterImpl;
import org.subethamail.core.plugin.i.helper.GenericFilter;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * Test filter that has complicated options.  
 * 
 * @author Jeff Schnitzer
 */
@Service
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class BogusFilter extends GenericFilter implements Lifecycle
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
{
	/** */
	private static Log log = LogFactory.getLog(BogusFilter.class);
	
	/** */
	public static enum ColorType
	{
		RED,
		GREEN,
		BLUE,
		PURPLE,
		CHARTRUSE
	}
	
	/** */
	static FilterParameter[] PARAM_DEFS = new FilterParameter[] {
		new FilterParameterImpl(
				"Length",
				"This is a Long parameter.",
				Long.class,
				100L
			),
		new FilterParameterImpl(
				"Color",
				"This is an Enum of type ColorType",
				ColorType.class,
				ColorType.CHARTRUSE
			),
		new FilterParameterImpl(
				"Agreeable",
				"This is of type boolean.",
				Boolean.class,
				true
			),
		new FilterParameterImpl(
				"Characteristic",
				"This is a single Character.",
				Character.class,
				'm'
			),
		new FilterParameterImpl(
				"Area",
				"Lots of text.",
				String.class,
				"foo\nbar",
				5
			),
		new FilterParameterImpl(
				"TextArea",
				"This is a text area.",
				String.class,
				"",
				10
			)
	};

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getName()
	 */
	public String getName()
	{
		return "Bogus Filter";
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getDescription()
	 */
	public String getDescription()
	{
		return "For testing various parameter UI elements.";
	}
	
	/**
	 * @see PluginFactory#getParameters()
	 */
	public FilterParameter[] getParameters()
	{
		return PARAM_DEFS;
	}
}
