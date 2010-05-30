/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.filter;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Singleton;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.plugin.i.SendFilterContext;
import org.subethamail.core.plugin.i.helper.GenericFilter;

/**
 * This filter appends the 
 * <a href="http://www.faqs.org/rfcs/rfc2369.html">RFC2369</a> List-* 
 * headers to outgoing mail.
 * 
 * @author Jon Stevens
 * @author Scott Hernandez
 */
@Singleton
public class ListHeaderFilter extends GenericFilter
{	
	/** */
	private final static Logger log = LoggerFactory.getLogger(ListHeaderFilter.class);

	public enum ListHeader
	{
		LIST_ID ("List-Id"),
		LIST_HELP ("List-Help"),
		LIST_UNSUBSCRIBE ("List-Unsubscribe"),
		LIST_SUBSCRIBE ("List-Subscribe"),
		LIST_POST ("List-Post"),
		LIST_OWNER ("List-Owner"),
		LIST_ARCHIVE ("List-Archive");

		private String header;
		/** A set that contains all listHeaders */
		public static final Set<ListHeader> ALL;

		static
		{
			Set<ListHeader> tmp = new TreeSet<ListHeader>();

			for (ListHeader p: ListHeader.values())
				tmp.add(p);

			ALL = Collections.unmodifiableSet(tmp);
		}

		ListHeader(String header)
		{
			this.header = header;
		}
		
		public String getHeader()
		{
			return this.header;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getName()
	 */
	public String getName()
	{
		return "Add List Headers";
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getDescription()
	 */
	public String getDescription()
	{
		return "Appends the List-* headers as defined in RFC2369.";
	}

	/**
	 * @see Filter#onSend(SubEthaMessage, SendFilterContext)
	 */
	@Override
	public void onSend(SubEthaMessage msg, SendFilterContext ctx) throws IgnoreException, MessagingException
	{
		log.debug(this.getName() + " onSend()");

// 		Remove all RFC defined List-* headers then add our own...
//		The RFC defines some logic for this, but for now
//		let's just remove them unconditionally.
		for (ListHeader listHeader : ListHeader.ALL)
		{
			msg.removeHeader(listHeader.getHeader());
		}
		ListData listData = ctx.getList();

		try
		{
			msg.setHeader("List-Id", MimeUtility.encodeText(listData.getName()) + " <" + listData.getEmail() + ">");
		}
		catch (UnsupportedEncodingException e)
		{
		}
		msg.setHeader("List-Help", "<" + listData.getUrl() + ">");
		msg.setHeader("List-Unsubscribe", "<" + listData.getUrl() + ">");
		msg.setHeader("List-Subscribe", "<" + listData.getUrl() + ">");
		msg.setHeader("List-Post", "<mailto:" + listData.getEmail() + ">");
		msg.setHeader("List-Owner", "<mailto:" + listData.getOwnerEmail() + ">");
		msg.setHeader("List-Archive", "<" + listData.getUrlBase() + "archive.jsp?listId=" + listData.getId() + "> (Web Archive)");
	}
}