/*
 * $Id: AdminRemove.java 1364 2010-07-21 04:34:49Z lhoriman $
 * $URL: https://subetha.googlecode.com/svn/trunk/src/org/subethamail/web/action/AdminRemove.java $
 */

package org.subethamail.web.action;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;

/**
 * Triggers a rebuild of the fulltext indexes (all of them)
 *
 * @author Jeff Schnitzer
 */
public class RebuildSearchIndexes extends AuthRequired
{
	/** */
	@Override
	public void authExecute() throws Exception
	{
		Backend.instance().getAdmin().rebuildSearchIndexes();
	}
}
