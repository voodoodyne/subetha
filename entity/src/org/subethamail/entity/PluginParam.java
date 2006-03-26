/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.subethamail.common.valid.Validator;

/**
 * One parameter key and value for an enabled plugin.  This is actually
 * a base class for an inheritance hierarchy, one for every type of
 * value we allow.
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
abstract public class PluginParam implements Serializable, Comparable
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

	/**
	 */
	public PluginParam() {}
	
	/**
	 */
	public PluginParam(String name)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new PluginParam");
		
		this.name = name;
	}
	
	/** */
	public Long getId()		{ return this.id; }

	/**
	 */
	public String getName() { return this.name; }
	
	/** */
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", name=" + this.name + "}";
	}
	
	/**
	 * @return the value object in it's native type 
	 */
	abstract public Object getValue();

	/**
	 * Natural sort order is based on name
	 */
	public int compareTo(Object arg0)
	{
		PluginParam other = (PluginParam)arg0;

		return this.name.compareTo(other.getName());
	}
}

