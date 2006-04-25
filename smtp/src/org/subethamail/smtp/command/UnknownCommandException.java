package org.subethamail.smtp.command;

import org.subethamail.smtp.command.CommandException;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
@SuppressWarnings("serial")
public class UnknownCommandException extends CommandException {
  public UnknownCommandException() {
    super();
  }

  public UnknownCommandException(String string) {
    super(string);
  }

  public UnknownCommandException(String string, Throwable throwable) {
    super(string, throwable);
  }

  public UnknownCommandException(Throwable throwable) {
    super(throwable);
  }
}
