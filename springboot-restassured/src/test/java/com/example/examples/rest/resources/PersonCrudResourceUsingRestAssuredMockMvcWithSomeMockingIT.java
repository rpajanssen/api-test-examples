package com.example.examples.rest.resources;

import com.example.examples.dao.PersonDAO;
import com.example.examples.dao.exceptions.PersonAlreadyExistsException;
import com.example.examples.dao.exceptions.PersonNotFoundException;
import com.example.examples.domain.api.ErrorResponse;
import com.example.examples.domain.api.Person;
import com.example.examples.domain.api.SafeList;
import com.example.examples.exceptions.ErrorCodes;
import com.example.examples.rest.exceptionhandlers.PersonResourceExceptionHandler;
import com.example.examples.rest.filters.ErrorHandlingFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * This unit integration test starts a full fledged application against which these tests are ran. The SpringBootTest
 * annotation tells Spring to start the app with the embedded application server. We have configured for the port to be
 * randomly selected. We can inject the selected port-number into this test by using the LocalServerPort annotation, this
 * way we will be able to configure RestAssured - which we will use to write the rest-api tests - so it can send the
 * http requests to the application.
 *
 * We will use DBRider to initiate the starting points for each test regarding the database content. By annotating each
 * test with DataSet we can define the content the database will have before the test is run. By using the ExpectedDataSet
 * annotation we can specify what should be the expected content when the test has finished.
 */
@ExtendWith(MockitoExtension.class)
class PersonCrudResourceUsingRestAssuredMockMvcWithSomeMockingIT {
    private static final String BASE_URL = "http://localhost";
    private static final String BASE_API = "/api/person";

    @Mock
    private PersonDAO<Person> personDao;

    @InjectMocks
    private PersonCrudResource underTest;

    @BeforeAll
    static void initialSetup() {
        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.standaloneSetup(
                underTest,
                new PersonResourceExceptionHandler(), new ErrorHandlingFilter(new ObjectMapper()), new ErrorHandlerResource()
        );
    }

    @AfterEach
    void cleanup() {
        Mockito.reset(personDao);
    }

    @Test
    void shouldReturnAllPersons() {
        when(personDao.findAll()).thenReturn(Arrays.asList(testPersons()));

        SafeList<Person> persons = given().when().get(BASE_API).as(new TypeRef<SafeList<Person>>() { });

        assertThat(persons.getItems()).isNotNull();
        assertThat(persons.getItems().toArray())
                .hasSize(3)
                .contains(
                        new Person(1L, "Jan", "Janssen"),
                        new Person(2L, "Pieter", "Pietersen"),
                        new Person(3L, "Erik", "Eriksen")
                );
    }

    private Person[] testPersons() {
        return new Person[] {
                new Person(1L, "Jan", "Janssen"),
                new Person(2L, "Pieter", "Pietersen"),
                new Person(3L, "Erik", "Eriksen")
        };
    }

    @Test
    void shouldAddAPerson() throws PersonAlreadyExistsException {
        when(personDao.add(any(Person.class))).thenReturn(new Person(1001L, "Katy", "Perry"));

        Person person =
                given().contentType(ContentType.JSON).body(new Person(null, "Katy", "Perry"))
                .when().post(BASE_API)
                .then().status(HttpStatus.CREATED).contentType(ContentType.JSON).extract().as(Person.class);

        assertThat(person).isNotNull();
        assertThat(person).isEqualTo(new Person(1001L, "Katy", "Perry"));
    }

    @Test
    void shouldUpdateAPerson() throws PersonAlreadyExistsException {
        when(personDao.existsById(3L)).thenReturn(true);
        when(personDao.add(any(Person.class))).thenReturn(new Person(3L, "Erik", "Erikson"));

        given().contentType(ContentType.JSON).body(new Person(3L, "Erik", "Erikson"))
                .when().put(BASE_API)
                .then().assertThat().statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotUpdateIfPersonDoesNotExist() throws PersonNotFoundException {
        Person person = new Person(25L, "Johnie", "Hacker");
        doThrow(new PersonNotFoundException(person, "person does not exist")).when(personDao).update(eq(person));

        ErrorResponse<Person> errorResponse =
                given().contentType(ContentType.JSON).body(person)
                        .when().put(BASE_API)
                        .then().status(HttpStatus.NOT_FOUND).contentType(ContentType.JSON).extract().as(new TypeRef<ErrorResponse<Person>>() { })
                ;

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getCode()).isEqualTo(ErrorCodes.PERSON_NOT_FOUND.getCode());
        assertThat(errorResponse.getData()).isEqualTo(new Person(25L, "Johnie", "Hacker"));
    }

    @Test
    void shouldNotUpdateIfPersonIsInvalid() {
        ErrorResponse<Person> errorResponse =
                given().contentType(ContentType.JSON).body(new Person(3L, null, "Erikson"))
                        .when().put(BASE_API)
                        .then().status(HttpStatus.BAD_REQUEST).contentType(ContentType.JSON).extract().as(new TypeRef<ErrorResponse<Person>>() { });

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getCode()).isEqualTo(ErrorCodes.PERSON_INVALID.getCode());
        assertThat(errorResponse.getData()).isEqualTo(new Person(3L, null, "Erikson"));
    }

    @Test
    void shouldDeleteAPerson() {
        given().when().delete(BASE_API + "/2").then().assertThat().statusCode(HttpStatus.NO_CONTENT.value());

        verify(personDao, times(1)).delete(eq(2L));
    }
}
