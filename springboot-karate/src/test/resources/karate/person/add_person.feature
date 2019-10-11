# This is a re-usable feature that will be called from other features. We prevent it from being picked up by our runner
# by annotating it with @ignore.
#
# This feature will call the add-person REST API to add a person and we will use it to populate the initial
# database before each scenario.
#
# This feature has one scenario and it will be called with a person instance as argument. The first-name and last-name
# property values of this instance are used to build the json request body that we will send to the the REST API.
#
# So this is an example of a feature using arguments!
@ignore
Feature: common reusable feature to add a person

  Background:
    * url testBaseUrl + '/api/person'

  Scenario:
    Given request { "firstName":"#(firstName)","lastName":"#(lastName)" }
    When method post
    Then status 201
    And match response == { id:"#number","firstName":"#(firstName)","lastName":"#(lastName)" }
