package com.abnamro.examples.cloudcontracts;

import com.abnamro.examples.dao.HardCodedPersonDAO;
import com.abnamro.examples.jaxrs.MyApplication;
import com.abnamro.examples.utils.InMemoryLogger;
import com.abnamro.resteasy.InMemoryCdiRestServer;
import com.abnamro.resteasy.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.ws.rs.client.WebTarget;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CDC: (see the pom.xml for additional documentation) This is a base test class that will be extended by all generated
 * classes derived from the contracts in the "/resources/contracts/person/wired" folder.
 *
 * It will run a wired app using RestEasy!!! Since you can only specify the test method once for the complete
 * mvn project/module - see the pom.xml documentation - we know that spring-cloud-contract will generate tests
 * that will run with a WebTarget and we need to make an instance available in this class!
 */
public abstract class PersonBase {
    // use the rest-server that supports CDI
    private InMemoryCdiRestServer server;
    private RestClient restClient;

    // todo document : https://cloud.spring.io/spring-cloud-contract/reference/html/project-features.html#features-jax-rs
    protected WebTarget webTarget;

    /**
     * The before-each is preferred to the before-all because this way we can reset the state in between tests and we
     * can insure we will not experience test interference. This is especially useful when you global state, like we
     * have with the logger that us used by the tracer (a CDI method interceptor). It will most likely be a bit slower
     * because we will re-instantiate and rewire the resource under test for each unit test.
     */
    @BeforeEach
    void setup() throws IOException {
        server = InMemoryCdiRestServer.instance(MyApplication.class);
        restClient = new RestClient(server.getHost(), server.getPort());

        webTarget = restClient.target();

        // todo : comment
        HardCodedPersonDAO.reset();
    }

    /**
     * See comment at the @BeforeEach.
     *
     * We need to reset the global state of the logger after each test. We will use a hardcoded-dao implementation but
     * it has no static data and will be instantiated again for each unit test, and thus be reset. If the hardcoded-dao
     * used static data like the FakeLogger then we also would have to reset it after each test.
     */
    @AfterEach
    void teardown() throws IOException {
        restClient.close();
        server.close();

        InMemoryLogger.reset();
    }


    // todo : document - used in contracts because I was to dumb to get the regex working
    public void contains(String text, String value) {
        assertThat(text).contains(value);
    }
}
