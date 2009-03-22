/*
 * $Id: MassSubscribeType.java 593 2006-06-05 01:50:20Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/MassSubscribeType.java $
 */

package org.subethamail.core.lists.i;

/**
 * Ways of mass subscribing people.
 * 
 * @author Jeff Schnitzer
 */
public enum MassSubscribeType
{
	INVITE,		// Send an invitation to subscribe which must be confirmed
	WELCOME,	// Subscribe and send a welcome message
	SILENT		// Subscribe without notification
}
