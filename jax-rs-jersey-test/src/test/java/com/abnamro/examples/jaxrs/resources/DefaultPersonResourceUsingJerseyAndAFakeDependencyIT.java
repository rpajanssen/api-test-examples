package com.abnamro.examples.jaxrs.resources;

import com.abnamro.examples.aspects.Logger;
import com.abnamro.examples.dao.HardCodedPersonDAO;
import com.abnamro.examples.dao.PersonDAO;
import com.abnamro.examples.domain.api.Person;
import com.abnamro.examples.jaxrs.exceptionhandling.ConstraintViolationHandler;
import com.abnamro.examples.jaxrs.exceptionhandling.DefaultExceptionHandler;
import com.abnamro.examples.jaxrs.exceptionhandling.InvalidDataExceptionHandler;
import com.abnamro.examples.jaxrs.exceptionhandling.ValidationExceptionHandler;
import com.abnamro.examples.jaxrs.filters.AddCustomHeaderResponseFilter;
import com.abnamro.examples.jaxrs.filters.RestrictRequestSizeRequestFilter;
import com.abnamro.examples.jaxrs.interceptors.GZIPWriterInterceptor;
import com.abnamro.examples.jaxrs.interceptors.RemoveBlacklistedLastNameRequestInterceptor;
import com.abnamro.examples.utils.InMemoryLogger;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.GenericType;

/**
 * Wired integration test to show the use of Jersey to test a JAX-RS endpoint. We also show that we can use a define
 * a fake implementation for a dependency.
 *
 * The extended JerseyTest class will create an embedded running instance of a selected http server and deploy
 * your selected resources in it. In the unit tests you can then use utility methods to execute a rest-api call.
 *
 * The HTTP server used is determined by the dependency included in the pom.xml.
 *
 * NOTE : CDI interceptors on resource methods will NOT be triggered. For filtering/interception you should use the
 * JAX-RS filtering/interception mechanism!!!
 */
public class DefaultPersonResourceUsingJerseyAndAFakeDependencyIT extends AbstractPersonResourceUsingJerseyIT<Person> {

    @Override
    protected ResourceConfig buildResourceConfig() {
        return new ResourceConfig(DefaultPersonResource.class);
    }

    @Override
    protected ResourceConfig registerServerDependencies(ResourceConfig resourceConfig) {
        resourceConfig.register(RestrictRequestSizeRequestFilter.class);
        resourceConfig.register(AddCustomHeaderResponseFilter.class);

        resourceConfig.register(InvalidDataExceptionHandler.class);
        resourceConfig.register(ConstraintViolationHandler.class);
        resourceConfig.register(ValidationExceptionHandler.class);
        resourceConfig.register(DefaultExceptionHandler.class);

        resourceConfig.register(RemoveBlacklistedLastNameRequestInterceptor.class);

        // note : register the outgoing response zipper for the server
        resourceConfig.register(GZIPWriterInterceptor.class);

        AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(HardCodedPersonDAO.class).to(new GenericType<PersonDAO<Person>>(){});

                bind(InMemoryLogger.class).to(Logger.class);
            }
        };

        return resourceConfig.register(binder);
    }
}
