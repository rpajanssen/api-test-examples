package com.example.examples.cloudcontracts;

import com.example.examples.dao.PersonDao;
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
    private PersonDao personDao;

    @Autowired
    private PersonCrudResource resource;

    @Autowired
    private PersonResourceExceptionHandler exceptionHandler;

    @BeforeEach
    public void setup() {
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
    private void defineMockingBehavior() {
        // get all persons
        when(personDao.findAll()).thenReturn(Arrays.asList(testPersons()));

        // add a person
        when(personDao.save(new Person(null, "Katy", "Perry"))).thenReturn(new Person(1001L, "Katy", "Perry"));

        // update a person
        when(personDao.existsById(3L)).thenReturn(true);
        Person target = new Person(3L, "Erik", "Erikson");
        when(personDao.save(eq(target))).thenReturn(new Person(3L, "Erik", "Erikson"));

        // update a non existing person
        when(personDao.existsById(25L)).thenReturn(false);
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
}
