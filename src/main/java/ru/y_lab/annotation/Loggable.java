package ru.y_lab.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code Loggable} annotation indicates that logging should be added
 * to the annotated method or class. Methods or classes annotated with
 * {@code Loggable} will have their execution details logged to the console.
 *
 * <p>This annotation is retained at runtime and can be applied to both
 * methods and types (classes or interfaces).
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * @Loggable
 * public void someMethod() {
 *     // method implementation
 * }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Loggable {
}
