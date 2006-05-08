/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.filter;

import javax.annotation.security.RunAs;
import javax.mail.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.FilterParameter;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.plugin.i.helper.FilterParameterImpl;
import org.subethamail.core.plugin.i.helper.GenericFilter;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * This filter appends a customizable Subject to outgoing emails.
 * 
 * @author Jon Stevens
 */
@Service
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class SubjectFilter extends GenericFilter implements Lifecycle
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
{
	/** */
	private static Log log = LogFactory.getLog(SubjectFilter.class);

	public static final String ARG_SUBJECTPREFIX = "Subject";
	
	/** */
	static FilterParameter[] PARAM_DEFS = new FilterParameter[] {
		new FilterParameterImpl(
				ARG_SUBJECTPREFIX,
				"The prefix text which is appended to the beginning of the Subject of each message sent to the list.",
				"[${list.name}] ",
				1,
				true,
				null
			)
	};

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getName()
	 */
	public String getName()
	{
		return "Subject";
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getDescription()
	 */
	public String getDescription()
	{
		return "Appends text to the beginning of the Subject header on incoming messages.";
	}
	
	/**
	 * @see PluginFactory#getParameters()
	 */
	public FilterParameter[] getParameters()
	{
		return PARAM_DEFS;
	}

	/**
	 * @see Filter#onSend(SubEthaMessage msg, FilterContext ctx)
	 */
	@Override
	public void onInject(SubEthaMessage msg, FilterContext ctx) 
		throws IgnoreException, HoldException, MessagingException
	{
		log.debug("Subject Filter: onInject()");
		
		// get the parameter arguments
		String subjectArg = (String) ctx.getArgument(ARG_SUBJECTPREFIX);

		// get the subject for the message
		String subjectMsg = msg.getSubject();

		// do the expansion on the subjectArg
		String expandedSubjectArg = ctx.expand(subjectArg);

		// find any existing expandedSubjectArg's in the subjectMsg and remove them
		subjectMsg = subjectMsg.replace(expandedSubjectArg, "");

		// append the prefix
		subjectMsg = expandedSubjectArg + subjectMsg;
		
		// set the subject on the message
		msg.setSubject(subjectMsg);
	}
}
