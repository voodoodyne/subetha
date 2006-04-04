/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.Serializable;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.subethamail.common.valid.Validator;

/**
 * When a filter is added to a mailing list, one of these entities is
 * created.  It identifies the filter and stores all the filter arguments.
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EnabledFilter implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(EnabledFilter.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_FILTER_CLASSNAME)
	String className;
	
	/** If this is null, the plugin is enabled globally */
	@ManyToOne
	@JoinColumn(name="listId", nullable=true)
	MailingList list;
	
	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="filter")
	@MapKey(name="name")
	Map<String, FilterArgument> arguments;
	
	/**
	 */
	public EnabledFilter() {}
	
	/**
	 */
	public EnabledFilter(String className)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new EnabledFilter");
		
		this.className = className;
	}
	
	/** */
	public Long getId()		{ return this.id; }

	/** */
	public String getClassName() { return this.className; }
	
	/** */
	public MailingList getList() { return this.list; }
	
	/** */
	public Map<String, FilterArgument> getArguments() { return this.arguments; }
	
	/** */
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", class=" + this.className + "}";
	}

	/**
	 * Natural sort order is based on id?
	 */
	public int compareTo(Object arg0)
	{
		EnabledFilter other = (EnabledFilter)arg0;

		return this.id.compareTo(other.getId());
	}
}

