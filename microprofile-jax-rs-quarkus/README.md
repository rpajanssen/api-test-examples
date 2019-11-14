todo : WORK IN PROGRESS

# What?

This example project will help you write integration tests for JAX-RS REST resources using the
Quarkus framework as the MicroProfile implementation. RestAssured will be used in the
actual integration tests.

[MicroProfile](https://microprofile.io/) specifies a collection of Java EE APIs and technologies 
which together form a core baseline microservice that aims to deliver application portability 
across multiple runtimes. 

[Quarkus](https://quarkus.io/) is a Kubernetes Native Java stack tailored for OpenJDK HotSpot 
and GraalVM, crafted from the best of breed Java libraries and standards.
 
And guess what... Websphere Liberty also has support for MicroProfile!

# Requirements

* maven 3.6.1 (or higher)
* java 11
* GraalVM (Java 8) for native builds

# Tl;DR

I just wanna run it: ```mvn clean compile quarkus:dev```
I just wanna run the unit tests: ```mvn clean test```
I wanna have a native build and run the native integration tests:```mvn verify -Pnative``` 

# How to add Quarkus to your Micro Profile application?

It is not so much as adding Quarkus to your project... you need to setup it up as a Quarkus project from the
get-go!

* The first option is to use the initializer (a bit like Spring Initializar): https://code.quarkus.io/
* The second option is to run a mvn command:
```mvn io.quarkus:quarkus-maven-plugin:1.0.0.CR1:create \
           -DprojectGroupId=org.acme \
           -DprojectArtifactId=getting-started \
           -DclassName="org.acme.quickstart.GreetingResource" \
           -Dpath="/hello" 
```

# OpenAPI / Swagger

Simply add the _quarkus-smallrye-openapi_ dependency and automatically _/openapi_ resource will be available.
If you want the Swagger UI to be available as well then add `quarkus.swagger-ui.always-include=true` to the application properties,
automatically the _/swagger-ui_ resource will become available.

# Pros

* Lightweight applications / micro-services : fast, small memory footprint
* Full JAX-RS and CDI support during tests
* Container centric

# Cons

* GraalVM is Java 8
* Some quirks


# Quirks

* If a REST resource (or exception-mapper) returns a _javax.ws.rs.core.Response_ object, and the entity has been
  set with an instance of the object to be returned, you may run into serialization issues. The behavior you might
  observe is that the body of the response will be an empty json!


# Useful links

* https://microprofile.io/
* https://quarkus.io/
* https://quarkus.io/get-started/

