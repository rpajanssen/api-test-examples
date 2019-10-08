# What?

This example project will help you write integration tests for JAX-RS REST resources using RestEasy

# Unit Test Examples

This project will demonstrate some unit test examples. All names of products used in this intro are explained later in this document.
It will demonstrate wired unit tests for JAX-RS classes using RestEasy and using [Mockito](#Mockito) for CDI dependencies in the resources.
It will demonstrate wired unit tests for JAX-RS classes using RestEasy and using [RestEasy](#RestEasy) plugins to wire CDI dependencies in the resources
and activate other CDI classes like CDI method interceptors.

# Junit

As java unit test framework we will use [Junit](https://junit.org/).

We have examples of JUnit5 and will *NOT* demonstrate anything from Junit4.

<a name="Mockito"></a>
# Mockito

As mocking framework we will use [Mockito 2](https://site.mockito.org).

<a name="RestEasy"></a>
# RestEasy

One option to test JAX-RS resources is to use RestEasy. 

Like with JerseyTest with RestEasy the unit test will create an in-memory http-server and from your unit test
you deploy the resource(s) you want to test on that server. Then the unit tests will be executed and those
unit test will actually call your REST API in your resource. These test run pretty fast so most of you won't
mind running them as a unit instead of integration test.

# Execute unit tests

The execution of the unit tests is done through maven:

```mvn clean test```
