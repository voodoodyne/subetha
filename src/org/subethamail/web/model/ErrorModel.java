/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.model;


/**
 * Models with a simple error field.
 * 
 * @author Jeff Schnitzer
 */
public class ErrorModel 
{
	/** */
	String error = "";
	public String getError() { return this.error; }
	public void setError(String value) { this.error = value; }
}
