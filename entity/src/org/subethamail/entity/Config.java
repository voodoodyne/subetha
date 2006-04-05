/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.subethamail.common.valid.Validator;

/**
 * Contains a sitewide config parameter, key and value.
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SuppressWarnings("serial")
public class Config implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(Config.class);
	
	/** */
	@Id
	@Column(length=Validator.MAX_CONFIG_ID)
	String id;
	
	/** */
	@Type(type="anyImmutable")
	@Columns(columns={
		@Column(name="type"),
		@Column(name="value", length=Validator.MAX_CONFIG_VALUE)
	})
	Object value;
	
	/**
	 */
	public Config() {}
	
	/**
	 */
	public Config(String id, Object value)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new Config");
		
		this.id = id;
		this.value = value;
	}
	
	/** */
	public String getId()		{ return this.id; }

	/**
	 * @return the value object in its native type 
	 */
	public Object getValue() { return this.value; }

	public void setValue(Object val)
	{
		this.value = val;
	}
	
	/** */
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + "}";
	}
	
	/**
	 * Natural sort order is based on id
	 */
	public int compareTo(Object arg0)
	{
		Config other = (Config)arg0;

		return this.id.compareTo(other.getId());
	}
}

