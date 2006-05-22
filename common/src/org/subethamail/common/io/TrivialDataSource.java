/*
 * $Id$
 * $URL$
 */
package org.subethamail.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.subethamail.common.MailUtils;

/**
 * I can't believe this class didn't already exist.  JAF is an overarchitected,
 * underthought, irritating pile of rubbish.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
public class TrivialDataSource implements DataSource
{
	/** */
	InputStream input;
	String contentType;
	
	/**
	 */
	public TrivialDataSource(InputStream input, String contentType)
	{
		this.input = input;
		this.contentType = contentType;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getContentType()
	 */
	public String getContentType()
	{
		return this.contentType;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException
	{
		return this.input;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getName()
	 */
	public String getName()
	{
		return MailUtils.getNameFromContentType(getContentType());
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException
	{
		throw new UnsupportedOperationException();
	}

}
