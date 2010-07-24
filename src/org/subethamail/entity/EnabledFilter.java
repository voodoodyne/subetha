/*
 * $Id: EnabledFilter.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/entity/EnabledFilter.java $
 */

package org.subethamail.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.entity.i.Validator;

/**
 * When a filter is added to a mailing list, one of these entities is
 * created.  It identifies the filter and stores all the filter arguments.
 *
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class EnabledFilter implements Serializable, Comparable<EnabledFilter>
{
	private static final long serialVersionUID = 1L;

	/** */
	@Transient private final static Logger log = LoggerFactory.getLogger(EnabledFilter.class);

	/** */
	@Id
	@GeneratedValue
	Long id;

	/** */
	@Column(nullable=false, length=Validator.MAX_FILTER_CLASSNAME)
	String className;

	/** TODO:  consider allowing null to mean global filter */
	@ManyToOne
	@JoinColumn(name="listId", nullable=false)
	MailingList list;

	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="filter")
	@MapKey(name="name")
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	Map<String, FilterArgument> arguments;

	/**
	 */
	public EnabledFilter() {}

	/**
	 */
	public EnabledFilter(MailingList list, String className)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new EnabledFilter");

		this.list = list;
		this.className = className;
		this.arguments = new HashMap<String, FilterArgument>();
	}

	/** */
	public Long getId()		{ return this.id; }

	/** */
	public String getClassName() { return this.className; }

	/** */
	public MailingList getList() { return this.list; }

	/** */
	public Map<String, FilterArgument> getArguments() { return this.arguments; }

	/** Convenience method */
	public void addArgument(FilterArgument arg)
	{
		this.arguments.put(arg.getName(), arg);
	}

	/**
	 * Builds a nice map of the key/values
	 */
	public Map<String, Object> getArgumentMap()
	{
		Map<String, Object> result = new HashMap<String, Object>(this.arguments.size() * 2);

		for (FilterArgument arg: this.arguments.values())
			result.put(arg.getName(), arg.getValue());

		return result;
	}

	/** */
	@Override
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", class=" + this.className + "}";
	}

	/**
	 * Natural sort order is based on id?
	 */
	public int compareTo(EnabledFilter other)
	{
		return this.id.compareTo(other.getId());
	}
}

