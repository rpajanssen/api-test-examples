package com.example.examples.util;

import com.example.examples.FantasticSpringbootApplication;
import org.springframework.boot.SpringApplication;

import java.util.Optional;

/**
 * Utility class we use from Karate unit test runners to start and stop our Spring Boot application.
 */
public final class ApplicationStarter {
    private final static String DEFAULT_SERVER_PORT = "8080";
    private final static String PROPERTY_SERVER_PORT = "local.server.port";

    private final static String ARGUMENT_START_WITH_RANDOM_PORT = "--server.port=0";
    private final static String ARGUMENT_ACTIVE_PROFILES_TEMPLATE = "--spring.profiles.active=%s";

    private ApplicationStarter() {
        throw new UnsupportedOperationException();
    }

    /**
     * Somehow... don't ask why/how... an application may already be running... in that situation restart it!
     */
    public static void start(String profile) {
        if(FantasticSpringbootApplication.isRunning()) {
            restart(profile);
        } else {
            FantasticSpringbootApplication.main(new String[] { ARGUMENT_START_WITH_RANDOM_PORT, String.format(ARGUMENT_ACTIVE_PROFILES_TEMPLATE, profile) });
        }
    }

    private static void restart(String profile) {
        stop();
        start(profile);
    }

    public static void stop() {
        SpringApplication.exit(FantasticSpringbootApplication.getContext(), () -> 0);
    }

    public static int getPort() {
        return Integer.parseInt(
                Optional.ofNullable(FantasticSpringbootApplication.getContext().getEnvironment().getProperty(PROPERTY_SERVER_PORT)).orElse(DEFAULT_SERVER_PORT)
        );
    }
}
