/*
 * $Id$
 * $URL$
 */

package org.subethamail.entity;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.subethamail.common.valid.Validator;

/**
 * Entity for a single piece of mail.
 * 
 * @author Jeff Schnitzer
 */
@NamedQueries({
})
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class Attachment implements Serializable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(Attachment.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_ATTACHMENT_CONTENT_TYPE)
	String contentType;
	
	/** */
	@Lob
	@Column(nullable=false, length=Validator.MAX_ATTACHMENT_CONTENT)
	Blob content;
	
	/** */
	@ManyToOne
	@JoinColumn(name="mailId", nullable=false)
	Mail mail;
	
	/**
	 */
	public Attachment() {}
	
	/**
	 * Creates a new attachment.
	 */
	public Attachment(Mail mail, Blob content, String contentType)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new attachment");

		this.mail = mail;
		this.content = content;
		this.contentType = contentType;
	}
	
	/** */
	public Long getId() { return this.id; }

	/** */
	public Blob getContent() { return this.content; }
	
	/**
	 * Convenience method
	 */
	public InputStream getContentStream()
	{
		try
		{
			return this.content.getBinaryStream();
		}
		catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	/** */
	public String getContentType() { return this.contentType; }
	
	/** */
	public Mail getMail() { return this.mail; }
	
	/** */
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + "}";
	}
}

