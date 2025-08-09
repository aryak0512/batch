package org.aryak.batch.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aryak
 * Annotation to mark methods as event sources.
 * When applied, it indicates that the method is responsible for generating a domain event.
 * The event name is specified in the annotation.
 * This annotation can be used in conjunction with an event publishing mechanism to trigger events
 * when the annotated method is executed.
 * <p>
 * @see <code>org.aryak.batch.annotations.AnnotationManager</code>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventSource {
    String name(); // Name of the event source
}
