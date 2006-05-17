/*
 * $Id: GetMessage.java 263 2006-05-04 20:58:25Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/action/GetMessage.java $
 */

package org.subethamail.web.action;

import org.hibernate.validator.Email;
import org.subethamail.web.Backend;
import org.tagonist.propertize.Property;

/**
 * Resends the message
 * 
 * @author Scott Hernandez
 */
public class ResendMessage extends GetMessage 
{

	@Property
	@Email
	String email;
	
	/** */
	public void execute() throws Exception
	{
		Backend.instance().getArchiver().sendTo(msgId, email);
		super.execute();
	}
}
