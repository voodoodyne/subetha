/*
 * $Id: ErrorMapModel.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/ErrorMapModel.java $
 */

package org.subethamail.web.model;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Models with a simple error map.
 * 
 * @author Jeff Schnitzer
 */
public class ErrorMapModel 
{
	/** */
	Map<String, String> errors;
	
	/** */
	public Map getErrors()
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
	public void validate() throws IllegalAccessException
	{
		for (Field f: this.getClass().getFields())
		{
			StringConstraint constraint = f.getAnnotation(StringConstraint.class);
			if (constraint != null)
			{
				String value = (String)f.get(this);
				
				if (constraint.required() && value.length() == 0)
				{
					this.setError(f.getName(), "Cannot be empty");
					if (constraint.reset())
						f.set(this, "");
				}
				else if (constraint.maxLength() > 0 && value.length() > constraint.maxLength())
				{
					this.setError(f.getName(), "Too long; you have " + value.length() + " characters but at most " + constraint.maxLength() + " are allowed");
					if (constraint.reset())
						f.set(this, "");
				}
			}
		}
	}
}
