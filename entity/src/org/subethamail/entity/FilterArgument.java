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
 * One parameter key and argument value for an enabled filter.
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FilterArgument implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(FilterArgument.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	/** */
	@ManyToOne
	@JoinColumn(name="filterId", nullable=false)
	EnabledFilter filter;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_FILTER_ARGUMENT_NAME)
	String name;

	/** */
	@Type(type="anyImmutable")
	@Columns(columns={
		@Column(name="type"),
		@Column(name="value", length=Validator.MAX_FILTER_ARGUMENT_VALUE)
	})
	Object value;
	
	/**
	 */
	public FilterArgument() {}
	
	/**
	 */
	public FilterArgument(EnabledFilter filter, String name, Object value)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new FilterArgument");
		
		this.filter = filter;
		this.name = name;
	}
	
	/** */
	public Long getId()		{ return this.id; }

	/** */
	public EnabledFilter getFilter()		{ return this.filter; }

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
		FilterArgument other = (FilterArgument)arg0;

		return this.name.compareTo(other.getName());
	}
}

