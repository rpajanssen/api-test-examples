# What?

This example project will help you write integration tests for SpringBoot REST resources using Karate.


# Pros

- readable (it depends?) integration tests
- no 'real' programming skills required
- no back-end code required

# Cons

- you need the more complex karate features pretty quick -> you need to be a developer!
- different behavior running from your IDE and running a mvn command from the command line 

- bug : cleanup might fail because scenario runs before after-scenario has finished
- bug : delete_person feature is evaluated even though it is ignored and then the id (argument) is not known

        - mvn clean integration test from terminal : works
        - mvn clean integration test from ide configuration : delete_person feature is executed/evaulated
        - run integration test from ide : delete_person feature is executed/evaulated
        
        
