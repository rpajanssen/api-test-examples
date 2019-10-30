package com.abnamro.examples.jaxrs.resources;

import com.abnamro.examples.dao.HardCodedPersonDAO;
import com.abnamro.examples.domain.api.ErrorResponse;
import com.abnamro.examples.domain.api.Person;
import com.abnamro.examples.domain.api.SafeList;
import com.abnamro.examples.jaxrs.filters.AddCustomHeaderResponseFilter;
import com.abnamro.examples.jaxrs.interceptors.GZIPReaderInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.After;
import org.junit.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;

import static org.junit.Assert.*;

/**
 * This class holds the actual tests for the different example implementations of the JAX-RS DefaultPersonResource
 * integration tests using Jersey.
 *
 * We traded off test class readability for a reduction in code duplication. Note that this trade-off decision might not
 * always have the same result!!!
 *
 * Note that we have generified this class so we can use it for two different PersonResource implementations even
 * returning different json responses (a different person model).
 */
public abstract class AbstractPersonResourceUsingJerseyIT<T extends Person> extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig resourceConfig = buildResourceConfig();

        return registerServerDependencies(resourceConfig);
    }

    protected abstract ResourceConfig buildResourceConfig();
    protected abstract ResourceConfig registerServerDependencies(ResourceConfig resourceConfig);

    @After
    public void cleanup() {
        HardCodedPersonDAO.reset();
    }

    @Test
    public void shouldReturnOkIfResourceIsHealthy() {
        Response response = target("person/isAlive").request().get();

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        String content = response.readEntity(String.class);
        assertEquals("Content of response is: ", "OK", content);
    }

    @Test
    public void shouldReturnPersonWithId() {
        final Person result = target("person/1").request().get(Person.class);

        assertEquals(1L, result.getId());
        assertEquals("Jan", result.getFirstName());
        assertEquals("Janssen", result.getLastName());
    }

    @Test
    public void shouldReturnAllPersonsWithLastNameJanssen() {
        final SafeList<T> result = target("person/lastName/Janssen").request().get(new GenericType<SafeList<T>>(){});

        assertEquals(1, result.getItems().size());
        assertEquals("Jan", result.getItems().get(0).getFirstName());
        assertEquals("Janssen", result.getItems().get(0).getLastName());
    }

    /**
     * Observe that the /person/all resource is the only resource that is bound to a jax-rs interceptor that
     * compresses the response data. This implies that we need to register a jax-rs interceptor on the client used
     * for this test that decompresses the response before de-serialization kicks in.
     */
    @Test
    public void shouldReturnAllPersons() {
        // note : register the incoming response unzipper for the jax-rs client jersey-test uses to call our server under-test
        //        this client does not recoqnize the encoding header and will not automatically unzip
        client().register(GZIPReaderInterceptor.class);

        final SafeList<T> result = target("person/all").request().get(new GenericType<SafeList<T>>(){});

        assertEquals(3, result.getItems().size());
        assertEquals("Jan", result.getItems().get(0).getFirstName());
        assertEquals("Janssen", result.getItems().get(0).getLastName());
        assertEquals("Pieter", result.getItems().get(1).getFirstName());
        assertEquals("Pietersen", result.getItems().get(1).getLastName());
        assertEquals("Erik", result.getItems().get(2).getFirstName());
        assertEquals("Eriksen", result.getItems().get(2).getLastName());
    }

    @Test
    public void shouldAddPerson() {
        Entity<Person> entity = Entity.json(new Person(5L, "Despicable", "Me"));
        Person result = target("person").request().post(entity, Person.class);

        assertEquals("Despicable", result.getFirstName());
        assertEquals("Me", result.getLastName());
    }

    @Test
    public void shouldUpdateAPerson() {
        Entity<Person> entity = Entity.json(new Person(1L, "Jan-Klaas", "Janssen"));
        Person result = target("person").request().put(entity, Person.class);

        // verify response of the resource under test
        assertEquals("Jan-Klaas", result.getFirstName());
        assertEquals("Janssen", result.getLastName());
    }

    @Test
    public void shouldDeleteAPerson() {
        Response response = target("person/3").request().delete();

        // verify response of the resource under test
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void shouldRejectPersistPersonRequestBecauseRequestIsToLarge() {
        Entity<Person> entity = Entity.json(new Person(5L, "Despicable", StringUtils.repeat("Ooops", 250)));
        Response result = target("person").request().post(entity);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus());
    }

    @Test
    public void shouldReturnCustomHeaderForGet() {
        final Response result = target("person/lastName/Janssen").request().get();

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, result.getHeaders().get(AddCustomHeaderResponseFilter.CUSTOM_HEADER).get(0));
    }

    @Test
    public void shouldReturnCustomHeaderEvenOnBadRequest() {
        Entity<Person> entity = Entity.json(new Person(5L, "Despicable", StringUtils.repeat("Ooops", 250)));
        Response result = target("person").request().post(entity);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus());
        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, result.getHeaders().get(AddCustomHeaderResponseFilter.CUSTOM_HEADER).get(0));
    }

    @Test
    public void shouldReturnCustomHeaderForPost() {
        Entity<Person> entity = Entity.json(new Person(5L, "Despicable", "Me"));
        Response result = target("person").request().post(entity);

        // NOTE : WARNING - with the current setup the StatusFilter does not work because the @Status annotation is not
        // found on the resource method, there fore the incorrect status is returned!
        //assertEquals(Response.Status.CREATED.getStatusCode(), result.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());

        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, result.getHeaders().get(AddCustomHeaderResponseFilter.CUSTOM_HEADER).get(0));
    }

    @Test
    public void shouldTriggerInterceptorToReplaceUnacceptableLastName() {
        Entity<Person> entity = Entity.json(new Person(5L, "John", "Asshole"));
        Person result = target("person").request().post(entity, new GenericType<Person>(){});

        assertEquals("John", result.getFirstName());
        assertEquals("A***e", result.getLastName());
    }

    @Test
    public void shouldNotTriggerInterceptorToReplaceUnacceptableLastNameIfResourceIsNotBoundToInterceptor() {
        Entity<Person> entity = Entity.json(new Person(1L, "John", "Asshole"));
        Person result = target("person").request().put(entity, new GenericType<Person>(){});

        assertEquals("John", result.getFirstName());
        assertEquals("Asshole", result.getLastName());
    }

    @Test
    public void shouldThrowAnExceptionWhenAutomaticMarshallingIsUsedAndAConstraintErrorResponseIsReceived() {
        try {
            Entity<Person> entity = Entity.json(new Person(1L, "John", null));
            // note: strongly typed statement below expects a Person but... we know it will be an ErrorResponse but
            //       luckily our client transforms the response into an exception!
            Person result = target("person").request().put(entity, new GenericType<Person>() {});

            fail("a bad-request-exception should have been thrown");
        } catch (BadRequestException exception) {
            // note how we have lost all exception details
            assertEquals("HTTP 400 Bad Request", exception.getMessage());
        }
    }

    @Test
    public void shouldThrowAnExceptionWhenAutomaticMarshallingIsUsedAndAnValidationErrorResponseIsReceived() {
        try {
            Entity<Person> entity = Entity.json(new Person(1L, "ohoh", "duh"));
            // note: strongly typed statement below expects a Person but... we know it will be an ErrorResponse but
            //       luckily our client transforms the response into an exception!
            Person result = target("person").request().put(entity, new GenericType<Person>() {});

            fail("a bad-request-exception should have been thrown");
        } catch (BadRequestException exception) {
            // note how we have lost all exception details
            assertEquals("HTTP 400 Bad Request", exception.getMessage());
        }
    }

    @Test
    public void shouldThrowAnExceptionWhenAutomaticMarshallingIsUsedAndAnGenericErrorResponseIsReceived() {
        try {
            Entity<Person> entity = Entity.json(new Person(1L, "oops", "duh"));
            // note: strongly typed statement below expects a Person but... we know it will be an ErrorResponse but
            //       luckily our client transforms the response into an exception!
            Person result = target("person").request().put(entity, new GenericType<Person>() {});

            fail("a bad-request-exception should have been thrown");
        } catch (InternalServerErrorException exception) {
            // note how we have lost all exception details
            assertEquals("HTTP 500 Internal Server Error", exception.getMessage());
        }
    }

    @Test
    public void shouldAcceptConstraintErrorResponseWithoutAutomaticMarshalling() {
        Entity<Person> entity = Entity.json(new Person(1L, null, null));
        Response result = target("person").request().put(entity);

        // we know this test should result in in error-response so we anticipate on it
        ErrorResponse errorResponse = result.readEntity(ErrorResponse.class);

        assertNotNull(errorResponse);
        assertEquals("BAD_REQUEST", errorResponse.getCode());
        // note: the order of validations may differ so we do not know the order of the validation messages in the message
        assertTrue(errorResponse.getMessage().contains("update.arg0.lastName lastName is not allowed to be empty"));
        assertTrue(errorResponse.getMessage().contains("update.arg0.firstName firstName is not allowed to be empty"));
    }

    @Test
    public void shouldAcceptValidationErrorResponseWithoutAutomaticMarshalling() {
        Entity<Person> entity = Entity.json(new Person(1L, "ohoh", "duh"));
        Response result = target("person").request().put(entity);

        // we know this test should result in in error-response so we anticipate on it
        ErrorResponse errorResponse = result.readEntity(ErrorResponse.class);

        assertNotNull(errorResponse);
        assertEquals("BAD_REQUEST", errorResponse.getCode());
        assertEquals("demo exception to test validation-exception handler", errorResponse.getMessage());
    }

    @Test
    public void shouldAcceptServiceErrorResponseWithoutAutomaticMarshalling() {
        Entity<Person> entity = Entity.json(new Person(1L, "oops", "duh"));
        Response result = target("person").request().put(entity);

        // we know this test should result in in error-response so we anticipate on it
        ErrorResponse errorResponse = result.readEntity(ErrorResponse.class);

        assertNotNull(errorResponse);
        assertEquals("INTERNAL_SERVER_ERROR", errorResponse.getCode());
        assertEquals("demo exception to test default-exception handler", errorResponse.getMessage());
    }
}
