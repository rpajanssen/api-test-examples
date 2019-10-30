package com.abnamro.examples.jaxrs.interceptors;

import com.abnamro.examples.jaxrs.bindings.CompressData;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * A JAX-RS 2.0 interceptor gives a developer access to a message body as it is being read or written. Therefore
 * interceptors are executed after the filters and only if a message body is present.
 *
 * You can install multiple interceptors at each extension point, in which case the interceptors are executed in a
 * chain (the order of execution is undefined, unless you specify a priority value for each installed interceptor).
 *
 * This response interceptor is bounded with @CompressData. It does imply that client calling the resources that
 * are also bounded with @CompressData need to implement a ReaderInterceptor that unzips the body first!
 */
@Provider
@CompressData
public class GZIPWriterInterceptor implements WriterInterceptor {
    private static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CONTENT_ENCODING_TYPE = "gzip";

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException {
        MultivaluedMap<String,Object> headers = context.getHeaders();
        headers.add(CONTENT_ENCODING, CONTENT_ENCODING_TYPE);

        try(GZIPOutputStream gzipOutputStream = new GZIPOutputStream(context.getOutputStream())) {
            context.setOutputStream(gzipOutputStream);
            context.proceed();
        }
    }
}
