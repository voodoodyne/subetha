/*
 * $Id: $
 * $URL:$
 */

package org.subethamail.core.util;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.BindingType;
import javax.persistence.EntityManager;

/**
 * Binding Type (annotation) for our {@link EntityManager}, 
 * {@link SubEthaEntityManager}.
 * 
 * This binding type is attached to the 
 * {@link SubEthaEntityManager} class as the producer, and 
 * the injection point where we need the {@link SubEthaEntityManager}.
 * 
 * TODO: Put this back in place once the bug is fixed in 
 * resin.
 * 
 * @author Scott Hernandez
 */

@BindingType
@Target({TYPE,FIELD,METHOD,PARAMETER})
@Retention(RUNTIME)
public @interface SubEtha
{
}
