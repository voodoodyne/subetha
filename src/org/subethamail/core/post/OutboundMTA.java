/*
 * $Id: $
 * $URL:$
 */

package org.subethamail.core.post;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Binding Type (annotation) for our mail session.
 * 
 * This binding type is attached to the producer
 * of the session, and the injection point it is used.
 * 
 * TODO: Put this back in place once the bug is fixed in 
 * resin.
 * 
 * 
 * @author Scott Hernandez
 *
 */

@Qualifier
@Target({FIELD,METHOD,PARAMETER})
@Retention(RUNTIME)
public @interface OutboundMTA
{}