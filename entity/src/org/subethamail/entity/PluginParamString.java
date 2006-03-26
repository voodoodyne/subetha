/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A plugin parameter that has a java.lang.String value
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("java.lang.String")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PluginParamString extends PluginParam
{
	/** */
	@Transient private static Log log = LogFactory.getLog(PluginParamString.class);

	/** */
	@Column(name="stringValue")
	String value;
	
	/**
	 */
	public PluginParamString() {}
	
	/**
	 */
	public PluginParamString(String name, String value)
	{
		this.value = value;
	}
	
	/**
	 */
	public String getStringValue()
	{
		return this.value;
	}

	/**
	 * @see PluginParam#getValue() 
	 */
	@Override
	public Object getValue()
	{
		return this.value;
	}

}

