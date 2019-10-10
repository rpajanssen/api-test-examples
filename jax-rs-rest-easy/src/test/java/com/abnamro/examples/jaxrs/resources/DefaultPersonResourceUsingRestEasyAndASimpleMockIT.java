package com.abnamro.examples.jaxrs.resources;

import com.abnamro.examples.dao.PersonDAO;
import com.abnamro.examples.dao.PersonFinderMocker;
import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.domain.api.ErrorResponse;
import com.abnamro.examples.domain.api.Person;
import com.abnamro.examples.domain.api.SafeList;
import com.abnamro.examples.jaxrs.exceptionhandling.ConstraintViolationHandler;
import com.abnamro.examples.jaxrs.exceptionhandling.DefaultExceptionHandler;
import com.abnamro.examples.jaxrs.exceptionhandling.ValidationExceptionHandler;
import com.abnamro.examples.jaxrs.filters.AddCustomHeaderResponseFilter;
import com.abnamro.examples.jaxrs.filters.RestrictRequestSizeRequestFilter;
import com.abnamro.examples.jaxrs.interceptors.GZIPReaderInterceptor;
import com.abnamro.examples.jaxrs.interceptors.GZIPWriterInterceptor;
import com.abnamro.examples.jaxrs.interceptors.RemoveBlacklistedLastNameRequestInterceptor;
import com.abnamro.resteasy.InMemoryRestServer;
import com.abnamro.resteasy.RestClient;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test is an example of how you can use RestEasy to have a fully wired test of a resource. It demonstrates you
 * can wire all JAX-RS classes, mock the dependencies of your resource under test. Other CDI classes like
 * method-interceptors will not be instantiated and thus will not operate.
 *
 * This integration test will NOT use the Application class. It will instantiate the resource under test itself and then
 * tell RestEasy which other JAX-RS classes should be managed. This shows you how you can create a completely customized
 * set of wired classes for your test independent of the Application class setup.
 */
@ExtendWith(MockitoExtension.class)
class DefaultPersonResourceUsingRestEasyAndASimpleMockIT {
    // use the rest-server that does NOT support CDI
    private InMemoryRestServer server;
    private RestClient restClient;

    @Mock
    private PersonDAO<Person> personDAO;

    /**
     * The before-each is preferred to the before-all because this way we can reset the state in between tests and we
     * can insure we will not experience test interference. This is especially useful when you global state, like we
     * have with the logger that us used by the tracer (a CDI method interceptor). It will most likely be a bit slower
     * because we will re-instantiate and rewire the resource under test for each unit test.
     */
    @BeforeEach
    void setup() throws DataAccessException, NoSuchMethodException, IOException {
        /*
         * We will construct the resource we want to test ourselves and use the constructor also used for
         * constructor injection to inject the mocked DAO.
         */
        PersonFinderMocker.mockPersonFinder(personDAO, Person.class.getConstructor(Long.TYPE, String.class, String.class));
        DefaultPersonResource underTest = new DefaultPersonResource(personDAO);

        /*
         * We list the whole shebang of JAX-RS classes we want to wire for this test, resources, filters, interceptors,
         * exception-mappers... the whole shebang! We do not list CDI classes because they will not be supported by
         * the rest-server we will be using.
         */
        Object[] theWholeShebang = {
                underTest,
                ConstraintViolationHandler.class, ValidationExceptionHandler.class, DefaultExceptionHandler.class,
                RemoveBlacklistedLastNameRequestInterceptor.class,
                RestrictRequestSizeRequestFilter.class, AddCustomHeaderResponseFilter.class,
                GZIPWriterInterceptor.class
        };

        server = InMemoryRestServer.instance(theWholeShebang);
        restClient = new RestClient(server.getHost(), server.getPort());
    }

    /**
     * See comment at the @BeforeEach.
     */
    @AfterEach
    void teardown() {
        server.close();

        //noinspection unchecked
        Mockito.reset(personDAO);
    }

    /**
     * Observe that the /person/all resource is the only resource that is bound to a jax-rs interceptor that
     * compresses the response data. This implies that we need to register a jax-rs interceptor on the client used
     * for this test that decompresses the response before de-serialization kicks in.
     */
    @Test
    void shouldReturnAllPersons() {
        Response response = restClient.newRequest(
                "/person/all",
                Collections.singletonList(GZIPReaderInterceptor.class)
        ).request().buildGet().invoke();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        final SafeList<Person> result = response.readEntity(new GenericType<SafeList<Person>>(){});
        assertEquals(3, result.getItems().size());
    }

    @Test
    void shouldReturnPersonWithId() {
        Response response = restClient.newRequest("/person/1").request().buildGet().invoke();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        final Person result = response.readEntity(Person.class);
        assertEquals(1L, result.getId());
        assertEquals("Jan", result.getFirstName());
        assertEquals("Janssen", result.getLastName());
    }

