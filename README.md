# What?

This example project will help you write integration tests for your REST resources.

The sub projects demonstrate REST API testing:
* JAX-RS with JerseyTest
* JAX-RS with RestEasy
* JAX-RS with SpringCloudContract
* SpringBoot with RestAssured
  * and with SpringBoot with MockMVC 
  * and with SpringBoot with RestAssuredMockMVC 
* SpringBoot with Karate
* SpringBoot with SpringCloudContract

The two SpringCloudContract examples will show you how you can generate your REST API integration tests!

The SpringBoot with RestAssured example(s) poses you a question... why even use RestAssured if you can use 
Springs own MockMVC?

# Requirements

* maven 3.6.1 (or higher)
* java 11 (or higher)

# TL;DR

Just run all tests: `mvn clean integration-test`
