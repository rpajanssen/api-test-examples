# What?

This example project will help you write integration tests for JAX-RS REST resources using RestEasy

Like with JerseyTest with [RestEasy](https://resteasy.github.io/) the unit test will create an in-memory http-server and from your unit test
you deploy the resource(s) you want to test on that server. Then the unit tests will be executed and those
unit test will actually call your REST API in your resource. These test run pretty fast so most of you won't
mind running them as a unit instead of integration test.

# Requirements

* maven 3.6.1 (or higher)
* java 11

# Tl;DR

I just wanna run it: `mvn clean test`

# How to add RestEasy to your JAX-RS application?

Assuming you have all dependencies for your JAX-RS application in place and of course you have
your Junit5 dependencies for your unit tests in your pom.xml.

* Add the required RestEasy dependencies to your pom.xml:
  * servlet implementation
  * http server
  * dependency injection engine
  * (de)serialization
  * bean validation 
* Implement your test class:
  * setup a rest server (configure the resources and dependencies under test)
  * setup a rest client
  * implement your tests using using the rest client

We implemented some basic test examples with a _simple_ setup, to show you a mocked example and
a wired example. 

And we abstracted away some boilerplate setup code and used that approach with similar mocked 
and wired examples.

Note also how we handled the integration test for the one resource that compresses the returned result.

# Pros

* fast execution
* simple to program and apply
* CDI support (not like JerseyTest)
* Support for JUnit5
* Reactive Support
* EJB, Seam, Guice, Spring, Spring MVC and Spring Boot integration

# Cons

* bean validation does NOT throw javax.validation.ValidationException so this will not be 
  picked up our custom handler handler but by some default handler
* not a production like application container

# Useful links

https://resteasy.github.io/
https://www.baeldung.com/integration-testing-a-rest-api

