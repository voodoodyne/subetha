package org.subethamail.core.smtp;

import java.io.IOException;
import java.io.InputStream;

import org.subethamail.client.SmartClient;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;

/**
 * Deliverer which delivers to a SmartClient
 * 
 * @author Jeff Schnitzer
 */
public class FallbackDeliverer implements Deliverer
{
	/** */
	protected SmartClient client;

	/** */
	public FallbackDeliverer(SmartClient client)
	{
		this.client = client;
	}

	/* */
	@Override
	public void deliver(InputStream data) throws RejectException, TooMuchDataException, IOException
	{
		this.client.dataStart();
		
		byte[] buffer = new byte[8192];
		int numRead;
		while ((numRead = data.read(buffer)) > 0)
			this.client.dataWrite(buffer, numRead);
		
		this.client.dataEnd();
	}
}