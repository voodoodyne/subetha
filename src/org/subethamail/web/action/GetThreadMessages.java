/*
 * $Id: GetMessage.java 474 2006-05-22 04:44:23Z skot $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/action/GetMessage.java $
 */

package org.subethamail.web.action;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.lists.i.MailData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Gets detailed information about the mails in this thread.  Model becomes a Map<Long, MailData>.
 * 
 * @author Scott Hernandez
 */
public class GetThreadMessages extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(GetThreadMessages.class);

	/** */
	@Getter @Setter Long msgId;

	/** */
	public void execute() throws Exception
	{
		MailData[] mails = Backend.instance().getArchiver().getThreadMessages(this.msgId);
		Map<Long, MailData> map = new HashMap<Long, MailData>(mails.length);
		for (int i = 0; i < mails.length; i++)
			map.put(mails[i].getId(), mails[i]);

		this.getCtx().setModel(map);
	}
}
