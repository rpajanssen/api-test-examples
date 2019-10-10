package com.example.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The root of our spring-boot application.
 */
@SpringBootApplication
public class FantasticSpringbootApplication {
    private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(FantasticSpringbootApplication.class, args);
	}

	public static boolean isRunning() {
	    return context != null && context.isRunning();
    }

	public static ApplicationContext getContext() {
	    return context;
    }
}
