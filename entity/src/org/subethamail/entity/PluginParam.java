/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.subethamail.common.valid.Validator;

/**
 * One parameter key and value for an enabled plugin.  This is actually
 * a base class for an inheritance hierarchy, one for every type of
 * value we allow.
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PluginParam implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(PluginParam.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	/** */
	@ManyToOne
	@JoinColumn(name="pluginId", nullable=false)
	EnabledPlugin plugin;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_PLUGIN_PARAM_NAME)
	String name;

	/** */
	@Type(type="anyImmutable")
	@Columns(columns={
		@Column(name="type"),
		@Column(name="value", length=Validator.MAX_PLUGIN_PARAM_VALUE)
	})
	Object value;
	
	/**
	 */
	public PluginParam() {}
	
	/**
	 */
	public PluginParam(EnabledPlugin plugin, String name, Object value)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new PluginParam");
		
		this.plugin = plugin;
		this.name = name;
	}
	
	/** */
	public Long getId()		{ return this.id; }

	/** */
	public EnabledPlugin getPlugin()		{ return this.plugin; }

	/**
	 */
	public String getName() { return this.name; }
	
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
		return this.getClass() + " {id=" + this.id + ", name=" + this.name + "}";
	}
	
	/**
	 * Natural sort order is based on name
	 */
	public int compareTo(Object arg0)
	{
		PluginParam other = (PluginParam)arg0;

		return this.name.compareTo(other.getName());
	}
}

