# What?

This example project will help you write integration tests for SpringBoot REST resources using RestAssured
and using Spring MockMvc (yeah... a bonus!).

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

## Pros RestAssured

* well established testing framework with a large community
* in-built library of assertions, matchers and extractors
* specifically designed and built for API automation testing

## Cons RestAssured

* some incompatibilities with the Spring BOM
      
# MockMVC   
      
## How to add MockMVC to your Spring Boot project?

* add spring-boot-starter-test as dependency to your pom.xml
* implement java integration test classes
      
## Pros MockMVC

* well established testing framework with a large community
* in-built library of assertions, matchers and extractors
* specifically designed and built for API automation testing
* really easy to mock dependencies

## Cons MockMVC

* a framework lock-in with Spring
         
      
# Useful links        
        
* http://rest-assured.io/
* https://www.baeldung.com/rest-assured-tutorial
