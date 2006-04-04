/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.Permission;
import org.subethamail.core.admin.i.BlueprintData;
import org.subethamail.core.lists.i.MailingListData;
import org.subethamail.core.lists.i.MySubscription;
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Role;
import org.subethamail.entity.Subscription;



/**
 * Offers static utility methods to convert between internal entity
 * objects and public interface value objects. 
 * 
 * @author Jeff Schnitzer
 */
public class Transmute
{
	/** */
	private static Log log = LogFactory.getLog(Transmute.class);
	
	/** */
	public static List<BlueprintData> blueprints(Collection<Blueprint> rawColl)
	{
		List<BlueprintData> result = new ArrayList<BlueprintData>(rawColl.size());
		
		for (Blueprint raw: rawColl)
			result.add(blueprint(raw));
		
		return result;
	}
	
	/** */
	public static BlueprintData blueprint(Blueprint raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
			
		return new BlueprintData(
				raw.getClass().getName(),
				raw.getName(),
				raw.getDescription());
	}
	
	/** */
	public static List<MailingListData> mailingLists(Collection<MailingList> rawColl)
	{
		List<MailingListData> result = new ArrayList<MailingListData>(rawColl.size());
		
		for (MailingList raw: rawColl)
			result.add(mailingList(raw));
		
		return result;
	}
	
	/** */
	public static MailingListData mailingList(MailingList raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
	
		return new MailingListData(
				raw.getId(),
				raw.getEmail(),
				raw.getName(),
				raw.getUrl(),
				raw.getDescription());
	}
	
	/** */
	/**
	 * @param rawPerson can be null
	 * @return the status of the person for the given list. 
	 */
	public static MySubscription mySubscription(Person rawPerson, MailingList rawList)
	{
		MailingListData listData = mailingList(rawList);
		
		Role role = rawList.getRoleFor(rawPerson);
		
		Set<Permission> perms = role.getPermissions();
		
		// If we're the site admin, override with all.
		if (rawPerson != null && rawPerson.isSiteAdmin())
			perms = Permission.ALL;
		
		Subscription rawSub = (rawPerson == null) ? null : rawPerson.getSubscription(rawList.getId());
		
		return new MySubscription(
				listData,
				(rawSub == null) ? null : rawSub.getDeliverTo().getId(),
				(rawSub != null),
				role.isOwner(),
				role.getName(),
				perms);
	}
}
