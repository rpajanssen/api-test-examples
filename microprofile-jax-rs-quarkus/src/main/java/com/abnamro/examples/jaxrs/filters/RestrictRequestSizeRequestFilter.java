package com.abnamro.examples.jaxrs.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Example JAX-RS request filter to demonstrate using JAX-RS filters and how to test them in an Jersey unit integration test.
 *
 * A filter is suitable for processing the metadata associated with a message: HTTP headers, query parameters,
 * media type, and other metadata. Filters have the capability to abort a message invocation (useful for security
 * plug-ins, for example).
 *
 * You can install multiple filters at each extension point, in which case the filters are executed in a chain
 * (the order of execution is undefined, unless you specify a priority value for each installed filter).
 *
 * Note: this is a server request filter that runs on the server processing the incoming http requests. You can also
 * implement client request filters pre-processing the request right before it is send. Then you need to implement
 * the ClientRequestFilter interface.
 *
 * Note: this filter is executed after the resource has been matched. If you want it to be executed before the resource
 * has been matched then add the @PreMatching annotation to this filter class.
 */
@Provider
public class RestrictRequestSizeRequestFilter implements ContainerRequestFilter {
    private static int MAX_ALLOWED_REQUEST_SIZE = 1024;

    @Override
    public void filter(ContainerRequestContext context) {
        if (MAX_ALLOWED_REQUEST_SIZE < context.getLength()) {
            context.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
        }
    }
}
