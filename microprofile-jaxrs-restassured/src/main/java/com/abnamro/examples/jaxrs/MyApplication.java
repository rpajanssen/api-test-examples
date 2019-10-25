package com.abnamro.examples.jaxrs;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Demo JAX-RS application that registers just one resource.
 *
 * Kumuluzee will detect all beans and instantiate them.
 */
@ApplicationPath("/api")
public class MyApplication extends Application {

}
