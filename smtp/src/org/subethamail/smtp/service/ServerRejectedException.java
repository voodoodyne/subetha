package org.subethamail.smtp.service;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
@SuppressWarnings("serial")
public class ServerRejectedException extends Exception
{
	public ServerRejectedException()
	{
	}

	public ServerRejectedException(String message)
	{
		super(message);
	}

	public ServerRejectedException(String message, Throwable nestedException)
	{
		super(message, nestedException);
	}

	public ServerRejectedException(Throwable nestedException)
	{
		super(nestedException);
	}
}
