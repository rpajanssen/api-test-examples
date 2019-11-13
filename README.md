# What?

This example project will help you write (or generate) integration tests for your REST resources.

The sub projects demonstrate REST API testing:
* a JAX-RS application with JerseyTest
* a JAX-RS application with RestEasy
* a JAX-RS application with Karate
* a JAX-RS application with SpringCloudContract
* a MicroProfile JAX-RS application with KumuluzEE and RestAssured
* a MicroProfile JAX-RS application with Quarkus
* SpringBoot with RestAssured
  * and with MockMVC 
  * and with RestAssuredMockMVC 
* SpringBoot with Karate
* SpringBoot with SpringCloudContract

The two SpringCloudContract examples will show you how you can generate your REST API integration tests!

The SpringBoot with RestAssured example(s) poses you a question... why even use RestAssured if you can use 
Springs own MockMVC?

# Requirements

* maven 3.6.1 (or higher)
* java 11 (or higher)
  note: the Quarkus example needs Java 8 for native builds (you need to install the GraalVM first!)

# TL;DR

Just run all tests: `mvn clean integration-test`

