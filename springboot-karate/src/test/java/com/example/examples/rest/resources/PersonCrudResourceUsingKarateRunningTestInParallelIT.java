package com.example.examples.rest.resources;

import com.example.examples.util.ApplicationStarter;
import com.intuit.karate.KarateOptions;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.util.AssertionErrors.assertTrue;


/**
 * Runner for the parallel Karate features.
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
 * NOTE: since this example will run the scenarios in parallel, we can use the integration with cucumber-reports
 * to create 'nicer' reports. Running the scenarios in parallel json output will be generated that can serve as input
 * for the cucumber reports. It is unclear why no json reports will be generated running the tests sequentially.
 *
 * NOTE: the startup/shutdown will be executed regardless of an @Ignore annotation on this class so we need to implement
 * a validation ourselves and stop execution of these methods if we have ignored this class.
 *
 * NOTE: we are using relative path in the feature files but these have to be OS independent. So we define these path
 * during startup of the tests dynamically and make them available in the Karate environment for use in the feature
 * files.
 *
 * NOTE: Default this integration test is disabled because it interferes with the other integration test(s).
 */
@Disabled
@KarateOptions
class PersonCrudResourceUsingKarateRunningTestInParallelIT {
    private static final int maxConcurrentTest = 5;
    private static final String supportFolderPath = ".." + File.separator + "support" + File.separator;
    private static final String featureFolderPath = "classpath:karate" + File.separator + "person" + File.separator + "features";
    private static final String reportFolderPath = "target" + File.separator + "surefire-reports";

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

    private static boolean isIgnored() {
        return PersonCrudResourceUsingKarateRunningTestInParallelIT.class.isAnnotationPresent(Disabled.class);
    }

    @Test
    void testParallel() {
        System.setProperty("karate.env", "local");

        // only run the parallel tests that are not ignored
        Results results = Runner.parallel(
                Arrays.asList("@nonsequential", "~@ignore"), Collections.singletonList(featureFolderPath), maxConcurrentTest, reportFolderPath
        );

        generateReport(results.getReportDir());
        assertTrue(results.getErrorMessages(), results.getFailCount() == 0);
    }

    private static void generateReport(String reportDir) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(reportDir), new String[] {"json"}, true);
        List<String> jsonPaths = jsonFiles.stream().map(file -> file.getAbsolutePath()).collect(Collectors.toList());

        Configuration config = new Configuration(new File("target"), "Person Demo Application");
        config.addClassifications("Environment", System.getProperty("karate.env"));

        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }
}
