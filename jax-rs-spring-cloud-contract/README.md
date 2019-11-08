# What?

This example project will help you generate and run integration tests for JAX-RS REST resources using SpringCloudContract.

[Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract) is an umbrella project holding solutions that 
help users in successfully implementing the [Consumer Driven Contracts approach](https://martinfowler.com/articles/consumerDrivenContracts.html).

In short, consumer Driven Contract approach is nothing more than an agreement, to test integration points, 
between the consumer and provider about the format of data that they communicate between each other.

Currently Spring Cloud Contract consists of the Spring Cloud Contract Verifier project and it gives us
the following features:
* ensure that HTTP / Messaging stubs (used when developing the client) are doing exactly what actual server-side implementation will do,
* promote acceptance test driven development method and micro services architectural style,
* provide a way to publish changes in contracts that are immediately visible on both sides of the communication,
* generate _boilerplate_ test code used on the server side.

_(taken from spring.io page)_

This project will focus on the generation of the test code!

We will be using RestEasy as well as the JAX-RS implementation and for integration test support. 
Check out the jax-rs-rest-easy example project to learn more about RestEasy usage!

# Requirements

* maven 3.6.1 (or higher)
* java 11

# Tl;DR

I just wanna run it: `mvn clean integration-test`

Generated tests will be in the target folder and will be automatically run with the traditional
surefire reports as output.

# How to add Spring Cloud Contract to your JAX-RS project?

Assume you already added the required JAX-RS dependencies and the JUnit5 dependencies to your pom.

* Add spring cloud contract and rest-easy (the JAX-RS implementation we will use) dependencies in pom.xml.
* Configure appropriate springboot dependency management (__BE AWARE!__ The order of the dependencies is important!).
  Since Spring Boot projects normally depend on a Spring Boot parent pom, that won't be possible if you
  configure a different parent pom. You then have to add the Spring Boot BOM to the dependency management!
* Add and configure spring-cloud-contract-maven-plugin configuration to the pom.xml
  * Configure the unit test framework of choice (here we use Junit5).
  * Configure the test mode. This can be tricky, see the Spring Boot equivalent example project). In this
    project we will need the JAX-RS mode! 
  * Configure the location of your base test class(es).
  * Configure how the generated classes will be bound to a base test class! (See documentation in the pom.xml).  
* Add a base test class (or more when required). The generated integration tests will extend a base class. 
  Which one will be selected for a test is determined by the configuration of the plugin in the pom.xml. 
  The base test classes will setup the runtime environment for your unit test. So they determine which runner to
  use, start the application, wire the components, mock the components, prepare initial state (like DB content), etc.
  Mocking is the tricky part! If you have one base test class for all your contracts and you have chosen to mock,
  then you need to define all the mocking behavior upfront for all the scenarios/contracts! You have to
  code careful to make sure what mocking belongs to which contract and you have to make sure they do not
  clash/interfere with each other! The other option is to have a separate base test class for each contract! 
  Best approach choosing that solution might be to have a high level abstract test class that defines the generic 
  mocked environment and application setup, which will be extended by the abstract base test class that you
  implement for the different scenarios that only have the scenario specific mocking setup.
  We will demonstrate a __different__ problem in this project. One of the REST resources compresses the
  returned result, the others don't. This means that the client used in the integration test, testing the
  REST resource that compresses the result, needs to decompress the data. So we cannot share the base test
  class across all scenarios because we need different behavior in the client. In this scenario we 
  implemented to base test classes, one that configures a client that decompresses the result and one that 
  does not.
* Add the contracts. You can write them in YML or Groovy. With Groovy you have a lot more options. We
  selected Groovy in this example project. Check out the Spring documentation for all the options, there
  are many!

You are good to go! Rune the maven integration test command and check the build directory (target) for a folder _generated-test-resources_.
In that folder you will find the generated integration tests and in the surefire-reports folder you
will find the test reports, they will have been run by the maven test command.

If a test failed, you can run them from your IDE (maybe you need to tell your IDE that these are 
test-sources first). So you can debug and trace like with any normal hand written integration test!

I advice against copying these files in the the _src/test_ folder. They will be generated each time and the
big benefit is that if the behavior of your REST API changes, you document this in your contracts and you 
do __NOT__ have to refactor any test! 

# Pros

* your customers/clients can help define the behavior of your service
* automatic __generation__ of REST API tests
* automatic stub generation (Wiremock)
* no more refactoring required of your integration tests when REST API changes
* a stub runner for mocking dependencies (using the generated stubs of other services)
* support for messaging APIs as well
* powerful coding options in contract files (regular expressions, matchers, call java code, ...)

# Cons

* when mocking, you have to define ALL mocking behavior required for ALL the scenarios the contracts
  describe for all generated tests up front in the base test classes
* different REST API behavior (like compressing the returned result) for different APIs require 
  different base test class implementations
* only one test mode supported per project (maven plugin configuration)
* a little buy-in into Spring - just one or two Spring Cloud libraries
* might not be using the same application container as in prod

# Useful links

* https://martinfowler.com/articles/consumerDrivenContracts.html
* https://spring.io/projects/spring-cloud-contract
* https://cloud.spring.io/spring-cloud-contract/reference/html/project-features.html#features-jax-rs
* https://resteasy.github.io/
* https://www.baeldung.com/integration-testing-a-rest-api
