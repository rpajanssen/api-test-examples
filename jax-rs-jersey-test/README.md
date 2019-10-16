# What?

This example project will help you write integration tests for JAX-RS REST resources using JerseyTest.

Some features of the JerseyTest framework are:
* support for multiple containers like: gGrizzly, in-memory, JDK, Jetty
* able to run against any external container
* automated configurable traffic logging

If you want to develop a test using Jersey Test Framework, your test class needs to subclass the
JerseyTest base class and configure the set of resources and/or providers that will be deployed 
as part of the application under test.

# Requirements

* maven 3.6.1 (or higher)
* java 11

# Tl;DR

I just wanna run it: `mvn clean test`


# How to add JerseyTest to your JAX-RS application?

Assuming you have all dependencies for your JAX-RS application in place and of course you have
your Junit5 dependencies for your unit tests in your pom.xml.

* Add the junit vintage dependency to run Junit4 test under Junit5 since JerseyTest does not run (yet)
  with Junit5.
* Add the required JerseyTest dependencies to your pom.xml:
  * core
  * http server
  * dependency injection engine
  * (de)serialization
  * bean validation
  * ... some other sometimes fishy libs... (you will probably find out by trial-and-error)  
* Implement your test class:
  * extend JerseyTest
  * configure the resources and dependencies under test
  * implement your tests using constructs/methods from the extended JerseyTest class to perform
    the REST calls

In our example we implemented several different concrete test classes but they all require the exact
same test code, so we abstracted that into an abstract test class to prevent code duplication.

Note also how we handled the integration test for the one resource that compresses the returned result.

# Pros

* fast execution
* simple to program and apply

# Cons

* based on Junit4
* you need to extend JerseyTest
* no full CDI support (at least not without a fight)
  * method interceptors may not work
  * the implemented StatusFilter does not work
* you might need to run the tests against an external application container to test _everything_
* not a production like application container

# Useful links

* https://eclipse-ee4j.github.io/jersey/
* https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/test-framework.html
* https://www.baeldung.com/jersey-test
* https://javaee.github.io/hk2/
