package com.abnamro.examples.jaxrs.filters;

import com.abnamro.examples.jaxrs.annotations.Status;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * This filter sets the http response status code if it detects the default 200 will be returned by checking if the API
 * method is annotated with our own implemented @Status annotation. It will then use the http status code given with
 * that annotation. This way we can prevent the use of the response code boilerplate.
 */
@Status
public class StatusFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (responseContext.getStatus() == 200) {
            for (Annotation annotation : responseContext.getEntityAnnotations()) {
                if(annotation instanceof Status){
                    responseContext.setStatus(((Status) annotation).value().getStatusCode());
                    break;
                }
            }
        }
    }
}
