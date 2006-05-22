package org.subethamail.smtp.command;

import java.io.IOException;
import java.io.InputStream;

import org.subethamail.common.io.DeferredFileOutputStream;
import org.subethamail.smtp.server.BaseCommand;
import org.subethamail.smtp.server.CharTerminatedInputStream;
import org.subethamail.smtp.server.ConnectionContext;
import org.subethamail.smtp.server.DotUnstuffingInputStream;
import org.subethamail.smtp.server.Session;
import org.subethamail.smtp.server.Session.Delivery;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 * @author Jon Stevens
 */
public class DataCommand extends BaseCommand
{
    private final static char[] SMTP_TERMINATOR = { '\r', '\n', '.', '\r', '\n' };

    public DataCommand()
	{
		super("DATA",
				"Following text is collected as the message.\n"
				+ "End data with <CR><LF>.<CR><LF>");
	}

	@Override
	public void execute(String commandString, ConnectionContext context) throws IOException
	{
		Session session = context.getSession();

		if (session.getSender() == null)
		{
			context.sendResponse("503 Error: need MAIL command");
			return;
		}
		else if (session.getDeliveries().size() == 0)
		{
			context.sendResponse("503 Error: need RCPT command");
			return;
		}

		context.sendResponse("354 End data with <CR><LF>.<CR><LF>");
		session.setDataMode(true);

		InputStream dotInputStream = 
			new DotUnstuffingInputStream(
					new CharTerminatedInputStream(context.getConnection().getInput(), SMTP_TERMINATOR));

		if (session.getDeliveries().size() == 1)
		{
			Delivery delivery = session.getDeliveries().get(0);
			delivery.getListener().deliver(session.getSender(), delivery.getRecipient(), dotInputStream);
		}
		else
		{
			// 5 megs
			DeferredFileOutputStream dfos = new DeferredFileOutputStream(1024*1024*5);

			try
			{
				int value;
				while ((value = dotInputStream.read()) >= 0)
				{
					dfos.write(value);
				}
	
				for (Delivery delivery : session.getDeliveries())
				{
					delivery.getListener().deliver(session.getSender(), delivery.getRecipient(), dfos.getInputStream());
				}
			}
			finally
			{
				dfos.close();
			}
		}

		context.sendResponse("250 Ok");
	}
}
