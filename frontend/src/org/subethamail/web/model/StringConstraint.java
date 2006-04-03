/*
 * $Id: ErrorModel.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/ErrorModel.java $
 */

package org.subethamail.web.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation that the validator will notice and check.
 * 
 * @author Jeff Schnitzer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StringConstraint 
{
	int maxLength() default 0;
	boolean required() default false;
	boolean reset() default false;
}
