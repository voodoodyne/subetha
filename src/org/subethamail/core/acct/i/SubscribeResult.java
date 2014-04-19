package org.subethamail.core.acct.i;


/**
 * The possible results of a subscribe request.
 * 
 * @author Jeff Schnitzer
 */
public enum SubscribeResult
{
	OK,				// You're subscribed.
	TOKEN_SENT,		// A token was sent to the email address.  You aren't subscribed.
	HELD			// Your request is being held for approval.  You aren't subscribed.
}
