package com.example.examples.cloudcontracts;

import com.example.examples.dao.PersonDAO;
import com.example.examples.dao.exceptions.PersonAlreadyExistsException;
import com.example.examples.dao.exceptions.PersonNotFoundException;
import com.example.examples.domain.api.Person;
import com.example.examples.rest.exceptionhandlers.PersonResourceExceptionHandler;
import com.example.examples.rest.resources.PersonCrudResource;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * CDC: (see the pom.xml for additional documentation) This is a base test class that will be extended by all generated
 * classes derived from the contracts in the "/resources/contracts/person/mocked" folder.
 *
 * It will run an app with only the resource we want to test being deployed, by using the WebMvcTest annotation.
 * It will mock the dependencies and define the mocking behavior. It will not run a http server, but still test your
 * actual calls using MockMvc.
 */
@SuppressWarnings("unused")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PersonCrudResource.class)
public abstract class PersonMockedBase {

    @MockBean
    private PersonDAO<Person> personDao;

    @Autowired
    private PersonCrudResource resource;

    @Autowired
    private PersonResourceExceptionHandler exceptionHandler;

    @BeforeEach
    public void setup() throws PersonAlreadyExistsException, PersonNotFoundException {
        // Using the MOCK test mode Spring Cloud Contract will generate integration test that use a MockMvc and
        // RestAssured. Our base class needs to setup this environment.

        // deploy the resource and global exception handler
        RestAssuredMockMvc.standaloneSetup(resource, exceptionHandler);

        defineMockingBehavior();
    }

    /**
     * This base test-class is used for ALL person contracts so we need to define ALL required mocking behavior. There
     * is a trade off here. If this method becomes to complex we need to split this base test-class into a generic
     * base class with an abstract defineMockingBehavior method and multiple base test-classes extending that base class
     * with only the implementation of the defineMockingBehavior method defining the the behavior for just one
     * contract that will be used by spring-cloud-contract when generating the test classes.
     */
    private void defineMockingBehavior() throws PersonAlreadyExistsException, PersonNotFoundException {
        // get all persons
        when(personDao.findAll()).thenReturn(Arrays.asList(testPersons()));

        // add a person
        when(personDao.add(new Person(null, "Katy", "Perry"))).thenReturn(new Person(1001L, "Katy", "Perry"));

        // update a person
        // ... nothing required

        // update a non existing person
        when(personDao.existsById(25L)).thenReturn(false);
        Person toBeUpdated = new Person(25L, "Johnie", "Hacker");
        doThrow(new PersonNotFoundException(toBeUpdated, "person does not exist")).when(personDao).update(eq(toBeUpdated));

        // create already existing person
        Person toBeCreated = new Person(1L, "Jan-Klaas", "Janssen");
        when(personDao.add(eq(toBeCreated))).thenThrow(new PersonAlreadyExistsException(toBeCreated, "person already exists"));
    }

    private Person[] testPersons() {
        return new Person[] {
                new Person(1L, "Jan", "Janssen"),
                new Person(2L, "Pieter", "Pietersen"),
                new Person(3L, "Erik", "Eriksen")
        };
    }

    @AfterEach
    public void cleanup() {
        RestAssuredMockMvc.reset();
        Mockito.reset(personDao);
    }

//    /**
//     * Since the developer on duty was to dumb to figure out how to get the right regular expression to work in the
//     * Groovy contract, he wrote this method to perform the operation he wanted.
//     *
//     * Public methods in extended base test classes will be available for use in the Groovy contracts!
//     */
//    public void contains(String text, String value) {
//        assertThat(text).contains(value);
//    }
}
