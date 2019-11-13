package com.abnamro.examples.jaxrs.resources;

import com.abnamro.examples.domain.api.SafeList;
import com.abnamro.examples.jaxrs.annotations.Status;
import com.abnamro.examples.utils.InMemoryLogger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Testing with Arquillian the application with this resource runs in a different JVM the test! So in order to verify if
 * the logger was called, we can't inject it in the test, we need to expose the logger through an additional API that
 * we can use for assertions in our integration tests.
 */
@Path("/log")
public class LoggingResource {
    private InMemoryLogger logger;

    @Inject
    public LoggingResource(InMemoryLogger logger) {
        this.logger = logger;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SafeList<String> allLogEntries() {
        return new SafeList<>(logger.getLogStatements());
    }

    @PUT
    @Status(Response.Status.NO_CONTENT)
    public void reset() {
        logger.reset();
    }

}
