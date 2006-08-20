/*
 * $Id$
 * $URL$
 */

package org.subethamail.entity.type;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * <p>Custom hibernate type that will store any java type that follows
 * the following rules:</p>
 * 
 * <ul>
 *   <li>The type must be immutable.</li>
 *   <li>The type must implement a toString() method.</li>
 *   <li>The type must have one of (in order of preference):
 *     <ol>
 *       <li>A public static valueOf(String) method that will
 *           convert the String back into the object type.</li>
 *       <li>A constructor which takes a String.</li>
 *     </ol>
 *   <li>The object must be serializable.</li>
 * </ul>
 * 
 * <p>The type is stored in two String columns, the first
 * stores the classname and the second stores the toString() value.</p>
 * 
 * <p>Noteably, this can be used to store any java primitive type 
 * or Enum.  Technically java.lang.Character is missing the valueOf(String)
 * method but we handle that case specially.</p>
 * 
 * <p>When this type is cached in the 2nd-level cache, the materialized
 * object is stored, eliminating the need for String conversion.</p>
 * 
 * <p>The method comments are copied from UserType.</p>
 * 
 * @author Jeff Schnitzer
 */
public class AnyImmutableType implements UserType
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(AnyImmutableType.class);

	private static final int[] SQL_TYPES = new int[] {
		Hibernate.STRING.sqlType(),
		Hibernate.STRING.sqlType()
	};

	/**
	 * Return the SQL type codes for the columns mapped by this type. The
	 * codes are defined on <tt>java.sql.Types</tt>.
	 * @see java.sql.Types
	 * @return int[] the typecodes
	 */
	public int[] sqlTypes()
	{
		return SQL_TYPES;
	}

	/**
	 * The class returned by <tt>nullSafeGet()</tt>.
	 *
	 * @return Class
	 */
	public Class returnedClass()
	{
		return Object.class;
	}

	/**
	 * Compare two instances of the class mapped by this type for persistence "equality".
	 * Equality of the persistent state.
	 *
	 * @param x
	 * @param y
	 * @return boolean
	 */
	public boolean equals(Object x, Object y) throws HibernateException
	{
		if (x == y)
		{
			return true;
		}
		else if (x != null && y != null)
		{
			if (x instanceof InternetAddress && y instanceof InternetAddress)
			{
				// InternetAddress is stupid and ignores the personal
				// part of the address in the equals() method.
				InternetAddress iax = (InternetAddress)x;
				InternetAddress iay = (InternetAddress)y;
				return equals(iax.getAddress(), iay.getAddress()) && equals(iax.getPersonal(), iay.getPersonal());
			}
			else
				return x.equals(y);
		}
		else
			return false;
	}

	/**
	 * Get a hashcode for the instance, consistent with persistence "equality"
	 */
	public int hashCode(Object x) throws HibernateException
	{
		return x.hashCode();
	}

	/**
	 * Retrieve an instance of the mapped class from a JDBC resultset. Implementors
	 * should handle possibility of null values.
	 *
	 * @param rs a JDBC result set
	 * @param names the column names
	 * @param owner the containing entity
	 * @return Object
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException
	{
		String type = (String)Hibernate.STRING.nullSafeGet(rs, names[0]);
		if (type == null)
			return null;
		
		String value = (String)Hibernate.STRING.nullSafeGet(rs, names[1]);
		if (value == null)
			return null;
		
		try
		{
			Class clazz = Class.forName(type);
			
			if (clazz.equals(String.class))
			{
				return value;
			}
			else if (clazz.equals(Character.class))
			{
				return value.charAt(0);
			}
			else
			{
				try
				{
					Method m = clazz.getMethod("valueOf", String.class);
					return m.invoke(null, value);
				}
				catch (NoSuchMethodException ex)
				{
					Constructor c = clazz.getConstructor(String.class);
					return c.newInstance(value);
				}
			}
		}
		catch (ClassNotFoundException ex)
		{
			throw new HibernateException("Unable to find class " + type, ex); 
		}
		catch (NoSuchMethodException ex)
		{
			throw new HibernateException("Class " + type + " does not have a valueOf(String) method or a constructor(String)", ex); 
		}
		catch (InvocationTargetException ex)
		{
			throw new HibernateException(type + ".valueOf(\"" + value + "\") threw an exception:  " + ex.getCause(), ex); 
		}
		catch (InstantiationException ex)
		{
			throw new HibernateException(type + "(\"" + value + "\") threw an exception:  " + ex.getCause(), ex); 
		}
		catch (IllegalAccessException ex)
		{
			throw new HibernateException("Unable to invoke " + type + ".valueOf(String)", ex);
		}
	}

	/**
	 * Write an instance of the mapped class to a prepared statement. Implementors
	 * should handle possibility of null values. A multi-column type should be written
	 * to parameters starting from <tt>index</tt>.
	 *
	 * @param st a JDBC prepared statement
	 * @param value the object to write
	 * @param index statement parameter index
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException
	{
		if (value == null)
		{
			st.setNull(index, Hibernate.STRING.sqlType());
			st.setNull(index+1, Hibernate.STRING.sqlType());
		}
		else
		{
			st.setString(index, value.getClass().getName());
			st.setString(index+1, value.toString());
		}
	}

	/**
	 * Return a deep copy of the persistent state, stopping at entities and at
	 * collections. It is not necessary to copy immutable objects, or null
	 * values, in which case it is safe to simply return the argument.
	 *
	 * @param value the object to be cloned, which may be null
	 * @return Object a copy
	 */
	public Object deepCopy(Object value) throws HibernateException
	{
		// Object is immutable
		return value;
	}

	/**
	 * Are objects of this type mutable?
	 *
	 * @return boolean
	 */
	public boolean isMutable()
	{
		return false;
	}

	/**
	 * Transform the object into its cacheable representation. At the very least this
	 * method should perform a deep copy if the type is mutable. That may not be enough
	 * for some implementations, however; for example, associations must be cached as
	 * identifier values. (optional operation)
	 *
	 * @param value the object to be cached
	 * @return a cachable representation of the object
	 * @throws HibernateException
	 */
	public Serializable disassemble(Object value) throws HibernateException
	{
		return (Serializable)value;
	}

	/**
	 * Reconstruct an object from the cacheable representation. At the very least this
	 * method should perform a deep copy if the type is mutable. (optional operation)
	 *
	 * @param cached the object to be cached
	 * @param owner the owner of the cached object
	 * @return a reconstructed object from the cachable representation
	 * @throws HibernateException
	 */
	public Object assemble(Serializable cached, Object owner) throws HibernateException
	{
		return cached;
	}

	/**
	 * During merge, replace the existing (target) value in the entity we are merging to
	 * with a new (original) value from the detached entity we are merging. For immutable
	 * objects, or null values, it is safe to simply return the first parameter. For
	 * mutable objects, it is safe to return a copy of the first parameter. For objects
	 * with component values, it might make sense to recursively replace component values.
	 *
	 * @param original the value from the detached entity being merged
	 * @param target the value in the managed entity
	 * @return the value to be merged
	 */
	public Object replace(Object original, Object target, Object owner) throws HibernateException
	{
		return original;
	}

}