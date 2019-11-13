# What?

This example project will help you write integration tests for JAX-RS REST resources using the
KumuluzEE framework as the MicroProfile implementation. RestAssured will be used in the
actual integration tests.

[MicroProfile](https://microprofile.io/) specifies a collection of Java EE APIs and technologies 
which together form a core baseline microservice that aims to deliver application portability 
across multiple runtimes. 

[KumuluzEE](https://ee.kumuluz.com/) is a lightweight framework for developing 
microservices using standard Java, Java EE / Jakarta EE technologies and migrating 
existing Java applications to microservices. KumuluzEE packages microservices as 
standalone JARs. KumuluzEE microservices are lightweight and optimized for size and 
start-up time.
 
Another well known framework is [Quarkus](https://quarkus.io/).

And guess what... Websphere Liberty also has support for MicroProfile!

# Requirements

* maven 3.6.1 (or higher)
* java 11

# Tl;DR

I just wanna run it: `mvn clean integration-test`

# How to add Arquillian to your test configuration?

We will be using Arquillian in our integration tests to deploy and run our application.

* add Arquillian bom to your dependency management in your pom
* add an arquillian.xml to your test resources
* add and configure the mvn resources plugin to copy this test resources to the class path
* add the KumuluzEE Arquillian container dependency
* add the KumuluzEE Junit container dependency
* add the Junit Vintage engine if you are using Junit5

# How to add KumuzulEE to your Micro Profile application?

* add the KumuluzEE to your dependency management in your pom
* add the KumuluzEE MicroProfile dependency and set the __includeRequiredLibraries__ property
  in the arquillian.xml 
* add the KumuluzEE dependencies for all the Java specs that you need for your application  
* add and configure the mvn dependency plugin for packaging

# How to add RestAssured to your application?

* add the rest-assured dependency
* optionally add the assertj-core dependency of you prefer to use AssertJ

# How to debug while running the integration tests?

* in-comment the __javaArguments__ line in the arquillian.xml 
* run ```mvn clean integration-test```
* wait till the build stops and announces it is listening to port 8787
* start your debug session from your IDE... and the the mvn build will continue automatically 
  running your tests

# Pros

* Lightweight applications / microservices
* Full JAX-RS and CDI support during tests

# Cons

* Arquillian quirks (Junit4 only!)
* A tad more difficult to trace/debug
* You may need to extend your API for testing (see LoggingResource)

# Useful links

* https://microprofile.io/
* https://ee.kumuluz.com/

* https://github.com/kumuluz/kumuluzee-testing/tree/master/kumuluzee-arquillian-container
* https://kumuluz.io/blog/kumuluzee/architecture/2015/06/04/microservices-with-java-ee-and-kumuluzee/

