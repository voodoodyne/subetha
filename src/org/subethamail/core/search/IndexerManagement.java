/*
 * $Id: IndexerManagement.java 988 2008-12-30 08:51:13Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.search;

import org.subethamail.core.lists.ArchiverBean;


/**
 * Management interface for index management {@link ArchiverBean}.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */

public interface IndexerManagement
{

	/**
	 * Rebuilds the entire index from scratch.  Searches will still
	 * be serviced from the (old) index while the new index is being
	 * built.  VERY EXPENSIVE.
	 */
	public void rebuild();
	
	/**
	 * Updates the index with any changes in since the last update.
	 */
	public void update();
}