/*
 * $Id: PostOffice.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/post/PostOffice.java $
 */

package org.subethamail.core.post.i;


/**
 * Just a few random constants.
 *
 * @author Jeff Schnitzer
 */
public class Constant
{
	/**
	 * Constant which defines the start of a token string in an email,
	 * but only when debug is enabled.  This makes the token automatically
	 * recognizable by the unit test harness.
	 */
	public static final String DEBUG_TOKEN_BEGIN = "---BEGINTOKEN---";
	public static final String DEBUG_TOKEN_END = "---ENDTOKEN---";
}
