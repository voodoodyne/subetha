/*
 * $Id$
 * $URL$
 */
package org.subethamail.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

/**
 * I can't believe this class didn't already exist.  JAF is an overarchitected,
 * underthought, irritating pile of rubbish.
 * 
 * @author Jeff Schnitzer
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
		// Not sure what this is for.  Probably should parse the content-type
		// looking for the filename.  Doesn't seem to actually be used,
		// but it can't be null.
		// TODO:  consider implementing getName()
		return "TODO?";
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException
	{
		throw new UnsupportedOperationException();
	}

}
