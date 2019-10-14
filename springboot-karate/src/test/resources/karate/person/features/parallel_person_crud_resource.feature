@parallel=true
@nonsequential
Feature: Testing the Person REST API with Karate

  Background:
    # set the base url to be used for each scenario, if a scenario omits its local path only the base url will be used
    * url testBaseUrl + '/api/person'
    # execute the before_each_person_scenario.feature before each scenario, the JUnit @Before equivalent
    * def beforeScenarioFile = supportFolderPath + 'before_each_person_scenario.feature'
    * call read(beforeScenarioFile)
    * def afterScenarioFile = supportFolderPath + 'after_each_person_scenario.feature'
    * configure afterFeature = function(){ karate.call(afterScenarioFile); }

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
