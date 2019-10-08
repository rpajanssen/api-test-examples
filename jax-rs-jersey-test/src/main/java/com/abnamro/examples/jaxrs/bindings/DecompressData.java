package com.abnamro.examples.jaxrs.bindings;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is a JAX-RS name-binding that can be used to bind specific JAX-RS clients to specific
 * JAX-RS filters and interceptors.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface DecompressData {
}
