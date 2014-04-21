package org.subethamail.entity;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import lombok.extern.java.Log;

import org.subethamail.entity.i.Validator;

/**
 * Entity for a single piece of mail.
 * 
 * @author Jeff Schnitzer
 */
@Entity
// Disabled caching until instrumentation problem with 4.0.4.GA resolved
//@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL, include="non-lazy")
@Log
public class Attachment implements Serializable
{
	private static final long serialVersionUID = 1L;
	
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
	    log.log(Level.FINE,"Creating new attachment");

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

