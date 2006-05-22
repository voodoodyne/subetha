/*
 * $Id$
 * $URL$
 */

package org.subethamail.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

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
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class Config implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(Config.class);
	
	/** Known sitewide config keys */	
	public static enum ConfigKey
	{
		ID_SITE_POSTMASTER("sitePostmaster", "The postmaster email address for the site.", String.class.getName()),
		ID_SITE_URL("siteUrl", "The global url for the site. (http://host:port/se/)", String.class.getName());

		/** A set that contains all permissions */
		public static final Set<ConfigKey> ALL;
		static
		{
			Set<ConfigKey> tmp = new TreeSet<ConfigKey>();

			for (ConfigKey p: ConfigKey.values())
				tmp.add(p);

			ALL = Collections.unmodifiableSet(tmp);
		}

		private String key;
		private String description;
		private String type;
		private Class typeCache = null;
		
		private ConfigKey(String key, String description, String type)
		{
			this.key = key;
			this.description = description;
			this.type = type;
		}
		
		public Class getType() throws ClassNotFoundException
		{
			if (this.typeCache == null)
			{
				this.typeCache = Class.forName(this.type);
			}
			return this.typeCache;
		}

		public String getKey()
		{
			return this.key;
		}

		public String getDescription()
		{
			return this.description;
		}

		public static ConfigKey getConfigKey(String key)
		{
			ConfigKey result = null;
			for(ConfigKey aKey: ALL)
			{
				if (aKey.getKey().equals(key))
				{
					result = aKey;
					continue;
				}
			}
			return result;
		}

		public String getDescription(String key)
		{
			return this.description;
		}
	}

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
			log.debug("Creating new Config: " + id);
		
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

	public ConfigKey getConfigKey(String key)
	{
		return ConfigKey.getConfigKey(key);
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

