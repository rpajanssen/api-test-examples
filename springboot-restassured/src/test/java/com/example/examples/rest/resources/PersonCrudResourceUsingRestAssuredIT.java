package com.example.examples.rest.resources;

import com.example.examples.AvailableProfiles;
import com.example.examples.domain.api.ErrorResponse;
import com.example.examples.domain.api.Person;
import com.example.examples.domain.api.SafeList;
import com.example.examples.exceptions.ErrorCodes;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.spring.api.DBRider;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

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
 *
 * Note how we use a regular expression in the expected result set after an insert, because we cannot know for sure the
 * generated id!
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DBRider
@ActiveProfiles(AvailableProfiles.LOCAL)
class PersonCrudResourceUsingRestAssuredIT {
    private static final String BASE_URL = "http://localhost";
    private static final String BASE_API = "/api/person";

    @LocalServerPort
    private int port;

    @BeforeAll
    static void initialSetup() {
        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = port;
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    void shouldReturnAllPersons() {
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

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("expected_persons_after_insert.yml")
    void shouldAddAPerson() {
        Person person = given().when().contentType(ContentType.JSON).body(new Person(null, "Katy", "Perry")).post(BASE_API).as(Person.class);

        assertThat(person).isNotNull();
        assertThat(person).isEqualTo(new Person(1001L, "Katy", "Perry"));
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("expected_persons_after_update.yml")
    void shouldUpdateAPerson() {
        given().when().contentType(ContentType.JSON).body(new Person(3L, "Erik", "Erikson")).put(BASE_API)
                .then().assertThat().statusCode(HttpStatus.OK.value());
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("persons.yml")
    void shouldNotUpdateIfPersonDoesNotExist() {
        ErrorResponse<Person> errorResponse = given().when().contentType(ContentType.JSON)
                .body(new Person(25L, "Johnie", "Hacker"))
                .put(BASE_API).as(new TypeRef<ErrorResponse<Person>>() { })
                ;

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getCode()).isEqualTo(ErrorCodes.PERSON_NOT_FOUND.getCode());
        assertThat(errorResponse.getData()).isEqualTo(new Person(25L, "Johnie", "Hacker"));
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("persons.yml")
    void shouldNotUpdateIfPersonIsInvalid() {
        ErrorResponse<Person> errorResponse = given().when().contentType(ContentType.JSON)
                .body(new Person(3L, null, "Erikson"))
                .put(BASE_API).as(new TypeRef<ErrorResponse<Person>>() { })
                ;

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getCode()).isEqualTo(ErrorCodes.PERSON_INVALID.getCode());
        assertThat(errorResponse.getData()).isEqualTo(new Person(3L, null, "Erikson"));
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("expected_persons_after_delete.yml")
    void shouldDeleteAPerson() {
        given().when().contentType(ContentType.JSON)
                .delete(BASE_API + "/2").then().assertThat().statusCode(HttpStatus.NO_CONTENT.value());
    }
}
