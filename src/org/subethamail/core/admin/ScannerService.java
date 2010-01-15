/*
 * $Id: PlumberBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/PlumberBean.java $
 */

package org.subethamail.core.admin;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.New;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple service that starts the scanner.
 * 
 * @author Scott Hernandez
 */
//@Startup
@Singleton
public class ScannerService
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(ScannerService.class);

	@New
	BeanScanner scanner;
	
	@PostConstruct
	public void postConstruct()
	{
		try 
		{
			scan();
		}
		catch (NullPointerException npe)
		{
			log.error("Error scanning for Filters and Blueprints!");
			log.error(npe.getStackTrace().toString());
			//continue as it will get run later if the scanning failed.
		}
		
	}
	
	/**
	 * Scans for classes and registers them {@link BeanScanner}
	 */
	public void scan()
	{
		this.scanner.registerScannedClasses();
	}
}