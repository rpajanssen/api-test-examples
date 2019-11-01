Feature: Testing the Hello DevCon REST API with Karate
  Scenario: Should say hi
    * url 'http://localhost:8080/api/hello'
    When method GET
    Then status 200
    And match response == "Hi DevCon"

