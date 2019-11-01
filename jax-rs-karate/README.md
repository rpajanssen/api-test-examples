# What?

This example project will help you write integration tests for JAX-RS REST resources using Karate and RestEasy.

Karate used to be build on top of Cucumber but they broke free from Cucumber. The latest version is no
longer build on top / dependent on Cucumber.

We implemented two Karate test runners. One executes the features sequential, one executes the features
in parallel and integrates with Cucumber Reports. Note however that although running the scenarios in parallel
seems useful... it will severely hamper you! Just look at the feature file! You may need the occasional
trick and/or you may not be able to actually include scenarios! Concurrency can be a b*tch!  

In the _surefire-reports_ folder an html report will be available for the sequential executed features.
The parallel executed features will produce a json file that will be used by Cucumber Reports to produce
a report that will be exported to the _cucumber-html-reports_ folder.

Of course the traditional surefire reports will also be generated.

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

We only use RestEasy to wire up the application, not to actually test it. For more info on RestEasy
checkout the __jax-rs-resteasy__ module!

# How to add Karate to your Spring Boot project?

* add the Karate dependencies to your pom.xml
  * core karate
  * optional UI
  * optional integration with Cucumber Reports 
* implement java integration test class that serves as a Karate feature runner:
  * it starts up the application
  * it sets up (part of) the karate environment
  * it will run all features
  * optionally it integrates with Cucumber Reports
* implement karate config file
  * it sets up the environment / context the Karate test will use when executed
  * a lot more is possible then we do in this example (see the Karate documentation)
* implement feature files
  * main feature files : the actual features
  * supporting feature files : plumbing used by the main feature files - often having supporting
    features for setup/cleanup etc.

# Pros

* BDD in one place (only gherkin)
* readable (it depends?) integration tests
* if you are lucky, no 'real' programming skills required
* no back-end code required
* for complex stuff : javascript support (from gherkin files)
* ability to call java classes (utility libs)
* integration with cucumber reports

# Cons

* if you need the more complex karate features pretty quick -> you need to be a developer!
* managing the required state for your test to start may get complicated
* cucumber reports require json test output, json test output only generated when running the tests in parallel
* it does not always work as expected (as example: tags, ignoring features, ...) 
* issues in combining the Karate runner with RestEasy (see issues below).

## Bugs

* delete_person feature is evaluated even though it is ignored and then the id (argument) is not known
  * mvn clean integration test from terminal : works
  * mvn clean integration test from ide configuration : delete_person feature is incorrectly executed/evaluated
  * run integration test from ide : delete_person feature is incorrectly executed/evaluated
        
## Issues

Use of tags is supported but not implemented well. There is a @parallel tag that you can use to run
scenarios in parallel. Is has an argument (a value) that is true or false like: @parallel=false. 
You are supposed to be able to use the tags to include/exclude features from running. But... if I
annotated a feature with @parallel=false and run the suite with '~@parallel' then this won't work!
For our purposes we had to define two tags ourselves

The combination of Karate and RestEasy in the integration tests results in a re-instantiation of the 
application and beans for each rest call!!!! So multiple instances of our beans will be created and 
used during setup/test/teardown... so we need to implement a hack in the DAO to make the state 
last a bit longer :(
        
# Useful links        
        
* https://github.com/intuit/karate
* https://apiumhub.com/tech-blog-barcelona/karate-framework-api-tests/
* https://automationpanda.com/2018/12/10/testing-web-services-with-karate/
* https://www.testingexcellence.com/karate-api-testing-tool-cheat-sheet/
