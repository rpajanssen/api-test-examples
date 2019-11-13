package com.abnamro.examples.jaxrs.resources;

import com.abnamro.examples.domain.api.ErrorResponse;
import com.abnamro.examples.domain.api.Person;
import com.abnamro.examples.domain.api.SafeList;
import com.abnamro.examples.jaxrs.filters.AddCustomHeaderResponseFilter;
import com.abnamro.examples.utils.InMemoryLogger;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class DefaultPersonResourceTest {
    public static final String BASE_API = "/person";

    @BeforeAll
    static void initialSetup() {
        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void setup() {
        InMemoryLogger.reset();
    }

    @Test
    public void shouldReturnAllPersons() {
        SafeList<Person> persons = given().when().get(BASE_API + "/all")
                .then().statusCode(HttpStatus.SC_OK).extract().as(new TypeRef<SafeList<Person>>() { });

        assertNotNull(persons.getItems());
        assertEquals(3, persons.getItems().size());

        assertIterableEquals(
                Arrays.asList(
                        new Person(1L, "Jan", "Janssen"),
                        new Person(2L, "Pieter", "Pietersen"),
                        new Person(3L, "Erik", "Eriksen")
                ),
                persons.getItems()
        );

        assertEquals(2, InMemoryLogger.getLogStatements().size());
    }

    @Test
    public void shouldAddAPerson() {
        Person person = given().when().contentType(ContentType.JSON).body(new Person(1001L, "Katy", "Perry"))
                .post(BASE_API)
                .then().statusCode(HttpStatus.SC_CREATED).extract().as(Person.class);

        assertNotNull(person);
        assertEquals(new Person(1001L, "Katy", "Perry"), person);

        assertEquals(2, InMemoryLogger.getLogStatements().size());
    }

    @Test
    public void shouldUpdateAPerson() {
        given().when().contentType(ContentType.JSON).body(new Person(3L, "Erik", "Erikson")).put(BASE_API)
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        assertEquals(2, InMemoryLogger.getLogStatements().size());
    }

    @Test
    public void shouldNotUpdateIfPersonDoesNotExist() {
        ErrorResponse errorResponse = given().when().contentType(ContentType.JSON)
                .body(new Person(25L, "Johnie", "Hacker"))
                .put(BASE_API).then().statusCode(HttpStatus.SC_NOT_FOUND).extract().as(ErrorResponse.class)
                ;

        assertNotNull(errorResponse);
        assertEquals("NOT_FOUND", errorResponse.getCode());
        assertEquals("person does not exist", errorResponse.getMessage().trim());

        assertEquals(1, InMemoryLogger.getLogStatements().size());
    }

    @Test
    public void shouldNotUpdateIfPersonIsInvalid() {
        ErrorResponse errorResponse = given().when().contentType(ContentType.JSON)
                .body(new Person(3L, null, "Erikson"))
                .put(BASE_API).then().statusCode(HttpStatus.SC_BAD_REQUEST).extract().as(ErrorResponse.class)
                ;

        assertNotNull(errorResponse);
        assertEquals("BAD_REQUEST", errorResponse.getCode());
        assertEquals("update.person.firstName firstName is not allowed to be empty", errorResponse.getMessage().trim());

        // todo : set low prio on interceptor
        // note: with quarkus the CDI method interceptor is called before the validator kicks in
        //       using kumuluzee or resteasy the validator is triggerd before the interceptor
        assertEquals(1, InMemoryLogger.getLogStatements().size());
    }

    @Test
    public void shouldDeleteAPerson() {
        given().when().delete(BASE_API + "/2").then().assertThat().statusCode(HttpStatus.SC_NO_CONTENT);

        assertEquals(2, InMemoryLogger.getLogStatements().size());
    }

    @Test
    public void shouldRejectPersistPersonRequestBecauseRequestIsToLarge() {
        int statusCode = given().when().contentType(ContentType.JSON).body(new Person(5L, "Despicable", StringUtils.repeat("Ooops", 250)))
                .post(BASE_API).andReturn().getStatusCode();

        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);

        assertEquals(0, InMemoryLogger.getLogStatements().size());
    }

    @Test
    public void shouldReturnCustomHeaderForGet() {
        Response response = given().when().contentType(ContentType.JSON)
                .get(BASE_API + "/lastName/Janssen").andReturn();

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, response.getHeader(AddCustomHeaderResponseFilter.CUSTOM_HEADER));
    }

    @Test
    public void shouldReturnCustomHeaderEvenOnBadRequest() {
        Response response = given().when().contentType(ContentType.JSON).body(new Person(5L, "Despicable", StringUtils.repeat("Ooops", 250)))
                .post(BASE_API).andReturn();

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCode());
        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, response.getHeader(AddCustomHeaderResponseFilter.CUSTOM_HEADER));
    }

    @Test
    public void shouldReturnCustomHeaderForPost() {
        Response response = given().when().contentType(ContentType.JSON).body(new Person(5L, "Despicable", "Me"))
                .post(BASE_API).andReturn();

        assertEquals(HttpStatus.SC_CREATED, response.getStatusCode());
        assertEquals(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE, response.getHeader(AddCustomHeaderResponseFilter.CUSTOM_HEADER));
    }

    @Test
    public void shouldTriggerInterceptorToReplaceUnacceptableLastName() {
        Person person = given().when().contentType(ContentType.JSON).body(new Person(6L, "John", "Asshole")).post(BASE_API).as(Person.class);

        assertNotNull(person);
        assertEquals(new Person(6l, "John", "A***e"), person);
    }

    @Test
    public void shouldNotTriggerInterceptorToReplaceUnacceptableLastNameIfResourceIsNotBoundToInterceptor() {
        Person person = given().when().contentType(ContentType.JSON).body(new Person(1L, "John", "Asshole")).put(BASE_API).as(Person.class);

        assertNotNull(person);
        assertEquals(new Person(1l, "John", "Asshole"), person);
    }
}
