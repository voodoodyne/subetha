/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Models with a simple error map.
 *
 * @author Jeff Schnitzer
 */
public class ErrorMapModel
{
	/** */
	private static Validator validator;
	static 
	{
	    ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
	    validator=vf.getValidator();   
	}


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
	 * and modify the error map accordingly.
	 */
	public void validate() throws IllegalAccessException
	{
		for (ConstraintViolation<?> invalid: validator.validate(this))
		{
			String simplePath = invalid.getPropertyPath().iterator().next().getName();
			
			Object existingError = this.getErrors().get(simplePath);
			if (existingError == null)
				this.setError(simplePath, invalid.getMessage());
			else
				this.setError(simplePath, existingError + "\n" + invalid.getMessage());
		}
	}
}
