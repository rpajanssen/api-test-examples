package com.abnamro.examples.jaxrs.resources;

import com.abnamro.examples.aspects.EnableTracing;
import com.abnamro.examples.dao.PersonDAO;
import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.dao.exceptions.PersonAlreadyExistsException;
import com.abnamro.examples.dao.exceptions.PersonDoesNotExistException;
import com.abnamro.examples.domain.api.Person;
import com.abnamro.examples.domain.api.SafeList;
import com.abnamro.examples.jaxrs.annotations.Status;
import com.abnamro.examples.jaxrs.bindings.BlackListLastNames;
import com.abnamro.examples.jaxrs.bindings.CompressData;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * JAX-RS resource used to demo the testability of such a resource. The JAX-RS API is defined in the PersonResource
 * interface. All JAX-RS annotations can be found inside that interface. The CDI annotations for interceptors are
 * present in this implementation on the methods they are valid for. They WON'T work when you use them on the
 * interface method declarations.
 *
 * Note that JAX-RS has its own filter/interceptor mechanism and that you should use that mechanism! Use the filters
 * to decorate the incoming/outgoing response properties, use the interceptors to decorate the response body.
 *
 * We implemented custom exception-handlers (-mappers) that will catch any exception thrown during the processing of the
 * rest api calls and translate every exception into an error response (see the handlers for more explanation).
 *
 * We added the usage of the Tracer using the CDI interceptor and will demonstrate that we can test JAX-RS interceptors
 * as well as CDI method interceptors with RestEasy.
 */
@Path("/person")
public class DefaultPersonResource {

    private PersonDAO<Person> personDAO;

    @Inject
    public DefaultPersonResource(final PersonDAO<Person> personDAO) {
        this.personDAO = personDAO;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/isAlive")
    public Response healthCheck() {
        return Response.status(200).entity("OK").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @EnableTracing
    public Person findById(@PathParam("id") long id) throws DataAccessException {
        return personDAO.findById(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/lastName/{lastName}")
    @EnableTracing
    public SafeList<Person> findPersonsByLastName(@PathParam("lastName") String lastName) throws DataAccessException {
        return new SafeList<>(personDAO.findWithLastName(lastName));
    }

    /**
     * Example resource that uses the tracer and is bound to an interceptor that compresses the returned response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    @CompressData
    @EnableTracing
    public SafeList<Person> findAllPersons() throws DataAccessException {
        return new SafeList<>(personDAO.findAll());
    }

    /**
     * Example resource that uses the tracer and is bound to an interceptor that cleans up last names.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    @BlackListLastNames
    @Status(Response.Status.CREATED)
    @EnableTracing
    public Person add(@Valid Person person) throws DataAccessException, PersonAlreadyExistsException {
        return personDAO.add(person);
    }

    /**
     * Example resource that uses the tracer and is NOT bound to an interceptor that cleans up last names.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @EnableTracing
    public Person update(@Valid Person person) throws DataAccessException, PersonDoesNotExistException {
        // should be some persistence code here

        onSpecificTestInputThrowASpecificException(person);

        personDAO.update(person);

        return person;
    }

    /**
     * Example resource that uses the tracer and is NOT bound to an interceptor that cleans up last names.
     */
    @DELETE
    @Status(Response.Status.NO_CONTENT)
    @Path("/{personId}")
    @EnableTracing
    public void delete(@PathParam("personId") long personId) throws DataAccessException {
        personDAO.delete(personId);
    }

    /**
     * Implements failure scenarios for testing purposes that is the same for all person-resources.
     */
    private void onSpecificTestInputThrowASpecificException(Person person) {
        if("ohoh".equalsIgnoreCase(person.getFirstName())) {
            throw new ValidationException("demo exception to test validation-exception handler");
        }

        if("oops".equalsIgnoreCase(person.getFirstName())) {
            throw new UnsupportedOperationException("demo exception to test default-exception handler");
        }
    }
}
