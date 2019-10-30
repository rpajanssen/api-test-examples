package com.abnamro.examples.jaxrs.resources;

import com.abnamro.examples.dao.HardCodedPersonDAO;
import com.abnamro.examples.domain.api.ErrorResponse;
import com.abnamro.examples.domain.api.Person;
import com.abnamro.examples.domain.api.SafeList;
import com.abnamro.examples.jaxrs.MyApplication;
import com.abnamro.examples.jaxrs.filters.AddCustomHeaderResponseFilter;
import com.abnamro.examples.jaxrs.interceptors.GZIPReaderInterceptor;
import com.abnamro.examples.utils.InMemoryLogger;
import com.abnamro.resteasy.InMemoryCdiRestServer;
import com.abnamro.resteasy.RestClient;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test is an example of how you can use RestEasy to have a fully wired test of a resource. It demonstrates you
 * can wire all JAX-RS classes but also CDI classes like CDI method interceptors.
 *
 * This integration test will use the Application class. The Application class defines the complete setup of required
 * JAX-RS and CDI classes and by only listing the Application class the whole application will be setup.
 */
@SuppressWarnings("Duplicates")
class DefaultPersonResourceUsingRestEasyAndCdiIT {
    // use the rest-server that supports CDI
    private InMemoryCdiRestServer server;
    private RestClient restClient;

    /**
     * The before-each is preferred to the before-all because this way we can reset the state in between tests and we
     * can insure we will not experience test interference. This is especially useful when you global state, like we
     * have with the logger that us used by the tracer (a CDI method interceptor). It will most likely be a bit slower
     * because we will re-instantiate and rewire the resource under test for each unit test.
     */
    @BeforeEach
    void setup() throws IOException {
        server = InMemoryCdiRestServer.instance(MyApplication.class);
        restClient = new RestClient(server.getHost(), server.getPort());
    }

    /**
     * See comment at the @BeforeEach.
     *
     * We need to reset the global state of the logger after each test. We will use a hardcoded-dao implementation but
     * it has no static data and will be instantiated again for each unit test, and thus be reset. If the hardcoded-dao
     * used static data like the FakeLogger then we also would have to reset it after each test.
     */
    @AfterEach
    void teardown() {
        server.close();

        InMemoryLogger.reset();

        HardCodedPersonDAO.reset();
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

        // verify response of the resource under test
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        final SafeList<Person> result = response.readEntity(new GenericType<SafeList<Person>>(){});
        assertEquals(3, result.getItems().size());

        // verify the CDI method interceptor call
        assertEquals(2, InMemoryLogger.getLogStatements().size());
        assertEquals("com.abnamro.examples.jaxrs.resources.DefaultPersonResource entering findAllPersons", InMemoryLogger.getLogStatements().get(0));
        assertEquals("com.abnamro.examples.jaxrs.resources.DefaultPersonResource exiting findAllPersons", InMemoryLogger.getLogStatements().get(1));
    }

    @Test
    void shouldReturnPersonWithId() {
        Response response = restClient.newRequest("/person/1").request().buildGet().invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        final Person result = response.readEntity(Person.class);
        assertEquals(1L, result.getId());
        assertEquals("Jan", result.getFirstName());
        assertEquals("Janssen", result.getLastName());

        // verify the CDI method interceptor call
        assertEquals(2, InMemoryLogger.getLogStatements().size());
        assertEquals("com.abnamro.examples.jaxrs.resources.DefaultPersonResource entering findById", InMemoryLogger.getLogStatements().get(0));
        assertEquals("com.abnamro.examples.jaxrs.resources.DefaultPersonResource exiting findById", InMemoryLogger.getLogStatements().get(1));
    }

    @Test
    void shouldReturnAllPersonsWithLastNameJanssen() {
        Response response = restClient.newRequest("/person/lastName/Janssen").request().buildGet().invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        final SafeList<Person> result = response.readEntity(new GenericType<SafeList<Person>>(){});
        assertEquals(1, result.getItems().size());
        assertEquals("Jan", result.getItems().get(0).getFirstName());
        assertEquals("Janssen", result.getItems().get(0).getLastName());

        // verify the CDI method interceptor call
        assertEquals(2, InMemoryLogger.getLogStatements().size());
        assertEquals("com.abnamro.examples.jaxrs.resources.DefaultPersonResource entering findPersonsByLastName", InMemoryLogger.getLogStatements().get(0));
        assertEquals("com.abnamro.examples.jaxrs.resources.DefaultPersonResource exiting findPersonsByLastName", InMemoryLogger.getLogStatements().get(1));
    }

    @Test
    void shouldAddAPerson() {
        Entity<Person> entity = Entity.json(new Person(1001L, "Wim", "Willemsen"));
        Response response = restClient.newRequest("/person").request().buildPost(entity).invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        final Person result = response.readEntity(Person.class);
        assertEquals(1001L, result.getId());
        assertEquals("Wim", result.getFirstName());
        assertEquals("Willemsen", result.getLastName());
    }

    @Test
    void shouldUpdateAPerson() {
        Entity<Person> entity = Entity.json(new Person(1L, "Jan-Klaas", "Janssen"));
        Response response = restClient.newRequest("/person").request().buildPut(entity).invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        final Person result = response.readEntity(Person.class);
        assertEquals("Jan-Klaas", result.getFirstName());
        assertEquals("Janssen", result.getLastName());
    }

