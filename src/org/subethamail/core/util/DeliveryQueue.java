/*
 * $Id: $
 * $URL:$
 */

package org.subethamail.core.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.BindingType;

@BindingType
@Target({FIELD,METHOD,PARAMETER})
@Retention(RUNTIME)
public @interface DeliveryQueue {

}
