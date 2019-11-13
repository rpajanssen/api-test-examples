package com.abnamro.examples.jaxrs.interceptors;

import com.abnamro.examples.jaxrs.bindings.BlackListLastNames;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.*;
import java.util.stream.Collectors;

/**
 * A JAX-RS 2.0 interceptor gives a developer access to a message body as it is being read or written. Therefore
 * interceptors are executed after the filters and only if a message body is present.
 *
 * You can install multiple interceptors at each extension point, in which case the interceptors are executed in a
 * chain (the order of execution is undefined, unless you specify a priority value for each installed interceptor).
 *
 * We have bound this interceptor using @BlackListLastNames to resources that use the same binding.
 */
@Provider
@BlackListLastNames
public class RemoveBlacklistedLastNameRequestInterceptor implements ReaderInterceptor {
    private static final String[] BLACKLIST = { "Asshole", "Shitface" };
    private static final String[] CLEANLIST = { "A***e", "S***e" };

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        String body = extractMessageBody(context);

        context.setInputStream(
                new ByteArrayInputStream(StringUtils.replaceEach(body, BLACKLIST, CLEANLIST).getBytes())
        );

        return context.proceed();
    }

    private String extractMessageBody(ReaderInterceptorContext context) {
        InputStream is = context.getInputStream();
        return new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
    }
}
