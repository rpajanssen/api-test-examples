package com.example.examples.rest.resources;

import com.example.examples.util.ApplicationStarter;
import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

import java.io.File;


/**
 * Runner for the Karate features. it will:
 * - start up the application
 * - set up the karate environment
 * - it will run all features
 *
 * Since the philosophy of Karate is to have NO dependency on any Java code, we need to keep this class as empty as
 * possible. Normally you would only specify some Karate options (like which features should be run by this runner)
 * and nothing more. You then need the application to run - somewhere - and then you run the features against that
 * instance of your application.
 *
 * But... we have a Spring Boot application and want to start it automatically before the tests are run against it.
 * Luckily the @BeforeClass and @AfterClass annotations are still available to us (the @Before/@After will not be
 * triggered by the Karate runner!). So in the @BeforeClass annotated class we will start our Spring Boot application
 * and in the @AfterClass we will shut it down.
 *
 * We implemented a static utility class to implement the start/stop logic. We also need the spring application-
 * context because we want access to the application environment. As example it has the server-port number and
 * we need to pass that to Karate so it know on which port the application runs. We do this by setting a system
 * property. The karate-config.js configuration file we use this system property to initialize the feature
 * environment.
 *
 * In the feature files we will show you the Karate-way of implementing a before/after action.
 *
 * NOTE: the startup/shutdown will be executed regardless of an @Ignore annotation on this class so we need to implement
 * a validation ourselves and stop execution of these methods if we have ignored this class.
 *
 * NOTE: we are using relative path in the feature files but these have to be OS independent. So we define these path
 * during startup of the tests dynamically and make them available in the Karate environment for use in the feature
 * files.
 */
@SuppressWarnings("Duplicates")
class PersonCrudResourceUsingKarateIT {
    private static final String supportFolderPath = ".." + File.separator + "support" + File.separator;
    private static final String featureFolderPath = "classpath:karate" + File.separator + "person" + File.separator + "features";

    @BeforeAll
    static void startup() {
        if(isIgnored()) {
            return;
        }

        ApplicationStarter.start("karate");

        // fetch the application port and set a system property we can use in the karate-config.js
        String baseUrl = "http://localhost:" + ApplicationStarter.getPort();
        System.setProperty("baseUrl", baseUrl);

        System.setProperty("supportFolderPath", supportFolderPath);
    }

    @AfterAll
    static void shutdown() {
        if(isIgnored()) {
            return;
        }

        ApplicationStarter.stop();
    }

    /**
     * Verifies if this test should be ignored.
     */
    private static boolean isIgnored() {
        return PersonCrudResourceUsingKarateIT.class.isAnnotationPresent(Disabled.class);
    }

    /**
     * Only run the sequential tests that are not ignored.
     */
    @Karate.Test
    Karate runAllPersonFeatures() {
        return new Karate()
                .feature(featureFolderPath)
                .tags("@sequential", "~@ignore")
                ;
    }
}