    @Test
    void shouldDeleteAPerson() {
        Response response = restClient.newRequest("/person/3").request().buildDelete().invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void shouldTriggerInterceptorToReplaceUnacceptableLastName() {
        Entity<Person> entity = Entity.json(new Person(5L, "John", "Asshole"));
        Response response = restClient.newRequest("/person").request().buildPost(entity).invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        final Person result = response.readEntity(Person.class);
        assertEquals("John", result.getFirstName());
        assertEquals("A***e", result.getLastName());

        // verify the CDI method interceptor call
        assertEquals(2, InMemoryLogger.getLogStatements().size());
        assertEquals("com.abnamro.examples.jaxrs.resources.DefaultPersonResource entering add", InMemoryLogger.getLogStatements().get(0));
        assertEquals("com.abnamro.examples.jaxrs.resources.DefaultPersonResource exiting add", InMemoryLogger.getLogStatements().get(1));
    }

    @Test
    void shouldNotTriggerInterceptorToReplaceUnacceptableLastNameIfResourceIsNotBoundToInterceptor() {
        Entity<Person> entity = Entity.json(new Person(1L, "John", "Asshole"));
        Response response = restClient.newRequest("/person").request().buildPut(entity).invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        final Person result = response.readEntity(Person.class);
        assertEquals("John", result.getFirstName());
        assertEquals("Asshole", result.getLastName());

        // verify the CDI method interceptor call
        assertEquals(2, InMemoryLogger.getLogStatements().size());
        assertEquals("com.abnamro.examples.jaxrs.resources.DefaultPersonResource entering update", InMemoryLogger.getLogStatements().get(0));
        assertEquals("com.abnamro.examples.jaxrs.resources.DefaultPersonResource exiting update", InMemoryLogger.getLogStatements().get(1));
    }

    @Test
    void shouldReceiveAnErrorResponseWithInvalidLastName() {
        Entity<Person> entity = Entity.json(new Person(1L, "John", null));
        Response response = restClient.newRequest("/person").request().buildPut(entity).invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        final ErrorResponse result = response.readEntity(ErrorResponse.class);
        assertEquals(Response.Status.BAD_REQUEST.name(), result.getCode());
        assertEquals("update.arg0.lastName lastName is not allowed to be empty\n", result.getMessage());

        // verify the CDI method interceptor call
        assertEquals(0, InMemoryLogger.getLogStatements().size());
    }

    @Test
    void shouldReceiveAnErrorResponseFromTheValidationExceptionHandler() {
        Entity<Person> entity = Entity.json(new Person(1L, "ohoh", "duh"));
        Response response = restClient.newRequest("/person").request().buildPut(entity).invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        final ErrorResponse result = response.readEntity(ErrorResponse.class);
        assertEquals(Response.Status.BAD_REQUEST.name(), result.getCode());
        assertEquals("demo exception to test validation-exception handler", result.getMessage());

        // verify the CDI method interceptor call
        assertEquals(1, InMemoryLogger.getLogStatements().size());
        assertEquals("com.abnamro.examples.jaxrs.resources.DefaultPersonResource entering update", InMemoryLogger.getLogStatements().get(0));
    }

    @Test
    void shouldReceiveAnErrorResponseFromTheDefaultExceptionHandler() {
        Entity<Person> entity = Entity.json(new Person(1L, "oops", "duh"));
        Response response = restClient.newRequest("/person").request().buildPut(entity).invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        final ErrorResponse result = response.readEntity(ErrorResponse.class);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.name(), result.getCode());
        assertEquals("demo exception to test default-exception handler", result.getMessage());
    }

    @Test
    void shouldReceiveAnErrorResponseFromTheValidationExceptionHandlerWithMultipleErrorMessages() {
        Entity<Person> entity = Entity.json(new Person(1L, null, null));
        Response response = restClient.newRequest("/person").request().buildPut(entity).invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        final ErrorResponse result = response.readEntity(ErrorResponse.class);
        assertNotNull(result);
        assertEquals(Response.Status.BAD_REQUEST.name(), result.getCode());
        // note: the order of validations may differ so we do not know the order of the validation messages in the message
        assertTrue(result.getMessage().contains("update.arg0.lastName lastName is not allowed to be empty"));
        assertTrue(result.getMessage().contains("update.arg0.firstName firstName is not allowed to be empty"));

        // verify the CDI method interceptor call
        assertEquals(0, InMemoryLogger.getLogStatements().size());
    }

    @Test
    void shouldRejectPersistPersonRequestBecauseRequestIsToLarge() {
        Entity<Person> entity = Entity.json(
                new Person(5L, "Despicable", StringUtils.repeat("Ooops", 250))
        );
        Response response = restClient.newRequest("/person").request().buildPost(entity).invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        // verify the CDI method interceptor call
        assertEquals(0, InMemoryLogger.getLogStatements().size());
    }

    @Test
    void shouldReturnCustomHeaderForGet() {
        Response result = restClient.newRequest("/person/lastName/Janssen").request().buildGet().invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, result.getHeaders().get(AddCustomHeaderResponseFilter.CUSTOM_HEADER).get(0));
    }

    @Test
    void shouldReturnCustomHeaderForPost() {
        Entity<Person> entity = Entity.json(new Person(5L, "Despicable", "Me"));
        Response result = restClient.newRequest("/person").request().buildPost(entity).invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.CREATED.getStatusCode(), result.getStatus());
        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, result.getHeaders().get(AddCustomHeaderResponseFilter.CUSTOM_HEADER).get(0));
    }

    @Test
    void shouldReturnCustomHeaderEvenOnBadRequest() {
        Entity<Person> entity = Entity.json(new Person(5L, "Despicable", StringUtils.repeat("Ooops", 250)));
        Response result = restClient.newRequest("/person").request().buildPost(entity).invoke();

        // verify response of the resource under test
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus());
        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, result.getHeaders().get(AddCustomHeaderResponseFilter.CUSTOM_HEADER).get(0));
    }
}
