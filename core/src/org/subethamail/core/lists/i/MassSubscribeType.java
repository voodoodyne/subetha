/*
 * $Id$
 * $URL$
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
