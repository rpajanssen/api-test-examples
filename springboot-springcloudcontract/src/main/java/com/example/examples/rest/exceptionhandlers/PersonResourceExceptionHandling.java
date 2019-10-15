package com.example.examples.rest.exceptionhandlers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation will be used to bind a RestController and a specific ControllerAdvice error handler together. This
 * way we can have different error handlers handling the same errors different (or returning models) for different
 * rest resources.
 *
 * As example, the error handler for the person-resource will return an ErrorResponse<Person> instance. If you have
 * another resource handling different domain objects, the error-response while have a different type.
 *
 * Another option might be to generify (parts of) the error handler.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PersonResourceExceptionHandling {
}
