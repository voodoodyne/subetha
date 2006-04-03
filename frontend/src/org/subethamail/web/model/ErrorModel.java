/*
 * $Id: ErrorModel.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/ErrorModel.java $
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
