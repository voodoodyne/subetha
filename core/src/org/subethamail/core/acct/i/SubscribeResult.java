/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

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
