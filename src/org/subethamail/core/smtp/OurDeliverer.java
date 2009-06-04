package org.subethamail.core.smtp;

import java.io.IOException;
import java.io.InputStream;

import org.subethamail.core.injector.i.Injector;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;

/**
 * Deliverer which injects to a subetha mailing list
 * 
 * @author Jeff Schnitzer
 */
public class OurDeliverer implements Deliverer
{
	/** */
	protected Injector injector;
	protected String from;
	protected String to;

	/** */
	public OurDeliverer(Injector inj, String from, String to)
	{
		this.injector = inj;
		this.from = from;
		this.to = to;
	}

	/* */
	@Override
	public void deliver(InputStream data) throws RejectException, TooMuchDataException, IOException
	{
		boolean accepted = this.injector.inject(this.from, this.to, data);
		if (!accepted)
			throw new RejectException("Not accepted for " + this.to);
	}
}