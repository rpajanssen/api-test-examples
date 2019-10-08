package com.abnamro.examples.jaxrs.resources;

import com.abnamro.examples.dao.PersonDAO;
import com.abnamro.examples.dao.PersonFinderMocker;
import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.domain.api.PersistablePerson;
import com.abnamro.examples.jaxrs.exceptionhandling.ConstraintViolationHandler;
import com.abnamro.examples.jaxrs.exceptionhandling.DefaultExceptionHandler;
import com.abnamro.examples.jaxrs.exceptionhandling.ValidationExceptionHandler;
import com.abnamro.examples.jaxrs.filters.AddCustomHeaderResponseFilter;
import com.abnamro.examples.jaxrs.filters.RestrictRequestSizeRequestFilter;
import com.abnamro.examples.jaxrs.interceptors.GZIPWriterInterceptor;
import com.abnamro.examples.jaxrs.interceptors.RemoveBlacklistedLastNameRequestInterceptor;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.mockito.Mockito;

import javax.ws.rs.core.GenericType;

/**
 * Wired integration test to show the use of Jersey to test a JAX-RS endpoint. We also show that we can use a define
 * a simple mock implementation for a dependency.
 *
 * The extended JerseyTest class will create an embedded running instance of a selected http server and deploy
 * your selected resources in it. In the unit tests you can then use utility methods to execute a rest-api call.
 *
 * The HTTP server used is determined by the dependency included in the pom.xml.
 *
 * NOTE : CDI interceptors on resource methods will NOT be triggered. For filtering/interception you should use the
 * JAX-RS filtering/interception mechanism!!!
 */
public class DefaultPersonResourceUsingJerseyAndASimpleMockIT extends AbstractPersonResourceUsingJerseyIT<PersistablePerson> {

    // note: we explicitly need to cast the mock instance to the required generic type otherwise the injection framework
    //       will not find our mock instance
    @SuppressWarnings("unchecked")
    private PersonDAO<PersistablePerson> personDAO = (PersonDAO<PersistablePerson>)Mockito.mock(PersonDAO.class);

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws DataAccessException, NoSuchMethodException {
        PersonFinderMocker.mockPersonFinder(personDAO, PersistablePerson.class.getConstructor(Long.TYPE, String.class, String.class));
    }

    @Override
    protected ResourceConfig buildResourceConfig() {
        return new ResourceConfig(DefaultPersonResource.class);
    }

    @Override
    protected ResourceConfig registerServerDependencies(ResourceConfig resourceConfig) {
        resourceConfig.register(RestrictRequestSizeRequestFilter.class);
        resourceConfig.register(AddCustomHeaderResponseFilter.class);

        resourceConfig.register(ConstraintViolationHandler.class);
        resourceConfig.register(ValidationExceptionHandler.class);
        resourceConfig.register(DefaultExceptionHandler.class);

        resourceConfig.register(RemoveBlacklistedLastNameRequestInterceptor.class);

        // note : register the outgoing response zipper for the server
        resourceConfig.register(GZIPWriterInterceptor.class);

        AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(personDAO).to(new GenericType<PersonDAO<PersistablePerson>>(){});
            }
        };

        return resourceConfig.register(binder);
    }
}