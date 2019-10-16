package com.abnamro.examples.cloudcontracts;

import com.abnamro.examples.dao.HardCodedPersonDAO;
import com.abnamro.examples.jaxrs.MyApplication;
import com.abnamro.examples.jaxrs.interceptors.GZIPReaderInterceptor;
import com.abnamro.examples.utils.InMemoryLogger;
import com.abnamro.resteasy.InMemoryCdiRestServer;
import com.abnamro.resteasy.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.ws.rs.client.WebTarget;
import java.io.IOException;

/**
 * CDC: (see the pom.xml for additional documentation) This is a base test class that will be extended by all generated
 * classes derived from the contracts in the "/resources/contracts/person/wired" folder.
 *
 * It will run a wired app using RestEasy!!! Since you can only specify the test method once for the complete
 * mvn project/module - see the pom.xml documentation - we know that spring-cloud-contract will generate tests
 * that will run with a WebTarget and we need to make an instance available in this class!
 */
public abstract class PersonOptimizedBase {
    // use the rest-server that supports CDI
    private InMemoryCdiRestServer server;
    private RestClient restClient;

    // Using the JAX-RS test mode Spring Cloud Contract will generate integration test that use a WebTarget. Our
    // base class need to provide an instance.
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

        // this base class will be used by contracts on resources that will compress the returned result so we need
        // to add decompress functionality to this web-target instance
        webTarget = restClient.target().register(GZIPReaderInterceptor.class);

        // make sure each test starts with the initial DB state
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
}
