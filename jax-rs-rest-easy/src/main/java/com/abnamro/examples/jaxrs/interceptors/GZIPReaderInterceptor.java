package com.abnamro.examples.jaxrs.interceptors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * A JAX-RS 2.0 interceptor gives a developer access to a message body as it is being read or written. Therefore
 * interceptors are executed after the filters and only if a message body is present.
 *
 * You can install multiple interceptors at each extension point, in which case the interceptors are executed in a
 * chain (the order of execution is undefined, unless you specify a priority value for each installed interceptor).
 *
 * This response interceptor is bounded with @CompressData. It needs to be used bij JAX-RS clients to process
 * responses from resources have compressed their data!
 *
 * Note : we did not give both the gzip-interceptors the same binding because we only want ONE of them to be bound to
 * a resource (the writer) and not both of them.
 */
@Provider
public class GZIPReaderInterceptor implements ReaderInterceptor {

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context)
            throws IOException, WebApplicationException {
        context.setInputStream(new GZIPInputStream(context.getInputStream()));
        return context.proceed();
    }
}
