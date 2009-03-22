/*
 * $Id: PlumberManagement.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/i/PlumberManagement.java $
 */

package org.subethamail.core.admin.i;

import org.jboss.ejb3.annotation.Management;


/**
 * Miscellaneous administrative tools requiring god role.
 * 
 * @author Jeff Schnitzer
 */
@Management
public interface PlumberManagement extends Plumber
{
}
