package com.abnamro.examples.aspects;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

/**
 * An interceptor that logs the entry and exit of method calls. We use this interceptor to demonstrate how you can
 * unit-integration (wired) test a resource that has CDI method-interceptors (this tracer) using RestEasy.
 *
 * To prevent the need of a beans.xml specifying the interceptor we use the @Priority annotation
 * (see https://docs.oracle.com/javaee/7/tutorial/cdi-adv006.htm).
 *
 * Note: the name of this interceptor class describes what it is, and it does not have the obsolete 'Interceptor'
 * postfix, something that does not add value.
 */
@Interceptor
@EnableTracing
@Priority(10000) // todo : used to be Interceptor.Priority.LIBRARY_BEFORE in java8
public class Tracer {
    private static final String MESSAGE_TEMPLATE = "%s %s";

    private static final String ACTION_ENTERING = "entering";
    private static final String ACTION_EXITING = "exiting";

    @SuppressWarnings("CdiInjectionPointsInspection")
    private final Logger logger;

    @Inject
    public Tracer(final Logger logger) {
        this.logger = logger;
    }

    @AroundInvoke
    public Object traceMethod(final InvocationContext ctx) throws Exception {
        logEntry(ctx.getMethod());

        final Object result = ctx.proceed();

        logExit(ctx.getMethod());

        return result;
    }

    private void logEntry(final Method method) {
        logMessage(method, ACTION_ENTERING);
    }

    private void logExit(final Method method) {
        logMessage(method, ACTION_EXITING);
    }

    private void logMessage(final Method method, final String action) {
        logger.debug(method.getDeclaringClass().getName(), String.format(MESSAGE_TEMPLATE, action, method.getName()));
    }
}

