package com.abnamro.examples.jaxrs.resources;

import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.dao.exceptions.PersonAlreadyExistsException;
import com.abnamro.examples.dao.exceptions.PersonDoesNotExistException;
import com.abnamro.examples.domain.api.Person;
import com.abnamro.examples.domain.api.SafeList;
import com.abnamro.examples.jaxrs.annotations.Status;
import com.abnamro.examples.jaxrs.bindings.BlackListLastNames;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Interface describing a PersonResource. We can have multiple implementations that now won't have the JAX-RS annotation
 * boilerplate, so also no duplications. The resources will read a bit easier and we have a clearer separation of
 * concerns between the definition of the JAX-RS API and the implementation.
 *
 * Note that the we can use all JAX-RS annotations on this interface, so not only the annotations defining the
 * properties of the REST API but also the annotations enabling the JAX-RS filters/interceptors.
 *
 * If you want to use Apache CFX clients, you even require an interface to be able to create an instance of such a
 * client.
 *
 * Note that JAX-RS has its own filter/interceptor mechanism and that you should use that mechanism! Use the filters
 * to decorate the incoming/outgoing response properties, use the interceptors to decorate the response body.
 */
@Path("/person")
public interface PersonResource<T extends Person> {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/isAlive")
    Response healthCheck();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    T findById(@PathParam("id") long id) throws DataAccessException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/lastName/{lastName}")
    SafeList<T> findPersonsByLastName(@PathParam("lastName") String lastName) throws DataAccessException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //@CompressData
    SafeList<T> findAllPersons() throws DataAccessException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BlackListLastNames
    @Status(Response.Status.CREATED)
    T add(@Valid T person) throws DataAccessException, PersonAlreadyExistsException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    T update(@Valid T person) throws DataAccessException, PersonDoesNotExistException;

    @DELETE
    @Status(Response.Status.NO_CONTENT)
    @Path("/{personId}")
    void delete(@PathParam("personId") long personId) throws DataAccessException;

    /**
     * Implements failure scenarios for testing purposes that is the same for all person-resources.
     */
    default void onSpecificTestInputThrowASpecificException(Person person) {
        if("ohoh".equalsIgnoreCase(person.getFirstName())) {
            throw new ValidationException("demo exception to test validation-exception handler");
        }

        if("oops".equalsIgnoreCase(person.getFirstName())) {
            throw new UnsupportedOperationException("demo exception to test default-exception handler");
        }
    }
}
