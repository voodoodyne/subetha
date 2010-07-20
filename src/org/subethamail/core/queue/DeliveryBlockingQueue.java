package org.subethamail.core.queue;

import javax.inject.Named;

import com.caucho.config.Service;
import com.caucho.jms.file.FileQueueImpl;

/**
 * This class provides a workaround for broken CDI bean management in Resin 4.0.6.
 * For some reason it can't inject queues, possibly because of @Named.  By deriving
 * our own impl of a FileQueue, we eliminate any ambiguity.
 * 
 * @author Jeff Schnitzer
 */
@Service
@Named("delivery")
public class DeliveryBlockingQueue extends FileQueueImpl<DeliveryQueueItem>
{
	private static final long serialVersionUID = 1L;
}
