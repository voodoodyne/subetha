package org.subethamail.core.smtp;

import java.io.IOException;
import java.io.InputStream;

import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;

/**
 * Interface for final delivery, ie to the Injector or to a default smtp host
 * 
 * @author Jeff Schnitzer
 */
public interface Deliverer
{
	/** Do final delivery */
	public void deliver(InputStream data) throws RejectException, TooMuchDataException, IOException;
}