# What?

This example project will help you write/generate integration tests for SpringBoot REST resources using SpringCloudContract.

[Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract) is an umbrella project holding solutions that 
help users in successfully implementing the [Consumer Driven Contracts approach](https://martinfowler.com/articles/consumerDrivenContracts.html).

In short, consumer Driven Contract approach is nothing more than an agreement, to test integration points, 
between the consumer and provider about the format of data that they communicate between each other.

Currently Spring Cloud Contract consists of the Spring Cloud Contract Verifier project and it gives us
the following features:
* ensure that HTTP / Messaging stubs (used when developing the client) are doing exactly what actual server-side implementation will do,
* promote acceptance test driven development method and Microservices architectural style,
* provide a way to publish changes in contracts that are immediately visible on both sides of the communication,
* generate _boilerplate_ test code used on the server side.

This project will focus on the generation of the test code!

# Requirements

* maven 3.6.1 (or higher)
* java 11

# Tl;DR

I just wanna run it: `mvn clean test`

Generated test will be in the target folder and will be automatically run with the traditional
surefire reports.

# How to add Spring Cloud Contract to your Spring Boot project?

- add spring cloud contract and rest-easy dependencies in pom.xml
- add and configure spring-cloud-contract-maven-plugin configuration to the pom.xml
- add a base test class (or more when required) 
- add the contracts

# Pros

* automatic __generation__ of REST API tests
* automatic stub generation (wiremock)
* a stub runner for mocking dependencies (using the generated stubs of other services)
* support for messaging APIs as well
* powerful coding options in contract files (regular expressions, matchers, call java code, ...)

# Cons

* when mocking, you have to define ALL mocking behavior required for ALL the scenarios the contracts
describe for all generated tests up front in the base test classes
* different REST API behavior (like compressing the returned result) for different APIs require 
different base test class implementations (see jax-rs-spring-cloud-contract equivalent example project)
* only one mode supported per project (maven plugin configuration)
* jvm based
* maven / gradle dependency
* a buy-in into Spring
* compatibility issues with rest-assured
* versioning issues with dependencies in the Spring BOM

# Links

* https://martinfowler.com/articles/consumerDrivenContracts.html
* https://spring.io/projects/spring-cloud-contract
