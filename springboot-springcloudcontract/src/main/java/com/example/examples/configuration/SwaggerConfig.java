package com.example.examples.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * This configuration class - and the two io.springfox imports in the pom - is all that is required to have generated
 * swagger API documentation.
 *
 * For more information on additional configuration options... ask the Google!
 */
@Configuration
@EnableSwagger2
@Import({BeanValidatorPluginsConfiguration.class})
public class SwaggerConfig {
    private static final String BASE_PACKAGE = "com.example.examples.rest.resources";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE)) // explicitly define which packages to include (exclude spring-boot resources like the actuator)
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "SpringBoot REST Examples",
                "REST API's used to demo SpringBoot functionality and solutions.",
                "API 1",
                "Terms of service",
                new Contact("Tacky Nerd", "www.cleanest-code.com", "tacky.nerd@cleanest-code.com"),
                "License of API", "API license URL", Collections.emptyList());
    }
}
