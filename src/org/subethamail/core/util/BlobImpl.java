/*
 * $Id$
 * $URL$
 *
 * Similar to hibernate BlobImpl
 */

package org.subethamail.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * Trivial implementation of <code>java.sql.Blob</code>.  Mostly borrowed
 * from hibernate code.
 * 
 * @author Gavin King
 * @author Jeff Schnitzer
 */
public class BlobImpl implements Blob
{
	private InputStream stream;
	private int length;
	private boolean needsReset = false;

	public BlobImpl(byte[] bytes)
	{
		this.stream = new ByteArrayInputStream(bytes);
		this.length = bytes.length;
	}

	public BlobImpl(InputStream stream, int length)
	{
		this.stream = stream;
		this.length = length;
	}

	/**
	 * @see java.sql.Blob#length()
	 */
	public long length() throws SQLException
	{
		return length;
	}

	/**
	 * @see java.sql.Blob#getBinaryStream()
	 */
	public InputStream getBinaryStream() throws SQLException
	{
		// First time through we don't need reset, all other times we do
		if (needsReset)
		{
			try
			{
				stream.reset();
			}
			catch (IOException ex)
			{
				throw new SQLException("Could not reset stream");
			}
		}
		else
			needsReset = true;
		
		return stream;
	}

	/**
	 * @see java.sql.Blob#truncate(long)
	 */
	public void truncate(long pos) throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.sql.Blob#getBytes(long, int)
	 */
	public byte[] getBytes(long pos, int len) throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.sql.Blob#setBytes(long, byte[])
	 */
	public int setBytes(long pos, byte[] bytes) throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.sql.Blob#setBytes(long, byte[], int, int)
	 */
	public int setBytes(long pos, byte[] bytes, int i, int j) throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.sql.Blob#position(byte[], long)
	 */
	public long position(byte[] bytes, long pos) throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.sql.Blob#setBinaryStream(long)
	 */
	public OutputStream setBinaryStream(long pos) throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.sql.Blob#position(Blob, long)
	 */
	public long position(Blob blob, long pos) throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void free() throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getBinaryStream(long pos, long length) throws SQLException
	{
		throw new UnsupportedOperationException();
	}
}
