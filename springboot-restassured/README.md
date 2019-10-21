# What?

This example project will help you write integration tests for SpringBoot REST resources using RestAssured
and using Spring MockMvc (yeah... a bonus!).

We have two MockMVC examples! One sliced test that mocks non-MVC dependencies/classes. An one fully wired
MockMVC test (nothing mocked!), that works exactly the same as a fully wired RestAssured driven integration 
test.

And... we also have some examples of RestAssuredMockMvc! Yes... RestAssured integrates with MockMVC. Again
we demonstrate a mocked environment and a fully wired.

Note that you have to question yourselves why you are using RestAssured if you are using Spring Boot!

# Requirements

* maven 3.6.1 (or higher)
* java 11

# Tl;DR

I just wanna run it: `mvn clean integration-test`

# RestAssured

## How to add RestAssured to your Spring Boot project?

* add the RestAssured dependencies to your pom.xml
  * core: rest-assured and rest-assured-common
  * fix: json-path and xml-path are required when using the Spring Boot BOM 
* implement java integration test classes 

Note that RestAssured provides an integration with MockMVC as well!

## Pros RestAssured

* well established testing framework with a large community
* in-built library of assertions, matchers and extractors
* specifically designed and built for API automation testing

## Cons RestAssured

* you need to add - too many? - dependencies to your pom
* some incompatibilities with the Spring BOM (dependency hell)
      
# MockMVC   
      
## How to add MockMVC to your Spring Boot project?

* add spring-boot-starter-test as dependency to your pom.xml
* implement java integration test classes

Note: RestAssured has a MockMVC integration. It just requires you to add a spring-mock-mvc test dependency
and then you can write integration test using Rest Assured Mock MVC :)
      
## Pros MockMVC

* well established testing framework with a large community
* in-built library of assertions, matchers and extractors
* specifically designed and built for API automation testing
* really easy to mock dependencies

## Cons MockMVC

* a framework lock-in with Spring
         
      
# Useful links        
        
* https://spring.io/guides/gs/testing-web/
* http://rest-assured.io/
* https://www.baeldung.com/rest-assured-tutorial
* https://www.baeldung.com/spring-mock-mvc-rest-assured
