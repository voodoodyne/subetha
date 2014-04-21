package org.subethamail.core.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.activation.DataContentHandler;
import javax.activation.DataSource;

import lombok.extern.java.Log;

/**
 * This content handler stores an attachment id as ascii text.
 * In the future this could get a lot more sophisticated, returning
 * the java.sql.Blob of the attachment, but for now this works.
 * In particular, it allows us to use application/subetha-detachment
 * as a legit mime-type.
 * 
 * The data in and out is java.lang.Long
 * 
 * @author Jeff Schnitzer
 */
@Log
public class DetachmentContentHandler implements DataContentHandler
{
	/*
	 * (non-Javadoc)
	 * @see javax.activation.DataContentHandler#getContent(javax.activation.DataSource)
	 */
	public Object getContent(DataSource ds) throws IOException
	{
		// Extract the String id
		InputStreamReader reader = new InputStreamReader(ds.getInputStream(), "ASCII");
		StringBuilder builder = new StringBuilder();
		
		int ch;
		while ((ch = reader.read()) >= 0)
			builder.append((char)ch);
		
		// Return it as a Long
		return Long.valueOf(builder.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.activation.DataContentHandler#getTransferData(java.awt.datatransfer.DataFlavor, javax.activation.DataSource)
	 */
	public Object getTransferData(DataFlavor df, DataSource ds) throws UnsupportedFlavorException, IOException
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.activation.DataContentHandler#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors()
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.activation.DataContentHandler#writeTo(java.lang.Object, java.lang.String, java.io.OutputStream)
	 */
	public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException
	{
		byte[] data = obj.toString().getBytes("ASCII");
		os.write(data);
	}
	
}
	