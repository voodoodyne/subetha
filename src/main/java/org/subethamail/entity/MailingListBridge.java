/*
 */

package org.subethamail.entity;

import org.hibernate.search.bridge.StringBridge;

/**
 * Lets us index the mailing list id in a Mail.  Really simple.
 * 
 * @author Jeff Schnitzer
 */
public class MailingListBridge implements StringBridge
{
	@Override
	public String objectToString(Object arg0)
	{
		MailingList list = (MailingList)arg0;
		return list.getId().toString();
	}
}

