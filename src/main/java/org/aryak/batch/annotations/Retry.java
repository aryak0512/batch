package org.aryak.batch.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * @author aryak
 * Annotation to retry a method execution.
 * When applied, it will retry the method execution a specified number of times with a delay
 * between attempts in case of failure.
 *
 * @see <code>org.aryak.batch.annotations.AnnotationManager</code>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    int times() default 1;

    int delay() default 500; // delay in milliseconds
}
