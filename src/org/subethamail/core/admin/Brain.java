/*
 * $Id: PlumberBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/PlumberBean.java $
 */

package org.subethamail.core.admin;

import javax.enterprise.context.ApplicationScoped;

/**
 * This is really stupid.  Resin4 has a bug with @ApplicationScoped objects
 * that are also mapped as hessian services.  In that case, it seems to create
 * multiple instances of the object rather than a singleton.
 *
 * This is an *actual* singleton which holds the info we need.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@ApplicationScoped
public class Brain
{
	public String mailSmtpHost;
	public String mailSmtpPort;
}
