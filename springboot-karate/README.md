# What?

This example project will help you write integration tests for SpringBoot REST resources using Karate.

Karate used to be build on top of Cucumber but they broke free from Cucumber. The latest version is no
longer build on top / dependent on Cucumber.

todo

- java integration test class that:
  - starts up the application
  - sets up part of the karate environment
  - will run all features
- karate config file
- feature files
  - main feature files
  - supporting feature files

-> sure-fire reports
-> cucumber reports


# Issues

You have a 'background' in which you can run an afterScenario or afterFeature. There is no beforeScenario,
that is each command in the 'background' by default and also NO beforeFeature. You have to use the 'callonce'
command to mimic a before feature.

Use of tags is supported but not implemented well. There is a @parallel tag that you can use to run
scenarios in parallel. Is has an argument (a value) that is true or false like: @parallel=false. 
You are supposed to be able to use the tags to include/exclude features from running. But... if I
annotated a feature with @parallel=false and run the suite with '~@parallel' then this won't work!
For our purposes we had to define two tags ourselves


# Pros

- BDD in one place (only gherkin)
- readable (it depends?) integration tests
- no 'real' programming skills required
- no back-end code required
- for complex stuff : javascript support (from gherkin files)
- ability to call java classes (utility libs)
- integration with cucumber reports

# Cons

-- soso implementation, seems a bit inconsistent (before/after hooks, tags, ...)
- you need the more complex karate features pretty quick -> you need to be a developer!
- different behavior running from your IDE and running a mvn command from the command line
- cucumber reports require json test output, json test output only generated when running the tests in parallel 

- bug : cleanup might fail because scenario runs before after-scenario has finished
- bug : delete_person feature is evaluated even though it is ignored and then the id (argument) is not known

        - mvn clean integration test from terminal : works
        - mvn clean integration test from ide configuration : delete_person feature is executed/evaulated
        - run integration test from ide : delete_person feature is executed/evaulated
        
        
        
        
# Some links        
        
https://apiumhub.com/tech-blog-barcelona/karate-framework-api-tests/
https://automationpanda.com/2018/12/10/testing-web-services-with-karate/
https://www.testingexcellence.com/karate-api-testing-tool-cheat-sheet/
https://github.com/intuit/karate/blob/master/karate-demo/src/test/java/demo/DemoTestParallel.java#L43


