/*
 * $Id: Login.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Login.java $
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Gets data about a mailing list.  Model becomes a MailingListData.
 * 
 * @author Jeff Schnitzer
 */
public class GetList extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(GetList.class);
	
	/** */
	Long id;
	public Long getId() { return this.id; }
	public void setId(Long value) { this.id = value; }
		
	/** */
	public void execute() throws Exception
	{
		this.getCtx().setModel(Backend.instance().getListMgr().getMailingList(this.id));
	}
}
