package com.abnamro.examples.jaxrs.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * Example JAX-RS response filter to demonstrate using JAX-RS filters and how to test them in an Jersey unit integration test.
 *
 * A filter is suitable for processing the metadata associated with a message: HTTP headers, query parameters,
 * media type, and other metadata. Filters have the capability to abort a message invocation (useful for security
 * plug-ins, for example).
 *
 * You can install multiple filters at each extension point, in which case the filters are executed in a chain
 * (the order of execution is undefined, unless you specify a priority value for each installed filter).
 *
 * Note: this is a server response filter that runs on the server processing the outgoing http response. You can also
 * implement client response filters post-processing the response right after it is received. Then you need to implement
 * the ClientResponseFilter interface.
 *
 * Filters let us modify the properties of the incoming requests and returned responses – for example, HTTP headers.
 */
@Provider
public class AddCustomHeaderResponseFilter implements ContainerResponseFilter {
    public static final String CUSTOM_HEADER = "X-Custom-Header";
    public static final String CUSTOM_HEADER_VALUE = "Damn this is a nice header!";

    /**
     * Note that the ContainerRequestContext instance is READ ONLY since we already handled the incoming request and are
     * no finalizing the response!
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        responseContext.getHeaders().add(CUSTOM_HEADER, CUSTOM_HEADER_VALUE);
    }
}
