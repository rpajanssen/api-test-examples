package com.abnamro.examples.jaxrs;

import com.abnamro.examples.aspects.Logger;
import com.abnamro.examples.dao.HardCodedPersonDAO;
import com.abnamro.examples.dao.PersonDAO;
import com.abnamro.examples.domain.api.Person;
import com.abnamro.examples.jaxrs.exceptionhandling.*;
import com.abnamro.examples.jaxrs.filters.AddCustomHeaderResponseFilter;
import com.abnamro.examples.jaxrs.filters.RestrictRequestSizeRequestFilter;
import com.abnamro.examples.jaxrs.filters.StatusFilter;
import com.abnamro.examples.jaxrs.interceptors.GZIPWriterInterceptor;
import com.abnamro.examples.jaxrs.interceptors.RemoveBlacklistedLastNameRequestInterceptor;
import com.abnamro.examples.jaxrs.resources.DefaultPersonResource;
import com.abnamro.examples.utils.InMemoryLogger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Demo JAX-RS application that registers just one resource.
 *
 * To make CDI work (with RestEasy) we also added an empty beans.xml.
 */
@ApplicationPath("/") // todo : figure out how to define path and test with rest-easy
public class MyApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(DefaultPersonResource.class);

        classes.add(PersonAlreadyExistsExceptionHandler.class);
        classes.add(PersonDoesNotExistExceptionHandler.class);
        classes.add(ConstraintViolationHandler.class);
        classes.add(ValidationExceptionHandler.class);
        classes.add(DefaultExceptionHandler.class);

        classes.add(RemoveBlacklistedLastNameRequestInterceptor.class);

        classes.add(RestrictRequestSizeRequestFilter.class);
        classes.add(AddCustomHeaderResponseFilter.class);
        classes.add(StatusFilter.class);

        classes.add(GZIPWriterInterceptor.class);
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return Collections.singleton(personDAO());
    }

    @Produces
    public PersonDAO<Person> personDAO() {
        return new HardCodedPersonDAO();
    }

    @Produces
    public Logger logger() {
        return new InMemoryLogger();
    }
}
