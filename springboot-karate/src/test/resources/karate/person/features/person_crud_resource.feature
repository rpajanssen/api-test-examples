@parallel=false
@sequential
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
    # the idea is to execute a setup and teardown before and after each scenario, and that will only work if you run the
    # tests sequentially!
    # execute the before_each_person_scenario.feature before each scenario, the JUnit @BeforeEach equivalent
    * def beforeScenarioFile = supportFolderPath + 'before_each_person_scenario.feature'
    * call read(beforeScenarioFile)
    # execute the after_each_person_scenario.feature after each scenario, the JUnit @AfterEach equivalent
    * def afterScenarioFile = supportFolderPath + 'after_each_person_scenario.feature'
    * configure afterScenario = function(){ karate.call(afterScenarioFile); }

  Scenario: Should return all the persons
    When method GET
    Then status 200
    And match response.items[*].firstName contains only ['Jan', 'Pieter', 'Erik']
    And match response.items[*].lastName contains only ['Janssen', 'Pietersen', 'Eriksen']
    # below might not work if the order in which the data is returned is not fixed
    #And match response.items == [{"id":"#number","firstName":"Jan","lastName":"Janssen"},{"id":"#number","firstName":"Pieter","lastName":"Pietersen"},{"id":"#number","firstName":"Erik","lastName":"Eriksen"}]

  Scenario: Should add a person
    Given request { id:1005, "firstName":"Katy","lastName":"Perry" }
    When method post
    Then status 201
    And match response == { id:"#number","firstName":"Katy","lastName":"Perry" }

  Scenario: Should update a person
    Given request {"id":"3","firstName":"Erik","lastName":"Erikson" }
    When method put
    Then status 200

  Scenario: Should not update if the person data is invalid
    Given request {"id":"3","lastName":"Erikson" }
    When method put
    Then status 400
    And match response.code == "0020"
    And match response == { "data":{ "id":3, "firstName":null,"lastName":"Erikson" },"code":"0020" }

  Scenario: Should delete a person
    Given path "/2"
    When method delete
    Then status 204
