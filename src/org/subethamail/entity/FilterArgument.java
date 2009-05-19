/*
 * $Id: FilterArgument.java 991 2009-01-15 02:46:02Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/entity/FilterArgument.java $
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.entity.i.Validator;

// In JBoss4, the copy of this in Config was sufficient to register
// the type.  In JBoss5, either the order of loading changed or the
// type must be registered in every class.
@org.hibernate.annotations.TypeDefs({
	@org.hibernate.annotations.TypeDef(
		name="anyImmutable",
		typeClass=org.subethamail.entity.type.AnyImmutableType.class
	)
})

/**
 * One parameter key and argument value for an enabled filter.
 *
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class FilterArgument implements Serializable, Comparable<FilterArgument>
{
	/** */
	@Transient private final static Logger log = LoggerFactory.getLogger(FilterArgument.class);

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
		this.value = value;
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
	@Override
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", name=" + this.name + "}";
	}

	/**
	 * Natural sort order is based on name
	 */
	public int compareTo(FilterArgument other)
	{
		return this.name.compareTo(other.getName());
	}
}

