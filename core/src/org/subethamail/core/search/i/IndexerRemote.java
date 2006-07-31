/*
 * $Id$
 * $URL$
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

