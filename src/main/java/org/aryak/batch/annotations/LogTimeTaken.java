package org.aryak.batch.annotations;

import java.lang.annotation.*;

/*
 * @author aryak
 * Annotation to log the time taken by a method.
 * When applied, it will log the execution time of the method.
 * This annotation can be used to monitor performance and identify bottlenecks in the code.
 * @see <code>org.aryak.batch.annotations.AnnotationManager</code>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogTimeTaken {
}
