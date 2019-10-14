# What?

This example project will help you write integration tests for SpringBoot REST resources using Karate.

Karate used to be build on top of Cucumber but they broke free from Cucumber. The latest version is no
longer build on top / dependent on Cucumber.

# Pros

- BDD in one place (only gherkin)
- readable (it depends?) integration tests
- no 'real' programming skills required
- no back-end code required
- for complex stuff : javascript support (from gherkin files)
- ability to call java classes (utility libs)
- integration with cucumber reports

# Cons

- you need the more complex karate features pretty quick -> you need to be a developer!
- different behavior running from your IDE and running a mvn command from the command line
- cucumber reports require json test output, json test output only generated when running the tests in parallel 

- bug : cleanup might fail because scenario runs before after-scenario has finished
- bug : delete_person feature is evaluated even though it is ignored and then the id (argument) is not known

        - mvn clean integration test from terminal : works
        - mvn clean integration test from ide configuration : delete_person feature is executed/evaulated
        - run integration test from ide : delete_person feature is executed/evaulated
        
        
        
        
        
        
https://apiumhub.com/tech-blog-barcelona/karate-framework-api-tests/
https://automationpanda.com/2018/12/10/testing-web-services-with-karate/


