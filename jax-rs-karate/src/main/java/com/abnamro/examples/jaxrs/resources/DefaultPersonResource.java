package com.abnamro.examples.jaxrs.resources;

import com.abnamro.examples.aspects.EnableTracing;
import com.abnamro.examples.dao.PersonDAO;
import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.dao.exceptions.PersonAlreadyExistsException;
import com.abnamro.examples.dao.exceptions.PersonDoesNotExistException;
import com.abnamro.examples.domain.api.Person;
import com.abnamro.examples.domain.api.SafeList;

import javax.inject.Inject;
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
public class DefaultPersonResource implements PersonResource<Person> {

    private PersonDAO<Person> personDAO;

    public DefaultPersonResource() {
        // required by arquillian test to enable proxy creation
    }

    @Inject
    public DefaultPersonResource(final PersonDAO<Person> personDAO) {
        this.personDAO = personDAO;
    }

    @Override
    public Response healthCheck() {
        return Response.status(200).entity("OK").build();
    }

    @Override
    @EnableTracing
    public Person findById(long id) throws DataAccessException {
        return personDAO.findById(id);
    }

    @Override
    @EnableTracing
    public SafeList<Person> findPersonsByLastName(String lastName) throws DataAccessException {
        return new SafeList<>(personDAO.findWithLastName(lastName));
    }

    /**
     * Example resource that uses the tracer and is bound to an interceptor that compresses the returned response.
     */
    @Override
    @EnableTracing
    public SafeList<Person> findAllPersons() throws DataAccessException {
        return new SafeList<>(personDAO.findAll());
    }

    /**
     * Example resource that uses the tracer and is bound to an interceptor that cleans up last names.
     */
    @Override
    @EnableTracing
    public Person add(Person person) throws DataAccessException, PersonAlreadyExistsException {
        return personDAO.add(person);
    }

    /**
     * Example resource that uses the tracer and is NOT bound to an interceptor that cleans up last names.
     */
    @Override
    @EnableTracing
    public Person update(Person person) throws DataAccessException, PersonDoesNotExistException {
        // should be some persistence code here

        onSpecificTestInputThrowASpecificException(person);

        personDAO.update(person);

        return person;
    }

    /**
     * Example resource that uses the tracer and is NOT bound to an interceptor that cleans up last names.
     */
    @Override
    @EnableTracing
    public void delete(long personId) throws DataAccessException {
        personDAO.delete(personId);
    }
}
