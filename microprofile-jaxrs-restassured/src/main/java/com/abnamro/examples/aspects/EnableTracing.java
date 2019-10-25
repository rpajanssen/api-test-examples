package com.abnamro.examples.aspects;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Example annotation as example to be used if you want to start tracing method calls.
 *
 * Note that the name explains/describes what you will achieve if you annotate a method: you will enable tracing!
 * Also note that we did not add the postfix 'InterceptorBinding' to the name of this annotation, because that does
 * not add value and obscures the functional meaning of the annotations name.
 *
 * Search for usage to find out how it is used and to find an example on how we can test the tracer.
 *
 * You will notice that for testing your aspects (wired unit tests) you can add a simple test service to your test
 * sources - of just a couple of lines - and use that to test your aspect.
 */
@InterceptorBinding
@Target( { METHOD, TYPE } )
@Retention( RUNTIME )
public @interface EnableTracing {

}