    @Test
    void shouldReturnAllPersonsWithLastNameJanssen() {
        Response response = restClient.newRequest("/person/lastName/Janssen").request().buildGet().invoke();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        final SafeList<Person> result = response.readEntity(new GenericType<SafeList<Person>>(){});
        assertEquals(1, result.getItems().size());
        assertEquals("Jan", result.getItems().get(0).getFirstName());
        assertEquals("Janssen", result.getItems().get(0).getLastName());
    }

    @Test
    void shouldTriggerInterceptorToReplaceUnacceptableLastName() {
        Entity<Person> entity = Entity.json(new Person(5L, "John", "Asshole"));
        Response response = restClient.newRequest("/person").request().buildPost(entity).invoke();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        final Person result = response.readEntity(Person.class);
        assertEquals("John", result.getFirstName());
        assertEquals("A***e", result.getLastName());
    }

    @Test
    void shouldNotTriggerInterceptorToReplaceUnacceptableLastNameIfResourceIsNotBoundToInterceptor() {
        Entity<Person> entity = Entity.json(new Person(1L, "John", "Asshole"));
        Response response = restClient.newRequest("/person").request().buildPut(entity).invoke();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        final Person result = response.readEntity(Person.class);
        assertEquals("John", result.getFirstName());
        assertEquals("Asshole", result.getLastName());
    }

    @Test
    void shouldReceiveAnErrorResponseWithInvalidLastName() {
        Entity<Person> entity = Entity.json(new Person(1L, "John", null));
        Response response = restClient.newRequest("/person").request().buildPut(entity).invoke();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        final ErrorResponse result = response.readEntity(ErrorResponse.class);
        assertEquals(Response.Status.BAD_REQUEST.name(), result.getCode());
        assertEquals("update.arg0.lastName lastName is not allowed to be empty\n", result.getMessage());
    }

    @Test
    void shouldReceiveAnErrorResponseFromTheValidationExceptionHandler() {
        Entity<Person> entity = Entity.json(new Person(1L, "ohoh", "duh"));
        Response response = restClient.newRequest("/person").request().buildPut(entity).invoke();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        final ErrorResponse result = response.readEntity(ErrorResponse.class);
        assertEquals(Response.Status.BAD_REQUEST.name(), result.getCode());
        assertEquals("demo exception to test validation-exception handler", result.getMessage());
    }

    @Test
    void shouldReceiveAnErrorResponseFromTheDefaultExceptionHandler() {
        Entity<Person> entity = Entity.json(new Person(1L, "oops", "duh"));
        Response response = restClient.newRequest("/person").request().buildPut(entity).invoke();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        final ErrorResponse result = response.readEntity(ErrorResponse.class);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.name(), result.getCode());
        assertEquals("demo exception to test default-exception handler", result.getMessage());
    }

    @Test
    void shouldReceiveAnErrorResponseFromTheValidationExceptionHandlerWithMultipleErrorMessages() {
        Entity<Person> entity = Entity.json(new Person(1L, null, null));
        Response response = restClient.newRequest("/person").request().buildPut(entity).invoke();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        final ErrorResponse result = response.readEntity(ErrorResponse.class);

        assertNotNull(result);
        assertEquals(Response.Status.BAD_REQUEST.name(), result.getCode());
        // note: the order of validations may differ so we do not know the order of the validation messages in the message
        assertTrue(result.getMessage().contains("update.arg0.lastName lastName is not allowed to be empty"));
        assertTrue(result.getMessage().contains("update.arg0.firstName firstName is not allowed to be empty"));
    }

    @Test
    void shouldRejectPersistPersonRequestBecauseRequestIsToLarge() {
        Entity<Person> entity = Entity.json(
                new Person(5L, "Despicable", StringUtils.repeat("Ooops", 250))
        );
        Response response = restClient.newRequest("/person").request().buildPost(entity).invoke();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void shouldReturnCustomHeaderForGet() {
        Response result = restClient.newRequest("/person/lastName/Janssen").request().buildGet().invoke();

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, result.getHeaders().get(AddCustomHeaderResponseFilter.CUSTOM_HEADER).get(0));
    }

    @Test
    void shouldReturnCustomHeaderForPost() {
        Entity<Person> entity = Entity.json(new Person(5L, "Despicable", StringUtils.repeat("Ooops", 250)));
        Response result = restClient.newRequest("/person").request().buildPost(entity).invoke();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus());
        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, result.getHeaders().get(AddCustomHeaderResponseFilter.CUSTOM_HEADER).get(0));
    }

    @Test
    void shouldReturnCustomHeaderEvenOnBadRequest() {
        Entity<Person> entity = Entity.json(new Person(5L, "Despicable", "Me"));
        Response result = restClient.newRequest("/person").request().buildPost(entity).invoke();

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, result.getHeaders().get(AddCustomHeaderResponseFilter.CUSTOM_HEADER).get(0));
    }
}
