/*
 * $Id: IndexerRemote.java 704 2006-07-31 00:04:03Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/search/i/IndexerRemote.java $
 */

package org.subethamail.core.search.i;

import javax.ejb.Remote;

/**
 * @see Indexer
 *
 * @author Jeff Schnitzer
 */
@Remote
public interface IndexerRemote extends Indexer
{
	/** */
	public static final String JNDI_NAME = "subetha/IndexerBean/remote";
}

