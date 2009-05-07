/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.entity.i.Permission;


/**
 * A class and static method that will wrap a Set<Permission> in a
 * Map<String, Boolean>.  This way permissions can be checked
 * in the JSP as "${perms.PERMISSION_NAME}".
 * 
 * This implements only the methods that are likely to be called
 * from JSP expressions.  The static wrap() method is suitable
 * for calling as a JSP function.
 *  
 * @author Jeff Schnitzer
 */
public class PermissionWrapper implements Map<String, Boolean>
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(PermissionWrapper.class);
	
	/** */
	Set<Permission> perms;
	
	/** */
	public PermissionWrapper(Set<Permission> perms)
	{
		this.perms = perms;
	}

	/**
	 * Creates a new wrapper with the useful interface.
	 */
	public static Map<String, Boolean> wrapPerms(Set<Permission> perms)
	{
		return new PermissionWrapper(perms);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key)
	{
		Permission p = Permission.valueOf(key.toString());
		return this.perms.contains(p);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value)
	{
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<String, Boolean>> entrySet()
	{
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Boolean get(Object key)
	{
		Permission p = Permission.valueOf(key.toString());
		if (this.perms.contains(p))
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty()
	{
		return this.perms.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set<String> keySet()
	{
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(K, V)
	 */
	public Boolean put(String key, Boolean value)
	{
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends String, ? extends Boolean> t)
	{
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Boolean remove(Object key)
	{
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size()
	{
		return this.perms.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection<Boolean> values()
	{
		throw new UnsupportedOperationException();
	}
	
}
