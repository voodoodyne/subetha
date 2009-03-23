package org.subethamail.core.util;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.caucho.config.Service;


@Service
abstract public class Management {
	
	/**
	 * Called when the service starts.
	 */

	@PostConstruct
	public void PostConstruct() throws IOException
	{
		start();
	}
	abstract public void start() throws IOException;

	/**
	 * Called when the service stops.
	 */
	@PreDestroy
	public void PreDestroy(){
		stop();
	}
	abstract public void stop();
}
