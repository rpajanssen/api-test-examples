package com.example.examples.cloudcontracts;

import com.example.examples.AvailableProfiles;
import com.example.examples.FantasticSpringbootApplication;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.spring.api.DBRider;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CDC: (see the pom.xml for additional documentation) This is a base test class that will be extended by all generated
 * classes derived from the contracts in the "/resources/contracts/person/wired" folder.
 *
 * It will run a fully wired app!!! Since you can only specify the test method once for the complete mvn project/module -
 * see the pom.xml documentation - we know that spring-cloud-contract will generated tests that will run with mock-mvc!
 * To pass on the wired environment to the context the tests will be executed in we need to pass the application context
 * to the mocked environment using RestAssuredMockMvc!
 *
 * Note that normally when having wired tests you do not have to do this! You need to change the test method in the pom
 * to a wired test method!
 *
 * Note that we use DBRider to initialize the database. Since the tests are generated we cannot control the data-sets by
 * adding annotations to the test methods! We can only annotate this base class! We are limited to specify only these
 * data-sets that are applicable for all the generated tests! Alternatives to DBRider are to have a before and after
 * method that initialize/cleanup the database. Be aware that if you use the JPA repository you can't control the ID's
 * of the persisted objects. If you want to control these then you need to use plain JDBC to manage the database
 * content.
 */
@SuppressWarnings("unused")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FantasticSpringbootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(AvailableProfiles.LIVE)
@DBRider
@DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
public abstract class PersonWiredBase {
    private static final String BASE_URL = "http://localhost";

    @LocalServerPort
    private int port;

    @Autowired
    protected WebApplicationContext context;


//    private ConnectionHolder connectionHolder = () ->
//            EntityManagerProvider.instance("junit5-pu").clear().connection()

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = port;

        // CDC : pass the application context to the mocked environment the generated tests will use (see documentation
        // above and in the pom.xml.
        RestAssuredMockMvc.webAppContextSetup(context);


//        DataSetConfig dataSetConfig = new DataSetConfig("datasets/yml/users.yml");(1)
//        executor.createDataSet(dataSetConfig);(2)
    }

    @AfterEach
    public void cleanup() {
        RestAssuredMockMvc.reset();
    }

    // todo : document
    public void contains(String text, String value) {
        assertThat(text).contains(value);
    }
}
