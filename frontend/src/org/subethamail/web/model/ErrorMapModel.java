/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

/**
 * Models with a simple error map.
 * 
 * @author Jeff Schnitzer
 */
public class ErrorMapModel 
{
	/** Keep all the validators around */
	private static Map<Class, ClassValidator> validators = new ConcurrentHashMap<Class, ClassValidator>();
	
	/** */
	Map<String, String> errors;
	
	/** */
	@SuppressWarnings("unchecked")
	public Map<String, String> getErrors()
	{
		if (this.errors == null)
			return Collections.EMPTY_MAP;
		else
			return this.errors;
	}

	/** */
	public void setError(String key, String msg)
	{
		if (this.errors == null)
			this.errors = new HashMap<String, String>();
		
		this.errors.put(key, msg);
	}
	
	/**
	 * Reflect any public fields that have been annotated with contraints
	 * and modify the error map accordingly.  The public requirement is
	 * inherent to java reflection, unfortunately.
	 */
	@SuppressWarnings("unchecked")
	public void validate() throws IllegalAccessException
	{
		ClassValidator val = validators.get(this.getClass());
		if (val == null)
		{
			val = new ClassValidator(this.getClass());
			validators.put(this.getClass(), val);
		}
		
		for (InvalidValue invalid: val.getInvalidValues(this))
		{
			String existingError = this.getErrors().get(invalid.getPropertyPath());
			if (existingError == null)
				this.setError(invalid.getPropertyPath(), invalid.getMessage());
			else
				this.setError(invalid.getPropertyPath(), existingError + "\n" + invalid.getMessage());
		}
	}
}
