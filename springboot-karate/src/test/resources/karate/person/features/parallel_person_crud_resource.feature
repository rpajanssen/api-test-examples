@parallel=true
@nonsequential
Feature: Testing the Person REST API with Karate

  # the background executes code before the scenarios are executed (of course there are some exceptions :) )
  # this background :
  # - defines the url to the resource using the base-url set in the karate-config.js, that might use environment
  #   settings set on application / test startup
  # - runs a setup feature to create the proper state for each scenario, like the DB state
  # - defines a teardown / cleanup to be run after each scenario
  Background:
    # set the base url to be used for each scenario, if a scenario omits its local path only the base url will be used
    * url testBaseUrl + '/api/person'
    # run a setup and teardown before and after the feature, we can't run it before and after each scenario because we
    # will run the scenarios in parallel and the setup / teardown will interfere with executing scnarios!
    * def beforeFeatureFile = supportFolderPath + 'before_each_person_scenario.feature'
    * callonce read(beforeFeatureFile)
    * def afterFeatureFile = supportFolderPath + 'after_each_person_scenario.feature'
    * configure afterFeature = function(){ karate.call(afterFeatureFile); }

  Scenario: Should return all the persons
    When method GET
    Then status 200
    And match response.items == [{"id":"#number","firstName":"Jan","lastName":"Janssen"},{"id":"#number","firstName":"Pieter","lastName":"Pietersen"},{"id":"#number","firstName":"Erik","lastName":"Eriksen"}]

  Scenario: Should add a person
    Given request { "firstName":"Katy","lastName":"Perry" }
    When method post
    Then status 201
    And match response == { id:"#number","firstName":"Katy","lastName":"Perry" }

  Scenario: Should update a person
    Given request {"id":"1003","firstName":"Erik","lastName":"Erikson" }
    When method put
    Then status 200
    And match response == { id:1003,"firstName":"Erik","lastName":"Erikson" }

  Scenario: Should not update if the person data is invalid
    Given request {"id":"1003","lastName":"Erikson" }
    When method put
    Then status 400
    And match response.code == "0020"
    And match response == { "data":{ id:1003, "firstName":null,"lastName":"Erikson" },"code":"0020" }

  Scenario: Should delete a person
    Given path "/1002"
    When method delete
    Then status 202
