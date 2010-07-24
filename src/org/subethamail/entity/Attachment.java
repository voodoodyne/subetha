/*
 * $Id: Attachment.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/entity/Attachment.java $
 */

package org.subethamail.entity;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.entity.i.Validator;

/**
 * Entity for a single piece of mail.
 * 
 * @author Jeff Schnitzer
 */
@Entity
// Disabled caching until instrumentation problem with 4.0.4.GA resolved
//@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL, include="non-lazy")
public class Attachment implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** */
	@Transient private final static Logger log = LoggerFactory.getLogger(Attachment.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_ATTACHMENT_CONTENT_TYPE)
	String contentType;
	
	/** */
	@Lob
	@Basic(fetch=FetchType.LAZY)
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

