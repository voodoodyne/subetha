/*
 * $Id: PlumberBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/PlumberBean.java $
 */

package org.subethamail.core.admin;

import javax.annotation.PostConstruct;
import javax.inject.Current;

/**
 * Simple service that starts the scanner.
 * 
 * @author Scott Hernandez
 */
//@Service
public class ScannerService
{
	@Current 
	BeanScanner scanner;
	//ClassLoaderScanner scanner;
	
	@PostConstruct
	public void postConstruct()
	{
		scanner.scan();
	}
}