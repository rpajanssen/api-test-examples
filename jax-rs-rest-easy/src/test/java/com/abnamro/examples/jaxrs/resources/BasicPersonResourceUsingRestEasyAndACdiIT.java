package com.abnamro.examples.jaxrs.resources;

import com.abnamro.examples.dao.HardCodedPersonDAO;
import com.abnamro.examples.domain.api.ErrorResponse;
import com.abnamro.examples.domain.api.Person;
import com.abnamro.examples.domain.api.SafeList;
import com.abnamro.examples.jaxrs.MyApplication;
import com.abnamro.examples.jaxrs.filters.AddCustomHeaderResponseFilter;
import com.abnamro.examples.jaxrs.interceptors.GZIPReaderInterceptor;
import com.abnamro.examples.utils.InMemoryLogger;
import com.abnamro.resteasy.RestClient;
import com.abnamro.utils.SocketUtil;
import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Minimal integration test of a JAX-RS resource using RestEasy and Undertow.
 */
@ExtendWith(MockitoExtension.class)
public class BasicPersonResourceUsingRestEasyAndACdiIT {

    private UndertowJaxrsServer jaxrsServer;
    private RestClient restClient;

    @BeforeEach
    void setup() throws IOException {
        jaxrsServer = new UndertowJaxrsServer();

        ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        deployment.setInjectorFactoryClass(org.jboss.resteasy.cdi.CdiInjectorFactory.class.getName());
        deployment.setApplication(new MyApplication());

        DeploymentInfo di = jaxrsServer.undertowDeployment(deployment, "/");
        di.setDeploymentName("Undertow + RestEasy CDI example").setContextPath("").setClassLoader(MyApplication.class.getClassLoader());

        //Add CDI listener
        di.addListeners(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class));

        jaxrsServer.deploy(di);

        int port = SocketUtil.findFreePort();
        jaxrsServer.start(Undertow.builder().addHttpListener(port, "localhost"));

        restClient = new RestClient("localhost", port);
    }

    @AfterEach
    void teardown() {
        jaxrsServer.stop();

        InMemoryLogger.reset();

        HardCodedPersonDAO.reset();
    }

    /**
     * Note that this resource is the only resource that zips the returned result. So we have to 'tell' the client
     * to unzip the result before doing anything else. We do this by passing the gzip-reader-interceptor class as
     * provider to the client.
     *
     * This client does not recoqnize the encoding header and will not automatically unzip!
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
                new Person(1L, "Despicable", StringUtils.repeat("Ooops", 250))
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
