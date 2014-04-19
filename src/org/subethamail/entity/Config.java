package org.subethamail.entity;

import java.io.Serializable;
import java.util.logging.Level;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.extern.java.Log;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.subethamail.entity.i.Validator;

// Moved this here from package-info.java to fix JDK5/6 incompatibility problem
// For some reason package-info.java built with JDK6 doesn't work on JDK5
@org.hibernate.annotations.TypeDefs({
	@org.hibernate.annotations.TypeDef(
		name="anyImmutable",
		typeClass=org.subethamail.entity.type.AnyImmutableType.class
	)
})

/**
 * Contains config parameter, key and value.
 *
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@Log
public class Config implements Serializable, Comparable<Config>
{
	private static final long serialVersionUID = 1L;

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
	    log.log(Level.FINE,"Creating new Config: {0}", id);

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
	@Override
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + "}";
	}

	/**
	 * Natural sort order is based on id
	 */
	public int compareTo(Config other)
	{
		return this.id.compareTo(other.getId());
	}
}

