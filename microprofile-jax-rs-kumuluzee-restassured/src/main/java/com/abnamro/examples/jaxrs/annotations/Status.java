package com.abnamro.examples.jaxrs.annotations;

import javax.ws.rs.NameBinding;
import javax.ws.rs.core.Response;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Status {
    Response.Status value() default Response.Status.OK;
}
