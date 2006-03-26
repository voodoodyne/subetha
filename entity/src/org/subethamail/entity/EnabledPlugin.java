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
 * When a plugin is added to a mailing list, one of these entities is
 * created.  It identifies the plugin and stores all the plugin parameters.
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EnabledPlugin implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(EnabledPlugin.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_PLUGIN_CLASSNAME)
	String className;
	
	/** If this is null, the plugin is enabled globally */
	@ManyToOne
	@JoinColumn(name="listId", nullable=true)
	MailingList mailingList;
	
	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="plugin")
	@MapKey(name="name")
	Map<String, PluginParam> params;
	
	/**
	 */
	public EnabledPlugin() {}
	
	/**
	 */
	public EnabledPlugin(String className)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new EnabledPlugin");
		
		this.className = className;
	}
	
	/** */
	public Long getId()		{ return this.id; }

	/** */
	public String getClassName() { return this.className; }
	
	/** */
	public MailingList getMailingList() { return this.mailingList; }
	
	/** @return a Map of param name to PluginParam-derived object */
	public Map<String, PluginParam> getParams() { return this.params; }
	
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
		EnabledPlugin other = (EnabledPlugin)arg0;

		return this.id.compareTo(other.getId());
	}
}

