package tests;

import com.abnamro.examples.domain.api.ErrorResponse;
import com.abnamro.examples.domain.api.Person;
import com.abnamro.examples.domain.api.SafeList;
import com.abnamro.examples.jaxrs.filters.AddCustomHeaderResponseFilter;
import com.abnamro.examples.jaxrs.resources.DefaultPersonResource;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Arquillian test of the KumuluzEE application.
 *
 * We can't inject components like the logger or dao in this test class for verifications since they exist in a
 * different JVM! So we had to extend the REST API exposing the logger so we could verify the CDI method interceptor
 * actually worked.
 */
@RunWith(Arquillian.class)
public class DefaultPersonResourceIT {
    private static final String BASE_API = "/api/person";
    private static final String BASE_API_LOG = "/api/log";

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, "com.abnamro.examples")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                ;
    }

    @Inject
    private DefaultPersonResource underTest;

    @BeforeClass
    public static void initialSetup() {
        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @After
    public void cleanup() {
        // reset logger by calling the API
        given().when().put(BASE_API_LOG).then().statusCode(javax.ws.rs.core.Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @InSequence(1)
    public void verifyWiring() {
        Assert.assertNotNull(underTest);
    }

    /**
     * Note that because the GZIP interceptor added the content-encoding header with the value 'gzip', rest-assured
     * automatically unzips the response before it tries to de-serialize it. We do not have to do anything specific
     * in this test to make it work.
     */
    @Test
    @RunAsClient
    @InSequence(2)
    public void shouldReturnAllPersons() {
        SafeList<Person> persons = given().when().get(BASE_API + "/all")
                .then().statusCode(200).extract().as(new TypeRef<>() { });

        assertThat(persons.getItems()).isNotNull();
        assertThat(persons.getItems().toArray())
                .hasSize(3)
                .contains(
                        new Person(1L, "Jan", "Janssen"),
                        new Person(2L, "Pieter", "Pietersen"),
                        new Person(3L, "Erik", "Eriksen")
                );

        SafeList<String> logEntries = given().when().get(BASE_API_LOG)
                .then().statusCode(200).extract().as(new TypeRef<>() { });
        assertThat(logEntries.getItems().size()).isEqualTo(2);
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void shouldAddAPerson() {
        Person person = given().when().contentType(ContentType.JSON).body(new Person(1001L, "Katy", "Perry")).post(BASE_API).as(Person.class);

        assertThat(person).isNotNull();
        assertThat(person).isEqualTo(new Person(1001L, "Katy", "Perry"));

        SafeList<String> logEntries = given().when().get(BASE_API_LOG)
                .then().statusCode(200).extract().as(new TypeRef<>() { });
        assertThat(logEntries.getItems().size()).isEqualTo(2);
    }

    @Test
    @RunAsClient
    @InSequence(4)
    public void shouldUpdateAPerson() {
        given().when().contentType(ContentType.JSON).body(new Person(3L, "Erik", "Erikson")).put(BASE_API)
                .then().assertThat().statusCode(200);

        SafeList<String> logEntries = given().when().get(BASE_API_LOG)
                .then().statusCode(200).extract().as(new TypeRef<>() { });
        assertThat(logEntries.getItems().size()).isEqualTo(2);
    }

    @Test
    @RunAsClient
    @InSequence(5)
    public void shouldNotUpdateIfPersonDoesNotExist() {
        ErrorResponse errorResponse = given().when().contentType(ContentType.JSON)
                .body(new Person(25L, "Johnie", "Hacker"))
                .put(BASE_API).as(ErrorResponse.class)
                ;

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getCode()).isEqualTo("NOT_FOUND");
        assertThat(errorResponse.getMessage()).isEqualTo("person does not exist");

        // in case of an exception the second trace-point int the Tracer is never reached
        SafeList<String> logEntries = given().when().get(BASE_API_LOG)
                .then().statusCode(200).extract().as(new TypeRef<>() { });
        assertThat(logEntries.getItems().size()).isEqualTo(1);
    }

    @Test
    @RunAsClient
    @InSequence(6)
    public void shouldNotUpdateIfPersonIsInvalid() {
        ErrorResponse errorResponse = given().when().contentType(ContentType.JSON)
                .body(new Person(3L, null, "Erikson"))
                .put(BASE_API).as(ErrorResponse.class)
                ;

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getCode()).isEqualTo("BAD_REQUEST");
        assertThat(errorResponse.getMessage()).contains("update.arg0.firstName firstName is not allowed to be empty");

        // we don't even enter the method so tracer will record nothing
        SafeList<String> logEntries = given().when().get(BASE_API_LOG)
                .then().statusCode(200).extract().as(new TypeRef<>() { });
        assertThat(logEntries.getItems().size()).isEqualTo(0);
    }

    @Test
    @RunAsClient
    @InSequence(7)
    public void shouldDeleteAPerson() {
        given().when().delete(BASE_API + "/2").then().assertThat().statusCode(204);

        SafeList<String> logEntries = given().when().get(BASE_API_LOG)
                .then().statusCode(200).extract().as(new TypeRef<>() { });
        assertThat(logEntries.getItems().size()).isEqualTo(2);
    }


    @Test
    @RunAsClient
    @InSequence(8)
    public void shouldRejectPersistPersonRequestBecauseRequestIsToLarge() {
        int statusCode = given().when().contentType(ContentType.JSON).body(new Person(5L, "Despicable", StringUtils.repeat("Ooops", 250)))
                .post(BASE_API).andReturn().getStatusCode();

        assertThat(statusCode).isEqualTo(400);

        // we don't even enter the method so tracer will record nothing
        SafeList<String> logEntries = given().when().get(BASE_API_LOG)
                .then().statusCode(200).extract().as(new TypeRef<>() { });
        assertThat(logEntries.getItems().size()).isEqualTo(0);
    }

    @Test
    @RunAsClient
    @InSequence(9)
    public void shouldReturnCustomHeaderForGet() {
        Response response = given().when().contentType(ContentType.JSON)
                .get(BASE_API + "/lastName/Janssen").andReturn();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getHeader(AddCustomHeaderResponseFilter.CUSTOM_HEADER)).isEqualTo(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE);
    }

    @Test
    @RunAsClient
    @InSequence(10)
    public void shouldReturnCustomHeaderEvenOnBadRequest() {
        Response response = given().when().contentType(ContentType.JSON).body(new Person(5L, "Despicable", StringUtils.repeat("Ooops", 250)))
                .post(BASE_API).andReturn();

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.getHeader(AddCustomHeaderResponseFilter.CUSTOM_HEADER)).isEqualTo(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE);
    }


    @Test
    @RunAsClient
    @InSequence(11)
    public void shouldReturnCustomHeaderForPost() {
        Response response = given().when().contentType(ContentType.JSON).body(new Person(5L, "Despicable", "Me"))
                .post(BASE_API).andReturn();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getHeader(AddCustomHeaderResponseFilter.CUSTOM_HEADER)).isEqualTo(AddCustomHeaderResponseFilter.CUSTOM_HEADER_VALUE);
    }


    @Test
    @RunAsClient
    @InSequence(12)
    public void shouldTriggerInterceptorToReplaceUnacceptableLastName() {
        Person person = given().when().contentType(ContentType.JSON).body(new Person(5L, "John", "Asshole")).post(BASE_API).as(Person.class);

        assertThat(person).isNotNull();
        assertThat(person).isEqualTo(new Person(5l, "John", "A***e"));
    }


    @Test
    @RunAsClient
    @InSequence(13)
    public void shouldNotTriggerInterceptorToReplaceUnacceptableLastNameIfResourceIsNotBoundToInterceptor() {
        Person person = given().when().contentType(ContentType.JSON).body(new Person(1L, "John", "Asshole")).put(BASE_API).as(Person.class);

        assertThat(person).isNotNull();
        assertThat(person).isEqualTo(new Person(1l, "John", "Asshole"));
    }
}
